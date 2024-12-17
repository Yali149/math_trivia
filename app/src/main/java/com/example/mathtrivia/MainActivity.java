package com.example.mathtrivia;

import android.widget.Toast;
import android.os.Bundle;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView questionTextView;
    private TextView scoreTextView;
    private LinearLayout answersLayout;
    private Button selectAnswerCountButton;

    // Quiz Data
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int displayedOptions = 4;
    private int correctCount = 0;
    private int totalAnswered = 0;

    private List<String> currentOptions; // Options for the current question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        questionTextView = findViewById(R.id.questionTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        answersLayout = findViewById(R.id.answersLayout);
        selectAnswerCountButton = findViewById(R.id.selectAnswerCountButton);

        // Answer count selection button
        selectAnswerCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswerCountDialog();
            }
        });

        // Initialize quiz
        initializeQuestions();
        loadQuestion();
    }

    private void initializeQuestions() {
        questionList = new ArrayList<>();

        // Adding sample questions
        questionList.add(new Question("What is 5 + 3?", new String[]{"6", "7", "8", "9"}, 2));
        questionList.add(new Question("What is 10 - 6?", new String[]{"2", "3", "4", "5"}, 2));
        questionList.add(new Question("What is 4 * 2?", new String[]{"6", "7", "8", "10"}, 2));
        questionList.add(new Question("What is 12 / 4?", new String[]{"1", "2", "3", "4"}, 1));
        questionList.add(new Question("What is 7 + 6?", new String[]{"11", "12", "13", "14"}, 2));
        questionList.add(new Question("What is 15 - 7?", new String[]{"6", "7", "8", "9"}, 3));
        questionList.add(new Question("What is 3 * 5?", new String[]{"10", "12", "13", "15"}, 3));
        questionList.add(new Question("What is 16 / 4?", new String[]{"2", "3", "4", "5"}, 2));

        // Shuffle questions to randomize order
        Collections.shuffle(questionList);
    }

    private void loadQuestion() {
        // Reset options layout
        answersLayout.removeAllViews();

        if (currentQuestionIndex >= questionList.size()) {
            // End of quiz
            showQuizCompletion();
            return;
        }

        // Get the current question
        Question currentQuestion = questionList.get(currentQuestionIndex);

        // Display the question
        questionTextView.setText(currentQuestion.getQuestionText());
        scoreTextView.setText("Score: " + correctCount + " / " + totalAnswered);

        // Prepare options
        currentOptions = new ArrayList<>();
        List<String> allOptions = new ArrayList<>();
        Collections.addAll(allOptions, currentQuestion.getOptions());

        String correctOption = currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()];

        // Ensure the correct answer is added first
        currentOptions.add(correctOption);
        allOptions.remove(correctOption);

        // Add random incorrect answers until we reach the desired count
        while (currentOptions.size() < displayedOptions && !allOptions.isEmpty()) {
            int randomIndex = (int) (Math.random() * allOptions.size());
            currentOptions.add(allOptions.remove(randomIndex));
        }

        // Shuffle the options
        Collections.shuffle(currentOptions);

        // Dynamically create buttons for each option
        for (final String option : currentOptions) {
            Button answerButton = new Button(this);
            answerButton.setText(option);
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer(option, correctOption);
                }
            });
            answersLayout.addView(answerButton);
        }
    }


    private void checkAnswer(String selectedOption, String correctOption) {
        totalAnswered++;

        // Check if the answer is correct
        boolean isCorrect = selectedOption.equals(correctOption);
        if (isCorrect) {
            correctCount++;
            // Show a Toast for correct answer
            Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            // Show a Toast for incorrect answer
            Toast.makeText(MainActivity.this, "Wrong! Correct answer: " + correctOption, Toast.LENGTH_SHORT).show();
        }

        // Update score immediately
        scoreTextView.setText("Score: " + correctCount + " / " + totalAnswered);

        // Move to the next question
        currentQuestionIndex++;
        loadQuestion();
    }


    private void showQuizCompletion() {
        // Display quiz completion message
        questionTextView.setText("Quiz Completed!");
        answersLayout.removeAllViews();
        selectAnswerCountButton.setVisibility(View.GONE);

        scoreTextView.setText("Final Score: " + correctCount + " / " + totalAnswered);
    }

    private void showAnswerCountDialog() {
        final String[] items = {"2 answers", "3 answers", "4 answers", "5 answers", "6 answers"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of Answers");

        builder.setSingleChoiceItems(items, displayedOptions - 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                displayedOptions = which + 2; // Update displayed answers
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadQuestion();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Question helper class
    static class Question {
        private final String questionText;
        private final String[] options;
        private final int correctAnswerIndex;

        public Question(String questionText, String[] options, int correctAnswerIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String[] getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }
    }
}
