# HOW_TO_BUILD.md — Echoes of Aetheria

## Prerequisites
- Java 21+ (OpenJDK recommended)
- `javac` and `java` available in the system PATH

## Build Process

### Windows

1. **Compile:**
   ```batch
   mkdir out
   dir /s /b *.java > sources.txt
   javac -d out --enable-preview @sources.txt
   ```

2. **Asset Generation:**
   ```batch
   cd tools
   gen_all_assets.bat
   cd ..
   ```

### macOS / Linux

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

### Windows

```batch
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
```

Or create `run.bat`:
```batch
@echo off
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
pause
```

### macOS / Linux

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

### All Platforms
- **Heap Issues:** Ensure `-Xmx` is at least 512m to prevent `OutOfMemoryError` during asset-heavy scenes.

### Windows
- **OpenGL Errors:** If the game fails to start, try `-Dsun.java2d.opengl=false`.
- **Path Issues:** Ensure Java is in your PATH. Test with `java -version`.
- **Compilation Fails:** If `sources.txt` is empty, verify `.java` files exist and try: `dir /s /b src\*.java > sources.txt`

### macOS / Linux
- **OpenGL Errors:** If the game fails to start on systems without a GPU, try `-Dsun.java2d.opengl=false`.
- **Permission Denied:** Run `chmod +x tools/gen_all_assets.sh` to make the asset script executable.

## Quick Start Checklist
- [ ] Java 21+ installed (`java -version` shows 21+)
- [ ] In project root directory
- [ ] Run `build.bat` (Windows) or `./build.sh` (macOS/Linux)
- [ ] Run `run.bat` (Windows) or `./run.sh` (macOS/Linux)
