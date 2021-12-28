package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BreweryManagerGUI {
    private static final Integer BREWERY_MANAGER = 1;
    private static final Integer USERNAME_ROW = 0;
    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    /**
     * function used to create the button that allows to login as a brewery manager
     *
     * @param frame: frame used by the application
     */
    public static void setBreweryManagerButton(JFrame frame){
        JButton btn = new JButton("Login as Brewery Manager");
        GridBagConstraints gbc = new GridBagConstraints(3,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 40, 0),10,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            BeerZoneGUI.prepareLoginSection(frame, BREWERY_MANAGER);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * function that creates the brewery manager section
     *
     * @param frame: frame used by the application
     * @param inputData: data inserted by the user
     */
    public static void breweryManagerSection(JFrame frame, String[] inputData){
        JButton[] btnArray = new JButton[4];
        frame.setTitle("BeerZone - BREWERY MANAGER");
        frame.setLayout(new GridLayout(1,2));

        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        rjp.setLayout(new GridBagLayout());
        ljp.setLayout(new GridBagLayout());
        btnArray[0] = new JButton("Add beer");
        btnArray[0].addActionListener(e -> generateAddBeerMenu(rjp, frame, inputData));
        btnArray[1] = new JButton("Browse Beer");
        btnArray[1].addActionListener(e -> BeerZoneGUI.generateBrowseBeerMenu(rjp, frame, BREWERY_MANAGER));
        btnArray[2] = new JButton("Extract Brewery Statistics");
        btnArray[2].addActionListener(e -> generateBreweryStatisticsMenu(rjp, frame, inputData));
        btnArray[3] = new JButton("Logout");
        btnArray[3].addActionListener(e -> {
            Arrays.fill(inputData, null); BeerZoneGUI.prepareLogRegister(frame);});
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
     * @param inputData: data inserted by the user
     */
    private static void generateAddBeerMenu(JPanel containerPanel, JFrame frame, String[] inputData) {
        containerPanel.removeAll();
        JTextField[] inputs = new JTextField[5];
        createInputField(containerPanel, inputs);
        JComboBox<String> cb = createInputStyle(containerPanel);
        BeerZoneGUI.createRecipeSection(containerPanel, 3);
        JButton btn = new JButton("Add Beer to Brewery");
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.addActionListener(e->{

        });
        containerPanel.add(btn, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the input field in the "add beer" section
     *
     * @param panel: panel containing the add beer section
     * @param inputs: vector containing the brewery manager's inputs
     */
    private static void createInputField(JPanel panel, JTextField[] inputs) {
        JTextField description = new JTextField("Beer Name");
        description.setFont(new Font("Arial", Font.PLAIN ,15));
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 10),0,0));

        JTextField inputField = new JTextField();
        inputs[0] = inputField;
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
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),99,30);
        jp.add(btnArray[0], gbc);
        gbc.ipadx = 75;
        gbc.gridy = 1;
        jp.add(btnArray[1], gbc);
        gbc.gridy = 2;
        gbc.ipadx = 3;
        jp.add(btnArray[2], gbc);
        gbc = new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),110,30);
        jp.add(btnArray[3], gbc);
    }

    /**
     * function that creates the brewery statistics section
     *
     * @param containerPanel: panel containing the "brewery statistics" section
     * @param frame: frame used by the application
     * @param inputData: data inserted by the user
     */
    private static void generateBreweryStatisticsMenu(JPanel containerPanel, JFrame frame, String[] inputData) {
        containerPanel.removeAll();
        Object[][] data = {{"Look", "--"}, {"Smell", "--"}, {"Taste", "--"}, {"Feel", "--"}};
        String[] colHeader = {"Feature", "Score"};

        JTextField breweryStatsTitle = new JTextField(inputData[USERNAME_ROW]);
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
}
