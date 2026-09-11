# RICE_POT_SeleniumAdvanceFramewrk

Run tests with:

```bash
mvn test -Dbrowser=chrome -Dusername=YOUR_USER -Dpassword=YOUR_PASS
```

Credentials: pass credentials via system properties (`-Dusername` and `-Dpassword`) or configure your CI secrets. Do not hardcode credentials in `config.properties`.

Logging: logs are written to `target/logs/test.log` and console via Logback.
