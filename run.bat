@echo off
echo Starting Echoes of Aetheria...
java ^
  -Xmx512m ^
  -Xms128m ^
  -XX:+UseG1GC ^
  -XX:MaxGCPauseMillis=5 ^
  -XX:G1HeapRegionSize=4m ^
  -XX:+DisableExplicitGC ^
  -XX:+AlwaysPreTouch ^
  -Dsun.java2d.opengl=true ^
  -Dsun.java2d.accthreshold=0 ^
  --enable-preview ^
  -cp "out;." com.aetheria.Main
if %errorlevel% neq 0 (
    echo Game exited with error code %errorlevel%
    pause
)
