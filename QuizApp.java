import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class QuizApp {
    private static int score;
    private static boolean timeExpired;
    private static Timer timer;
    private static int timePerQuestion;
    private static JFrame frame;
    private static JTextArea questionArea;
    private static JButton[] optionButtons;
    private static JLabel scoreLabel;
    private static JTextField timerField;
    private static int questionIndex;
    private static String[] questions;
    private static String[][] options;
    private static char[] answers;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Color PRIMARY_COLOR = new Color(76, 175, 80); // Green
    private static final Color ACCENT_COLOR = new Color(255, 167, 38); // Orange-Amber
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // Dark Gray

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Interactive Quiz App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null); // Center the frame

            // Welcome Panel
            JPanel welcomePanel = new JPanel();
            welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
            welcomePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
            welcomePanel.setBackground(BACKGROUND_COLOR);

            JLabel welcomeLabel = new JLabel("Welcome to the Interactive Quiz App!");
            welcomeLabel.setFont(BOLD_FONT.deriveFont(24f));
            welcomeLabel.setForeground(PRIMARY_COLOR);
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel topicLabel = new JLabel("Select your topic to begin!");
            topicLabel.setFont(MAIN_FONT);
            topicLabel.setForeground(TEXT_COLOR);
            topicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topicLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

            JButton startButton = new JButton("Start Quiz");
            startButton.setFont(MAIN_FONT);
            startButton.setBackground(PRIMARY_COLOR);
            startButton.setForeground(Color.WHITE);
            startButton.setFocusPainted(false);
            startButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            startButton.addActionListener(e -> showTopicSelection());

            welcomePanel.add(welcomeLabel);
            welcomePanel.add(topicLabel);
            welcomePanel.add(startButton);

            frame.add(welcomePanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static void showTopicSelection() {
        String[] topics = {"Java Basics", "C++ Questions", "OOPs Basics", "DBMS Basics", "C Basics", "General Knowledge", "DCCN Basics"};
        String topic = (String) JOptionPane.showInputDialog(
                frame,
                "Choose a Topic:",
                "Topic Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                topics,
                topics[0]
        );

        if (topic != null) {
            showLevelSelection(topic);
        }
    }

    private static void showLevelSelection(String topic) {
        String[] levels = {"Easy", "Intermediate", "Hard"};
        String level = (String) JOptionPane.showInputDialog(
                frame,
                "Select Difficulty Level:",
                "Level Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levels,
                levels[0]
        );

        if (level != null) {
            showTimerSelection(topic, level);
        }
    }

    private static void showTimerSelection(String topic, String level) {
        String timerInput = JOptionPane.showInputDialog(
                frame,
                "Set Timer for Each Question (in seconds):",
                "Timer Setting",
                JOptionPane.QUESTION_MESSAGE
        );
        try {
            timePerQuestion = Integer.parseInt(timerInput);
            if (timePerQuestion <= 0) {
                timePerQuestion = 10; // Default value
            }
        } catch (NumberFormatException e) {
            timePerQuestion = 10; // Default value
        }

        startQuiz(topic, level);
    }

    private static void startQuiz(String topic, String level) {
        initializeQuestions(topic, level);
        score = 0;
        questionIndex = 0;
        timeExpired = false;

        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.setBackground(BACKGROUND_COLOR);

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFont(BOLD_FONT);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBackground(BACKGROUND_COLOR);
        questionArea.setForeground(TEXT_COLOR);
        questionArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(questionArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(MAIN_FONT);
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setForeground(TEXT_COLOR);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            optionsPanel.add(optionButtons[i]);
            final int index = i;
            optionButtons[i].addActionListener(e -> handleAnswer(index));
        }
        frame.add(optionsPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(BOLD_FONT);
        scoreLabel.setForeground(PRIMARY_COLOR);
        bottomPanel.add(scoreLabel, BorderLayout.WEST);

        timerField = new JTextField("Time: ");
        timerField.setEditable(false);
        timerField.setFont(MAIN_FONT);
        timerField.setForeground(ACCENT_COLOR);
        timerField.setBackground(BACKGROUND_COLOR);
        timerField.setHorizontalAlignment(JTextField.RIGHT);
        timerField.setBorder(BorderFactory.createEmptyBorder());
        bottomPanel.add(timerField, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.NORTH);
        frame.revalidate();
        frame.repaint();

        showQuestion();
    }

    private static void initializeQuestions(String topic, String level) {
        // Example questions based on the selected topic and difficulty
        if (topic.equals("Java Basics")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        "Which keyword is used to define a class in Java?",
                        "Which method is the entry point of a Java program?",
                        "Which operator is used to compare two values in Java?",
                        "Which of the following is not a Java keyword?"
                };
                options = new String[][]{
                        {"A. class", "B. def", "C. function", "D. Class"},
                        {"A. start()", "B. run()", "C. main()", "D. execute()"},
                        {"A. =", "B. ==", "C. !=", "D. equals()"},
                        {"A. static", "B. final", "C. main", "D. private"}
                };
                answers = new char[]{'A', 'C', 'B', 'C'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        "Which of the following is not a Java feature?",
                        "Which of these keywords is used to define a class in Java?",
                        "What is the size of an int variable in Java?",
                        "Which method is the entry point of a Java program?"
                };
                options = new String[][]{
                        {"A. Object-oriented", "B. Use of pointers", "C. Portable", "D. Dynamic"},
                        {"A. class", "B. union", "C. define", "D. struct"},
                        {"A. 4 bytes", "B. 2 bytes", "C. depends on the system", "D. 1 bytes"},
                        {"A. public static void main(String[] args)", "B. static public void main(String[] args)", "C. public static void main(String args[])", "D. All of the above"}
                };
                answers = new char[]{'B', 'A', 'A', 'D'};
            } else { // Hard
                questions = new String[]{
                        "Which design pattern is used for creating only one instance of a class?",
                        "Which Java feature allows memory management automatically?",
                        "Which is not a Java memory area?",
                        "Java collections are part of which package?"
                };
                options = new String[][]{
                        {"A. Singleton", "B. Factory", "C. Observer", "D. Prototype"},
                        {"A. Automatic Garbage Collection", "B. Manual Memory Management", "C. Stack Management", "D. Heap Management"},
                        {"A. Stack", "B. Heap", "C. Method Area", "D. Cache"},
                        {"A. java.util", "B. java.io", "C. java.lang", "D. java.awt"}
                };
                answers = new char[]{'A', 'A', 'D', 'A'};
            }
        }
        // Additional topics can be added similarly
        else if (topic.equals("C++ Questions")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        "Which symbol is used to end a statement in C++?",
                        "Which of the following is a correct C++ comment?",
                        "Which keyword is used to define a class in C++?",
                        "Which of the following is the correct header file for 'cout'?"
                };
                options = new String[][]{
                        {"A. :", "B. ;", "C. .", "D. ,"},
                        {"A. /* comment */", "B. # comment", "C. -- comment", "D. %% comment"},
                        {"A. object", "B. define", "C. class", "D. structure"},
                        {"A. #include<iostream>", "B. #include<conio.h>", "C. #include<stdio.h>", "D. #include<math.h>"}
                };
                answers = new char[]{'B', 'A', 'C', 'A'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        "Which concept allows using the same function name with different parameters?",
                        "Which is used to allocate memory dynamically in C++?",
                        "What is the correct syntax for a constructor in C++?",
                        "Which keyword is used to inherit a class in C++?"
                };
                options = new String[][]{
                        {"A. Function overriding", "B. Function overloading", "C. Abstraction", "D. Encapsulation"},
                        {"A. malloc", "B. calloc", "C. new", "D. alloc"},
                        {"A. void constructor() {}", "B. int Constructor() {}", "C. ClassName() {}", "D. new ClassName()"},
                        {"A. inherits", "B. extends", "C. using", "D. :"}
                };
                answers = new char[]{'B', 'C', 'C', 'D'};
            } else {
                questions = new String[]{
                        "Which feature of C++ allows defining functions with the same name in a base and derived class?",
                        "Which of the following supports Runtime Polymorphism?",
                        "Which operator cannot be overloaded in C++?",
                        "What does STL stand for in C++?"
                };
                options = new String[][]{
                        {"A. Inheritance", "B. Constructor", "C. Function Overriding", "D. Function Overloading"},
                        {"A. static function", "B. friend function", "C. virtual function", "D. inline function"},
                        {"A. +", "B. =", "C. ::", "D. <<"},
                        {"A. Standard Type Library", "B. Simple Template Library", "C. Static Template Library", "D. Standard Template Library"}
                };
                answers = new char[]{'C', 'C', 'C', 'D'};
            }

        } else if (topic.equals("OOPs Basics")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        " Which of the following is NOT a pillar of OOP?",
                        " What is an object?",
                        " Which OOP concept binds data and methods together?",
                        " What is a class?",
                };
                options = new String[][]{
                        {"A. Encapsulation", "B. Inheritance", "C. Compilation", "D. Polymorphism"},
                        {"A. A collection of functions only", "B. A loop structure", "C. An instance of a class", "D. A comment in code"},
                        {"A. Abstraction", "B. Polymorphism", "C. Encapsulation", "D. Looping"},
                        {"A. A blueprint for an object", "B. A function", "C. A variable", "D. A loop"},
                };
                answers = new char[]{'C', 'C', 'C', 'A'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        " Which of the following allows a subclass to provide a specific implementation of a method that is already defined in its superclass?",
                        " Which of the following is used to achieve runtime polymorphism in Java?",
                        " What is true about abstract classes?",
                        " What does dynamic binding mean in OOP?",
                };
                options = new String[][]{
                        {"A. Overloading", "B. Overriding", "C. Inheriting", "D. Encapsulation"},
                        {"A. Method overloading", "B. Static methods", "C. Method overriding", "D. Constructors"},
                        {"A. They cannot have any methods", "B. They can be instantiated", "C. They can have both abstract and concrete methods", "D. They are the same as interfaces"},
                        {"A. Binding data at compile time", "B. Binding function call to its definition at runtime", "C. Binding two variables together", "D. Binding a loop to a function"},
                };
                answers = new char[]{'B', 'C', 'C', 'B'};
            } else {
                questions = new String[]{
                        " Which of the following is true about virtual functions in C++?",
                        " What is the diamond problem in OOP?",
                        " What happens if a class implements two interfaces with methods having the same signature in Java?",
                        " What is object slicing in C++?",
                };
                options = new String[][]{
                        {"A. They are resolved at compile time", "B. They are only available in abstract classes", "C. They support dynamic (runtime) polymorphism", "D. They cannot be overridden"},
                        {"A. A type of syntax error", "B. When two classes inherit from the same parent and are inherited by a single class", "C. When class names conflict", "D. When too many interfaces are implemented"},
                        {"A. Compilation error", "B. It inherits both as is", "C. It must override the method", "D. The method from the first interface is used"},
                        {"A. Creating multiple copies of an object", "B. Losing part of the object when assigning a derived class object to a base class object", "C. Cutting strings using object methods", "D. Splitting memory space between objects"},
                };
                answers = new char[]{'C', 'B', 'C', 'B'};
            }

        } else if (topic.equals("DBMS Basics")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        " Which of the following is a feature of a DBMS?",
                        " Which of the following is NOT a type of DBMS?",
                        " What is the language used to interact with a database?",
                        " Which of these is an example of a DBMS?",
                };
                options = new String[][]{
                        {"A. Compiling programs", "B. Data storage", "C. Operating system control", "D. Internet browsing"},
                        {"A. Hierarchical", "B. Relational", "C. Network", "D. Linear"},
                        {"A. HTML", "B. CSS", "C. SQL", "D. XML"},
                        {"A. Microsoft Word", "B. Google Chrome", "C. Oracle", "D. Adobe Photoshop"},
                };
                answers = new char[]{'B', 'D', 'C', 'C'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        " Which of the following is true about a primary key in a relational database?",
                        " What does the term normalization refer to in DBMS?",
                        " Which SQL command is used to remove a table from a database?",
                        " What is a foreign key in a database?",
                };
                options = new String[][]{
                        {"A. It can have duplicate values", "B. It uniquely identifies each record in a table", "C. It can be null", "D. It is not necessary for a table"},
                        {"A. Organizing data to reduce redundancy", "B. Creating backups of data", "C. Converting data into binary format", "D. Encrypting database content"},
                        {"A. DELETE", "B. REMOVE", "C. ERASE", "D. DROP"},
                        {"A. A key that uniquely identifies rows within the same table", "B. A key used for data encryption", "C. A key used to link two tables", "D. A key that automatically generates values"},
                };
                answers = new char[]{'B', 'A', 'D', 'C'};
            } else {
                questions = new String[]{
                        " Which of the following ensures ACID properties in a transaction?",
                        " In which normal form is a relation if it is in 2NF and there is no transitive dependency?",
                        " What is the purpose of the view in SQL?",
                        " Which of the following is not true about relational algebra?",
                };
                options = new String[][]{
                        {"A. Data Dictionary", "B. Transaction log", "C. Locking mechanism", "D. Query optimizer"},
                        {"A. 1NF", "B. 2NF", "C. 3NF", "D. BCNF"},
                        {"A. To physically store data", "B. To control access and present data in a specific format", "C. To permanently modify base tables", "D. To speed up INSERT operations"},
                        {"A. It is a procedural query language", "B. It works on relations", "C. It provides a theoretical foundation for SQL", "D. It is used to define triggers"},
                };
                answers = new char[]{'C', 'C', 'B', 'D'};
            }

        } else if (topic.equals("General Knowledge")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        "What is the capital of France?",
                        "Who wrote the play Romeo and Juliet?",
                        "What is the largest planet in our solar system?",
                        "Which is the smallest prime number?"
                };
                options = new String[][]{
                        {"A. Berlin", "B. Madrid", "C. Paris", "D. Rome"},
                        {"A. Charles Dickens", "B. William Shakespeare", "C. Mark Twain", "D. Jane Austen"},
                        {"A. Earth", "B. Saturn", "C. Jupiter", "D. Mars"},
                        {"A. 0", "B. 1", "C. 2", "D. 3"}
                };
                answers = new char[]{'C', 'B', 'C', 'C'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        "Who was the first Prime Minister of India?",
                        "Which element has the chemical symbol 'O'?",
                        "In which year did the Titanic sink?",
                        "Which continent is the Sahara Desert located in?"
                };
                options = new String[][]{
                        {"A. Mahatma Gandhi", "B. Jawaharlal Nehru", "C. Sardar Patel", "D. Rajendra Prasad"},
                        {"A. Gold", "B. Oxygen", "C. Silver", "D. Iron"},
                        {"A. 1910", "B. 1912", "C. 1920", "D. 1932"},
                        {"A. Asia", "B. Africa", "C. Australia", "D. Europe"}
                };
                answers = new char[]{'B', 'B', 'B', 'B'};
            } else {
                questions = new String[]{
                        "What is the currency of Japan?",
                        "Who developed the theory of relativity?",
                        "Which country is known as the Land of the Rising Sun?",
                        "Which is the longest river in the world?"
                };
                options = new String[][]{
                        {"A. Yen", "B. Won", "C. Yuan", "D. Peso"},
                        {"A. Isaac Newton", "B. Albert Einstein", "C. Galileo Galilei", "D. Stephen Hawking"},
                        {"A. Korea", "B. Thailand", "C. Japan", "D. China"},
                        {"A. Amazon", "B. Ganga", "C. Nile", "D. Mississippi"}
                };
                answers = new char[]{'A', 'B', 'C', 'C'};
            }

        } else if (topic.equals("C Basics")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        " Which  is a valid C identifier?",
                        " What is the size of an int variable typically in C ?",
                        " Which is used to take input in C?",
                        " What will be the output of the code? int main() {printf(\"%d\", 5/2);}",
                };
                options = new String[][]{
                        {"A. int", "B. 2value", "C. _total", "D. #define"},
                        {"A. 2 bytes", "B. 4 bytes", "C. 8 bytes", "D. Depends on the compiler only"},
                        {"A. cin", "B. input()", "C. scanf()", "D. read()"},
                        {"A. 2.5", "B. 2", "C. 3", "D. Error"},
                };
                answers = new char[]{'C', 'B', 'C', 'B'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        " Which operator has the highest precedence in C?",
                        " Which is a correct declaration of a pointer to a function in C?",
                        " Which is not a valid storage class in C?",
                        " What is the primary purpose of a header file in C?",
                };
                options = new String[][]{
                        {"A. &&", "B. =", "C. sizeof", "D. ++ (postfix)"},
                        {"A. int f();", "B. int (*f)();", "C. int f();", "D. int *(f());"},
                        {"A. auto", "B. static", "C. dynamic", "D. extern"},
                        {"A. To execute the main program", "B. To declare functions and macros to be shared across files", "C. To handle memory allocation", "D. To define variables globally"},
                };
                answers = new char[]{'D', 'B', 'C', 'B'};
            } else {
                questions = new String[]{
                        " What happens if you declare a variable register int x; and then try to get its address using &x?",
                        " What does the volatile keyword mean in C?",
                        " What is the difference between #define and const in C?",
                        " What will happen if you write code without a return 0; in the main() function in C?",
                };
                options = new String[][]{
                        {"A. The address of x is returned", "B. Compilation error", "C. Runtime error", "D. It returns 0"},
                        {"A. The variable's value can be changed only once", "B. The variable is stored in read-only memory", "C. The variable can be changed unexpectedly (e.g., by hardware or threads)", "D. It disables compiler optimization for the variable"},
                        {"A. #define uses memory, const does not", "B. #define is a macro replaced by the preprocessor; const is a typed variable", "C. They are the same", "D. const is replaced at compile time; #define is replaced at runtime"},
                        {"A.  Compilation error", "B. Undefined behavior", "C. Program won't run", "D. Depends on the compiler, but often returns 0 implicitly"},
                };
                answers = new char[]{'B', 'C', 'B', 'D'};
            }

        } else if (topic.equals("DCCN Basics")) {
            if (level.equals("Easy")) {
                questions = new String[]{
                        " Which is responsible for end-to-end communication and error recovery in the OSI model?",
                        " In data communication, what is the main purpose of modulation ?",
                        " What is the default port number for HTTP?",
                        " Which is a connection-oriented protocol?",
                };
                options = new String[][]{
                        {"A. Physical Layer", "B. Transport Layer", "C. Data Link Layer", "D. Network Layer"},
                        {"A. To decrease noise", "B. To compress the data", "C. To allow the signal to be transmitted over long distances", "D. To encrypt the data"},
                        {"A. 21", "B. 80", "C. 443", "D. 25"},
                        {"A. UDP", "B. FTP", "C. IP", "D. TCP"},
                };
                answers = new char[]{'B', 'C', 'B', 'D'};
            } else if (level.equals("Intermediate")) {
                questions = new String[]{
                        " What is the maximum number of hosts that can be assigned IP addresses in a Class C network?",
                        " Which multiplexing technique transmits data from multiple sources one at a time in a sequence?",
                        " Which error detection method uses polynomial division to detect errors in frames?",
                        " Which IP address class provides a very large number of host addresses per network?",
                };
                options = new String[][]{
                        {"A. 254", "B. 256", "C. 65,534", "D. 2,097,152"},
                        {"A. FDM", "B. TDM", "C. CDM", "D. WDM"},
                        {"A. Parity Check", "B. Checksum", "C. CRC (Cyclic Redundancy Check)", "D. Hamming Code"},
                        {"A. Class A", "B. Class B", "C. Class C", "D. Class D"},
                };
                answers = new char[]{'A', 'B', 'C', 'A'};
            } else {
                questions = new String[]{
                        " which transmission mode do both sender and receiver transmit data simultaneously?",
                        " Which switching techniques is most suitable for real-time voice communication?",
                        " What is the purpose of congestion control in networking?",
                        " Which protocol is used to send an email from a client to a server or between servers?",
                };
                options = new String[][]{
                        {"A. Simplex", "B. Half-Duplex", "C. Full-Duplex", "D. Parallel"},
                        {"A. Packet Switching", "B. Message Switching", "C. Circuit Switching", "D. Hybrid Switching"},
                        {"A. Encrypt data before transmission", "B. Prevent packet collisions at the data link layer", "C. Avoid overwhelming the network with too much traffic", "D. Increase the number of active connections"},
                        {"A. POP3", "B. IMAP", "C. SMTP", "D. FTP"},
                };
                answers = new char[]{'C', 'C', 'C', 'C'};
            }
        }
    }

    private static void showQuestion() {
        if (questionIndex < questions.length) {
            questionArea.setText(questions[questionIndex]);
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(options[questionIndex][i]);
                optionButtons[i].setEnabled(true); // Enable buttons for the new question
                optionButtons[i].setBackground(Color.WHITE); // Reset button color
                optionButtons[i].setForeground(TEXT_COLOR);
            }
            startTimer();
        } else {
            endQuiz();
        }
    }

    private static void startTimer() {
        timeExpired = false;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int timeLeft = timePerQuestion;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    timerField.setText("Time: " + timeLeft);
                    timeLeft--;
                } else {
                    timeExpired = true;
                    timer.cancel();
                    highlightCorrectAnswer();
                    JOptionPane.showMessageDialog(frame, "Time's up! Correct answer: " + answers[questionIndex]);
                    questionIndex++;
                    // Delay before showing the next question
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            showQuestion();
                        }
                    }, 1500); // 1.5 seconds delay
                }
            }
        }, 0, 1000);
    }

    private static void handleAnswer(int selectedOption) {
        if (timeExpired) return;

        timer.cancel();
        char selectedAnswer = (char) ('A' + selectedOption);
        boolean isCorrect = (selectedAnswer == answers[questionIndex]);

        if (isCorrect) {
            score++;
            optionButtons[selectedOption].setBackground(new Color(144, 238, 144)); // LightGreen
            JOptionPane.showMessageDialog(frame, "Correct!");
        } else {
            optionButtons[selectedOption].setBackground(new Color(255, 160, 122)); // LightSalmon
            highlightCorrectAnswer();
            JOptionPane.showMessageDialog(frame, "Incorrect! Correct answer: " + answers[questionIndex]);
        }

        // Disable all option buttons after an answer is selected
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }

        scoreLabel.setText("Score: " + score);
        questionIndex++;

        // Delay before showing the next question
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                showQuestion();
            }
        }, 1500); // 1.5 seconds delay
    }

    private static void highlightCorrectAnswer() {
        char correctAnswer = answers[questionIndex];
        int correctIndex = correctAnswer - 'A';
        if (correctIndex >= 0 && correctIndex < optionButtons.length) {
            optionButtons[correctIndex].setBackground(new Color(144, 238, 144)); // LightGreen
        }
    }

    private static void endQuiz() {
        JOptionPane.showMessageDialog(frame, "Quiz Completed! Your Score: " + score + "/" + questions.length);
        frame.dispose();
    }
}
