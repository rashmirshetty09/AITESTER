import requests
from config_store import get


class LLMError(Exception):
    pass


OLLAMA_BASE = "http://localhost:11434"
OLLAMA_MODEL = "gemma3:1b"
GROQ_BASE = "https://api.groq.com/openai/v1"
GROQ_MODEL = "llama-3.1-8b-instant"


def generate(prompt: str, provider: str | None = None) -> str:
    """
    Generate text from the LLM.

    Tries Ollama first (unless provider explicitly set to "groq").
    Falls back to Groq if Ollama is unavailable.
    """
    if provider is None:
        provider = get("LLM_PROVIDER") or "ollama"

    # Try Ollama unless user explicitly chose Groq
    if provider != "groq":
        try:
            return _call_ollama(prompt)
        except (LLMError, requests.exceptions.ConnectionError, requests.exceptions.Timeout):
            pass  # fall through to Groq

    # Fallback to Groq
    return _call_groq(prompt)


def _call_ollama(prompt: str) -> str:
    """Call local Ollama API."""
    try:
        resp = requests.post(
            f"{OLLAMA_BASE}/api/generate",
            json={
                "model": OLLAMA_MODEL,
                "prompt": prompt,
                "stream": False,
            },
            timeout=30,
        )
        resp.raise_for_status()
        data = resp.json()
        return data.get("response", "").strip()
    except requests.exceptions.ConnectionError:
        raise LLMError("Ollama is not running. Start it with `ollama serve` or switch to Groq.")
    except requests.exceptions.Timeout:
        raise LLMError("Ollama timed out. The model may be too large or the prompt too complex.")
    except requests.exceptions.RequestException as e:
        raise LLMError(f"Ollama error: {e}")


def _call_groq(prompt: str) -> str:
    """Call Groq cloud API."""
    api_key = get("GROQ_API_KEY")
    if not api_key:
        raise LLMError(
            "Groq API key not configured. Add it in Settings or ensure Ollama is running."
        )

    try:
        resp = requests.post(
            f"{GROQ_BASE}/chat/completions",
            json={
                "model": GROQ_MODEL,
                "messages": [{"role": "user", "content": prompt}],
            },
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json",
            },
            timeout=60,
        )
        resp.raise_for_status()
        data = resp.json()
        return data["choices"][0]["message"]["content"].strip()
    except requests.exceptions.Timeout:
        raise LLMError("Groq request timed out.")
    except requests.exceptions.RequestException as e:
        raise LLMError(f"Groq error: {e}")


def test_ollama(cfg: dict | None = None) -> dict:
    """Check if Ollama is reachable. Returns dict {ok, status, error}.

    Optional `cfg` may include `OLLAMA_URL`.
    """
    url = OLLAMA_BASE
    if cfg and cfg.get("OLLAMA_URL"):
        url = cfg.get("OLLAMA_URL")
    try:
        resp = requests.get(f"{url.rstrip('/')}/api/tags", timeout=5, verify=False)
        return {"ok": resp.ok, "status": resp.status_code, "body": resp.text[:500]}
    except requests.exceptions.RequestException as e:
        # Common misconfiguration: Ollama may be served over plain HTTP while
        # config uses https://localhost:11434 which triggers an SSL handshake error.
        # If we get an SSL error or other connection issue, try the same host over HTTP.
        try:
            if url.lower().startswith("https://"):
                http_url = "http://" + url.split("://", 1)[1]
                resp = requests.get(f"{http_url.rstrip('/')}/api/tags", timeout=5)
                return {"ok": resp.ok, "status": resp.status_code, "body": resp.text[:500]}
        except requests.exceptions.RequestException:
            pass
        return {"ok": False, "error": str(e)}


def test_groq(cfg: dict | None = None) -> dict:
    """Check Groq API key validity. Returns dict {ok, status, error}.

    Optional `cfg` may include `GROQ_API_KEY`.
    """
    api_key = get("GROQ_API_KEY")
    if cfg and cfg.get("GROQ_API_KEY"):
        api_key = cfg.get("GROQ_API_KEY")
    if not api_key:
        return {"ok": False, "error": "Missing Groq API key"}
    try:
        resp = requests.get(
            f"{GROQ_BASE}/models",
            headers={"Authorization": f"Bearer {api_key}"},
            timeout=10,
        )
        return {"ok": resp.ok, "status": resp.status_code, "body": resp.text}
    except requests.exceptions.RequestException as e:
        return {"ok": False, "error": str(e)}