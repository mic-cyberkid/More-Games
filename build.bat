@echo off
echo Building Echoes of Aetheria...
if not exist out mkdir out
dir /s /b *.java > sources.txt
javac -d out --enable-preview --release 21 @sources.txt
if %errorlevel% neq 0 (
    echo Build FAILED!
    pause
    exit /b %errorlevel%
)
echo Build successful.
del sources.txt
pause
