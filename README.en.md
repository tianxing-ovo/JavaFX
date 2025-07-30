# JavaFX Multi-Function Application

## Introduction

**JavaFX Implementation of Music Player, CMD Command Executor, and Online Chat System**

This is a comprehensive JavaFX desktop application that combines three main functionalities:
- 🎵 **Music Player**: A feature-rich audio player with playlist management
- 💻 **WinShell**: A Windows command prompt executor with GUI interface
- 💬 **Chat System**: An online chat application with client-server architecture

## Features

### 🎵 Music Player
- **Audio Playback**: Support for multiple audio formats
- **Playlist Management**: Create and manage music playlists
- **Play Modes**: Loop and sequential playback options
- **Media Controls**: Play, pause, and track navigation
- **Modern UI**: Beautiful and intuitive user interface

### 💻 WinShell (CMD Executor)
- **Command Execution**: Execute Windows CMD commands through GUI
- **Real-time Output**: Display command results in real-time
- **Process Management**: Monitor and control running processes
- **User-friendly Interface**: Easy-to-use command input and output display

### 💬 Chat System
- **Online Chat**: Real-time messaging between users
- **User Authentication**: Login system with user management
- **Client-Server Architecture**: Network-based communication
- **Modern Chat Interface**: Clean and responsive chat UI

## Project Structure

```
JavaFX/
├── src/
│   ├── app/                    # Main application classes
│   │   ├── BaseApplication.java # Base JavaFX application
│   │   ├── MusicPlayer.java    # Music player application
│   │   ├── WinShell.java       # CMD executor application
│   │   └── Chat.java           # Chat application
│   ├── controller/             # FXML controllers
│   ├── entity/                 # Data models
│   ├── server/                 # Chat server implementation
│   ├── client/                 # Chat client implementation
│   ├── util/                   # Utility classes
│   └── constant/               # Constants and enums
├── resources/
│   ├── fxml/                   # FXML layout files
│   ├── css/                    # Stylesheet files
│   ├── png/                    # Image resources
│   └── wav/                    # Audio resources
└── lib/                        # External libraries
```

## Requirements

- **Java**: JDK 8 or higher
- **JavaFX**: Built-in with JDK 8+, or separate installation for newer JDKs
- **Operating System**: Windows (primary), Linux, macOS (compatible)

## Installation & Setup

### Prerequisites
1. Install Java JDK 8 or higher
2. Ensure JavaFX is available in your Java installation

### Building the Project
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd JavaFX
   ```

2. Compile the project:
   ```bash
   javac -cp "lib/*" src/**/*.java
   ```

3. Run the applications:

   **Music Player:**
   ```bash
   java -cp "lib/*:src" app.MusicPlayer
   ```

   **WinShell (CMD Executor):**
   ```bash
   java -cp "lib/*:src" app.WinShell
   ```

   **Chat Application:**
   ```bash
   java -cp "lib/*:src" app.Chat
   ```

## Usage

### Music Player
1. Launch the Music Player application
2. Use the file browser to select music files
3. Control playback using the media controls
4. Switch between loop and sequential play modes
5. Manage your playlist with add/remove functions

### WinShell
1. Launch the WinShell application
2. Enter CMD commands in the input field
3. View real-time command output
4. Execute system commands safely through the GUI

### Chat System
1. Start the chat server (if running locally)
2. Launch the Chat application
3. Login with your credentials
4. Start chatting with other online users

## Configuration

### Music Player Settings
- Default music directory: `%USER_HOME%/Music`
- Supported audio formats: MP3, WAV, and other JavaFX supported formats

### Chat System Configuration
- Server settings can be configured in the server implementation
- Client connection settings in the client handler

## Dependencies

- **JavaFX**: GUI framework
- **bubble-animation.jar**: Animation library for UI effects

## Development

### Adding New Features
1. Extend `BaseApplication` for new JavaFX applications
2. Create corresponding FXML files in `resources/fxml/`
3. Implement controllers in `src/controller/`
4. Add utility classes in `src/util/` as needed

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain consistent code formatting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the terms specified in the LICENSE file.

## Author

**tianxing** - Initial work

## Acknowledgments

- JavaFX team for the excellent GUI framework
- Contributors and testers of this application

---

For more information or support, please refer to the Chinese README or contact the development team.
