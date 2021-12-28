package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.util.Arrays;

import static javax.swing.BorderFactory.createEmptyBorder;

public class StandardUserGUI {
    private static final Integer STANDARD_USER = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);

    /**
     * function used to create the button that allows to login as a standard user
     *
     * @param frame: frame used by the application
     */
    public static void setStandardUserButton(JFrame frame){
        JButton btn = new JButton("Login as Standard User");
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 40, 0),10,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            BeerZoneGUI.prepareLoginSection(frame, STANDARD_USER);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * function that creates the user section
     *
     * @param frame: frame used by the application
     * @param inputData: data inserted by the user
     */
    public static void standardUserSection(JFrame frame, String[] inputData){
        JButton[] btnArray = new JButton[4];
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        ljp.setLayout(new GridBagLayout());
        rjp.setLayout(new GridBagLayout());

        btnArray[0] = new JButton("Browse Favorites");
        btnArray[0].addActionListener(e -> browseUserFavorites(rjp, frame, inputData));

        btnArray[1] = new JButton("View Suggestions");
        btnArray[1].addActionListener(e -> userSuggestions(rjp, frame, inputData));

        btnArray[2] = new JButton("Browse Beer");
        btnArray[2].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, STANDARD_USER, inputData));

        btnArray[3] = new JButton("Logout");
        btnArray[3].addActionListener(e -> {
            Arrays.fill(inputData, null); BeerZoneGUI.prepareLogRegister(frame);});

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
     * @param inputData:
     */
    private static void browseUserFavorites(JPanel rjp, JFrame frame, String[] inputData) {
        rjp.removeAll();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param rjp:
     * @param frame:
     * @param inputData:
     */
    private static void userSuggestions(JPanel rjp, JFrame frame, String[] inputData) {
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
     * function that creates "add to favorites" and "Review Beer" buttons
     *
     * @param containerPanel : panel containing the btnPanel
     * @param inputData: data inserted by the user
     */
    public static void createButtonFunctionalities(JFrame frame, JPanel containerPanel, String[] inputData, int beerID) {
        JButton addFav = new JButton("Add to Favorites");
        JButton reviewBeer = new JButton("Review Beer");
        JPanel btnPanel = new JPanel();

        addFav.addActionListener(e -> {
            //add to favorites
        });
        btnPanel.add(addFav, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        reviewBeer.addActionListener(e -> {
            reviewBeerPage(frame, containerPanel, inputData, beerID);
        });
        reviewBeer.setPreferredSize(new Dimension(130,26));
        btnPanel.add(reviewBeer, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        btnPanel.setBackground(BACKGROUND_COLOR);

        containerPanel.add(btnPanel, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
    }

    private static void reviewBeerPage(JFrame frame, JPanel rjp, String[] inputData, int beerID) {
        rjp.removeAll();
        JTextField reviewAvg = new JTextField("3.0");
        reviewAvg.setHorizontalAlignment(JTextField.CENTER);
        reviewAvg.setFont(new Font("Arial", Font.BOLD, 15));
        JSpinner[] spinners = new JSpinner[4];
        rjp.add(reviewAvg, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),40, 5));
        JPanel votesPanel = new JPanel();
        votesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        votesPanel.setBackground(BACKGROUND_COLOR_RECIPE);
        votesPanel.setLayout(new GridBagLayout());
        createBeerReviewFields("Look", votesPanel, 0, reviewAvg, spinners);
        createBeerReviewFields("Smell", votesPanel, 1, reviewAvg, spinners);
        createBeerReviewFields("Taste", votesPanel, 2, reviewAvg, spinners);
        createBeerReviewFields("Feel", votesPanel, 3, reviewAvg, spinners);

        rjp.add(votesPanel, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),20, 20));

        frame.repaint();
        frame.setVisible(true);
    }

    private static void createBeerReviewFields(String reviewType, JPanel rjp, int row, JTextField reviewAvg, JSpinner[] spinners) {
        JTextField description = new JTextField(reviewType);
        description.setEditable(false);
        description.setBackground(BACKGROUND_COLOR_RECIPE);
        description.setBorder(createEmptyBorder());
        description.setFont(new Font("Arial", Font.BOLD, 15));
        if(row != 3)
            rjp.add(description, new GridBagConstraints(0,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),0, 5));
        else
            rjp.add(description, new GridBagConstraints(0,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0, 5));

        SpinnerModel spinnerModel = new SpinnerNumberModel(3, 0, 5, 0.25);
        JSpinner spinner = new JSpinner();
        spinner.setModel(spinnerModel);
        spinner.setFont(new Font("Arial", Font.PLAIN, 15));
        spinners[row] = spinner;
        spinner.addChangeListener(e -> {
            Double currTot = 0.0;
            for(int i = 0; i < spinners.length; i++)
                currTot = currTot + (Double)spinners[i].getValue();

            currTot = currTot/4;
            reviewAvg.setText(currTot.toString());
        });
        if(row != 3)
            rjp.add(spinner, new GridBagConstraints(1,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 10, 0),20,5));
        else
            rjp.add(spinner, new GridBagConstraints(1,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 0),20,5));

    }
}
