package quickCheck;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;


public class QuickCheck {
    private static final int WINDOW_WIDTH = 480;
    private static final int WINDOW_HEIGHT = 540;
    private static final Color LEFT_COLOR = Color.WHITE;
    private static final Color RIGHT_COLOR = new Color(126, 205, 205);
    private static final Color TOP_COLOR = new Color(80, 166, 166);
    
    private static final JTextArea text = new JTextArea();
    private static final JTextArea errorText = new JTextArea(10,20);

    
    private static JLabel upperLabel = new JLabel("quickCheck Your Writing");
    private static final String DICTIONARY_FILE_NAME ="words.txt";

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("quickCheck");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        /*Required to instantiate an inner class*/
        QuickCheck thisProgram = new QuickCheck();
        QuickCheck.myPane lowerPanel = thisProgram.new myPane();
        
        //Sets up upper panel
        JPanel upperPanel = new JPanel();
        upperPanel.setBackground(TOP_COLOR);
        upperPanel.setPreferredSize(new Dimension(WINDOW_WIDTH,30));
        upperPanel.add(upperLabel);
        
        frame.getContentPane().add(upperPanel, "North");
        frame.getContentPane().add(lowerPanel, "South");



        //Creates the textfield on the left side
        text.setCaretPosition(0);
        lowerPanel.getLeft().add(text);

        //Adds textfield listener
        QuickCheck.typeListener lstn = thisProgram.new typeListener(lowerPanel);
        text.getDocument().addDocumentListener(lstn);
        text.getDocument().putProperty("name", "Text Field");

        //Creates errortext
        lowerPanel.getRight().setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        
        //errortext//new JList( wordsInError(text.getText()));
        
        //JScrollPane scrollPane = new JScrollPane( errortext );
        lowerPanel.getRight().add(errorText);
        
        
        /*
        textFieldVector.add(tf);
        myPanel.add(tf);
        myPanel.repaint;
        */
        
        //lowerPanel.add(startBtn);
        //lowerPanel.add(stopBtn);
        //lowerPanel.add(resetBtn);

        
        frame.pack();
        frame.setVisible(true);
    }
    
    public class myPane extends JPanel{
        private JPanel leftSide;
        private JPanel rightSide;
        
        public myPane() {
            setLayout(new GridLayout(0, 2));
            leftSide = createLeftPane();
            rightSide = createRightPane();
            
            add(leftSide);
            add(rightSide);
        }
     
        protected JPanel createLeftPane() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setBackground(LEFT_COLOR);
            
            return panel;
        }
        
        protected JPanel createRightPane() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.setBackground(RIGHT_COLOR);
            panel.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
            
            
            return panel;
        }
        
        public JPanel getLeft() {
            return leftSide;
        }
        public JPanel getRight() {
            return rightSide;
        }
        
    }
    //sets the font to a specific one
    public static void setFont(String fontToSetTo) {
        Font myFont = new Font(fontToSetTo, Font.PLAIN, 18);
        text.setFont(myFont);
    }
    
    class typeListener implements DocumentListener {
        
        private myPane panelPassedToSpellCheck;
        
        typeListener(myPane panelToChange){
            super();
            panelPassedToSpellCheck = panelToChange;
        }
        public void changedUpdate(DocumentEvent arg0) {
            
        }

        public void insertUpdate(DocumentEvent arg0) {
            try {
                spellCheck(panelPassedToSpellCheck);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }

        public void removeUpdate(DocumentEvent arg0) {
            try {
                spellCheck(panelPassedToSpellCheck);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
    
    public static void spellCheck(myPane panelSwap) throws FileNotFoundException{
        //Reads user input as a string
        String textToCheck = text.getText();
   
        //Add the words to a list of wrong words
        String[] wrongWords = wordsInError(textToCheck);

        //Display that list on the right side of the document
        String wrongAppends = "";
        for (int i = 0; i<wrongWords.length; i++) {
            if (wrongWords[i]!=null)
                wrongAppends+=wrongWords[i]+"\n";
        }
        errorText.setText(wrongAppends);
    }
    
    public static String[] wordsInError(String wordsToCheck) throws FileNotFoundException {
        if (wordsToCheck.length()==0) {
            String[] empty =new String[0];
            return empty;
        }
        //formats incoming string to an array of all words
        String[] words = wordsToCheck.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        int errCount = 0;
        
        String[] wrongWords = new String [wordsToCheck.length()];
        
        for (int i=0; i<words.length; i++) {
            if (!QuickCheck.isValidWord(words[i])) {
                wrongWords[errCount] = words[i];
                errCount++;
            }
        }
        
        return wrongWords;
    }    
    
    public static boolean isValidWord(String wordToCheck) throws FileNotFoundException{
     
        File tmpFile = new File("words.txt");
        
        String a = null;
        try {
            a=FileUtils.readFileToString(tmpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a.contains("\n"+wordToCheck+"\n");
        }
    

}
