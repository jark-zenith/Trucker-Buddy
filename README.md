# Study Tracker (Android)

Study Tracker is a native Android local-first student operating system by PRUDEN TECHNOLOGIES, built with Kotlin and Jetpack Compose.

It is designed to grow with a student across every semester instead of being a single-semester timetable app.

## Features & Modules

### Academic Management
- **Dashboard**: Command center with active semester status, quick task addition, next lesson highlight, upcoming deadlines, weekly target progress, and weekly class overview.
- **Timetable**: Recurring weekly timetable by day (Monday to Sunday) with start/end times, room designations, and reminder minutes. Click any class to edit or delete.
- **Academic Units**: Subject overview with unit codes, lecturers, rooms, weekly class frequencies, and self-study progress bars compared against target hours.
- **Tasks & Assessments**: Track assignments, CATs, tests, exams, projects, and revision with priority levels (Normal, High, Critical), completion status, and due dates.
- **Attendance**: Unit-level attendance tracking (present, late, absent, excused) with calculated percentage health indicators.
- **Grades**: Academic performance logging with scores, percentage calculations, assessment type categorization, and performance stats (average, highest score).
- **Academic Calendar**: Chronological deadlines and calendar events view with event creation.

### Study Intelligence
- **Study Sessions**: Log independent self-study hours outside class with topic, duration, date, study type, and notes.
- **Focus Lab**: Integrated Pomodoro / deep-work countdown timer with 25m and 50m presets, haptic/vibration feedback upon completion, and direct logging to study history.
- **Notes**: Coursework and revision notes organized with subject tags, preview snippets, and full text editing.
- **Resources**: Reference links, documentation, and videos with native Android intent launcher.
- **Analytics**: Semester study time, task completion rate, attendance health, and study effort breakdown by unit.
- **Semesters**: Multi-semester management to archive completed terms and switch between academic years.
- **Settings & Sync**: Student profile customization, study goal targets, dark/light theme toggle, optional REST server synchronization, and full JSON backup export/import.

## Tech Stack

- **Target OS**: Android (minSdk 26, targetSdk 35, compileSdk 36)
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture**: MVVM with Repository Pattern, StateFlow, and Coroutines
- **Storage**: Local-first JSON repository with export/import capabilities
- **Theming**: Dark and Light theme matching Pruden Technologies Student OS aesthetic
- **Adaptive Icon**: Custom adaptive app launcher icon
