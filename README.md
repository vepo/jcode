# JCode

A modern, lightweight Java-based code editor built with JavaFX and RichTextFX.

## Features

### 🎨 **Syntax Highlighting**
- **Multi-language Support**: Java, XML, JSON, CSS, HTML
- **Real-time Highlighting**: Syntax highlighting updates as you type
- **Customizable Themes**: Dark theme with color-coded syntax elements
- **Language Detection**: Automatic language detection based on file extension

### 📁 **Workspace Management**
- **Workspace Persistence**: Automatically opens the last used workspace on startup
- **Tree View**: Hierarchical file browser with expandable/collapsible folders
- **Tree State Persistence**: Remembers expanded/collapsed folder states between sessions
- **File Filtering**: Configurable file filtering to hide unwanted files/directories

### 📝 **Code Editor**
- **Rich Text Editing**: Advanced text editing with RichTextFX
- **Line Numbers**: Built-in line numbering for better code navigation
- **Word Wrap**: Optional text wrapping for long lines
- **Caret Positioning**: Automatically positions cursor at the first line when opening files
- **Tab Management**: Multi-tab support for editing multiple files simultaneously

### 🖥️ **User Interface**
- **Modern Dark Theme**: Built-in dark theme using JMetro styling
- **Responsive Layout**: Resizable split-pane layout with workspace and editor
- **Window State Persistence**: Remembers window position, size, and maximized state
- **Dynamic Window Title**: Shows current workspace path in window title

### 📋 **File Operations**
- **File Loading**: Asynchronous file loading with progress indication
- **Large File Support**: Efficient handling of large files with virtualized scrolling
- **File Type Detection**: Automatic detection and appropriate highlighting for different file types

### ⚙️ **Preferences & Configuration**
- **User Preferences**: Persistent user settings using Java Preferences API
- **Workspace Settings**: Per-workspace configuration options
- **Customizable File Filters**: User-configurable file and folder filtering rules

### 🧪 **Testing & Quality**
- **Comprehensive Testing**: Unit tests for core functionality
- **Code Coverage**: JaCoCo integration for test coverage reporting
- **Quality Assurance**: Maven enforcer plugin for build consistency

## Installation

### Prerequisites
- Java 21 or higher
- Maven 3.9.0 or higher

### Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd jcode

# Build the project
mvn clean compile

# Run the application
mvn javafx:run
```

### Development
```bash
# Run tests
mvn test

# Generate test coverage report
mvn jacoco:report

# Build executable JAR
mvn clean package
```

## Usage

### Opening a Workspace
1. Click **File → Open Workspace** or click the **Open** button
2. Select a directory to open as your workspace
3. The workspace tree will appear on the left side

### Editing Files
1. Double-click any file in the workspace tree to open it
2. Files open in tabs for easy switching
3. Syntax highlighting is applied automatically based on file type

### Managing Workspaces
- **Close Workspace**: File → Close Workspace
- **Clear Last Workspace**: File → Clear Last Workspace (disables auto-opening)

### Navigation
- **File Tree**: Use the workspace tree to navigate and open files
- **Tabs**: Switch between open files using tabs
- **Line Numbers**: Click on line numbers for quick navigation

## Supported File Types

| File Type | Extension | Features |
|-----------|-----------|----------|
| Java | `.java` | Keywords, strings, comments, annotations, types |
| XML | `.xml` | Tags, attributes, comments, CDATA |
| JSON | `.json` | Keys, values, strings, numbers, booleans |
| CSS | `.css` | Selectors, properties, values, colors |
| HTML | `.html`, `.htm` | Tags, attributes, comments, DOCTYPE |

## Architecture

### Core Components
- **JCode**: Main application class and JavaFX entry point
- **Workbench**: Event-driven architecture for component communication
- **CodeEditor**: Multi-tab code editing with syntax highlighting
- **WorkspaceViewBuilder**: File tree management and workspace persistence
- **SyntaxHighlighter**: Language-specific syntax highlighting system

### Event System
The application uses a custom event system for loose coupling between components:
- `WorkspaceOpenEvent`: Workspace opened
- `CloseWorkspaceEvent`: Workspace closed
- `FileLoadEvent`: File loading requested
- `LoadedFileEvent`: File loaded successfully
- `TaskStartedEvent`: Background task started

### Preferences System
User preferences are stored using Java's Preferences API:
- Window state and position
- Last opened workspace
- Tree expansion state
- File dialog last directory

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

[Add your license information here]

## Acknowledgments

- **RichTextFX**: Advanced text editing capabilities
- **JavaFX**: Modern UI framework
- **JMetro**: Dark theme styling
- **Ikonli**: Icon library for UI elements