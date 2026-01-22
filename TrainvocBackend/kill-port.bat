@echo off
set PORT=8443
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%PORT% ^| findstr LISTENING') do (
    echo Port %PORT% is used by PID %%a, killing...
    taskkill /PID %%a /F
)