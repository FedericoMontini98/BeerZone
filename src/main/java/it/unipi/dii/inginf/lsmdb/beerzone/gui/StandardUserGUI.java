package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class StandardUserGUI {
    private static final Integer STANDARD_USER = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);

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
        btnArray[2].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, STANDARD_USER));

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
}
