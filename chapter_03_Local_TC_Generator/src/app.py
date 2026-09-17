import re
import streamlit as st
from pathlib import Path

from config_store import load_config
from jira_client import fetch_ticket
from llm_client import generate

ROOT = Path(__file__).parent
TEMPLATE_PATH = ROOT / "templates" / "testcase_template.md"


def parse_issue_key(text: str):
    m = re.search(r"\b[A-Z][A-Z0-9]+-\d+\b", text)
    return m.group(0) if m else None


st.set_page_config(page_title="Jira Test Case Generator")

st.title("Jira Test Case Generator")

if "messages" not in st.session_state:
    st.session_state.messages = []

config = load_config()

with st.form("chat_form"):
    user_input = st.text_input("Enter request", key="user_input")
    submitted = st.form_submit_button("Send")

if submitted and user_input:
    st.session_state.messages.append({"role": "user", "text": user_input})
    issue_key = parse_issue_key(user_input)
    if not issue_key:
        st.session_state.messages.append({"role": "system", "text": "No Jira issue key found in your message."})
    else:
        try:
            issue = fetch_ticket(issue_key)
        except Exception as e:
            st.session_state.messages.append({"role": "system", "text": f"Error fetching issue: {e}"})
        else:
            template = TEMPLATE_PATH.read_text(encoding="utf-8")
            prompt = template.replace("{{issue_key}}", issue_key)
            prompt = prompt.replace("{{summary}}", issue.get("summary", ""))
            prompt = prompt.replace("{{description}}", issue.get("description", ""))
            prompt = prompt.replace("{{acceptance_criteria}}", issue.get("acceptance_criteria", ""))
            st.session_state.messages.append({"role": "system", "text": "Generating test cases..."})
            try:
                provider = config.get("LLM_PROVIDER")
                resp = generate(prompt, provider)
            except Exception as e:
                resp = f"LLM error: {e}"
            st.session_state.messages.append({"role": "assistant", "text": resp})

for msg in st.session_state.messages:
    if msg["role"] == "user":
        st.markdown(f"**You:** {msg['text']}")
    elif msg["role"] == "assistant":
        st.markdown(f"**Generated:**\n\n{msg['text']}")
    else:
        st.info(msg["text"])
