import sys
from pathlib import Path
import json

sys.path.insert(0, str(Path(__file__).parent))
from config_store import load_config

import requests
import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

def main():
    cfg = load_config()
    print("Loaded config keys:", list(cfg.keys()))

    ollama = cfg.get("OLLAMA_URL")
    print("OLLAMA_URL:", ollama)
    if ollama:
        try:
            r = requests.get(ollama, timeout=5, verify=False)
            print("Ollama reachable, status:", r.status_code)
        except Exception as e:
            print("Ollama error:", e)

    jira = cfg.get("JIRA_URL")
    email = cfg.get("JIRA_EMAIL")
    token = cfg.get("JIRA_API_TOKEN")
    print("JIRA_URL:", jira, "email set:", bool(email))
    if jira and email and token:
        if not jira.startswith("http"):
            jira_base = "https://" + jira
        else:
            jira_base = jira
        try:
            url = jira_base.rstrip("/") + "/rest/api/3/serverInfo"
            r = requests.get(url, auth=(email, token), timeout=10)
            print("Jira serverInfo status:", r.status_code)
            print(r.text[:800])
        except Exception as e:
            print("Jira error:", e)
    else:
        print("Jira credentials missing; skipping Jira test")

if __name__ == '__main__':
    main()
