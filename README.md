# TrainVoc

TrainVoc is a vocabulary training application designed to help users improve their language skills by practicing words categorized by levels or exams. The app uses a database of words and provides various quiz types to enhance learning.

## Features

- **Word Levels**: Words are categorized into levels (A1, A2, B1, B2, C1, C2) based on difficulty.
- **Exam Types**: Words can also be grouped by specific exams (e.g., TOEFL, IELTS).
- **Quiz Types**: Multiple quiz modes are available, such as:
  - Random
  - Least Correct
  - Least Wrong
  - Least Reviewed
  - Least Recent
  - Most Correct
  - Most Wrong
  - Most Reviewed
  - Most Recent
- **Statistics**: Tracks user performance, including correct, wrong, and skipped answers.
- **Database**: Preloaded with words and their meanings, categorized by levels and exams.

## Technologies Used

- **Programming Language**: Kotlin
- **Framework**: Android
- **Database**: Room Database
- **Dependency Injection**: Hilt (if applicable)
- **Coroutines**: For asynchronous operations
- **Jetpack Compose**: For UI development

## Project Structure

- `classes/`: Contains core classes such as `Word`, `Exam`, `Statistic`, and enums like `WordLevel`.
- `database/`: Manages the Room database and DAO interfaces.
- `ui/`: Contains UI components and screens.
- `viewmodel/`: Includes ViewModel classes for managing UI-related data.
- `repository/`: Handles data operations and acts as a bridge between the database and ViewModel.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ahmetabdullahgultekin/TrainVoc.git
   ```
2. Open the project in Android Studio.
3. Sync the Gradle files.
4. Run the application on an emulator or a physical device.

## Usage

1. Launch the app.
2. Select a quiz type and a parameter (level or exam).
3. Answer the questions in the quiz.
4. View your performance statistics.

## Database Details

- **Entities**:
    - `Word`: Represents a word with its meaning, level, and other metadata.
    - `Exam`: Represents an exam type.
    - `Statistic`: Tracks user performance.
    - `WordExamCrossRef`: Cross-reference table for words and exams.
- **Preloaded Data**:
    - Words and their meanings are preloaded from a JSON file (`all_words.json`).
    - The database is created from an asset file (`word-db.db`).

## Key Classes

### `AppDatabase`

Manages the Room database and provides access to DAOs.

### `WordDao`

Defines database operations for the `Word` entity, such as:
- Fetching words by level or exam
- Inserting words, exams, and statistics

### `QuizParameter`

A sealed class used to represent quiz parameters:
- `Level`: For level-based quizzes
- `ExamType`: For exam-based quizzes

### `QuizType`

An enum representing different quiz modes.

## Unit Testing

Unit tests are written to verify the functionality of ViewModels and repositories. Example tests include:
- Fetching words
- Inserting words
- Generating quiz questions

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes and push them to your fork.
4. Submit a pull request.

## Contact

For any questions or feedback, please contact:
- **Name**: Ahmet Abdullah Gültekin
- **GitHub**: [ahmetabdullahgultekin](https://github.com/ahmetabdullahgultekin)
```
