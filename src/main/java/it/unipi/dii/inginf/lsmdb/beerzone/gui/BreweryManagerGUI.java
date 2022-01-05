package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.Brewery;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.DetailedBeer;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.GeneralUser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BreweryManagerGUI {
    private static final Integer BREWERY_MANAGER = 1;
    private static final Integer USERNAME_ROW = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);
    private static final Integer RECIPE_SECTION_ABV = 1;
    private static final Integer RECIPE_SECTION_AVAILABILITY = 2;
    private static final Integer RECIPE_SECTION_COLOR = 3;
    private static final Integer RECIPE_SECTION_FERMENTABLES = 4;
    private static final Integer RECIPE_SECTION_FG = 5;
    private static final Integer RECIPE_SECTION_HOPS= 6;
    private static final Integer RECIPE_SECTION_IBU = 7;
    private static final Integer RECIPE_SECTION_METHOD = 8;
    private static final Integer RECIPE_SECTION_NOTES = 9;
    private static final Integer RECIPE_SECTION_OG = 10;
    private static final Integer RECIPE_SECTION_OTHER = 11;
    private static final Integer RECIPE_SECTION_PHMASH = 12;
    private static final Integer RECIPE_SECTION_STYLE = 13;
    private static final Integer RECIPE_SECTION_URL = 14;
    private static final Integer RECIPE_SECTION_YEAST = 15;


    /**
     * function that creates the brewery manager section
     *
     * @param frame: frame used by the application
     * @param b: brewery informations
     */
    public static void breweryManagerSection(JFrame frame, Brewery b){
        JButton[] btnArray = new JButton[5];
        frame.setTitle("BeerZone - BREWERY MANAGER");
        frame.setLayout(new GridLayout(1,2));

        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        rjp.setLayout(new GridBagLayout());
        ljp.setLayout(new GridBagLayout());
        btnArray[0] = new JButton("Brewery Page");
        btnArray[0].addActionListener(e -> createBreweryPage(rjp, frame, BREWERY_MANAGER, b.getUserID(), b.getUserID()));
        btnArray[1] = new JButton("Add beer");
        btnArray[1].addActionListener(e -> generateAddBeerMenu(rjp, frame, b));
        btnArray[2] = new JButton("Browse Beer");
        btnArray[2].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, BREWERY_MANAGER, b.getUserID(), b.getUsername()));
        btnArray[3] = new JButton("Extract Brewery Statistics");
        btnArray[3].addActionListener(e -> generateBreweryStatisticsMenu(rjp, frame, b));
        btnArray[4] = new JButton("Logout");
        btnArray[4].addActionListener(e -> BeerZoneGUI.prepareLogRegister(frame));
        setLeftBreweryManagerButton(btnArray, ljp);
        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    /**
     * function that creates the "add beer" section
     *
     * @param containerPanel: panel containing the add beer section
     * @param frame: frame used by the application
     * @param b: brewery informations
     */
    private static void generateAddBeerMenu(JPanel containerPanel, JFrame frame, Brewery b) {
        containerPanel.removeAll();
        JTextField[] input = new JTextField[1];
        String[] recipeTexts = new String[16];
        createInputField(containerPanel, input);
        JComboBox<String> cb = createInputStyle(containerPanel);
        BeerZoneGUI.createRecipeSection(containerPanel, 3, recipeTexts, BREWERY_MANAGER);
        JButton btn = new JButton("Add Beer to Brewery");
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.addActionListener(e->{
            boolean recipeCorrect = checkRecipe(recipeTexts);
            boolean infoCorrect = checkInfo(input, cb);
            if(!recipeCorrect){
                System.out.println("OG - FG - IBU - COLOR - PHMASH must be numbers");
            }
            else{
                if(!infoCorrect)
                    System.out.println("Input not correct");
                else {
                    System.out.println("Correct");
                    DetailedBeer db = new DetailedBeer();
                }
            }
        });
        containerPanel.add(btn, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private static boolean checkInfo(JTextField[] input, JComboBox<String> cb) {
        boolean correct = true;
        if(input[0].getText().equals("")) {
            input[0].setBackground(Color.RED);
            correct = false;
        }
        else
            input[0].setBackground(Color.WHITE);

        if(cb.getSelectedIndex() == 0) {
            cb.setBackground(Color.RED);
            correct = false;
        }
        else
            cb.setBackground(Color.WHITE);

        return correct;
    }

    private static boolean checkRecipe(String[] recipeTexts) {
        boolean correct = true;
        for(int i = 0; i < recipeTexts.length; i++){
            if(recipeTexts[i] == null)
                continue;

            if(i == RECIPE_SECTION_OG || i == RECIPE_SECTION_FG || i == RECIPE_SECTION_IBU || i == RECIPE_SECTION_COLOR
                    || i == RECIPE_SECTION_PHMASH) {
                try {
                    Double.parseDouble(recipeTexts[i]);
                } catch (NumberFormatException nfe) {
                    correct = false;
                }
            }
        }

        return correct;
    }

    /**
     * function that creates the input field in the "add beer" section
     *
     * @param panel: panel containing the add beer section
     * @param input: brewery manager's input
     */
    private static void createInputField(JPanel panel, JTextField[] input) {
        JTextField description = new JTextField("Beer Name");
        description.setFont(new Font("Arial", Font.PLAIN ,15));
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 10),0,0));

        JTextField inputField = new JTextField();
        input[0] = inputField;
        panel.add(inputField, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),180,5));
    }

    /**
     * function creating the combo box containing the beer styles
     *
     * @param containerPanel: panel containing the "add beer" section
     * @return cb: combo box containing the possible style choices
     */
    private static JComboBox<String> createInputStyle(JPanel containerPanel) {
        GridBagConstraints gbc = new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 30, 0, 20),0,0);
        JTextField description = new JTextField("Style");
        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        containerPanel.add(description, gbc);
        String[] choices = {"Choose an option", "Option 1", "Option 2", "..."};
        final JComboBox<String> cb = new JComboBox<>(choices);
        cb.setVisible(true);
        gbc.insets = new Insets(20, 0, 0, 20);
        gbc.gridx = 1;
        containerPanel.add(cb, gbc);
        return cb;
    }

    /**
     * function that creates the buttons inside the brewery manager area
     *
     * @param btnArray: array containing the buttons inside the brewery manager area
     * @param jp: JPanel that contains the buttons in btnArray
     */
    private static void setLeftBreweryManagerButton(JButton[] btnArray, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),70,30);
        jp.add(btnArray[0], gbc);
        gbc.gridy = 1;
        gbc.ipadx = 99;
        jp.add(btnArray[1], gbc);
        gbc.ipadx = 75;
        gbc.gridy = 2;
        jp.add(btnArray[2], gbc);
        gbc.gridy = 3;
        gbc.ipadx = 3;
        jp.add(btnArray[3], gbc);
        gbc = new GridBagConstraints(0,4,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),110,30);
        jp.add(btnArray[4], gbc);
    }

    /**
     * function that creates the brewery statistics section
     *
     * @param containerPanel: panel containing the "brewery statistics" section
     * @param frame: frame used by the application
     * @param b: brewery informations
     */
    private static void generateBreweryStatisticsMenu(JPanel containerPanel, JFrame frame, Brewery b) {
        containerPanel.removeAll();
        Object[][] data = {{"Look", "--"}, {"Smell", "--"}, {"Taste", "--"}, {"Feel", "--"}};
        String[] colHeader = {"Feature", "Score"};

        JTextField breweryStatsTitle = new JTextField(b.getUsername());
        prepareBreweryStatsTitle(breweryStatsTitle, containerPanel);

        JTextField responseField = new JTextField("Not yet computed");
        prepareResponseField(responseField, containerPanel);

        JButton breweryStatsBtn = new JButton("Compute brewery score");
        prepareBreweryStatsBtn(breweryStatsBtn, containerPanel, responseField);

        JTable breweryStatsTable = new JTable(data, colHeader);
        prepareBreweryStatsTable(breweryStatsTable, containerPanel);

        breweryStatsBtn = new JButton("Compute average score for features");
        setBreweryStatsBtn(breweryStatsBtn, breweryStatsTable, containerPanel);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that set the parameters of the title of the brewery statistics section
     *
     * @param breweryStatsTitle: JTextField containing the name of the brewery
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareBreweryStatsTitle(JTextField breweryStatsTitle, JPanel containerPanel) {
        breweryStatsTitle.setFont(new Font("Arial", Font.BOLD, 25));
        breweryStatsTitle.setBackground(BACKGROUND_COLOR);
        breweryStatsTitle.setBorder(createEmptyBorder());
        containerPanel.add(breweryStatsTitle, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 80, 0),0,0));
    }

    /**
     * function that set the parameters of the brewery's average score section
     *
     * @param responseField: JTextField where the average score of the brewery will be written
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareResponseField(JTextField responseField, JPanel containerPanel) {
        responseField.setHorizontalAlignment(JTextField.CENTER);
        responseField.setBackground(BACKGROUND_COLOR);
        responseField.setBorder(createEmptyBorder());
        containerPanel.add(responseField, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 60, 0),0,0));
    }

    /**
     * function that set the parameters of the brewery's average score button
     *
     * @param breweryStatsBtn: button that allows the user to compute the brewery's average score
     * @param containerPanel: panel containing the "brewery statistics" section
     * @param responseField: field where the average score will be written
     */
    private static void prepareBreweryStatsBtn(JButton breweryStatsBtn, JPanel containerPanel, JTextField responseField) {
        containerPanel.add(breweryStatsBtn, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 60, 0),0,0));
        breweryStatsBtn.addActionListener(e -> responseField.setText("4.5/5"));
    }

    /**
     * function that prepares the table of the average score for all the possible vote's options
     *
     * @param breweryStatsTable: table where the vote's options will be written
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void prepareBreweryStatsTable(JTable breweryStatsTable, JPanel containerPanel) {
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        String[] col1 = {"Look", "Smell", "Taste", "Feel"};
        String[] col2 = {"--", "--", "--", "--"};
        tableModel.addColumn("Feature", col1);
        tableModel.addColumn("Score", col2);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        breweryStatsTable.setModel(tableModel);
        breweryStatsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        breweryStatsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane jsc = new JScrollPane(breweryStatsTable);
        jsc.setPreferredSize(new Dimension(300, 87));
        containerPanel.add(jsc, new GridBagConstraints(0,3,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
    }

    /**
     * function that prepares the button that allows the brewery to compute all the statistics
     *
     * @param breweryStatsBtn: button that allows the brewery to compute all the statistics
     * @param breweryStatsTable: table where the vote's options will be written
     * @param containerPanel: panel containing the "brewery statistics" section
     */
    private static void setBreweryStatsBtn(JButton breweryStatsBtn, JTable breweryStatsTable, JPanel containerPanel) {
        breweryStatsBtn.addActionListener(e->{
            breweryStatsTable.getModel().setValueAt("4",0,1);
            breweryStatsTable.getModel().setValueAt("5",1,1);
            breweryStatsTable.getModel().setValueAt("2",2,1);
            breweryStatsTable.getModel().setValueAt("3",3,1);
        });

        containerPanel.add(breweryStatsBtn, new GridBagConstraints(0,2,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),0,0));
    }

    /**
     * function that creates a button that allows the user to return to the browse beer section
     * @param jp : JPanel that contains the review page
     * @param frame : frame used by the application
     * @param userType: user type
     * @param userId: id of the user
     */
    public static void prepareReturnToBrowseButton(JPanel jp, JFrame frame, Integer userType, String userId, String username) {
        JButton returnToBrowse = new JButton("Go Back");
        returnToBrowse.addActionListener(e ->{
            BeerZoneGUI.generateBrowseBeerMenu(jp, frame, userType, userId, username);
        });

        jp.add(returnToBrowse, new GridBagConstraints(0,6,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0, 0));
    }


    /**
     * @param containerPanel
     * @param frame
     * @param userId
     * @param breweryIdBeer
     */
    public static void createBreweryPage(JPanel containerPanel, JFrame frame,  Integer usertype, String userId, String breweryIdBeer) {
        containerPanel.removeAll();

        //search brewery by brewery id
        Brewery brewery = new Brewery("2", "brewery@brewery.com", "brewery", "brewery", "location", "pub");
        ArrayList<Integer> breweryBeers = new ArrayList<Integer>(Arrays.asList(1,2,39));
        brewery.setBeers(breweryBeers);
        JPanel jp = new JPanel(new GridBagLayout());
        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jp.setBackground(BACKGROUND_COLOR_RECIPE);

        addBreweryFields(jp,"Brewery Name", "brewery", Objects.equals(userId, breweryIdBeer), 0);
        addBreweryFields(jp,"Email", "brewery@brewery.com", Objects.equals(userId, breweryIdBeer), 1);
        addBreweryFields(jp,"Location", "location", Objects.equals(userId, breweryIdBeer), 2);
        addBreweryFields(jp, "Brewery Type", "pub", Objects.equals(userId, breweryIdBeer), 3);

        //get the beers associated with the brewery
        String[] beerId = {"1", "2", "3", "4"};
        String[] beerList = {"Beer1", "Beer2", "Beer3", "Beer5"};
        JComboBox<String> beerListCB = new JComboBox<>(beerList);
        beerListCB.setVisible(true);
        beerListCB.setPreferredSize(new Dimension(100, 30));
        containerPanel.add(beerListCB, new GridBagConstraints(0, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 30, 15, 0),0,0));

        JButton goToBeer = new JButton("Go To Beer");
        goToBeer.addActionListener(e ->{
            DetailedBeer selBeer = new DetailedBeer(beerId[beerListCB.getSelectedIndex()], "name", "style", "abv", "4.0", "brewery", "Availability", "Notes",
                "Url", "Retired", "Method", "10", "20", "30", "40", "52", "Fermentables",
                "Hops", "Other", "Yeast");
            //DetailedBeer selBeer = searchBeerById(beerId[beerListCB.getSelectedIndex()]);
            BeerZoneGUI.createBeerPage(containerPanel, frame, usertype, userId, selBeer, brewery.getUsername());
        });
        containerPanel.add(goToBeer,  new GridBagConstraints(1, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));
        containerPanel.add(jp, new GridBagConstraints(0, 0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 15, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private static void addBreweryFields(JPanel containerPanel, String description, String breweryInfo, boolean editable, int row) {
        JTextField desc = new JTextField(description);
        desc.setFont(new Font("Arial", Font.BOLD, 14));
        desc.setEditable(false);
        desc.setBackground(BACKGROUND_COLOR_RECIPE);
        desc.setBorder(createEmptyBorder());
        containerPanel.add(desc, new GridBagConstraints(0,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 0)?15:0, 25, 15, 0),0, 0));

        JTextField info = new JTextField(breweryInfo);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setEditable(editable);
        info.setBorder(createEmptyBorder());
        info.setBackground(BACKGROUND_COLOR_RECIPE);
        containerPanel.add(info, new GridBagConstraints(1,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 0)?15:0, 0, 15, 15),0, 0));
    }
}
