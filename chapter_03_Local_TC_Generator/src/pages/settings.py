import streamlit as st
from config_store import load_config, save_config
from jira_client import test_connection as test_jira_connection
from llm_client import test_ollama, test_groq

st.set_page_config(page_title="Settings — Jira Test Case Generator", page_icon="⚙️", layout="centered")

st.title("⚙️ Settings")

# Load current config
config = load_config()

# === Jira Settings ===
st.subheader("🔗 Jira Connection")
jira_url = st.text_input("Jira URL", value=config.get("JIRA_URL", ""), placeholder="https://your-org.atlassian.net")
jira_email = st.text_input("Jira Email", value=config.get("JIRA_EMAIL", ""), placeholder="you@example.com")
jira_token = st.text_input("Jira API Token", value=config.get("JIRA_API_TOKEN", ""), type="password")

if st.button("Test Jira Connection"):
    # Test with the values provided in the UI (do not overwrite saved config)
    res = test_jira_connection({"JIRA_URL": jira_url, "JIRA_EMAIL": jira_email, "JIRA_API_TOKEN": jira_token})
    if res.get("ok"):
        st.success(f"Jira reachable — status {res.get('status')}")
    else:
        st.error(f"Jira test failed: {res.get('error') or res.get('body')}")

st.markdown("---")

# === LLM Settings ===
st.subheader("🤖 LLM Provider")
provider = st.radio(
    "Select LLM provider",
    options=["ollama", "groq"],
    index=0 if config.get("LLM_PROVIDER", "ollama") == "ollama" else 1,
    format_func=lambda x: f"Ollama (local, gemma3:1b)" if x == "ollama" else "Groq (cloud)",
    help="Ollama runs locally. Groq is the cloud fallback.",
)

groq_key = ""
if provider == "groq":
    groq_key = st.text_input(
        "Groq API Key",
        value=config.get("GROQ_API_KEY", ""),
        type="password",
        help="Get your key at https://console.groq.com/keys",
    )

col1, col2 = st.columns(2)
with col1:
    if st.button("Test Ollama"):
        res = test_ollama({"OLLAMA_URL": config.get("OLLAMA_URL")})
        if res.get("ok"):
            st.success(f"Ollama reachable — status {res.get('status')}")
        else:
            st.error(f"Ollama error: {res.get('error')}")

with col2:
    if st.button("Test Groq"):
        res = test_groq({"GROQ_API_KEY": groq_key})
        if res.get("ok"):
            st.success("Groq test completed")
        else:
            st.error(f"Groq error: {res.get('error')}")

st.markdown("---")

# === Save ===
if st.button("💾 Save Settings"):
    new_config = {
        "JIRA_URL": jira_url,
        "JIRA_EMAIL": jira_email,
        "JIRA_API_TOKEN": jira_token,
        "LLM_PROVIDER": provider,
        "GROQ_API_KEY": groq_key if provider == "groq" else config.get("GROQ_API_KEY", ""),
    }
    save_config({**config, **new_config})
    st.success("Settings saved! Go back to [Chat](/) to start generating test cases.")
    st.balloons()

# === Current config display ===
st.markdown("---")
st.caption("Settings are stored in `config.json` (git-ignored). Credentials from `.env` seed the initial values.")