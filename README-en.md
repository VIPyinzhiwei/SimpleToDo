[中文](README.md) | English

# Simple Todo

A to-do list management app developed using the latest Android technology stack.

## Screenshots

<img src="screenshots/screenshots1.png" width="50%"/><img src="screenshots/screenshots2.png" width="50%"/>
<img src="screenshots/screenshots3.png" width="50%"/><img src="screenshots/screenshots4.png" width="50%"/>
<img src="screenshots/screenshots5.png" width="50%"/><img src="screenshots/screenshots6.png" width="50%"/>

<br></br>

## Download Experience

- Scan QR code to install：<br></br>
  <a href="https://www.pgyer.com/YrsgyKDL"><img src="simpletodo.png"/></a>

- [Click to download simpletodo.apk][1]

## Features

- ✅ Basic Task Management
    - Add, edit, and delete tasks
    - Mark tasks as complete/incomplete
    - Task priority support (High, Medium, Low)
    - Task notes support
    - Bulk selection and deletion

- 🏷️ Category Management
    - Custom task labels
    - Label color customization
    - Task filtering by labels

- ⏰ Time Management
    - Set task due dates
    - Due date notifications
    - Automatic overdue task detection

- 🔍 Search and Filter
    - Task search
    - Show/hide completed tasks
    - Multi-dimensional task filtering

## Technical Architecture

- **UI**: Jetpack Compose + Material 3
- **Architecture Pattern**: MVVM
- **Dependency Injection**: Hilt
- **Local Storage**: Room
- **Background Tasks**: WorkManager
- **Asynchronous Processing**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/
├── java/com/example/simpletodo/
│ ├── data/ # Data layer
│ │ ├── local/ # Room database
│ │ ├── model/ # Data models
│ │ └── repository/ # Data repositories
│ ├── di/ # Dependency injection
│ ├── ui/ # UI layer
│ ├── worker/ # Background workers
│ ├── MainActivity.kt # Main activity
│ └── TodoApplication.kt # Application entry
└── res/ # Resources
```

## Development Environment

- Android Studio Hedgehog | 2023.1.1 or higher
- JDK 17
- Android SDK 34
- Kotlin 1.9.0 or higher

## Build and Run

1. Clone the project 

git clone https://github.com/VIPyinzhiwei/SimpleToDo.git

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Run the application

## Main Dependencies

- Jetpack Compose: UI framework
- Room: Local database
- Hilt: Dependency injection
- WorkManager: Background task scheduling
- Material3: Material Design 3 components
- Accompanist: System UI controller

## License

[Apache License 2.0][2]

## Contributing

Contributions via Issues and Pull Requests are welcome.

1. Fork the project
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Contact

For questions or suggestions, feel free to:

- Create an Issue
- Email: vipyinzhiwei@gmail.com
- Blog: vipyinzhiwei.com 

[1]:https://github.com/VIPyinzhiwei/SimpleToDo/raw/main/simpletodo.apk
[2]:http://www.apache.org/licenses/LICENSE-2.0