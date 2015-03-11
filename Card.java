//test
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.util.*;
public class Card
{
    //instance variables
    private JLabel label;
    private JPanel card;
    private JTextField[] input;
    private JButton apply, done;
    Question currQuestion;
    Question [] questions;
    EventTesterPanel eventTester;
    String fileName, extension;
    int versions;
    //Card(Container pane, String label2, EveventTEsterPanel etp, String fileName, String extension, int versions)
    //constructor
    public Card(Container pane, String label2, EventTesterPanel etp, String fileName, String extension, int versions)
    {
        //set's the panel to grid layout
        GridLayout grid = new GridLayout(8, 2);
        //sets the gaps between parts of the grid
        grid.setVgap(5);
        grid.setHgap(5);
        //sets instance variables
        this.versions = versions;
        this.fileName = fileName;
        this.extension = extension;
        label = new JLabel("Question");
        //sets the font to calibri size 18
        label.setFont(new Font("Calibri", Font.PLAIN, 18));
        card = new JPanel(grid);
        input = new JTextField[7];
        apply = new JButton("Add Question");
        apply.setFont(new Font("Calibri", Font.PLAIN, 18));
        done = new JButton("Done");
        done.setFont(new Font("Calibri", Font.PLAIN, 18));
        currQuestion = new Question();
        questions = new Question[0];
        eventTester=etp;
        //adds the label and first text field
        card.add(label);
        input[0] = new JTextField(18);
        card.add(input[0]);
        //adds all the jtextfields to the panel, along with labels
        for(int i = 1; i<7; i++)
        {
            if(i != 6) {
                //adds jtextfield
                JLabel curLabel = new JLabel("Answer Choice " + i);
                curLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
                card.add(curLabel);
                input[i]=new JTextField(12);
                card.add(input[i]);
            }
            else {
                JLabel curLabel = new JLabel("Number of Correct Answer");
                curLabel.setFont(new Font("Calibri", Font.PLAIN, 18));
                card.add(curLabel);
                input[i] = new JTextField(12);
                card.add(input[i]);
            }
        }
        //adds buttons
        card.add(apply);
        card.add(done);
        //adds card to pane
        pane.add(card, BorderLayout.CENTER);
        //adds actionListeners to buttons
        apply.addActionListener(new AListener());
        done.addActionListener(new AListener());
    }
    //getCard()
    //gets the card(panel)
    public JPanel getCard()
    {
        return card;
    }
    //setLabel(String label2)
    //sets the label
    public void setLabel(String label2)
    {
        label = new JLabel(label2);
    }
    //Action listener for the buttons on card
    private class AListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //if apply is pressed
            if (e.getSource().equals(apply))
            {
                //checks if the question is valid to add
                boolean valid = checkValid();
                if(valid) {
                //sets the question
                currQuestion.setQuestion2(input[0].getText());
                for(int i = 0; i<5; i++)
                {
                    currQuestion.setAnswer(i, input[i+1].getText());
                }
                currQuestion.setCorrectAnswer(input[6].getText());
                //adds the new question to the list, and resets the rest
                questions = addQuestion(currQuestion);
                currQuestion = new Question();
                clearTextFields();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Error in entering question.");
                }
            }
            //if done is pressed
            if(e.getSource().equals(done))
            {
                //makes all the tests for the input, and checks whether or not they were all made
                boolean closer = makeAllTests(versions, 1);
                //if they were all made it ends the program
                if(closer) {
                    System.exit(0);
                }
            }
        }
        //toString(Question[] qs)
        //Creates a test based on a question array
        public String toString(Question [] qs)
        {
            String temp = "";
            for(int i = 0; i<qs.length; i++)
            {
                //adds each question in a string to the string
                temp += "" + (i+1) + ". " + qs[i].toString2();
            }
            return temp;
        }
        //Makes a string for the answer key
        public String toStringKey(Question [] qs)
        {
            String temp = "";
            for(int i = 0; i<qs.length; i++)
            {
                //adds each answer to the string
                temp += "" + (i+1) + ". " + qs[i].getCorrectAnswer() + "\n";
            }
            return temp;
        }
        //checks whether or not the question input is valid
        public boolean checkValid() {
            String answer = input[6].getText();
            return isInt(answer) && !(answer.equals(""));
        }
        //checks whether input string is an int
        public boolean isInt(String input) {
            if(input.length() == 0) {
                return true;
            }
            else {
                int ascii = (int) input.charAt(0);
                return ascii >= 48 && ascii <= 57 && isInt(input.substring(1, input.length()));
            }
        }
        //scrambleQuestions(Question qs[])
        //srambles the questions
        public Question[] scrambleQuestions(Question qs[])
        {
            //creates a random
            Random random = new Random();
            for(int i = 0; i<(qs.length*2); i++)
            {
                //random indecies
                int r1 = random.nextInt(10000)%qs.length;
                int r2 = random.nextInt(10000)%qs.length;
                //switches them
                Question temp = qs[r1];
                qs[r1] = qs[r2];
                qs[r2] = temp;
            }
            return qs;
        }
        //adds a question to the array of questions
        public Question[] addQuestion(Question q) {
            int length = questions.length + 1;
            Question [] temp = new Question [length];
            for(int i = 0; i < length - 1; i++){
                temp[i] = questions[i];
            }
            temp[length-1] = q;
            return temp;
        }
        //scrambles all of the answers within the questions
        public void scrambleAnswers() {
            for(int i = 0; i < questions.length; i++) {
                questions[i].scrambleAnswers();
            }
        }
        //makes all the tests based upon the question array
        public boolean makeAllTests(int versions, int current) {
            //as long as it hasn't exceeded the version number
            if(current <= versions) {
                //Try: makes sure the file isn't currently open, if not does it
                try {
                    //generates name for file
                    String name = fileName + "_Version" + current + extension;
                    //makes file and the BW for the file
                    File f = new File(name);
                    FileWriter fwriter = new FileWriter(f);
                    BufferedWriter writer = new BufferedWriter(fwriter);
                    //makes the string to be written
                    String va = toString(questions);
                    //writes it and closes it
                    writer.write(va, 0, va.length());
                    writer.close();
                    //makes the answer key
                    makeKey(current);
                    //scrambles the questions and answers, makes the next test
                    scrambleQuestions(questions);
                    scrambleAnswers();
                    return true && makeAllTests(versions, current + 1); 
                }
                //Catches error if file is open
                catch (IOException c){
                    JOptionPane.showMessageDialog(null, "One of the documents is currently open.");
                    return false;
                }
            }
            return true;

        }
        public void makeKey(int current) {
            try {
                //generates the name of the file
                String name = fileName + "_Version" + current + "_AnswerKey" + extension;
                //makes the file and BW
                File f = new File(name);
                FileWriter fwriter = new FileWriter(f);
                BufferedWriter writer = new BufferedWriter(fwriter);
                //Makes the string for the answer key, writes it and closes it
                String va = toStringKey(questions);
                writer.write(va, 0, va.length());
                writer.close();
            }
            //catches if the document is open
            catch (IOException c){
                JOptionPane.showMessageDialog(null, "One of the documents is currently open.");
            }

        }
        //resets the text for all the fields
        public void clearTextFields() {
            for(int i = 0; i < input.length; i++) {
                input[i].setText("");
            }
        }
    }
}