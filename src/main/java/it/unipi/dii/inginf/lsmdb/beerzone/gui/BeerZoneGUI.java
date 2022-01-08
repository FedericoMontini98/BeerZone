package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BeerZoneGUI {
    private static final Integer USERNAME_ROW = 0;
    private static final Integer EMAIL_ROW = 1;
    private static final Integer PASSWORD_ROW = 2;
    private static final Integer PASS_CONFIRMATION_ROW = 3;
    private static final Integer LOCATION_ROW = 4;
    private static final Integer VARIABLE_ROW = 5;

    private static final Integer STANDARD_USER = 0;
    private static final Integer BREWERY_MANAGER = 1;

    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);

    /**
     * function that creates the table that allows the user to search the beers
     *
     * @param containerPanel: panel containing the table
     * @param frame: frame used by the application
     * @param user: logged user
     */
    public static void generateBrowseBeerMenu(JPanel containerPanel, JFrame frame, GeneralUser user) {
        containerPanel.removeAll();

        prepareBrowseBeerComponents(containerPanel, frame, user);
        addNavigationBar(containerPanel);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that prepares the components that are inside the browse beer section
     *
     * @param containerPanel: panel containing the table
     * @param frame: frame used by the application
     * @param user: logged user
     */
    private static void prepareBrowseBeerComponents(JPanel containerPanel, JFrame frame, GeneralUser user) {
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(createEmptyBorder());

        JTextField beerInput = new JTextField();
        beerInput.setFont(new Font("Arial", Font.PLAIN, 15));
        beerInput.setPreferredSize(new Dimension(200,30));
        searchPanel.add(beerInput, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton submitChoice1 = new JButton("<html><center>Search<br>Beer by<br>Name</center></html>");
        submitChoice1.setPreferredSize(new Dimension(120, 50));
        submitChoice1.addActionListener(e -> beerInput.setText(""));
        searchPanel.add(submitChoice1, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton submitChoice2 = new JButton("<html><center>Search<br>Brewery by<br>Name</html></center>");
        submitChoice2.setPreferredSize(new Dimension(120, 50));
        submitChoice2.addActionListener(e -> beerInput.setText(""));
        if(Objects.equals(user.getType(), STANDARD_USER))
            searchPanel.add(submitChoice2, new GridBagConstraints(1,0,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        searchPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.add(searchPanel, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JPanel tableContainer = new JPanel();
        JTable browseTable = new JTable();
        prepareBrowseTable(browseTable, tableContainer, frame, user, 1);
        JScrollPane jsc = new JScrollPane(browseTable);
        tableContainer.add(jsc, new GridBagConstraints(0,0,0,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        containerPanel.add(tableContainer, new GridBagConstraints(0,1,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
    }

    /**
     * function that creates the navigation arrows
     *
     * @param containerPanel: panel containing the table
     */
    private static void addNavigationBar(JPanel containerPanel) {
        JTextField currPage = new JTextField("1");
        currPage.setBackground(BACKGROUND_COLOR);
        currPage.setEditable(false);
        currPage.setBorder(createEmptyBorder());
        currPage.setFont(new Font("Arial", Font.BOLD, 15));
        containerPanel.add(currPage, new GridBagConstraints(1,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 90, 0, 0),0,0));

        JButton leftArr = new JButton("<");
        JButton rightArr = new JButton(">");
        setNavButtonFunctionalities(leftArr, rightArr, currPage);

        containerPanel.add(leftArr, new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 92, 0, 0),0,0));

        containerPanel.add(rightArr, new GridBagConstraints(2,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0,0));

    }

    /**
     * function that sets the functionalities of the navigation arraws
     *
     * @param leftArr: arrow that leads to the previous page
     * @param rightArr: arrow that leads to the next page
     * @param currPage: current page
     */
    private static void setNavButtonFunctionalities(JButton leftArr, JButton rightArr, JTextField currPage) {
        leftArr.setEnabled(false);
        leftArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            if(currNum == 1)
                leftArr.setEnabled(false);
            currNum = currNum - 1;
            currPage.setText(String.valueOf(currNum));
        });

        rightArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            if(currNum == 0)
                leftArr.setEnabled(true);
            currNum = currNum + 1;
            currPage.setText(String.valueOf(currNum));
        });
    }

    /**
     * function that creates the table inside the "browse beers" section
     * @param browseTable : table containing beers
     * @param containerPanel : panel containing browseTable
     * @param frame : frame used by the application
     * @param user: logged user
     */
    private static void prepareBrowseTable(JTable browseTable, JPanel containerPanel, JFrame frame, GeneralUser user, Integer currPage) {
        containerPanel.removeAll();

        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTableSettings(tableModel, browseTable, containerPanel, frame, user);
        BeerManager.getInstance().browseBeers(currPage, null);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * @param b: beer info to put in an array
     * @return beerInfo: array of info
     */
    private static String[] beerToStringArray(Beer b) {
        String[] beerInfo = new String[5];
        beerInfo[0] = b.getBeerID();
        beerInfo[1] = b.getBeerName();
        beerInfo[2] = b.getStyle();
        beerInfo[3] = b.getAbv();
        beerInfo[4] = (Objects.equals(b.getScore(), "-1.0"))? "--" : b.getScore();

        return beerInfo;
    }

    /**
     * funtion that sets the table graphic settings and the actions that can be performed on it
     *
     * @param tableModel: table model
     * @param browseTable: Jpanel with the table
     * @param containerPanel: Jpanel that contains the section
     * @param frame: frame used by the application
     */
    private static void setTableSettings(DefaultTableModel tableModel, JTable browseTable, JPanel containerPanel, JFrame frame, GeneralUser user) {
        tableModel.addColumn("BeerID");
        tableModel.addColumn("Beer Name");
        tableModel.addColumn("Style");
        tableModel.addColumn("Abv");
        tableModel.addColumn("Rate");
        browseTable.setModel(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        browseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

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
                    String id = browseTable.getModel().getValueAt(browseTable.getSelectedRow(),0).toString();
                    DetailedBeer b = new DetailedBeer(id, "name", "style", "52", "0.0", "brewery", "Availability", "Notes",
                            "Url", "Retired", "Method", "10", "20", "30", "40", "52", "Fermentables",
                            "Hops", "Other", "Yeast");
                    createBeerPage(containerPanel, frame, b, user);
                }
            }
        });
    }

    /**
     * function that creates the page containing all the information of a specific beer
     *
     * @param containerPanel : panel containing the beer information
     * @param frame : frame used by the application
     * @param selBeer: beer selected by the user
     * @param user: logged user
     */
    public static void createBeerPage(JPanel containerPanel, JFrame frame, DetailedBeer selBeer, GeneralUser user) {
        containerPanel.removeAll();

        JPanel beerFields = new JPanel();
        String[] recipeTexts = new String[16];
        prepareRecipeText(recipeTexts, selBeer);
        beerFields.setBackground(BACKGROUND_COLOR);
        beerFields.setLayout(new GridBagLayout());
        createBeerFields("Beer Name", beerFields, 0, 0);
        createBeerFields("Style", beerFields, 1, 0);
        createBeerFields("Rating", beerFields, 2, 0);
        createBeerFields("Num. of Ratings", beerFields, 3, 0);
        createBeerFields(selBeer.getBeerName(), beerFields, 0, 1);
        createBeerFields(selBeer.getStyle(), beerFields, 1, 1);
        createBeerFields(selBeer.getScore(), beerFields, 2, 1);
        createBeerFields(selBeer.getNumRating(), beerFields, 3, 1);

        containerPanel.add(beerFields, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton toBrewery = new JButton(selBeer.getBreweryID().equals("")?"No Corresponding Brewery":"Go to Brewery");
        toBrewery.setEnabled(!selBeer.getBreweryID().equals(""));
        if(Objects.equals(user.getType(), STANDARD_USER))
            containerPanel.add(toBrewery, new GridBagConstraints(0,1,2,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        toBrewery.addActionListener(e->{
           BreweryManagerGUI.createBreweryPage(containerPanel, frame, user, selBeer.getBreweryID());
        });
        createRecipeSection(containerPanel, 2, recipeTexts, user.getType());

        if(Objects.equals(user.getType(), STANDARD_USER))
            StandardUserGUI.createButtonFunctionalities(frame, containerPanel, selBeer, user);
        else
            BreweryManagerGUI.prepareReturnToBrowseButton(containerPanel, frame, user);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that sets the recipeText
     *
     * @param recipeTexts: array containing all the beer infos
     * @param selBeer: selected Beer
     */
    private static void prepareRecipeText(String[] recipeTexts, DetailedBeer selBeer) {
        recipeTexts[0] = "Insert a valid recipe section (the option 'choose an option' is NOT valid)";
        recipeTexts[1] = selBeer.getAbv();
        recipeTexts[2] = selBeer.getAvailability();
        recipeTexts[3] = selBeer.getColor();
        recipeTexts[4] = selBeer.getFermentables();
        recipeTexts[5] = selBeer.getFg();
        recipeTexts[6] = selBeer.getHops();
        recipeTexts[7] = selBeer.getIbu();
        recipeTexts[8] = selBeer.getMethod();
        recipeTexts[9] = selBeer.getNotes();
        recipeTexts[10] = selBeer.getOg();
        recipeTexts[11] = selBeer.getOther();
        recipeTexts[12] = selBeer.getPhMash();
        recipeTexts[13] = selBeer.getStyle();
        recipeTexts[14] = selBeer.getUrl();
        recipeTexts[15] = selBeer.getYeast();
    }

    /**
     * function used to add a generic description-input element
     *
     * @param containerPanel: Jpanel that will contain the elements
     * @param description: description text
     * @param info: initial content of the input fiels
     * @param row: row taht will contain the elements
     */
    public static void addGenericFields(JPanel containerPanel, String description, String info, int row, JTextPane[] inputs, boolean userRequest) {
        JTextField desc = new JTextField(description);
        desc.setFont(new Font("Arial", Font.BOLD, 14));
        desc.setEditable(false);
        desc.setBackground(BACKGROUND_COLOR_RECIPE);
        desc.setBorder(createEmptyBorder());
        containerPanel.add(desc, new GridBagConstraints(0,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 0)?15:0, 25, 15, 15),0, 0));

        JTextPane infoPane = new JTextPane();
        inputs[row] = infoPane;
        infoPane.setText(info);
        infoPane.setPreferredSize(new Dimension(200, 50));
        StyledDocument doc = infoPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        infoPane.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPane.setBorder(createEmptyBorder());
        infoPane.setEditable(userRequest);
        containerPanel.add(infoPane, new GridBagConstraints(1,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 0)?15:0, 0, 15, 15),0, 0));
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
        JTextPane description = new JTextPane();
        JTextField desc = new JTextField(fieldName);
        if(column == 0){
            desc.setBackground(BACKGROUND_COLOR);
            desc.setBorder(createEmptyBorder());
            desc.setEditable(false);
            desc.setFont(new Font("Arial", Font.PLAIN, 14));
            containerPanel.add(desc, new GridBagConstraints(column,row,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 80, 20, 0),0,0));
        }
        else {
            description.setPreferredSize(new Dimension(220, (row < 2)?50:25));
            StyledDocument doc = description.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
            description.setText(fieldName);
            description.setCaretPosition(0);
            description.setBackground(Color.WHITE);
            description.setEditable(false);
            description.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane jsp = new JScrollPane(description);
            jsp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            jsp.getVerticalScrollBar().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

            containerPanel.add(jsp, new GridBagConstraints(column, row, 1, 1, 0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 40, 20, 40), 0, 0));
        }
    }

    /**
     * function that creates the recipe of a beer
     *
     * @param containerPanel: panel containing the beer information
     * @param panelRow: row where the panel will be located
     * @param recipeTexts: array containing the recipe text
     * @param userType: type of user requesting the section
     */
    public static void createRecipeSection(JPanel containerPanel, int panelRow, String[] recipeTexts, Integer userType) {
        JPanel recipePanel = new JPanel();
        recipePanel.setBackground(BACKGROUND_COLOR_RECIPE);
        recipePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        recipePanel.setLayout(new GridBagLayout());

        JTextField title = new JTextField("Recipe Section");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBackground(BACKGROUND_COLOR_RECIPE);
        title.setBorder(createEmptyBorder());
        recipePanel.add(title, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),0,0));

        String[] choices = {"Choose an option", "Abv", "Availability", "Color", "Fermentables", "Fg", "Hops", "Ibu", "Method", "Notes", "Og",
                "Other", "Ph Mash", "Style", "Url", "Yeast"};
        JComboBox<String> recipeCB = new JComboBox<>(choices);
        recipeCB.setVisible(true);
        recipePanel.add(recipeCB, new GridBagConstraints(0, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));

        JTextArea inputRecipe = new JTextArea(4, 20);
        inputRecipe.setText((Objects.equals(userType, BREWERY_MANAGER))? "Choose an option, write the description and then press the 'Confirm' button"
                : "Choose an option and then press the 'Confirm' button");
        inputRecipe.setEditable(Objects.equals(userType, BREWERY_MANAGER));
        inputRecipe.setLineWrap(true);
        inputRecipe.setWrapStyleWord(true);
        inputRecipe.setCaretPosition(0);
        JScrollPane jsc = new JScrollPane(inputRecipe);
        recipePanel.add(jsc, new GridBagConstraints(0, 2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));

        JButton confirmSection = new JButton("Confirm description");
        recipeCB.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                inputRecipe.setForeground(Color.BLACK);
                inputRecipe.setText(recipeTexts[recipeCB.getSelectedIndex()]);
                if(recipeCB.getSelectedIndex() == 0){
                    inputRecipe.setText("'Choose an option' is not a valid recipe section");
                    inputRecipe.setForeground(Color.RED);
                }
            }
        });

        confirmSection.addActionListener(e->{
            if(recipeCB.getSelectedIndex() != 0)
                recipeTexts[recipeCB.getSelectedIndex()] = inputRecipe.getText();
        });
        if(Objects.equals(userType, BREWERY_MANAGER))
            recipePanel.add(confirmSection, new GridBagConstraints(0, 3,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));

        containerPanel.add(recipePanel, new GridBagConstraints(0, panelRow, 2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),30,10));
    }

    /**
     * function that creates the input section components
     *
     * @param jp: JPanel that contains the login components
     * @param inputs: array that contains the JTextField objects inside jp
     */
    private static void createLoginInputField(JPanel jp, JTextField[] inputs) {
        JTextField description = new JTextField("Email");
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        jp.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 45, 20, 30),0, 0));
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

        for (int i = 0; i < inputs.length; i++) {
            if (!inputs[i].getText().equals("")) {
                inputData[i] = inputs[i].getText();
                inputs[i].setBackground(Color.WHITE);
            } else {
                inputs[i].setBackground(Color.RED);
                correctData = false;
            }
        }

        if (correctData) {
            //ask database for user existance
            //if(user doesn't exist)
            //  correctData = false
        }

        return correctData;
    }

    /**
     * function that prepares the register section
     *
     * @param frame: frame used by the application
     */
    private static void prepareRegisterSection(JFrame frame) {
        frame.setTitle("it.unipi.dii.inginf.lsmdb.beerzone.BeerZone - REGISTER");
        JTextField[] inputs = new JTextField[6];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JRadioButton rb1 = new JRadioButton("Standard User");
        JRadioButton rb2 = new JRadioButton("Brewery Manager");
        rb1.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                createRegisterInputField("Age", jp, VARIABLE_ROW + 1, inputs);
                frame.repaint();
                frame.setVisible(true);
            }
        });
        rb2.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                createRegisterInputField("Type", jp, VARIABLE_ROW + 1, inputs);
                frame.repaint();
                frame.setVisible(true);
            }
        });
        frame.getContentPane().add(jp, gbc);

        createInputUserType(jp, rb1, rb2);
        createRegisterInputSection(jp, inputs);
        JButton registerButton = new JButton("Register");
        prepareRegisterButton(registerButton, rb1, rb2, inputs, frame);
        gbc.gridy = 7;
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
        createRegisterInputField("Username", jp, USERNAME_ROW + 1, inputs);
        createRegisterInputField("E-mail", jp, EMAIL_ROW + 1, inputs);
        createRegisterInputField("Password", jp, PASSWORD_ROW + 1, inputs);
        createRegisterInputField("Repeat password", jp, PASS_CONFIRMATION_ROW + 1, inputs);
        createRegisterInputField("Location", jp, LOCATION_ROW + 1, inputs);
    }

    /**
     * function used to set the register button functionalities
     *
     * @param registerButton: register button
     * @param rb1: RadioButton 1
     * @param rb2: RadioButton 2
     * @param inputs: JTextFields containing register informations
     * @param frame: frame used by the application
     */
    private static void prepareRegisterButton(JButton registerButton, JRadioButton rb1, JRadioButton rb2, JTextField[] inputs, JFrame frame) {
        String[] inputData = new String[6];
        final String[] bg = {null};
        registerButton.addActionListener(e -> {
            Boolean correctData = readRegisterInputs(rb1, rb2, inputs, inputData);
            if(correctData) {
                bg[0] = readUserType(rb1, rb2);
                frame.getContentPane().removeAll();
                frame.repaint();
                if(Objects.equals(bg[0], "Brewery Manager")){
                    Brewery b = new Brewery("1", inputData[EMAIL_ROW], inputData[USERNAME_ROW], inputData[PASSWORD_ROW], inputData[LOCATION_ROW], inputData[VARIABLE_ROW]);
                    //BreweryManager.getInstance().addBrewery(b);
                    BreweryManagerGUI.breweryManagerSection(frame, b);
                }
                else{
                    StandardUser s = new StandardUser("2", inputData[EMAIL_ROW], inputData[USERNAME_ROW], inputData[PASSWORD_ROW], Integer.parseInt(inputData[VARIABLE_ROW]), inputData[LOCATION_ROW]);
                    StandardUserGUI.standardUserSection(frame, s);
                }
            }
        });
    }

    /**
     * @param rb1 : RadioButton 1
     * @param rb2: RadioButton 2
     * @return String containing the correspnding type of user
     */
    private static String readUserType(JRadioButton rb1, JRadioButton rb2) {
        if(rb1.isSelected())
            return "Standard User";
        else
            return "Brewery Manager";
    }

    /**
     * function that reads the user's register informations and validates them
     *
     * @param rb1 RadioButton 1
     * @param rb2 RadioButton 2
     * @param inputs: array containing the JTextField inside the register section
     * @param inputData: array to be filled with the values in the JTextFields inside the inputs vector and the one inside cbInput
     * @return correctData: value representing the correctness of the data
     */
    private static Boolean readRegisterInputs(JRadioButton rb1, JRadioButton rb2, JTextField[] inputs, String[] inputData) {
        boolean correctData = true;
        if(!rb1.isSelected() && !rb2.isSelected()) {
            rb1.setBackground(Color.RED);
            rb2.setBackground(Color.RED);
            correctData = false;
        }
        else {
            rb1.setBackground(null);
            rb2.setBackground(null);
        }

        if(correctData) {
            for (int i = 0; i < inputs.length; i++) {
                if (!inputs[i].getText().equals("")) {
                    inputData[i] = inputs[i].getText();
                    inputs[i].setBackground(Color.WHITE);
                } else {
                    inputs[i].setBackground(Color.RED);
                    correctData = false;
                }
                if (i == (PASS_CONFIRMATION_ROW - 1) && !inputs[i - 1].getText().equals(inputs[i].getText())) {
                    inputs[i].setBackground(Color.RED);
                    inputs[i].setText("");
                    inputs[i - 1].setBackground(Color.RED);
                    inputs[i - 1].setText("");
                    correctData = false;
                }
            }
        }
        return correctData;
    }

    /**
     * function that create the combo box used by the user to select the user type
     *
     * @param panel: panel that contains the JComboBox
     * @param rb1: RadioButton 1
     * @param rb2: RadioButton 2
     */
    private static void createInputUserType(JPanel panel, JRadioButton rb1, JRadioButton rb2) {
        ButtonGroup bg = new ButtonGroup();

        bg.add(rb1);
        bg.add(rb2);
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 25, 0, 0),0,0);
        panel.add(rb1, gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        panel.add(rb2, gbc);
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
        if(row == LOCATION_ROW + 1)
            gbc.insets = new Insets(20,30,20,20);
        else if(row == VARIABLE_ROW + 1)
            gbc.insets = new Insets(0,30,20,20);
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
        if(row == LOCATION_ROW + 1)
            gbc.insets = new Insets(20,0,20,30);
        else if(row == VARIABLE_ROW + 1)
            gbc.insets = new Insets(0,0,20,30);
        else
            gbc.insets = new Insets(20,0,0,30);

        JPasswordField inputSectionPw = new JPasswordField();
        JTextField inputSection = new JTextField();
        if(row == PASSWORD_ROW + 1 || row == PASS_CONFIRMATION_ROW + 1){
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
        frame.setTitle("it.unipi.dii.inginf.lsmdb.beerzone.BeerZone");
        setLoginButton(frame);
        setRegisterButton(frame);
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that prepares the register button
     *
     * @param frame: frame used by the application
     */
    private static void setRegisterButton(JFrame frame){
        JButton btn = new JButton("Register");
        GridBagConstraints gbc = new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10),100,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            prepareRegisterSection(frame);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * funtion that creates the login button
     *
     * @param frame: frame used by the application
     */
    private static void setLoginButton(JFrame frame) {
        JButton btn = new JButton("Login");
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),115,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            prepareLoginSection(frame);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * function that prepares the login section of the application
     *
     * @param frame: frame used by the application
     */
    public static void prepareLoginSection(JFrame frame) {
        frame.setTitle("it.unipi.dii.inginf.lsmdb.beerzone.BeerZone - USER LOGIN");

        JTextField[] inputs = new JTextField[2];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.getContentPane().add(jp, gbc);
        jp.setLayout(new GridBagLayout());
        createLoginInputField(jp, inputs);
        JButton loginButton = new JButton("Login");
        createLoginButton(jp, frame, loginButton, inputs);

        JButton returnButton = new JButton("Go Back");
        returnButton.addActionListener(e -> prepareLogRegister(frame));
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,30,20,0);
        gbc.gridwidth = 1;
        jp.add(returnButton, gbc);
        frame.setVisible(true);
    }
    /**
     * functioon that creates the button used to log in
     *
     *  @param jp : JPanel that contains the login components
     * @param frame : frame used by the application
     * @param loginButton : login button
     * @param inputs
     */
    private static void createLoginButton(JPanel jp, JFrame frame, JButton loginButton, JTextField[] inputs) {
        GridBagConstraints gbc = new GridBagConstraints();
        String[] inputData = new String[2];
        loginButton.addActionListener(e -> {
            Boolean correctData = readLoginInputs(inputs, inputData);
            if(correctData) {
                frame.getContentPane().removeAll();
                frame.repaint();
                //send query to see what type of user it is
                int res = STANDARD_USER;
                if(res == BREWERY_MANAGER){
                    Brewery b = new Brewery("1", "email", "username", "password", "location", "types");
                    BreweryManagerGUI.breweryManagerSection(frame, b);
                }
                else if(res == STANDARD_USER) {
                    StandardUser s = new StandardUser("2", "email2", "username2", "password2", 20, "location");
                    UserManager.getInstance().getFavorites(s);
                    StandardUserGUI.standardUserSection(frame, s);
                }
                else
                    System.out.println("data not correct");
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
     * function that initialize the frame used by the application
     */
    public void createAndShowGUI(){
        JFrame frame = new JFrame("BeerZone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        //prepareLogRegister(frame);
        frame.setVisible(true);
    }
}


