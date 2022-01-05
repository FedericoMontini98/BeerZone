package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.StandardUser;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BorderFactory.createEmptyBorder;

public class StandardUserGUI {
    private static final Integer STANDARD_USER = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);

    /**
     * function that creates the user section
     *
     * @param frame: frame used by the application
     * @param s: brewery informations
     */
    public static void standardUserSection(JFrame frame, StandardUser s){
        JButton[] btnArray = new JButton[4];
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        ljp.setLayout(new GridBagLayout());
        rjp.setLayout(new GridBagLayout());

        btnArray[0] = new JButton("Browse Favorites");
        btnArray[0].addActionListener(e -> browseUserFavorites(rjp, frame, s));

        btnArray[1] = new JButton("View Suggestions");
        btnArray[1].addActionListener(e -> userSuggestions(rjp, frame, s));

        btnArray[2] = new JButton("Browse Beer");
        btnArray[2].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, STANDARD_USER, s.getUserID(), s.getUsername()));

        btnArray[3] = new JButton("Logout");
        btnArray[3].addActionListener(e -> BeerZoneGUI.prepareLogRegister(frame));

        setLeftStandardUserButton(btnArray, ljp);
        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    /**
     * @param rjp:
     * @param frame:
     * @param s: brewery informations
     */
    private static void browseUserFavorites(JPanel rjp, JFrame frame, StandardUser s) {
        rjp.removeAll();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param rjp:
     * @param frame:
     * @param s: brewery informations
     */
    private static void userSuggestions(JPanel rjp, JFrame frame, StandardUser s) {
        rjp.removeAll();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the buttons inside the standard user area
     *
     * @param btnArray: array containing the buttons inside the user section area
     * @param jp: JPanel that contains the buttons in btnArray
     */
    private static void setLeftStandardUserButton(JButton[] btnArray, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),25,30);
        jp.add(btnArray[0], gbc);
        gbc.ipadx = 22;
        gbc.gridy = 1;
        jp.add(btnArray[1], gbc);
        gbc.ipadx = 50;
        gbc.gridy = 2;
        jp.add(btnArray[2], gbc);
        gbc = new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),85,30);
        jp.add(btnArray[3], gbc);
    }

    /**
     * function that creates "add to favorites", "Review Beer" and "Go Back" buttons
     *
     * @param frame:
     * @param containerPanel : panel containing the btnPanel
     * @param userType:
     * @param userId:
     * @param selBeer: beer selected by the user
     */
    public static void createButtonFunctionalities(JFrame frame, JPanel containerPanel, Integer userType, String userId, DetailedBeer selBeer, String username) {
        JButton addFav = new JButton("Add to Favorites");
        JButton reviewBeer = new JButton("Review Beer");
        JPanel btnPanel = new JPanel();

        addFav.addActionListener(e -> {
            //add to favorites
        });
        btnPanel.add(addFav, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        reviewBeer.addActionListener(e -> reviewBeerPage(frame, containerPanel, userId, selBeer, username));
        reviewBeer.setPreferredSize(new Dimension(130,26));
        btnPanel.add(reviewBeer, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        btnPanel.setBackground(BACKGROUND_COLOR);

        containerPanel.add(btnPanel, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));

        prepareReturnToBrowseButton(containerPanel, frame, userId, username);
    }

    /**
     * function that creates a button that allows the user to return to the browse beer section
     *  @param jp : JPanel that contains the review page
     * @param frame : frame used by the application
     * @param userId: id of the user
     */
    private static void prepareReturnToBrowseButton(JPanel jp, JFrame frame, String userId, String username) {
        JButton returnToBrowse = new JButton("Go Back");
        returnToBrowse.addActionListener(e ->{
            BeerZoneGUI.generateBrowseBeerMenu(jp, frame, STANDARD_USER, userId, username);
        });

        jp.add(returnToBrowse, new GridBagConstraints(0,6,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0, 0));
    }

    /**
     * function that creates the section that allows the user to review a beer
     *
     * @param frame: frame used by the application
     * @param rjp: JPanel that contains the review page
     * @param selBeer: beer selected by the user
     */
    private static void reviewBeerPage(JFrame frame, JPanel rjp, String userId, DetailedBeer selBeer, String username) {
        rjp.removeAll();
        JTextField reviewAvg = new JTextField("3.0");
        JTextArea textArea = new JTextArea(5, 30);
        JSpinner[] spinners = new JSpinner[4];

        prepareAverageSection(reviewAvg, rjp);
        prepareVotesPanel(reviewAvg, spinners, rjp);
        prepareTextReviewArea(rjp, textArea);
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(createEmptyBorder());
        btnPanel.setBackground(BACKGROUND_COLOR);
        prepareReturnToBeerButton(rjp, btnPanel, frame, userId, selBeer, username);
        prepareSubmitReviewButton(btnPanel, textArea, spinners, reviewAvg, selBeer, username);
        rjp.add(btnPanel, new GridBagConstraints(0,4,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0),0, 0));

        frame.repaint();
        frame.setVisible(true);
    }

    private static void prepareReturnToBeerButton(JPanel rjp, JPanel btnPanel, JFrame frame, String userId, DetailedBeer selBeer, String username) {
        JButton returnToBeer = new JButton("Go Back");
        returnToBeer.setFont(new Font("Arial", Font.PLAIN, 16));
        returnToBeer.addActionListener(e -> BeerZoneGUI.createBeerPage(rjp, frame, STANDARD_USER, userId, selBeer, username));

        btnPanel.add(returnToBeer, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 0));
    }

    /**
     * function that prepares button that submits the review
     * @param btnPanel : JPanel that contains the review page
     * @param textArea : area where the user can insert the review
     * @param spinners : list of spinners to compute the average value
     * @param reviewAvg : field with the average vote
     * @param
     * @param selBeer : beer selected by the user
     */
    private static void prepareSubmitReviewButton(JPanel btnPanel, JTextArea textArea, JSpinner[] spinners, JTextField reviewAvg, DetailedBeer selBeer, String username) {
        JButton subReviewBtn = new JButton("Submit");
        subReviewBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        subReviewBtn.addActionListener(e -> {
            double avg = Double.parseDouble(reviewAvg.getText());
            Double[] values = new Double[4];
            for(int i = 0; i < spinners.length; i++)
                values[i] = (Double)spinners[i].getValue();
            String text = textArea.getText();
            System.out.println("username: " + username + "\n"+
                    "beerID: " + selBeer.getBeerID() + "\n" +
                    "avg: " + avg + "\n" +
                    "value[1]: " +  values[0] + "\n" +
                    "value[2]: " +  values[1] + "\n" +
                    "value[3]: " +  values[2] + "\n" +
                    "value[4]: " +  values[3] + "\n" +
                    "text: " + text);
        });

        btnPanel.add(subReviewBtn, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 0));
    }

    /**
     * function that prepares the area where the user can write the review
     *
     * @param rjp: JPanel that contains the review page
     * @param textArea: area where the user can insert the review
     */
    private static void prepareTextReviewArea(JPanel rjp, JTextArea textArea) {
        textArea.setFont(new Font("Arial", Font.BOLD, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JTextField description = new JTextField("Insert here the text of your review");
        description.setEditable(false);
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setFont(new Font("Arial", Font.BOLD, 18));
        rjp.add(description, new GridBagConstraints(0,2,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(30, 0, 10, 0),0, 0));

        rjp.add(scrollPane, new GridBagConstraints(0,3,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 0));
    }

    /**
     * function that prepares the area where the user can insert the numeric reviews
     *
     * @param reviewAvg: field with the average vote
     * @param spinners: list of spinners to compute the average value
     * @param rjp: JPanel that contains the review page
     */
    private static void prepareVotesPanel(JTextField reviewAvg, JSpinner[] spinners, JPanel rjp) {

        JPanel votesPanel = new JPanel();
        votesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        votesPanel.setBackground(BACKGROUND_COLOR_RECIPE);
        votesPanel.setLayout(new GridBagLayout());
        createBeerReviewFields("Look", votesPanel, 0, 0, reviewAvg, spinners);
        createBeerReviewFields("Smell", votesPanel, 0, 1, reviewAvg, spinners);
        createBeerReviewFields("Taste", votesPanel, 1, 0, reviewAvg, spinners);
        createBeerReviewFields("Feel", votesPanel, 1, 1, reviewAvg, spinners);

        rjp.add(votesPanel, new GridBagConstraints(0,1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),20, 20));
    }

    /**
     * function that prepares the section that contains the average review vote
     *
     * @param reviewAvg: field with the average vote
     * @param rjp: JPanel that contains the review page
     */
    private static void prepareAverageSection(JTextField reviewAvg, JPanel rjp) {
        reviewAvg.setEditable(false);
        reviewAvg.setHorizontalAlignment(JTextField.CENTER);
        reviewAvg.setFont(new Font("Arial", Font.BOLD, 15));
        rjp.add(reviewAvg, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 80),40, 5));

        JTextField description = new JTextField("Average review score");
        description.setFont(new Font("Arial", Font.BOLD, 18));
        description.setEditable(false);
        description.setBorder(createEmptyBorder());
        description.setBackground(BACKGROUND_COLOR);
        rjp.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 80, 10, 0),40, 5));

    }

    /**
     * function that creates the spinners that contain the user vote
     *
     * @param reviewType: review type
     * @param rjp: JPanel that contains the review page
     * @param row: row where to put the specific spinner
     * @param reviewAvg: field with the average vote
     * @param spinners: list of spinners to compute the average value
     */
    private static void createBeerReviewFields(String reviewType, JPanel rjp, int row, int col, JTextField reviewAvg, JSpinner[] spinners) {
        JTextField description = new JTextField(reviewType);
        description.setEditable(false);
        description.setBackground(BACKGROUND_COLOR_RECIPE);
        description.setBorder(createEmptyBorder());
        description.setFont(new Font("Arial", Font.BOLD, 15));
        GridBagConstraints gbc = new GridBagConstraints(2*col,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),0, 5);

        if(col == 1){
            gbc.insets.left = 70;
            if(row == 1)
                gbc.insets.bottom = 0;
        }
        if(col == 0)
            gbc.insets.bottom = 0;
        rjp.add(description, gbc);

        SpinnerModel spinnerModel = new SpinnerNumberModel(3, 0, 5, 0.25);
        JSpinner spinner = new JSpinner();
        spinner.setModel(spinnerModel);
        spinner.setFont(new Font("Arial", Font.PLAIN, 15));
        spinners[row*2 + col] = spinner;
        spinner.addChangeListener(e -> {
            double currTot = 0.0;
            for (JSpinner jSpinner : spinners) currTot = currTot + (Double) jSpinner.getValue();

            currTot = currTot/4;
            reviewAvg.setText(String.valueOf(currTot));
        });
        if(row != 1)
            rjp.add(spinner, new GridBagConstraints(2*col + 1,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 10, 0),20,5));
        else
            rjp.add(spinner, new GridBagConstraints(2*col + 1,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 0),20,5));

    }

}
