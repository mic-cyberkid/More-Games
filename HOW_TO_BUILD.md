# HOW_TO_BUILD.md — Echoes of Aetheria

## Prerequisites
- Java 21+ (OpenJDK recommended)
- `javac` and `java` available in the system PATH

## Build Process
1. **Compile:**
   ```bash
   mkdir -p out
   find . -name "*.java" > sources.txt
   javac -d out --enable-preview @sources.txt
   ```

2. **Asset Generation:**
   ```bash
   ./tools/gen_all_assets.sh
   ```

## Running the Game
The game **MUST** be started with the following flags to ensure correct GC performance and OpenGL acceleration.

```bash
java \
  -Xmx512m \
  -Xms128m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=5 \
  -XX:G1HeapRegionSize=4m \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -Dsun.java2d.opengl=true \
  -Dsun.java2d.accthreshold=0 \
  --enable-preview \
  -cp "out:." com.aetheria.Main
```

## Troubleshooting
- **OpenGL Errors:** If the game fails to start on Linux without a GPU, try `-Dsun.java2d.opengl=false`.
- **Heap Issues:** Ensure `-Xmx` is at least 512m to prevent `OutOfMemoryError` during asset-heavy scenes.
