# jCode Architecture

## Overview

jCode is a lightweight JavaFX-based code editor with syntax highlighting, workspace management, and preferences persistence. The application follows a clean architecture pattern with clear separation of concerns.

## Architecture Layers

### 1. Presentation Layer (`ui/` package)
- **UIManager**: Centralizes UI component management and lifecycle
- **WelcomeScreen**: Beautiful welcome interface for first-time users
- **CodeEditor**: Main code editing component with syntax highlighting
- **Controls**: Reusable UI components and builders

### 2. Application Layer (`controllers/` package)
- **ApplicationController**: Main application coordinator that handles event routing and service coordination
- Orchestrates communication between UI components and business services
- Manages application state and lifecycle

### 3. Business Logic Layer (`services/` package)
- **FileService**: Handles file operations (loading, validation, processing)
- **WorkspaceService**: Manages workspace operations (opening, closing, recent workspaces)
- **Workbench**: Event-driven communication hub for the application

### 4. Configuration Layer (`config/` package)
- **ConfigurationManager**: Centralized configuration and preferences management
- **AppConstants**: Application-wide constants and configuration values

### 5. Data Layer (`preferences/` package)
- **JsonPreferences**: Custom preferences implementation using JSON storage
- **JCodePreferencesFactory**: Factory for creating preference instances

## Key Design Patterns

### Event-Driven Architecture
The application uses an event-driven pattern through the `Workbench` class:
- Components subscribe to events they're interested in
- Services emit events when state changes occur
- Loose coupling between components

### Service Layer Pattern
Business logic is encapsulated in service classes:
- **FileService**: File operations and validation
- **WorkspaceService**: Workspace management and persistence
- Clear separation of concerns

### MVC Pattern
- **Model**: Services and data layer
- **View**: UI components in the `ui/` and `controls/` packages
- **Controller**: ApplicationController and event handlers

### Singleton Pattern
- **ConfigurationManager**: Ensures single instance for configuration management
- **Workbench**: Single event bus for the application

## Package Structure

```
src/main/java/io/vepo/jcode/
├── JCode.java                    # Main application entry point
├── Workbench.java                # Event-driven communication hub
├── controllers/                  # Application controllers
│   └── ApplicationController.java
├── services/                     # Business logic services
│   ├── FileService.java
│   └── WorkspaceService.java
├── ui/                          # UI management
│   └── UIManager.java
├── controls/                    # UI components
│   ├── CodeEditor.java
│   ├── WelcomeScreen.java
│   ├── SyntaxHighlighter.java
│   └── ...
├── events/                      # Event classes
│   ├── FileLoadEvent.java
│   ├── WorkspaceOpenEvent.java
│   └── ...
├── config/                      # Configuration management
│   └── ConfigurationManager.java
├── constants/                   # Application constants
│   └── AppConstants.java
├── preferences/                 # Data persistence
│   ├── JsonPreferences.java
│   └── JCodePreferencesFactory.java
├── workspace/                   # Workspace management
│   ├── WorkspaceRoot.java
│   └── WorkspaceViewBuilder.java
└── utils/                       # Utility classes
    └── FileId.java
```

## Data Flow

1. **User Interaction**: User interacts with UI components
2. **Event Emission**: UI components emit events through Workbench
3. **Service Processing**: Services handle business logic and emit result events
4. **UI Updates**: Controllers receive events and update UI accordingly

## Configuration Management

The application uses a hierarchical configuration system:
- **Window Configuration**: Position, size, maximized state
- **Editor Configuration**: Open tabs, editor preferences
- **Workspace Configuration**: Recent workspaces, workspace state
- **Application Configuration**: General application settings

## Benefits of This Architecture

### Maintainability
- Clear separation of concerns
- Modular design allows easy modification
- Well-documented interfaces

### Testability
- Services can be unit tested independently
- Event-driven architecture allows for easy mocking
- Clear dependencies make testing straightforward

### Scalability
- New features can be added without affecting existing code
- Services can be extended or replaced independently
- Event system allows for loose coupling

### Code Organization
- Logical package structure
- Consistent naming conventions
- Clear responsibility boundaries

## Adding New Features

### Adding a New Service
1. Create a new service class in the `services/` package
2. Implement business logic
3. Emit appropriate events through Workbench
4. Update ApplicationController to handle new events

### Adding a New UI Component
1. Create the component in the `controls/` package
2. Subscribe to relevant events in the component
3. Update UIManager if needed
4. Add any necessary CSS styling

### Adding New Configuration
1. Add constants to `AppConstants.java`
2. Add methods to `ConfigurationManager.java`
3. Update services to use the new configuration

## Best Practices

1. **Event-Driven Communication**: Use events for component communication
2. **Service Encapsulation**: Keep business logic in services
3. **Configuration Centralization**: Use ConfigurationManager for all settings
4. **Error Handling**: Implement proper error handling in services
5. **Logging**: Use SLF4J for consistent logging throughout the application
6. **Documentation**: Document public methods and complex logic
7. **Testing**: Write unit tests for services and integration tests for controllers 