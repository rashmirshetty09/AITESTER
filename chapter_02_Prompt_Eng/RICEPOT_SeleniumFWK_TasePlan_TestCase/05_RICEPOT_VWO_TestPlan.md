# RICEPOT Test Plan: VWO Login (https://app.vwo.com/#/login)

R: Requirement
- Verify login page functionality and resilience for `https://app.vwo.com/#/login` including: successful authentication, failed authentication handling, UI element presence, "remember me" persistence, basic security checks (injection attempts), and detection of CAPTCHA/MFA.

I: Input
- Test URL: `https://app.vwo.com/#/login`
- Test data: valid credentials (placeholder), invalid credentials, malformed inputs, injection payloads
- Environment: Chrome (local/CI) via WebDriverManager, timeouts: 10-20s explicit wait

C: Cause (Why)
- Recent releases touch auth, session, or UI; high user-impact area. Automation required to prevent regressions and validate secure handling of auth inputs.

E: Effect (Expected)
- Valid credentials establish a session and land on expected dashboard element.
- Invalid inputs show appropriate error messages without leaking details.
- Remember-me persists session where supported; CAPTCHA/MFA presence is detected and tests are blocked.

P: Procedure (Test Cases)
- TC-VWO-001 | Valid Login | High
  - Preconditions: Valid credentials available; no CAPTCHA/MFA
  - Steps: Navigate to URL → enter valid email → enter valid password → click login
  - Expected: Landing dashboard element visible within timeout

- TC-VWO-002 | Remember Me Persists | Medium
  - Preconditions: Valid credentials
  - Steps: Login with remember-me checked → close browser → reopen and navigate to app URL
  - Expected: User remains authenticated or redirected to landing

- TC-VWO-003 | UI Elements Presence | High
  - Preconditions: None
  - Steps: Navigate to URL
  - Expected: Email field, password field, login button, remember-me checkbox, forgot-password link present and enabled

- TC-VWO-004 | Invalid Password | High
  - Preconditions: Valid email
  - Steps: Enter valid email + wrong password → click login
  - Expected: Authentication error shown; remain on login page

- TC-VWO-005 | Empty Username | Medium
  - Steps: Leave email blank → enter password → click login
  - Expected: Validation shown; no login

- TC-VWO-006 | Empty Password | Medium
  - Steps: Enter email → leave password blank → click login
  - Expected: Validation shown; no login

- TC-VWO-007 | Both Fields Empty | Medium
  - Steps: Leave both blank → click login
  - Expected: Required field validations

- TC-VWO-008 | Invalid Email Format | Low
  - Steps: Enter malformed email → enter any password → click login
  - Expected: Email format validation; no login

- TC-VWO-009 | Injection Payloads | Low
  - Steps: Enter SQL/XSS payloads into fields → click login
  - Expected: Inputs sanitized; no script execution; safe error

- TC-VWO-010 | Account Lock / Rate Limit | Low
  - Steps: Multiple invalid attempts (as allowed) → observe behavior
  - Expected: Account lock or rate-limit response detected and recorded

- TC-VWO-011 | CAPTCHA/MFA Detection | High (detect)
  - Steps: Attempt login; detect presence of CAPTCHA or MFA widgets
  - Expected: Mark test as blocked and capture screenshot

- TC-VWO-012 | Accessibility Quick Check | Low
  - Steps: Inspect inputs for labels/ARIA
  - Expected: Inputs have accessible labels or ARIA attributes

O: Output
- Artifacts: Test case matrix (this file), `LoginPage.java` (Page Object using `@FindBy(xpath=...)`), `ValidLoginTest.java`, `InvalidLoginTest.java`, `pom.xml`, `testng.xml`, `config.properties`, screenshots on failure, TestNG reports

T: Tools
- Selenium WebDriver (Java 11+), TestNG, Maven, WebDriverManager, ChromeDriver, explicit waits (`WebDriverWait`), logging + screenshot utilities

Execution Notes
- Use Page Object Model with PageFactory and only xpath locators.
- No `Thread.sleep`; rely on `WebDriverWait`/explicit waits.
- Implement robust try–catch in page methods and tests for graceful failure and cleanup.

Test Data (examples)
- Valid credentials: supply via `-Dusername` and `-Dpassword` or `config.properties`
- Invalid credentials: `invalid@example.com` / `WrongPass123`
- Malformed emails: `user@@domain`, `userdomain.com`
- Injection payloads: `"' OR '1'='1"`, `"<script>alert(1)</script>"`

Reporting
- Save failure screenshots to `target/screenshots` and attach TestNG suite reports; extend with Allure/Extent if requested.

*** End Patch