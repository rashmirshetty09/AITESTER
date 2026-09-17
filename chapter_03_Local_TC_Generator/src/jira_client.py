import re
import requests
from requests.auth import HTTPBasicAuth
from config_store import get


class JiraError(Exception):
    pass


class ConnectionError(JiraError):
    pass


class AuthenticationError(JiraError):
    pass


class NotFoundError(JiraError):
    pass


def _build_url(path: str) -> str:
    base = get("JIRA_URL", "").rstrip("/")
    return f"{base}{path}"


def fetch_ticket(ticket_key: str) -> dict:
    """Fetch a Jira ticket and return {key, summary, description, acceptance_criteria}."""
    email = get("JIRA_EMAIL")
    token = get("JIRA_API_TOKEN")

    if not email or not token:
        raise AuthenticationError(
            "Jira credentials not configured. Go to Settings page to set them up."
        )

    url = _build_url(f"/rest/api/2/issue/{ticket_key}")

    try:
        resp = requests.get(
            url,
            auth=HTTPBasicAuth(email, token),
            headers={"Accept": "application/json"},
            timeout=15,
        )
    except requests.exceptions.ConnectionError:
        raise ConnectionError(
            f"Cannot reach Jira at {get('JIRA_URL')}. Check the URL in Settings."
        )
    except requests.exceptions.Timeout:
        raise ConnectionError("Jira request timed out. Check your network or Jira URL.")

    if resp.status_code == 401:
        raise AuthenticationError(
            "Jira authentication failed. Check your email and API token in Settings."
        )
    if resp.status_code == 404:
        raise NotFoundError(f"Ticket **{ticket_key}** not found.")
    if not resp.ok:
        raise JiraError(f"Jira error {resp.status_code}: {resp.text[:300]}")

    data = resp.json()
    fields = data.get("fields", {})

    summary = fields.get("summary", "")
    description_raw = fields.get("description", {})

    if isinstance(description_raw, dict):
        description = _extract_text_from_adf(description_raw)
    else:
        description = str(description_raw) if description_raw else ""

    acceptance_criteria = _extract_acceptance_criteria(description, fields)

    return {
        "key": data.get("key", ticket_key),
        "summary": summary,
        "description": description,
        "acceptance_criteria": acceptance_criteria,
    }


def _extract_text_from_adf(doc: dict) -> str:
    """Extract plain text from Atlassian Document Format (ADF)."""
    texts = []

    def walk(node):
        if node.get("type") == "text":
            texts.append(node.get("text", ""))
        for child in node.get("content", []):
            walk(child)

    walk(doc)
    return "\n".join(texts)


def _extract_acceptance_criteria(description: str, fields: dict) -> str:
    """Try to pull acceptance criteria from description headers or custom fields."""
    patterns = [
        r"(?i)acceptance\s*criteria\s*:?\s*\n(.*?)(?=\n\s*\n\w|\Z)",
        r"(?i)##\s*acceptance\s*criteria\s*\n(.*?)(?=\n#|\Z)",
        r"(?i)ac\s*:?\s*\n(.*?)(?=\n\s*\n\w|\Z)",
    ]
    for pat in patterns:
        match = re.search(pat, description, re.DOTALL)
        if match:
            return match.group(1).strip()

    for key, value in fields.items():
        if "acceptance" in key.lower() and value:
            return str(value)

    return ""


def test_connection(cfg: dict | None = None) -> dict:
    """Verify Jira credentials.

    Accepts an optional `cfg` dict with keys `JIRA_URL`, `JIRA_EMAIL`, `JIRA_API_TOKEN`.
    Returns dict: {ok: bool, status: int|None, body: str|None, error: str|None}
    """
    use_cfg = {}
    if cfg:
        use_cfg["JIRA_URL"] = cfg.get("JIRA_URL")
        use_cfg["JIRA_EMAIL"] = cfg.get("JIRA_EMAIL")
        use_cfg["JIRA_API_TOKEN"] = cfg.get("JIRA_API_TOKEN")
    else:
        use_cfg["JIRA_URL"] = get("JIRA_URL")
        use_cfg["JIRA_EMAIL"] = get("JIRA_EMAIL")
        use_cfg["JIRA_API_TOKEN"] = get("JIRA_API_TOKEN")

    if not use_cfg.get("JIRA_URL") or not use_cfg.get("JIRA_EMAIL") or not use_cfg.get("JIRA_API_TOKEN"):
        return {"ok": False, "error": "Missing Jira credentials"}

    url = use_cfg["JIRA_URL"].rstrip("/") + "/rest/api/2/myself"
    try:
        resp = requests.get(
            url,
            auth=HTTPBasicAuth(use_cfg["JIRA_EMAIL"], use_cfg["JIRA_API_TOKEN"]),
            headers={"Accept": "application/json"},
            timeout=10,
        )
    except requests.exceptions.RequestException as e:
        return {"ok": False, "error": str(e)}

    if resp.ok:
        try:
            body = resp.json()
        except Exception:
            body = resp.text
        return {"ok": True, "status": resp.status_code, "body": body}
    if resp.status_code == 401:
        return {"ok": False, "status": resp.status_code, "error": "Authentication failed"}
    return {"ok": False, "status": resp.status_code, "error": resp.text}