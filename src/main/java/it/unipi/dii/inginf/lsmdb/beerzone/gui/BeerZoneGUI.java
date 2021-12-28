package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.BorderFactory.createEmptyBorder;

public class BeerZoneGUI {
    /*private final Integer USERTYPE_ROW = 0;
    private static final Integer USERNAME_ROW = 1;
    private final Integer EMAIL_ROW = 2;*/
    private static final Integer PASSWORD_ROW = 3;
    private static final Integer PASS_CONFIRMATION_ROW = 4;
    private static final Integer LOCATION_ROW = 5;

    private static final Integer STANDARD_USER = 0;
    private static final Integer BREWERY_MANAGER = 1;

    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);

    /**
     * function that creates the table that allows the user to search the beers
     *
     * @param containerPanel: panel containing the table
     * @param frame: frame used by the application
     * @param userType: type of user  that is using the application
     */
    public static void generateBrowseBeerMenu(JPanel containerPanel, JFrame frame, Integer userType, String[] inputData) {
        containerPanel.removeAll();
        if(userType == BREWERY_MANAGER) {
            JCheckBox ownBeers = new JCheckBox("Select Own Beers", false);
            ownBeers.setBackground(BACKGROUND_COLOR);
            ownBeers.addItemListener(itemEvent -> {
                boolean test = ownBeers.isSelected();
                //select the correct list
            });
            containerPanel.add(ownBeers, new GridBagConstraints(0, 0, 2, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
        }

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(createEmptyBorder());

        JTextField beerInput = new JTextField();
        beerInput.setFont(new Font("Arial", Font.PLAIN, 15));
        beerInput.setPreferredSize(new Dimension(200,30));
        searchPanel.add(beerInput, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton submitChoice = new JButton("Submit");
        submitChoice.addActionListener(e -> beerInput.setText(""));
        searchPanel.add(submitChoice, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        searchPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.add(searchPanel, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JTable browseTable = new JTable();
        prepareBrowseTable(browseTable, containerPanel, frame, userType, inputData);
        JScrollPane jsc = new JScrollPane(browseTable);
        containerPanel.add(jsc, new GridBagConstraints(0,1,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        addNavigationBar(containerPanel);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the navigation arrows
     *
     * @param containerPanel: panel containing the table
     */
    private static void addNavigationBar(JPanel containerPanel) {
        JTextField currPage = new JTextField("0");
        currPage.setBackground(BACKGROUND_COLOR);
        currPage.setEditable(false);
        currPage.setBorder(createEmptyBorder());
        currPage.setFont(new Font("Arial", Font.BOLD, 15));
        containerPanel.add(currPage, new GridBagConstraints(1,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 90, 0, 0),0,0));

        JButton leftArr = new JButton("<");
        leftArr.setEnabled(false);
        containerPanel.add(leftArr, new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 92, 0, 0),0,0));
        leftArr.addActionListener(e -> {
            Integer currNum = Integer.parseInt(currPage.getText());
            if(currNum == 1)
                leftArr.setEnabled(false);
            currNum = currNum - 1;
            currPage.setText(currNum.toString());
        });

        JButton rightArr = new JButton(">");
        containerPanel.add(rightArr, new GridBagConstraints(2,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0,0));
        rightArr.addActionListener(e -> {
            Integer currNum = Integer.parseInt(currPage.getText());
            if(currNum == 0)
                leftArr.setEnabled(true);
            currNum = currNum + 1;
            currPage.setText(currNum.toString());
        });
    }

    /**
     * function that creates the table inside the "browse beers" section
     * @param browseTable : table containing beers
     * @param containerPanel : panel containing browseTable
     * @param frame : frame used by the application
     * @param userType : type of user that is requesting the table
     * @param inputData
     */
    private static void prepareBrowseTable(JTable browseTable, JPanel containerPanel, JFrame frame, Integer userType, String[] inputData) {
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        tableModel.addColumn("BeerID");
        tableModel.addColumn("Beer Name");
        tableModel.addColumn("Style");
        tableModel.addColumn("Abv");
        tableModel.addColumn("Rate");
        browseTable.setModel(tableModel);
        tableModel.addRow(new String[]{"1", "a", "a", "5.25", "3"});
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        browseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableModel.addRow(new String[]{"2", "b", "b", "6", "4"});
        browseTable.setModel(tableModel);
        TableColumnModel tcm = browseTable.getColumnModel();
        browseTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        browseTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        browseTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        browseTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        tcm.removeColumn(tcm.getColumn(0));
        browseTable.setRowHeight(30);
        browseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    int id = Integer.parseInt(browseTable.getModel().getValueAt(browseTable.getSelectedRow(),0).toString());
                    //Beer b = selectBeer(browseTable.getValueAt(browseTable.getSelectedRow(),0));
                    //createBeerPage(b);
                    createBeerPage(containerPanel, frame, userType, inputData, id);
                }
            }
        });
    }

    /**
     * function that creates the page containing all the information of a specific beer
     * @param containerPanel: panel containing the beer information
     * @param frame: frame used by the application
     * @param userType: type of user that is requesting the table
     * @param inputData: data inserted by the user
     */
    private static void createBeerPage(JPanel containerPanel, JFrame frame, Integer userType, String[] inputData, int beerID) {
        containerPanel.removeAll();
        createBeerFields("Beer Name", containerPanel, 0, 0);
        createBeerFields("Style", containerPanel, 1, 0);
        createBeerFields("Rating", containerPanel, 2, 0);
        createBeerFields("Num. of Ratings", containerPanel, 3, 0);

        createRecipeSection(containerPanel, 4);

        if(userType == STANDARD_USER)
            StandardUserGUI.createButtonFunctionalities(frame, containerPanel, inputData, beerID);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the fields of the beer page
     *
     * @param fieldName : Name of the field
     * @param containerPanel : panal containing the field
     * @param row: GridBagConstraint gridy
     * @param column: GridBagConstraint gridx
     */
    private static void createBeerFields(String fieldName, JPanel containerPanel, int row, int column) {
        JTextField description = new JTextField(fieldName);
        description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        if(row != 4)
            containerPanel.add(description, new GridBagConstraints(column,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0),0,0));
        else
            containerPanel.add(description, new GridBagConstraints(column,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
    }

    /**
     * function that creates the recipe of a beer
     *
     * @param containerPanel: panel containing the beer information
     * @param panelRow: row where the panel will be located
     */
    public static void createRecipeSection(JPanel containerPanel, int panelRow) {
        JPanel recipePanel = new JPanel();
        recipePanel.setBackground(BACKGROUND_COLOR_RECIPE);
        recipePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 25, 0, 0),20,5);
        recipePanel.setLayout(new GridBagLayout());
        JTextField tf = new JTextField("Recipe Section");
        tf.setFont(new Font("Arial", Font.BOLD, 15));
        tf.setBackground(BACKGROUND_COLOR_RECIPE);
        tf.setEditable(false);
        tf.setBorder(createEmptyBorder());
        recipePanel.add(tf, gbc);
        String[] choices = {"Choose an option", "abv", "method", "bash", "og", "fg", "ibu", "color",
                                "ph mash", "fermentables", "hops", "other", "yeast"};
        final JComboBox<String> cb = new JComboBox<>(choices);
        cb.setVisible(true);
        gbc = new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),20,5);
        recipePanel.add(cb, gbc);
        JButton btn = new JButton();
        if(panelRow == 3)
            btn.setText("Confirm");
        else
            btn.setText("Show Section");
        gbc.gridy = 4;
        recipePanel.add(btn, gbc);
        JTextField rv = new JTextField();
        gbc = new GridBagConstraints(0,2,1,2,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),220,80);
        recipePanel.add(rv, gbc);
        gbc = new GridBagConstraints(0,panelRow,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),50,0);
        containerPanel.add(recipePanel, gbc);
    }

    /**
     * function that prepares the login section of the application
     *
     * @param frame: frame used by the application
     * @param userType: type of user (standard or brewery manager) that is requesting to log in
     */
    public static void prepareLoginSection(JFrame frame, Integer userType) {
        if(userType == STANDARD_USER)
            frame.setTitle("BeerZone - STANDARD USER LOGIN");
        else
            frame.setTitle("BeerZone - BREWERY MANAGER LOGIN");

        String[] inputData = new String[2];
        JTextField[] inputs = new JTextField[2];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.getContentPane().add(jp, gbc);
        jp.setLayout(new GridBagLayout());
        createLoginInputField(jp, inputs);
        JButton loginButton = new JButton("Login");
        createLoginButton(loginButton, inputs, inputData, userType, frame, jp);

        JButton returnButton = new JButton("Go Back");
        returnButton.addActionListener(e -> prepareLogRegister(frame));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,0,20,0);
        gbc.gridwidth = 1;
        jp.add(returnButton, gbc);
        frame.setVisible(true);
    }

    /**
     * functioon that creates the button used to log in
     *
     * @param loginButton: login button
     * @param inputs: array that contains the JTextField objects inside jp
     * @param inputData: array that will be filled with the values inside the JTextFields in the inputs array
     * @param userType: type of user that request to log in
     * @param frame: frame used by the application
     * @param jp: JPanel that contains the login components
     */
    private static void createLoginButton(JButton loginButton, JTextField[] inputs, String[] inputData, Integer userType, JFrame frame, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints();
        loginButton.addActionListener(e -> {
            Boolean correctData = readLoginInputs(inputs, inputData);
            if(correctData) {
                frame.getContentPane().removeAll();
                frame.repaint();
                //Data ready for being saved
                if(userType == BREWERY_MANAGER)
                    BreweryManagerGUI.breweryManagerSection(frame, inputData);
                else
                    StandardUserGUI.standardUserSection(frame, inputData);
            }
            else
                System.out.println("Missing or incorrect data");
        });
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.insets = new Insets(0,0,20,0);
        gbc.gridwidth = 1;
        jp.add(loginButton, gbc);
    }

    /**
     * function that creates the input section components
     *
     * @param jp: JPanel that contains the login components
     * @param inputs: array that contains the JTextField objects inside jp
     */
    private static void createLoginInputField(JPanel jp, JTextField[] inputs) {
        JTextField description = new JTextField("Username");
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        jp.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 20, 20, 30),0, 0));
        JTextField inputText = new JTextField();
        jp.add(inputText, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 20, 20),100, 0));
        inputs[0] = inputText;

        description = new JTextField("Password");
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        jp.add(description, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 0),0, 0));
        JPasswordField pw = new JPasswordField();
        jp.add(pw, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 20),100, 0));
        inputs[1] = pw;
    }

    /**
     * function that reads the user's login informations and validates them
     *
     * @param inputs: array containing the JTextField inside the login section
     * @param inputData: array to be filled with the values in the JTextFields inside the inputs vector
     * @return correctData: value representing the correctness of the data
     */
    private static Boolean readLoginInputs(JTextField[] inputs, String[] inputData) {
        boolean correctData = true;

        for(int i = 0; i < inputs.length; i++){
            if(!inputs[i].getText().equals("")) {
                inputData[i] = inputs[i].getText();
                inputs[i].setBackground(Color.WHITE);
            }
            else {
                inputs[i].setBackground(new Color(255, 87, 112));
                correctData = false;
            }
        }

        if(correctData){
            //ask database for user existance
            //if(user doesn't exist)
            //  correctData = false
        }

        return correctData;
    }

    /**
     * function that prepares the register button
     *
     * @param frame: frame used by the application
     */
    private static void setRegisterButton(JFrame frame){
        JButton btn = new JButton("Register");
        GridBagConstraints gbc = new GridBagConstraints(2,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10),100,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            prepareRegisterSection(frame);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * function that prepares the register section
     *
     * @param frame: frame used by the application
     */
    private static void prepareRegisterSection(JFrame frame) {
        frame.setTitle("BeerZone - REGISTER");
        String[] inputData = new String[6];
        JTextField[] inputs = new JTextField[5];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        frame.getContentPane().add(jp, gbc);

        JComboBox<String> cbInput = createInputUserType(jp);
        createRegisterInputSection(jp, inputs);

        JButton registerButton = new JButton("Register");
        prepareRegisterButton(registerButton, cbInput, inputs, inputData, frame);
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.insets = new Insets(0,0,20,0);
        jp.add(registerButton, gbc);

        JButton returnButton = new JButton("Go Back");
        returnButton.addActionListener(e -> prepareLogRegister(frame));
        gbc.gridx = 0;
        jp.add(returnButton, gbc);

        frame.setVisible(true);
    }

    /**
     * function used to create the JTextFiled inputs sections
     *
     * @param jp: JPanel that contains the register components
     * @param inputs: array that contains the JTextFields in the register section
     */
    private static void createRegisterInputSection(JPanel jp, JTextField[] inputs) {
        createRegisterInputField("Username", jp, 1, inputs);
        createRegisterInputField("E-mail", jp, 2, inputs);
        createRegisterInputField("Password", jp, 3, inputs);
        createRegisterInputField("Repeat password", jp, 4, inputs);
        createRegisterInputField("Location", jp, 5, inputs);
    }

    /**
     * function used to set the register button functionalities
     *
     * @param registerButton: register button
     * @param cbInput: JComboBox used to select the user type
     * @param inputs: JTextFields containing register informations
     * @param inputData: array to be filled with the values in the JTextFields inside the inputs vector and the one inside cbInput
     * @param frame: frame used by the application
     */
    private static void prepareRegisterButton(JButton registerButton, JComboBox<String> cbInput, JTextField[] inputs, String[] inputData, JFrame frame) {
        registerButton.addActionListener(e -> {
            Boolean correctData = readRegisterInputs(cbInput, inputs, inputData);
            if(correctData) {
                frame.getContentPane().removeAll();
                frame.repaint();
                //Data ready for being saved
                if(inputData[0].equals("Brewery manager"))
                    BreweryManagerGUI.breweryManagerSection(frame, inputData);
                else
                    StandardUserGUI.standardUserSection(frame, inputData);
            }
            else
                System.out.println("Missing data");
        });
    }

    /**
     * function that reads the user's register informations and validates them
     *
     * @param cbInput: component that contains the user type
     * @param inputs: array containing the JTextField inside the register section
     * @param inputData: array to be filled with the values in the JTextFields inside the inputs vector and the one inside cbInput
     * @return correctData: value representing the correctness of the data
     */
    private static Boolean readRegisterInputs(JComboBox<String> cbInput, JTextField[] inputs, String[] inputData) {
        boolean correctData = true;
        if(!cbInput.getSelectedItem().toString().equals("Choose an option")) {
            inputData[0] = cbInput.getSelectedItem().toString();
            cbInput.setBackground(Color.WHITE);
        }
        else {
            cbInput.setBackground(new Color(255, 87, 112));
            correctData = false;
        }

        for(int i = 0; i < inputs.length; i++){
            if(!inputs[i].getText().equals("")) {
                inputData[i + 1] = inputs[i].getText();
                inputs[i].setBackground(Color.WHITE);
            }
            else {
                inputs[i].setBackground(new Color(255, 87, 112));
                correctData = false;
            }
            if(i == (PASS_CONFIRMATION_ROW - 1) && !inputs[i - 1].getText().equals(inputs[i].getText())) {
                inputs[i].setBackground(new Color(255, 87, 112));
                inputs[i].setText("");
                inputs[i - 1].setBackground(new Color(255, 87, 112));
                inputs[i - 1].setText("");
                correctData = false;
            }
        }
        return correctData;
    }

    /**
     * function that create the combo box used by the user to select the user type
     *
     * @param panel: panel that contains the JComboBox
     * @return cb: JComboBox used to select the user type
     */
    private static JComboBox<String> createInputUserType(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 30, 0, 20),0,0);
        JTextField description = new JTextField("User type");
        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, gbc);
        String[] choices = {"Choose an option", "Standard user", "Brewery manager"};
        final JComboBox<String> cb = new JComboBox<>(choices);
        cb.setVisible(true);
        gbc.insets = new Insets(20, 0, 0, 20);
        gbc.gridx = 1;
        panel.add(cb, gbc);
        return cb;
    }

    /**
     * function that creates the input fields for the register section
     *
     * @param type: description of the input field
     * @param panel: panel that contains the input field
     * @param row: row in which the input box will be located
     * @param inputs: array that contains the JTextField objects inside panel
     */
    private static void createRegisterInputField(String type, JPanel panel, Integer row, JTextField[] inputs) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        if(row == LOCATION_ROW)
            gbc.insets = new Insets(20,30,20,20);
        else
            gbc.insets = new Insets(20,30,0,20);

        gbc.ipady = 8;
        gbc.gridx = 0;
        gbc.gridy = row;
        JTextField description = new JTextField(type);
        description.setFont(new Font("Arial", Font.PLAIN ,15));
        if(type.equals("Beer Name"))
            description.setBackground(BACKGROUND_COLOR);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.ipadx = 122;
        if(row == LOCATION_ROW)
            gbc.insets = new Insets(20,0,20,30);
        else
            gbc.insets = new Insets(20,0,0,30);
        JPasswordField inputSectionPw = new JPasswordField();
        JTextField inputSection = new JTextField();
        if(row == PASSWORD_ROW || row == PASS_CONFIRMATION_ROW){
            inputs[row - 1] = inputSectionPw;
            panel.add(inputSectionPw, gbc);
        }
        else{
            inputs[row - 1] = inputSection;
            panel.add(inputSection, gbc);
        }
    }


    /**
     * funnction that creates the login and register buttons
     *
     * @param frame: frame used by the application
     */
    public static void prepareLogRegister(JFrame frame){
        frame.getContentPane().removeAll();
        frame.setLayout(new GridBagLayout());
        frame.setTitle("BeerZone");
        StandardUserGUI.setStandardUserButton(frame);
        BreweryManagerGUI.setBreweryManagerButton(frame);
        setRegisterButton(frame);
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that initialize the frame used by the application
     */
    public void createAndShowGUI(){
        JFrame frame = new JFrame("BeerZone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        prepareLogRegister(frame);
        //breweryManagerSection(frame);

        frame.setVisible(true);
    }
}


