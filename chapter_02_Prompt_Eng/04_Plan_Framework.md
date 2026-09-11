## Plan: RICE_POT_SeleniumAdvanceFramewrk

TL;DR - Scaffold a Maven-based, enterprise-grade Selenium+Java+TestNG framework named `RICE_POT_SeleniumAdvanceFramewrk` under `chapter_02_Prompt_Eng/`. Deliverables: a runnable Maven project with `pom.xml`, one Page Object (`LoginPage`), two TestNG tests (`ValidLoginTest`, `InvalidLoginTest`), a thread-safe `DriverFactory`, `BaseTest`, explicit-wait `WaitUtils`, screenshot utility, `testng.xml`, and `config.properties`.

**Steps**
1. Create project root: `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk` with standard Maven layout.
2. Add `pom.xml` with dependencies: Selenium Java, TestNG, WebDriverManager, SLF4J + Logback, and Surefire plugin. Configure Java 11+.
3. Add `src/test/resources/config.properties` containing `env.url=https://login.salesforce.com/?locale=in`, `timeout.seconds=15`, `browser=chrome` and credential placeholders.
4. Implement `DriverFactory` at `src/test/java/com/ricepot/core/DriverFactory.java` using WebDriverManager, ChromeOptions/FirefoxOptions, and `ThreadLocal<WebDriver>` for parallel safety.
5. Implement `BaseTest` at `src/test/java/com/ricepot/base/BaseTest.java` using TestNG lifecycle annotations (`@BeforeTest`, `@BeforeMethod`, `@AfterMethod`, `@AfterTest`) to initialize/quit drivers and capture failures.
6. Implement Page Object `LoginPage` at `src/main/java/com/ricepot/pages/LoginPage.java` using PageFactory and `@FindBy(xpath = "...")` exclusively. Provide methods: `enterUsername`, `enterPassword`, `toggleRememberMe`, `clickLogin`, `getErrorMessage`, `isLoggedIn` with robust try/catch and meaningful exceptions.
7. Implement `WaitUtils` at `src/test/java/com/ricepot/utils/WaitUtils.java` with WebDriverWait wrappers: `waitForVisible`, `waitForClickable`, `waitForText` (no Thread.sleep usage).
8. Implement `ScreenshotUtil` at `src/test/java/com/ricepot/utils/ScreenshotUtil.java` to capture and store screenshots under `target/screenshots` on failures.
9. Create TestNG suite `testng.xml` in project root to run both tests; add groups `smoke` and `negative` and allow running via `mvn test -Dsuite=...`.
10. Implement tests:
   - `src/test/java/com/ricepot/tests/ValidLoginTest.java` — uses credentials from system properties or `config.properties`, asserts successful landing by checking a post-login element.
   - `src/test/java/com/ricepot/tests/InvalidLoginTest.java` — covers invalid email/password combinations and empty-field scenarios asserting displayed error messages.
   Both tests must use explicit waits, TestNG annotations, and structured try/catch for exceptions.
11. Add logging configuration `src/test/resources/logback.xml` and integrate SLF4J logging calls across core classes.
12. Add a minimal `README.md` with `mvn test` run instructions and how to supply credentials via system properties.

**Relevant files (to create)**
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/pom.xml`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/testng.xml`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/main/java/com/ricepot/pages/LoginPage.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/core/DriverFactory.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/base/BaseTest.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/utils/WaitUtils.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/utils/ScreenshotUtil.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/tests/ValidLoginTest.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/java/com/ricepot/tests/InvalidLoginTest.java`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/src/test/resources/config.properties`
- `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk/README.md`

**Verification**
1. Run `mvn test` in `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk` and confirm tests execute.
2. Confirm reports under `target/surefire-reports` and screenshots under `target/screenshots` on failures.
3. Static check: ensure Page classes and tests use only xpath selectors (no By.id/By.name/CSS).
4. Validate that `@Test`, `@BeforeTest`, and related TestNG annotations are present and lifecycle hooks work.

**Decisions & Assumptions**
- Project will be Java 11+ and use WebDriverManager to avoid manual driver binaries.
- Credentials will be provided via system properties for security; config.properties will contain defaults only.
- XPath-only selector policy is strict; tests will include fallback xpaths for AB variants if needed.

**Next**
If you approve, I will scaffold the Maven project and generate the single Page Object and two TestNG test scripts into `chapter_02_Prompt_Eng/RICE_POT_SeleniumAdvanceFramewrk` now and run a quick verification of file creation.
