package spellcheckapp;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class QuickCheck {
    /** Window width, in pixels*/
    private static final int WINDOW_WIDTH = 480;
    
    /** Window height, in pixels*/
    private static final int WINDOW_HEIGHT = 540;
    
    /** Left panel background color*/
    private static final Color LEFT_COLOR = Color.WHITE;
    
    /** Right panel background color*/
    private static final Color RIGHT_COLOR = new Color(126, 205, 205);
   
    /** Top panel (quickCheck title bar) background color*/
    private static final Color TOP_COLOR = new Color(80, 166, 166);
    
    /** Initialization for main input textarea*/
    private static final JTextArea text = new JTextArea();

    /** Title bar label initialization*/
    private static JLabel upperLabel = new JLabel("quickCheck Your Writing");

    /** Dictionary filename */
    private static final String DICTIONARY_FILE_NAME = "words.txt";

    /** Initialization of font used in titles */
    private static Font titleFont = null;

    /** Main window initialization */
    private static JFrame frame;
    
    /** Wrong words list initialization*/
    private static JList<String> errorsList;
    private static DefaultListModel<String> errListModel = new DefaultListModel<String>();
    
    /** Correction suggestions list initialization*/
    private static JList<String> correctionsList;
    private static DefaultListModel<String> correctionsListModel = new DefaultListModel<String>();
    
    /** Converts dictionary file to HashSet for faster access time */   
    private static final Set<String> VALUES = 
            new HashSet<String>(Arrays.asList(initString().split("\n")));

    /**
     * Method converting the dictionary text file to a String, then returning it.
     * @return String containing every valid dictionary word, separated by '\n'
     */
    public static String initString() {
        try {
            return (FileUtils.readFileToString(new File(DICTIONARY_FILE_NAME)));
        } catch (IOException e) {
            //This exception should never occur, as the file name is hard-coded in.
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Method that checks if a given word is contained in the dictionary file.
     * @param wordToCheck a String representing the word to be checked.
     * @return a boolean representing if the word is valid. 
     */
    public static boolean isValidWordHash(String wordToCheck) {
        return VALUES.contains(wordToCheck);
    }
    
    /**
     * The main method that runs when the program is launched.
     * @param args command line arguments. Unused.
     */
    public static void main(String[] args) {
        //Sets up the Font used for title
        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("EXPLETUSSANS-BOLD.ttf"));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        titleFont = titleFont.deriveFont(Font.BOLD,28f);

        //Creates main window
        frame = new JFrame("quickCheck");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        /*Required to instantiate an inner class*/
        QuickCheck thisProgram = new QuickCheck();
        
        //Sets up upper panel
        JPanel upperPanel = new JPanel();
        upperPanel.setBackground(TOP_COLOR);
        upperPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,44));
        upperPanel.add(upperLabel);
        QuickCheck.ContentFrame lowerPanel = thisProgram.new ContentFrame();

        //Sets up title bar (upperPanel) and actual content pane (lowerPanel)
        frame.getContentPane().add(upperPanel, "North");
        frame.getContentPane().add(lowerPanel, "South");
        
        //Sets up font of title bar
        upperLabel.setFont(titleFont);

        //Creates the scroll bar for the text on the left
        JScrollPane textScrollHolder = new JScrollPane(text);
        textScrollHolder.setBorder(BorderFactory.createEmptyBorder());

        //Creates the text field on the left side  
        text.setCaretPosition(0);
        lowerPanel.getLeft().add(textScrollHolder);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        //Adds text field errorsListener 
        QuickCheck.TypeListener lstn = thisProgram.new TypeListener();
        text.getDocument().addDocumentListener(lstn);

        //Creates upper-right panel
        JPanel rightNestedUpper = new JPanel();
        rightNestedUpper.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        rightNestedUpper.setBackground(RIGHT_COLOR);
        
        //Creates lower-right panel
        JPanel rightNestedLower = new JPanel();
        rightNestedLower.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        rightNestedLower.setBackground(RIGHT_COLOR);
        
        BorderLayout rightLayout = new BorderLayout();
        lowerPanel.getRight().setLayout(rightLayout);
        lowerPanel.getRight().add(rightNestedUpper);
        
        //Creates middle-right panel
        JPanel rightNestedInner = new JPanel();
        BoxLayout rightNestedInnerLayout = new BoxLayout(rightNestedInner, BoxLayout.Y_AXIS);
        rightNestedInner.setLayout(rightNestedInnerLayout);
        
        //Adds middle-right panel to right side, then error text to it.
        lowerPanel.getRight().add(rightNestedInner,BorderLayout.CENTER);

        /*Error Word List Label Setup */
        
        //Creates Error JLabel and sets up its font
        Font errorFont = titleFont.deriveFont(Font.BOLD,22f);
        JLabel errorLabel = new JLabel("Errors");
        errorLabel.setFont(errorFont);
        
        //Creates wrapper to center JLabel
        JPanel errorLabelPanel = new JPanel();
        errorLabelPanel.add(errorLabel);
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(errorLabelPanel);
        box.add(Box.createVerticalGlue());
        
        //Adds first the JLabel wrapper, then the JLabel
        lowerPanel.getRight().add(box,BorderLayout.NORTH);
        box.add(errorLabelPanel); 
        
        
        /*Error Word List Setup*/
        
        //Error words List creation
        errorsList = new JList(errListModel);
        errorsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        errorsList.setLayoutOrientation(JList.VERTICAL);
        errorsList.setVisibleRowCount(10);
        errorsList.setFixedCellHeight(15);
        errorsList.setFixedCellWidth(100);
        
        //Error words List scroll bar
        JScrollPane errorsListScroller = new JScrollPane(errorsList);
        errorsListScroller.setPreferredSize(new Dimension(250, 80));
        errorsListScroller.setBorder(BorderFactory.createEmptyBorder());
        
        //Adding both the List and the Scroll bar to the right inner panel
        rightNestedInner.add(errorsListScroller);
        
        
        /*Correction Word List Label Setup */

        //Creates Correction JLabel and sets up its font
        Font correctionFont = titleFont.deriveFont(Font.BOLD,22f);
        JLabel correctionLabel = new JLabel("Corrections");
        correctionLabel.setFont(correctionFont);
        
        //Creates wrapper to center JLabel
        JPanel correctionLabelPanel = new JPanel();
        correctionLabelPanel.add(correctionLabel);
        Box box2 = new Box(BoxLayout.Y_AXIS);
        box2.setAlignmentX(Component.CENTER_ALIGNMENT);
        box2.add(Box.createVerticalGlue());
        box2.add(correctionLabelPanel);
        box2.add(Box.createVerticalGlue());
        
        //Adds first the JLabel wrapper, then the JLabel
        rightNestedLower.add(box2);
        box2.add(correctionLabelPanel); 
        
        
        /*Correction Word List Setup*/
        
        //Correction words List creation
        correctionsList = new JList(correctionsListModel);
        correctionsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        correctionsList.setLayoutOrientation(JList.VERTICAL);
        correctionsList.setVisibleRowCount(10);
        correctionsList.setFixedCellHeight(15);
        correctionsList.setFixedCellWidth(100);
        
        //Error words List scroll bar
        JScrollPane correctionsListScroller = new JScrollPane(correctionsList);
        correctionsListScroller.setPreferredSize(new Dimension(250, 80));
        correctionsListScroller.setBorder(BorderFactory.createEmptyBorder());
        
        //Creates wrapper JPanel for both corrections title and contents
        JPanel correctionsWrapper = new JPanel();
        BoxLayout correctionsWrapperLayout = new BoxLayout(correctionsWrapper, BoxLayout.Y_AXIS);
        correctionsWrapper.setLayout(correctionsWrapperLayout);
        correctionsWrapper.add(correctionLabelPanel);
        correctionsWrapper.add(correctionsListScroller);

        //Adding both the List and the Scroll bar to the right inner panel
        rightNestedLower.add(correctionsWrapper);

        //!!Test for matches
        String[] printArr = closeMatches("jasdasks");
        for (int i = 0; i < printArr.length; i++) {
            System.out.println(printArr[i]);
        }
        
        
        //Sets colors for panels
        rightNestedInner.setBackground(RIGHT_COLOR);
        errorLabelPanel.setBackground(RIGHT_COLOR);
        correctionLabelPanel.setBackground(RIGHT_COLOR);

        
        //Adds lower-right panel
        lowerPanel.getRight().add(rightNestedLower,BorderLayout.SOUTH);
        rightNestedLower.setPreferredSize(new Dimension(300,350));
        rightNestedInner.setMaximumSize(new Dimension(300,0));
        rightNestedUpper.setPreferredSize(new Dimension(300,0));
        lowerPanel.getLeft().setMaximumSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
        
   
        //errorsList lsiteners and setup
        errorsList.addListSelectionListener(selectClickListener);
        errorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        
        //ignore-correct-auto correct all?
        //ignore all?
        //add scroll area with possible corrections for each word
        //find close matches function
        
        frame.pack();
        frame.setVisible(true);
        
    }
    
    /**
     * A custom JPanel that composes the entirety of this program's "content" portion
     * of the GUI. Composed of two side by side panels that can be accessed by their related
     * getters and setters.
     * 
     * @author Lorgus
     * @version 1.0.1
     *
     */
    public class ContentFrame extends JPanel{
        private static final long serialVersionUID = 1L;
        private JPanel leftSide;
        private JPanel rightSide;
        
        /**
         * Default constructor. Initializes layout and adds both sides
         * of the panel.
         */
        public ContentFrame() {
            setLayout(new GridLayout(0, 2));
            leftSide = createLeftPane();
            rightSide = createRightPane();
            
            add(leftSide);
            add(rightSide);
        }
        
        /**
         * Function intended to initialize the left portion of the
         * Content Panel.
         * @return JPanel object with background color LEFT_COLOR
         */
        protected JPanel createLeftPane() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setBackground(LEFT_COLOR);
            return panel;
        }
        
        /**
         * Function intended to initialize the right portion of the
         * Content Panel.
         * @return JPanel object with background color RIGHT_COLOR
         */
        protected JPanel createRightPane() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setBackground(RIGHT_COLOR);
            panel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
            return panel;
        }
        
        /**
         * Getter for the left side of the Content Panel.
         * @return reference to the left side of the Content Panel.
         */
        public JPanel getLeft() {
            return leftSide;
        }
        
        /**
         * Getter for the right side of the Content Panel.
         * @return reference to the right side of the Content Panel.
         */
        public JPanel getRight() {
            return rightSide;
        }
        
    }
    
    /**
     * Sets font of left (input) textbox.
     * @param fontToSetTo String representing the font to set to.
     */
    public static void setFont(String fontToSetTo) {
        Font myFont = new Font(fontToSetTo, Font.PLAIN, 18);
        text.setFont(myFont);
    }
    
    /**
     * Custom DocumentListener that calls the spellCheck function.
     * @author Lorgus
     * @version 1.0.1
     */
    class TypeListener implements DocumentListener {
        TypeListener() {
            super();
        }
        /**No implementation.*/
        @Override
        public void changedUpdate(DocumentEvent arg0) {}
        
        /**Function that is called when a character 
         * is added to the left JTextArea.*/
        @Override
        public void insertUpdate(DocumentEvent arg0) {
            spellCheck();
        }
        /**Function that is called when a character is 
         * removed from the left JTextArea.*/
        @Override
        public void removeUpdate(DocumentEvent arg0) {
            spellCheck();
        }
    }
    
    
    /**
     * Function that checks the text contained in the JTextArea, as a string, then goes 
     * through each word sequentially, checking if they are contained in the dictionary file, and
     * if not, adding them to an array wrongWords and displaying it in the "Error" JTextArea on the
     * right.
     */
    public static void spellCheck() {
        //remove all words currently in the errorlist
        errListModel.clear();
                
        //Create and fill an array with all incorrect words
        String[] wrongWords = null;
        wrongWords = wordsInError(text.getText());            
        
        //Display that errorsList on the right side of the document
        for (int i = 0; i < wrongWords.length; i++) {
            if (wrongWords[i] != null) {
                //loop through all wrongWords and add them to the error errorsList
                errListModel.addElement(wrongWords[i]);
            }
        }
    }
    
    /**
     * Given a string of words separated by whitespace returns an array containing
     * the words among them that did not match any of the dictionary-defined "correct"
     * English words.
     * @param wordsToCheck String of words to check, separated by whitespace
     * @return array of Strings containing incorrect words only.
     */
    public static String[] wordsInError(String wordsToCheck) {
        
        //Empty string case
        if (wordsToCheck.length() == 0) {
            String[] empty = new String[0];
            return empty;
        }
        
        //Converts incoming string to a lower case array of all words
        String[] words = wordsToCheck.replaceAll("\\p{P}", "").toLowerCase().split("\\s+");
        int errCount = 0;
        
        String[] wrongWords = new String [wordsToCheck.length()];
        
        //For each word in the given string, we check that it is in the errorsList of allowed words
        for (int i = 0; i < words.length; i++) {
            
            //If it is not valid, add it to the wrongWords array, filling it from the start.
            if (!QuickCheck.isValidWordHash(words[i])) {
                wrongWords[errCount] = words[i];
                errCount++;
            }
        }
        //Truncate array of wrong words to optimal size before returning
        wrongWords = ArrayUtils.subarray(wrongWords,0,errCount);
        return wrongWords;
    }    
    
    /**
     * Detects selection events for the erroneous words List, in order to
     * detect which word to generate suggestions for.
     */
    public static ListSelectionListener selectClickListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent selectEvent) {
            correctionsListModel.clear();
                if (!errorsList.getValueIsAdjusting() && !errorsList.isSelectionEmpty()) {
                    List selectionValues = errorsList.getSelectedValuesList();
                    String selection = (String) selectionValues.get(0);
                    String[] nearWords = closeMatches(selection);
                    if (!arrayIsEmpty(nearWords)) {
                        for (int i = 0; i < nearWords.length; i++) {
                            correctionsListModel.addElement(nearWords[i]);
                        }
                    }
            }
        }
    };
    
        
    
    /**
     * Returns the nearest valid dictionary words to a given word, by
     * calculating the Levenshtein distance between the given word and all
     * valid ones, and returning an array of tiered sizes, based on the length of
     * the given word. The returned array is ordered by closeness of match, and
     * contains more close matches than distant ones. It also checks for more matches
     * with larger Levenshtein distances for long given words than for short ones.
     * @param wordToMatch the word to find similar valid words for.
     * @return an array of valid dictionary words close to the given one, ordered
     *      by relevance and in order of increasing Levenshtein distance.
     */
    public static String[] closeMatches(String wordToMatch) {
        String[] dictionary = VALUES.toArray(new String[VALUES.size()]);
        
        int count = 0;
        int countOne = 0;
        int countTwo = 0;
        int countThree = 0;
        String[] nearestMatches = new String[15];
        String[] nearestMatchesTwo = new String[10];
        String[] nearestMatchesThree = new String[5];
        for (int i = 0; i < dictionary.length; i++) {
            if (count < 30 && StringUtils.getLevenshteinDistance(wordToMatch,
                    dictionary[i]) == 1 && countOne < 15) {
                nearestMatches[countOne] = dictionary[i];
                countOne++;
                count++;
            } else if (count < 30 && StringUtils.getLevenshteinDistance(wordToMatch,
                            dictionary[i]) == 2 && countTwo < 10) {
                nearestMatchesTwo[countTwo] = dictionary[i];
                countTwo++;
                count++;
            } else if (count < 30 && StringUtils.getLevenshteinDistance(wordToMatch,
                            dictionary[i]) == 3 && countThree < 5) {
                nearestMatchesThree[countThree] = dictionary[i];
                countThree++;
                count++;
            }
        }
        
        String[] moreMatches = ArrayUtils.addAll(nearestMatches,nearestMatchesTwo);
        String[] evenMoreMatches = ArrayUtils.addAll(moreMatches,nearestMatchesThree);
        
        
        if (wordToMatch.length() > 5) {
            return removeNullVals(moreMatches);
        }
        if (wordToMatch.length() > 9) {
            return removeNullVals(evenMoreMatches);
        }
        
        return removeNullVals(nearestMatches);
    }
    
    /**
     * Removes the null values from an array of Strings and returns the sanitized 
     * array.
     * @param arrayToCheck the array being sanitized.
     * @return the input array with all null values removed.
     */
    public static String[] removeNullVals(final String[] arrayToCheck) {
        int checkIndex;
        int firstIndex;
        final int lastIndex = checkIndex = firstIndex = arrayToCheck.length;
        while (checkIndex > 0) {
            final String checkingString = arrayToCheck[--checkIndex];
            if (checkingString != null) {
                arrayToCheck[--firstIndex] = checkingString;
            }
        }
        return Arrays.copyOfRange(arrayToCheck, firstIndex, lastIndex);
    }

    /**
     * Helper method that checks if an array of Strings contains nothing but
     * null references or empty strings.
     * @param arrayToCheck the array of Strings to check
     * @return returns true if the array is empty
     */
    public static boolean arrayIsEmpty(String[] arrayToCheck) {
        for (int i = 0; i < arrayToCheck.length; i++) {
            if (!(arrayToCheck[i] == null || arrayToCheck[i] == "")) {
                return false;
            }
        }
        return true;
    }

}
