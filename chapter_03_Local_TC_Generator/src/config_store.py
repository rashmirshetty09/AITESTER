import json
from pathlib import Path
from dotenv import dotenv_values

ROOT = Path(__file__).parent
CONFIG_PATH = ROOT / "config.json"


def _env_defaults():
    env_path = ROOT / ".env"
    if env_path.exists():
        return dotenv_values(env_path)
    return {}


def load_config():
    defaults = _env_defaults()
    cfg = {}
    if CONFIG_PATH.exists():
        try:
            cfg = json.loads(CONFIG_PATH.read_text(encoding="utf-8"))
        except Exception:
            cfg = {}
    # overlay defaults where missing
    for k, v in defaults.items():
        if k not in cfg or cfg.get(k) in (None, ""):
            cfg[k] = v
    return cfg


def save_config(cfg: dict):
    CONFIG_PATH.write_text(json.dumps(cfg, indent=2), encoding="utf-8")


def get(key, default=None):
    cfg = load_config()
    return cfg.get(key, default)


def set(key, value):
    cfg = load_config()
    cfg[key] = value
    save_config(cfg)
