# QuickQuiz Java Project

## Project Overview
QuickQuiz is an interactive Java-based quiz application that allows users to test their knowledge across a variety of topics. The application supports multiple difficulty levels and features a timer for each question, adding a layer of excitement and challenge. The project offers a user-friendly console interface and allows the user to track their scores for each quiz.

## Features
- **Multiple Topics**: Choose from a variety of topics such as Java, C++, OOPs, DBMS, and more.
- **Difficulty Levels**: Select between Easy, Intermediate, and Hard levels for each quiz.
- **Timer**: Set a time limit for each question to increase the challenge.
- **Real-Time Scoring**: View your score instantly after completing each question.
- **Multiple Rounds**: Take multiple quizzes in one session.

## Technologies Used
- **Java** (Core Java)
- **Scanner** (for user input)
- **Timer** and **TimerTask** (for quiz timer functionality)

## Setup Instructions

### Prerequisites
- Java 8 or higher installed on your machine.
- IDE such as IntelliJ IDEA or Eclipse for Java development (optional).

### Steps to Run the Project
1. **Clone the repository**:
   ```bash
   git clone https://github.com/anushkaapoddar/QuickQuiz.git
2. **Navigate to the project directory**:
    ```bash
   cd QuickQuiz
3. **Compile the java file**:
    ```bash
   javac *.java
4. **Run the application**:
    ```bash
   java Quiz

### Usage 
-Start the application: After running the program, you will be prompted to select a quiz topic.                                                                    
-Choose difficulty level: Once a topic is selected, you can choose between Easy, Intermediate, and Hard levels.                                                    
-Set a timer: You will be asked to input a time limit for each question (in seconds).                                                                              
-Answer questions: The questions will be displayed with multiple-choice options. You must answer within the time limit.                                            
-Track your score: After completing all questions, your final score will be displayed.                                                                             
-Play again: You can choose to play another round by entering "yes" or quit by entering "no".                                                                      

### Code Structure
-Quiz.java: The main class containing the quiz logic, topics, and questions.                                                                                       
-Score Calculation: Keeps track of the user’s score based on correct answers.                                                                                      
-Timer: Implements a countdown for each question using Java’s Timer and TimerTask.                                                                                 
-Input Validation: Handles invalid inputs and ensures that only valid answers (A/B/C/D) are accepted.                                                              
-Dynamic Quiz Flow: Based on user input, it runs the quiz with the selected topic and difficulty level.                                                            

### Example of a Sample Quiz
Here’s an example of how a quiz session looks:                                                                                                                                                                                                                                       
===========================================
     Welcome to the Interactive Quiz App!     
===========================================

Choose a Topic to Test Your Knowledge:
-----------------------------------------
1. Java Basics
2. C++ Questions
3. OOPs Basics
4. DBMS Basics
5. C Basics
6. General Knowledge
7. DCCN Basics
Enter your choice (1-7): 1

Select Difficulty Level:
-------------------------
1. Easy
2. Intermediate
3. Hard
Enter your choice (1-3): 1

Set the Timer for Each Question (in seconds):
Enter time (greater than 0): 10

Starting Java Basics Quiz - Easy Level!
---------------------------------------------------

*Q1: Which keyword is used to define a class in Java?*                                                                                                               

A. class
B. def
C. function
D. Class

Your answer (A/B/C/D): A
Correct!

*Q2: Which method is the entry point of a Java program?*

A. start()
B. run()
C. main()
D. execute()

Your answer (A/B/C/D): C
Correct!

...

### Contributions
I welcome contributions! If you want to contribute, please fork the repository and create a pull request. Ensure that all tests pass and your changes are well-documented.
