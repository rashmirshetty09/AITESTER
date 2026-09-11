@echo off
setlocal enabledelayedexpansion
where mvn >nul 2>&1
if %ERRORLEVEL%==0 (
  mvn %*
  exit /b %ERRORLEVEL%
)
set MAVEN_VERSION=3.9.5
set WRAPPER_DIR=%~dp0.mvn\wrapper
set MAVEN_DIR=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%
set MVN_CMD=%MAVEN_DIR%\bin\mvn.cmd
if not exist "%MVN_CMD%" (
  echo Maven not found. Downloading Apache Maven %MAVEN_VERSION% to %WRAPPER_DIR%...
  if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
  powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%WRAPPER_DIR%\\apache-maven.zip' -UseBasicParsing"
  powershell -NoProfile -Command "Expand-Archive -Path '%WRAPPER_DIR%\\apache-maven.zip' -DestinationPath '%WRAPPER_DIR%' -Force"
)
"%MVN_CMD%" %*
