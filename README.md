# Quiz Application

A comprehensive Java Swing desktop application for creating and taking quizzes with persistent storage using SQLite database.

## Features

### User Authentication
- **Registration**: Create a new account with username, email, and password
- **Login**: Secure authentication to access the application
- **Session Management**: Stay logged in during the session

### Quiz Creation
- Create quizzes with custom titles and descriptions
- Add unlimited multiple-choice questions (4 options each)
- Automatic mark distribution (100 marks total)
- Warning for unequal mark distribution
- Save quizzes to the database for future use

### Quiz Taking
- Browse and select from available quizzes
- Navigate between questions freely
- Track progress with visual indicators
- View detailed results after completion
- Retry quizzes to improve scores

### Score Tracking & History
- View personal quiz history
- Track best scores for each quiz
- See percentage and grade for each attempt
- View quiz leaderboards

### Dashboard
- Overview of available quizzes
- Personal statistics (total attempts, average score)
- Quick access to quiz creation
- Manage your created quizzes

## Technology Stack

- **Language**: Java 8+
- **GUI Framework**: Java Swing
- **Database**: SQLite with JDBC
- **Architecture**: MVC Pattern
- **IDE**: Eclipse

## Project Structure

```
QuizApp/
├── src/
│   ├── QuizApp.java           # Main application entry point
│   ├── DatabaseManager.java   # Database operations (DAO)
│   ├── LoginPanel.java        # Login/Register GUI
│   ├── DashboardPanel.java    # Main dashboard after login
│   ├── QuizCreatorPanel.java  # Quiz creation interface
│   ├── QuizTakerPanel.java    # Quiz taking interface
│   ├── Quiz.java              # Quiz model
│   ├── Question.java          # Question model
│   ├── User.java              # User model
│   ├── QuizAttempt.java       # Quiz attempt model
│   └── LeaderboardEntry.java  # Leaderboard entry model
├── lib/
│   └── sqlite-jdbc-x.x.x.jar  # SQLite JDBC driver (required)
├── quizapp.db                 # SQLite database (auto-created)
└── README.md                  # This file
```

## Database Schema

### Tables

1. **users** - User accounts
   - id, username, password, email, created_at

2. **quizzes** - Quiz metadata
   - id, title, description, creator_id, total_marks, created_at

3. **questions** - Quiz questions
   - id, quiz_id, question_text, choice1-4, correct_answer_index, marks, question_order

4. **quiz_attempts** - User quiz attempts
   - id, user_id, quiz_id, score, total_marks, percentage, completed_at

5. **user_answers** - Individual answers for each attempt
   - id, attempt_id, question_id, selected_answer_index, is_correct

## Setup Instructions

### Prerequisites
- Java JDK 8 or higher
- Eclipse IDE (recommended) or any Java IDE
- SQLite JDBC Driver

### Step 1: Download SQLite JDBC Driver

1. Download the SQLite JDBC driver from: https://github.com/xerial/sqlite-jdbc/releases
2. Download the latest `sqlite-jdbc-x.x.x.jar` file

### Step 2: Import Project in Eclipse

1. Open Eclipse IDE
2. Go to `File` → `Import` → `General` → `Existing Projects into Workspace`
3. Select the QuizApp folder
4. Click `Finish`

### Step 3: Add SQLite JDBC to Build Path

1. Create a `lib` folder in the project root (if not exists)
2. Copy `sqlite-jdbc-x.x.x.jar` to the `lib` folder
3. In Eclipse, right-click on the project → `Properties`
4. Go to `Java Build Path` → `Libraries` tab
5. Click `Add JARs...` → Select `lib/sqlite-jdbc-x.x.x.jar`
6. Click `Apply and Close`

### Step 4: Run the Application

1. Right-click on `QuizApp.java`
2. Select `Run As` → `Java Application`

## Usage Guide

### First Time Setup
1. Launch the application
2. Click "Register here" on the login screen
3. Create an account with username and password
4. Login with your credentials

### Creating a Quiz
1. From the Dashboard, click "Create New Quiz"
2. Enter a quiz title and description
3. Add questions:
   - Enter the question text
   - Fill in all 4 choices
   - Select the correct answer
   - Click "Add Question"
4. Click "Save Quiz to Database" or "Save & Take Quiz Now"

### Taking a Quiz
1. From the Dashboard, select a quiz from the list
2. Click "Take Selected Quiz"
3. Answer each question by selecting a choice
4. Navigate using Previous/Next buttons or question numbers
5. Click "Finish Quiz" when done
6. View your results and detailed breakdown

### Viewing Leaderboard
1. Select a quiz from the Dashboard
2. Click "View Leaderboard"
3. See top 10 scores for that quiz

### Managing Your Quizzes
1. Click "My Created Quizzes" on the Dashboard
2. View all quizzes you've created
3. Delete quizzes if needed

## Features in Detail

### Mark Distribution
- Total marks: 100
- Marks are distributed equally among questions
- If marks cannot be divided evenly, a warning is shown

### Grading System
| Percentage | Grade |
|------------|-------|
| 90-100%    | A+    |
| 80-89%     | A     |
| 70-79%     | B     |
| 60-69%     | C     |
| 50-59%     | D     |
| Below 50%  | F     |

### Progress Tracking
- Visual progress bar during quiz
- Question navigator for quick access
- Unanswered questions warning before submission

## Troubleshooting

### "SQLite JDBC driver not found"
- Ensure `sqlite-jdbc-x.x.x.jar` is in the build path
- Verify the JAR file is not corrupted

### "Database initialization error"
- Check write permissions in the application directory
- Ensure the database file is not locked by another process

### Application won't start
- Verify Java JDK is installed and configured
- Check Eclipse console for error messages

## Contributing

This is an educational project. Feel free to fork and modify for your own learning purposes.

## License

This project is for educational purposes.

---

**Version**: 2.0
**Last Updated**: 2024
