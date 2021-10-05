package com.example.hangmantutorial;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView txtWordToBeGuessed; //Showing display
    String wordToBeGuessed; //String stored
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView txtLettersTried;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Letters tried:";
    TextView txtTriesLeft;
    String triesLeft;
    final String WINNING_MESSAGE = "You won!";
    final String LOSING_MESSAGE = "You lost!";
    Animation rotateAnimation;
    Animation scaleAnimation;
    Animation scaleAndRotationAnimation;

    void revealLetterInWord(char letter) {
        int indexOfLetter = wordToBeGuessed.indexOf(letter); //If not find -1 is returned

        //Loop if index is positive or 0
        while(indexOfLetter <= 0 ) {
            wordDisplayedCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
            //Find possible more equal letteers start on previous letterindex
            //while until index of letter is -1(last index)
        }

        //uppdate the string as well
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
    }

    void displayWordOnScreen() {
        String formattedString ="";
        for (char character : wordDisplayedCharArray) {
            formattedString += character + " ";
        }
        txtWordToBeGuessed.setText(formattedString);
    }

    void initializeGame() {
        //When create game
        //Set up game when click on reset
        //!WORD
        //shuffle arraylist and get first elementm and then remove it
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);

        //initialize character array
        wordDisplayedCharArray = wordToBeGuessed.toCharArray();

        //add underscores
        //underscores to letters firs/last character
        for(int i = 1; i < wordDisplayedCharArray.length - 1 ; i++) {
            wordDisplayedCharArray[i] = '_';
        }
        //reveal all occurences of first character
        revealLetterInWord(wordDisplayedCharArray[0]);

        //reveal all occurences of last character
        revealLetterInWord(wordDisplayedCharArray[wordDisplayedCharArray.length - 1]);

        //initialize a string from this char array (for search purposes)
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);

        //display word
        displayWordOnScreen();

        //2.INPUT
        //clear input fields
        edtInput.setText("");

        //3.LETTERS TRIED
        //initialize string for letters tried with a space
        lettersTried = "";

        //display on screen
        txtLettersTried.setText(MESSAGE_WITH_LETTERS_TRIED);

        //4.TRIES LEFT
        //initialize the string for tries left
        triesLeft = " X X X X X ";
        txtTriesLeft.setText(triesLeft);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        myListOfWords = new ArrayList<String>();
        txtWordToBeGuessed = (TextView) findViewById(R.id.txtWordToBeGuessed);
        edtInput = (EditText) findViewById(R.id.edtInput);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        txtTriesLeft = (TextView) findViewById(R.id.txtTriesLeft);

        //travase database file and populate array
        InputStream myInputStream = null;
        Scanner in = null;


        String aWord = "";
        try {
            myInputStream = getAssets().open("database_file.txt");
            in = new Scanner(myInputStream);
            while (in.hasNext()) {
                aWord = in.next();
                myListOfWords.add(aWord);
//                Toast.makeText(MainActivity.this, aWord, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getClass().getSimpleName() + ": " + e.getMessage() , Toast.LENGTH_SHORT).show();

            //            e.printStackTrace();
        } finally {
            //close scanner
            if(in != null) {
                in.close();
            }
            //close inputstream
            try {
                if(myInputStream != null) {
                    myInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.getClass().getSimpleName() + ": " + e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }//End scanner inputstream

        initializeGame();

        //setip the text changed listener for the edit text
        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if ther is some letter on the inputfield
                if(charSequence.length() != 0) {
                    checkIfLetterIsInWord(charSequence.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
    }//on create

    void checkIfLetterIsInWord(char letter) {
        //if the letter was found inside the word to be guesse

        if(wordToBeGuessed.indexOf(letter) >= 0) {
            //if the letter was not displayed yet
            if(wordDisplayedString.indexOf(letter) < 0 ){ //or -1
                //replace the underscores with letter
                revealLetterInWord(letter);

                //update the changes on the screen
                displayWordOnScreen();
                //check if the game is won
                // alt : if(wordDisplayedString.indexOf("_")){}
                if(!wordDisplayedString.contains("_")) {
                    txtTriesLeft.setText(WINNING_MESSAGE);
                }
            }
        }
        //otherwise if the letter was not found inside the word to be guessed
        else {
            //decreasing the number of tries left, and well show it on the screen
            decreaseAndDisplayTriesLeft();

            //check if the game is lost
            if(triesLeft.isEmpty()) {
              txtTriesLeft.setText(LOSING_MESSAGE);
              txtWordToBeGuessed.setText(wordToBeGuessed);
            }
        }

        //display the letter that was tried
        if(lettersTried.indexOf(letter) < 0) {
            lettersTried += letter + ", ";
            String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
            txtLettersTried.setText((messageToBeDisplayed));
        }
    }//check if letter in word

    void decreaseAndDisplayTriesLeft() {
        //if there are still some tries lfet
        if(!triesLeft.isEmpty()) {
            //take out the last 2 characters from the string
            triesLeft = triesLeft.substring(0, triesLeft.length() - 2);
            txtTriesLeft.setText(triesLeft);
        }
    }

    void resetGame(View v) {
        //setup a new game
        initializeGame();
    }

}//end