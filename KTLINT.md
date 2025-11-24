# KtLint Configuration

This project uses KtLint for Kotlin code style checking and formatting, configured specifically for Android development.

## Quick Usage

```bash
# Check code style
./gradlew ktlintCheck
# or use the custom task
./gradlew lintKotlin

# Format code automatically
./gradlew ktlintFormat
# or use the custom task
./gradlew formatKotlin
```

## Configuration

### Files:
- **`.editorconfig`**: Main configuration file with Android-friendly rules
- **`.ktlint.yml`**: Additional KtLint-specific configuration
- **`app/build.gradle.kts`**: Plugin configuration and custom tasks

### Android-Specific Rules:
- **Function naming**: Disabled to allow PascalCase for `@Composable` functions
- **Filename**: Disabled to allow flexible file naming (e.g., TestRunner.kt)
- **Wildcard imports**: Disabled for Compose imports (common in Android)
- **Property naming**: Disabled for patterns like singleton `INSTANCE`

### Reports:
KtLint generates reports in multiple formats:
- **Plain text**: `build/reports/ktlint/*/`
- **Checkstyle XML**: For CI/CD integration
- **HTML**: For visual review

## Integration

### Build Process:
- KtLint checks run automatically before build (`preBuild` task)
- Format runs automatically after clean for convenience

### Custom Tasks:
- `lintKotlin`: Run style checks
- `formatKotlin`: Auto-format code

## IDE Integration
Configure your IDE to use the `.editorconfig` settings for consistent formatting while coding.