## FVM Path Setter Plugin

### Description

The FVM Path Setter Plugin simplifies the process of configuring the Flutter SDK path in your IntelliJ IDEA projects. This plugin automatically detects the `.fvm/flutter_sdk` directory in your project root and sets it as the Flutter SDK path, eliminating the need for manual configuration.

### Features

- **Automatic SDK Detection**: Automatically detects the `.fvm/flutter_sdk` directory in the project root.
- **One-Click Configuration**: Adds a convenient action in the Tools menu to set the FVM path with a single click.
- **Localization**: Supports multiple languages including English, Japanese, Korean, Vietnamese, Simplified Chinese, and Traditional Chinese.

### How to Use

1. **Install the Plugin**: Download and install the FVM Path Setter Plugin from the IntelliJ IDEA plugin repository.
2. **Open Your Project**: Open the project where you want to set the Flutter SDK path.
3. **Set FVM Path**: Navigate to the Tools menu and click on the "Set FVM Path" action. The plugin will automatically configure the Flutter SDK path based on the `.fvm/flutter_sdk` directory in your project root.

### Localization Support

This plugin supports multiple languages. The interface texts are available in:
- English
- Japanese (日本語)
- Korean (한국어)
- Vietnamese (Tiếng Việt)
- Simplified Chinese (简体中文)
- Traditional Chinese (繁體中文)

### Requirements

- IntelliJ IDEA 2020.1 or later
- Flutter SDK
- FVM (Flutter Version Management) installed and configured

### License

This plugin is open-source and available under the MIT License. Feel free to contribute or modify it according to your needs.

### Contribution

Contributions are welcome! Please fork the repository and submit a pull request with your improvements.


## how to build

1. stop Android Studio.

2. run the command.

```
./gradlew buildPlugin
```

3. open the folder

```
build/distributions
```

## icon file

```
./src/main/resources/META-INF
```


