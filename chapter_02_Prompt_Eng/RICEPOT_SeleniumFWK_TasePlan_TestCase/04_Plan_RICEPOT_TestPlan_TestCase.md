# Test Plan: VWO Login Automation (RICE POT)

TL;DR — Enterprise-grade Selenium + Java + Maven + TestNG automation for the VWO login page (`https://app.vwo.com/#/login`). Deliverables: 1 Page Object (xpath-only), 2 TestNG test classes (ValidLoginTest, InvalidLoginTest), `pom.xml`, and verification artifacts. Use PageFactory, explicit waits, and robust exception handling. Credentials are placeholders and may be supplied later.

**Objectives**
- Verify login functionality for valid and invalid credentials.
- Verify UI elements and remember-me behavior.
- Detect blocks (CAPTCHA, MFA) and fail gracefully.

**Scope**
- In-scope: `https://app.vwo.com/#/login` page elements (email, password, login button, remember-me, error messages), session establishment after login, basic post-login landing verification.
- Out-of-scope: full application flows beyond initial landing, deep API validations, account provisioning, SSO configuration changes.

**Assumptions**
- Tests run against staging or production URL provided; credentials will be provided or passed as system properties.
- CAPTCHA / MFA may be present — tests will detect and mark as blocked.
- Chrome via WebDriverManager is default; headless optional via config.

**Test Environment**
- Java 11+ or 17, Maven 3.6+, Selenium 4.x, TestNG 7.x, WebDriverManager 5.x.
- Test execution: local developer machine or CI agent with Chrome installed.

**Deliverables**
- `LoginPage.java` (Page Object, `@FindBy(xpath=...)`, PageFactory)
- `ValidLoginTest.java` (TestNG)
- `InvalidLoginTest.java` (TestNG)
- `pom.xml`, `testng.xml`, `config.properties`
- This test plan and test case matrix

**Test Strategy**
- Page Object Model with PageFactory and xpath-only locators.
- Explicit waits via `WebDriverWait` wrapped in `WaitUtils`.
- BaseTest to handle WebDriver lifecycle in `@BeforeTest`/`@AfterTest`.
- Tests must use try–catch to surface actionable failures and ensure cleanup.

**Pass / Fail Criteria**
- Pass: Expected UI element presence, successful login leads to expected landing element, error messages appear for invalid flows.
- Fail: Unhandled exceptions, missing critical UI elements, CAPTCHA/MFA prevents automated login (marked as blocked).

**Risks**
- CAPTCHA/MFA and IP restrictions may block automation. Plan to detect and skip such runs.
- Dynamic JS may require resilient xpaths.

**Test Cases**

- TC001: Valid Login
  - Priority: High
  - Pre-conditions: Valid credentials available; no CAPTCHA/MFA.
  - Steps: Navigate to login URL; enter valid email; enter valid password; click login.
  - Expected: Post-login landing element visible (dashboard header or user avatar). Test passes if landing element displayed within timeout.

- TC002: Remember Me Persists Session
  - Priority: Medium
  - Pre-conditions: Valid credentials available.
  - Steps: Navigate to login URL; enter valid credentials; select remember-me; login; close browser; reopen browser and navigate to app URL.
  - Expected: User remains authenticated (redirected to landing) or cookie/session persisted.

- TC003: UI Elements Presence
  - Priority: High
  - Pre-conditions: None
  - Steps: Navigate to login URL.
  - Expected: Email field, password field, login button, remember-me checkbox, and links (forgot password) are present and enabled.

- TC004: Invalid Password
  - Priority: High
  - Pre-conditions: Valid email exists.
  - Steps: Enter valid email; enter incorrect password; click login.
  - Expected: Error message displayed indicating invalid credentials; user remains on login page.

- TC005: Empty Username
  - Priority: Medium
  - Pre-conditions: None
  - Steps: Leave email blank; enter valid password; click login.
  - Expected: Validation message appears (client-side or server-side) preventing login.

- TC006: Empty Password
  - Priority: Medium
  - Pre-conditions: None
  - Steps: Enter valid email; leave password blank; click login.
  - Expected: Validation message appears preventing login.

- TC007: Both Fields Empty
  - Priority: Medium
  - Steps: Leave email and password blank; click login.
  - Expected: Validation messages for required fields.

- TC008: Invalid Email Format
  - Priority: Low
  - Steps: Enter malformed email (e.g., "user@@domain"); enter any password; click login.
  - Expected: Client-side or server-side validation for email format; no login.

- TC009: SQL / Script Injection Attempt
  - Priority: Low
  - Steps: Enter payloads like "' OR '1'='1" or "<script>alert(1)</script>" in email/password; click login.
  - Expected: Input sanitized; no command execution; error or validation shown.

- TC010: Account Lock / Rate Limit Behavior
  - Priority: Low
  - Steps: Repeated invalid login attempts (as allowed by environment) and observe response.
  - Expected: System shows account lock message or rate-limited response; tests will detect and mark accordingly.

- TC011: CAPTCHA / MFA Detection
  - Priority: High (detect)
  - Steps: Attempt login; if CAPTCHA or MFA widget appears, mark test as blocked and capture screenshot.
  - Expected: Test records CAPTCHA presence and stops further automated steps.

- TC012: Accessibility Quick Check (labels & ARIA)
  - Priority: Low
  - Steps: Inspect email/password inputs for `aria-label` or associated `<label>` elements.
  - Expected: Inputs exposed to assistive tech; basic accessibility attributes present.

**Test Data**
- Provide valid username/password via `config.properties` or system properties `-Dusername`, `-Dpassword`.
- Invalid credentials and payloads embedded in test data provider arrays in `InvalidLoginTest`.

**Execution Notes**
- Use `WebDriverWait` for all element interactions; do not use `Thread.sleep`.
- Use only xpath selectors in `@FindBy(xpath=...)`.
- Tests must clean up sessions and quit WebDriver in `@AfterTest`.

**Reporting & Artifacts**
- Save screenshots on failure to `target/screenshots`.
- Output TestNG reports via Surefire; extend later with Allure/Extent if requested.

**Next Steps**
1. Implement Page Object and test classes according to this plan.
2. Provide valid staging credentials or confirm placeholders.
3. Run `mvn -q test -Dbase.url=https://app.vwo.com/#/login -Dusername=<user> -Dpassword=<pass>` and review results.
