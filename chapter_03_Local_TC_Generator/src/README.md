# Jira Test Case Generator

Run a lightweight Streamlit app that uses a local Ollama model (with Groq fallback) to generate test cases from a Jira ticket.

Setup

1. Create a virtualenv and install dependencies:
```powershell
python -m venv .venv
. .venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
```

2. Ensure `.env` in this folder (`src/.env`) contains Jira and Ollama defaults. The app reads these values as defaults but you can edit them in the Settings page.

Files added

- `app.py` — main Streamlit chat screen (parses Jira keys, fetches ticket, sends prompt to LLM)
- `pages/settings.py` — Streamlit settings multipage UI (Jira credentials, LLM provider, test buttons)
- `config_store.py` — persists settings to `config.json` and reads `.env` defaults
- `jira_client.py` — fetches ticket details from Jira and provides `test_connection`/`fetch_ticket`
- `llm_client.py` — calls Ollama (primary) and Groq (fallback); includes `test_ollama`/`test_groq` helpers
- `templates/testcase_template.md` — the prompt template used to generate test cases
- `smoke_test.py` — small script to verify local Ollama and Jira connectivity
- `requirements.txt` — minimal Python dependencies
- `README.md` — this file

Run

```powershell
cd D:\AI\AITESTER\chapter_03_Local_TC_Generator\src
python -m streamlit run app.py
```

Settings and usage

- Open the **Settings** page in the app to verify or update Jira URL, email, and API token, and to select the LLM provider.
- Use the **Test Jira Connection**, **Test Ollama**, and **Test Groq** buttons to verify connectivity before generating test cases.

Notes

- Credentials are not hardcoded. They are persisted to `config.json` (created on save) and seeded from `.env` if present.
- Ollama defaults to `http://localhost:11434` (update `src/.env` or Settings if your server differs).

