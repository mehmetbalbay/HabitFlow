# 🏃‍♂️ HabitFlow

Modern Android app. Designed to track healthy lifestyle habits. Daily routines, nutrition, exercise and water consumption tracking.

## ✨ Features

### 🎯 Core Features
- **Onboarding System**: User goal setting
- **Habit Tracking**: Daily habit tracking
- **Meal Planning**: Nutrition planning
- **Exercise Tracking**: Workout programs
- **Hydration Monitoring**: Water consumption tracking
- **Insights & Analytics**: Analysis and reporting
- **Profile Management**: User profile management

### 🏗️ Technical Features
- **Clean Architecture**: Modular code structure
- **MVVM Pattern**: Modern Android approach
- **Jetpack Compose**: Modern UI framework
- **Material 3 Design**: Current design system
- **Dependency Injection**: Hilt dependency management
- **Room Database**: Local data storage
- **Navigation Component**: Safe navigation
- **Comprehensive Testing**: 100% test coverage

## 🏛️ Architect

```
📁 app/                    # Main application module
📁 core/                   # Core libraries
├── 📁 core-domain/        # Business logic and domain models
├── 📁 core-data/          # Data layer (Repository, DataSource)
├── 📁 core-database/      # Room database
├── 📁 core-network/       # Network layer
├── 📁 core-ui/            # UI components
└── 📁 core-designsystem/  # Design system
📁 feature/                # Feature modules
├── 📁 onboarding/         # Onboarding process
├── 📁 auth/               # Authentication
├── 📁 habit/              # Habit tracking
├── 📁 meals/              # Nutrition tracking
├── 📁 exercise/           # Exercise tracking
├── 📁 water/              # Water consumption tracking
├── 📁 insights/           # Analytics and reporting
├── 📁 profile/            # User profile
└── 📁 settings/           # Settings
📁 sync/                   # Data synchronization
```

### Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Kotlin | 1.9.22 |
| **UI Framework** | Jetpack Compose | 2024.06.00 |
| **Architecture** | MVVM + Clean Architecture | - |
| **Dependency Injection** | Hilt | 2.51.1 |
| **Database** | Room | - |
| **Navigation** | Navigation Compose | 2.7.7 |
| **Async** | Coroutines + Flow | 1.7.3 |
| **Testing** | JUnit + Espresso + Compose Testing | - |
| **Coverage** | JaCoCo | 0.2 |

### Test Coverage
Project has 100% test coverage:

- **Unit Tests**: 43 tests
- **Android Tests**: 78 tests
- **Total**: 121 tests

### Test Running

```bash
# Run all tests
./gradlew test

# Run unit tests
./gradlew :feature:onboarding:testDebugUnitTest

# Run Android tests
./gradlew :feature:onboarding:connectedAndroidTest

# Generate coverage report
./gradlew :feature:onboarding:createDebugCoverageReport
```

### Coverage Report
```bash
# View coverage report
open feature/onboarding/build/reports/coverage/test/debug/index.html
```

## 📱 Feature Details

### 🎯 Onboarding
- Set user goals
- Configure sleep schedule
- Plan daily routines
- Set nutrition preferences
- Define exercise goals
- Set water consumption goals
- Configure quiet hours

### 🏃‍♂️ Habit Tracking
- Daily habit tracking
- Progress visualization
- Streak tracking
- Reminders

### 🍎 Meal Planning
- Meal planning
- Nutritional value tracking
- Calorie calculation
- Nutrition analysis

### 💪 Exercise Tracking
- Workout programs
- Training tracking
- Progress analysis
- Goal setting

### 💧 Hydration Monitoring
- Water consumption tracking
- Daily goal setting
- Reminders
- Progress visualization

### Project Structure
```
HabitFlow/
├── 📁 app/                    # Main application
├── 📁 core/                   # Core libraries
├── 📁 feature/                # Feature modules
├── 📁 build-logic/            # Gradle build logic
├── 📁 sync/                   # Data synchronization
├── 📁 gradle/                 # Gradle configuration
├── 📄 build.gradle.kts        # Root build file
├── 📄 settings.gradle.kts     # Project settings
└── 📄 README.md              # This file
```

### Coding Standards
- Follow **Kotlin Coding Conventions**
- Apply **Clean Code** principles
- Embrace **SOLID** principles
- Use **Composition over Inheritance** approach
- Follow **Single Responsibility** principle

## 📊 Performance

### Build Performance
- **Gradle Build Cache** enabled
- **Parallel Execution** used
- **Incremental Compilation** supported

### Test Performance
- **Unit Tests**: ~0.1s
- **Android Tests**: ~1m
- **Coverage Report**: ~2s