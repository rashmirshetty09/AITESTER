# Jira Test Case Generator — Implementation Plan

**Purpose**
Create a lightweight two-screen Streamlit app that turns a single Jira ticket into a draft test case using a local Ollama model with a Groq fallback.

**File structure**
- `app.py` — main chat screen
- `pages/settings.py` — Streamlit settings screen
- `config_store.py` — read/write persisted settings (JSON; respects `.env`)
- `jira_client.py` — fetch ticket details from Jira REST API
- `llm_client.py` — Ollama primary; Groq fallback (reads Groq key from config)
- `templates/` — contains `testcase_template.md`
- `requirements.txt`, `README.md`

**Two screens**
- Chat (`app.py`): ChatGPT-style interface, text input + Send; shows chat history and generated test cases.
- Settings (`pages/settings.py`): Jira URL, email, API token, LLM provider choice (Ollama/Groq), Groq API key; persisted by `config_store.py`.

**Data flow**
1. User enters a request (e.g., "create test cases for QA-102") and clicks Send.
2. App parses the Jira ticket key from the message.
3. `jira_client.py` fetches ticket summary/description/acceptance criteria using stored credentials from `config_store.py`.
4. Load `templates/testcase_template.md` and merge ticket content to build the LLM prompt.
5. `llm_client.py` calls Ollama (`OLLAMA_URL`, `OLLAMA_MODEL`). If Ollama is unreachable or the user selected Groq, call Groq with the stored API key.
6. Render the structured test cases in the chat pane.

**Security & constraints**
- Do not hardcode credentials. Persist settings to a local JSON file excluded from VCS.
- Only call Groq when Ollama is unavailable or when the user explicitly selects it.
- Use `.env` values as initial defaults but manage persisted overrides via `config_store.py`.

**Implementation steps (one module at a time)**
1. Create `templates/testcase_template.md` (sample template with placeholders).
2. Implement `config_store.py`:
   - Read `.env` on first run for defaults.
   - Persist changes in a local `config.json` outside VCS.
   - Provide `get()`/`set()` helpers.
3. Implement `jira_client.py`:
   - Authenticated GET to Jira REST API to fetch issue fields (summary, description, custom acceptance criteria).
   - Return a simple dict with the needed fields.
4. Implement `llm_client.py`:
   - Send prompt to Ollama HTTP API (`/api/generate` or model endpoint) using `OLLAMA_URL` and `OLLAMA_MODEL`.
   - On connection error or non-2xx, fallback to Groq using `GROQ_API_KEY` if allowed.
   - Expose a simple `generate(prompt, provider_preference=None)` API.
5. Implement UI:
   - `app.py`: Chat flow, parse ticket key, call jira + llm clients, show responses.
   - `pages/settings.py`: Edit and persist credentials + provider selection.
6. Add `requirements.txt` and a short `README.md` with run steps.
7. Local smoke test: verify fetching a real ticket and generating a sample output.

**Run / Test (developer)**
1. Create a virtualenv and install deps:
```
python -m venv .venv
.venv\Scripts\pip install -r requirements.txt
```
2. Run Streamlit:
```
streamlit run app.py
```

**Next actions (after your approval)**
1. Create `templates/testcase_template.md` and commit it.
2. Implement `config_store.py` and run a quick read/write smoke test.
3. Implement `jira_client.py`, `llm_client.py`, then the UI.

---
Generated plan created from `Finetune_Prompt.md` and `.env` defaults.
