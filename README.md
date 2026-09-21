# Quiz Management System

A Java Swing desktop application for creating, conducting, and managing quizzes.

## Features

* Student registration and login
* Teacher login and quiz management
* Create and edit quizzes
* Multiple-choice questions
* Automatic evaluation and score calculation
* Quiz results and history
* SQLite database for storing data

## Technologies Used

* Java
* Java Swing
* JDBC
* SQLite
* Object-Oriented Programming

## How to Run

Make sure Java JDK is installed.

Compile the project:

```bash
javac -cp "lib\sqlite-jdbc-3.42.0.0.jar" -d bin src\*.java
```

Run the application:

```bash
java -cp "bin;lib\sqlite-jdbc-3.42.0.0.jar" Main
```

## Project Structure

```text
src/
├── Main.java
├── LoginScreen.java
├── RegisterScreen.java
├── StudentDashboard.java
├── TeacherDashboard.java
├── CreateQuizScreen.java
├── QuizAttemptScreen.java
├── QuizResultsScreen.java
├── HistoryScreen.java
├── QuestionEditorScreen.java
├── DatabaseHelper.java
└── DAO and model classes

lib/
└── sqlite-jdbc-3.42.0.0.jar
```

## Author

Narayani Srinivas Panganti
Electronics and Telecommunication Engineering
TSSM's BSCOER | SPPU
