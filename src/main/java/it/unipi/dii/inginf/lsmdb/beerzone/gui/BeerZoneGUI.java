package it.unipi.dii.inginf.lsmdb.beerzone.gui;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BreweryManager;
import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.UserManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
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

    private static final Integer BEER_TABLE = 0;
    private static final Integer BREWERY_TABLE = 1;

    private static final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private static final Color BACKGROUND_COLOR_LIGHT = new Color(255, 186, 51);

    /**
     * function that creates the table that allows the user to search the beers
     *
     * @param rjp: panel containing the table
     * @param frame: frame used by the application
     * @param user: logged user
     */
    public static void generateBrowseBeerMenu(JPanel rjp, JFrame frame, GeneralUser user) {
        rjp.removeAll();
        JPanel tableContainer = new JPanel();
        JTable browseTable = new JTable();
        JTextField beerInput = new JTextField();
        JTextField currPage = new JTextField("1");
        JPanel searchPanel = new JPanel();
        Integer[] tableType = new Integer[1];
        tableType[0] = BEER_TABLE;

        prepareBrowseComponents(rjp, frame, user, tableContainer, beerInput, searchPanel, browseTable, tableType, currPage);
        addNavigationBar(rjp, frame, tableContainer, browseTable, user, tableType, beerInput, currPage);

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that prepares the components that are inside the browse beer section
     *
     * @param rjp : panel containing the table
     * @param frame : frame used by the application
     * @param user : logged user
     * @param tableContainer : panel containing the table
     * @param beerInput: user's search input
     * @param searchPanel: JPanel containing the search section
     * @param browseTable : table containing the beers
     * @param tableType: type of the table to show, for Beers or for Brewery
     * @param currPage: page of the table to show
     */
    private static void prepareBrowseComponents(JPanel rjp, JFrame frame, GeneralUser user, JPanel tableContainer, JTextField beerInput, JPanel searchPanel, JTable browseTable, Integer[] tableType, JTextField currPage) {
        tableContainer.removeAll();
        searchPanel.removeAll();
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(createEmptyBorder());

        beerInput.setFont(new Font("Arial", Font.PLAIN, 15));
        beerInput.setPreferredSize(new Dimension(200,30));
        searchPanel.add(beerInput, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton submitChoice1 = new JButton("<html><center>Search<br>Beer by<br>Name or Style</center></html>");
        submitChoice1.setPreferredSize(new Dimension(120, 50));
        submitChoice1.addActionListener(e -> {
            if(!Objects.equals(tableType[0], BEER_TABLE))
                beerInput.setText("");
            tableType[0] = BEER_TABLE;
            currPage.setText("1");
            prepareBrowseComponents(rjp, frame, user, tableContainer, beerInput, searchPanel, browseTable, tableType, currPage);
        });
        searchPanel.add(submitChoice1, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton submitChoice2 = new JButton("<html><center>Search<br>Brewery by<br>Name</html></center>");
        submitChoice2.setPreferredSize(new Dimension(120, 50));
        submitChoice2.addActionListener(e -> {
            if(!Objects.equals(tableType[0], BREWERY_TABLE))
                beerInput.setText("");
            currPage.setText("1");
            tableType[0] = BREWERY_TABLE;
            prepareBrowseComponents(rjp, frame, user, tableContainer, beerInput, searchPanel, browseTable, tableType, currPage);
        });
        searchPanel.add(submitChoice2, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        searchPanel.setBackground(BACKGROUND_COLOR);
        rjp.add(searchPanel, new GridBagConstraints(0,0,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        if(Objects.equals(tableType[0], BEER_TABLE))
            prepareBrowseTable(browseTable, tableContainer, frame, user, BeerManager.getInstance().browseBeers(1, beerInput.getText()), rjp, tableType);
        else
            prepareBrowseTableBrewery(browseTable, tableContainer, frame, user, BreweryManager.getInstance().browseBreweries(1, beerInput.getText()), rjp, tableType);

        rjp.add(tableContainer, new GridBagConstraints(0,1,3,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the navigation arrows
     *
     * @param rjp: panel containing the section
     * @param frame: frame used by the application
     * @param tableContainer : panel containing the table
     * @param browseTable: table containing the beers
     * @param user: logged user
     * @param tableType : type of table to show, Beer table or Brewery table
     * @param beerInput: user's search input
     * @param currPage : page to show in the table
     */
    private static void addNavigationBar(JPanel rjp, JFrame frame, JPanel tableContainer, JTable browseTable, GeneralUser user, Integer[] tableType, JTextField beerInput, JTextField currPage) {
        currPage.setBackground(BACKGROUND_COLOR);
        currPage.setEditable(false);
        currPage.setBorder(createEmptyBorder());
        currPage.setFont(new Font("Arial", Font.BOLD, 15));
        rjp.add(currPage, new GridBagConstraints(1,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 90, 0, 0),0,0));

        JButton leftArr = new JButton("<");
        JButton rightArr = new JButton(">");
        setNavButtonFunctionalities(leftArr, rightArr, currPage, browseTable, tableContainer, frame, user, rjp, tableType, beerInput);

        rjp.add(leftArr, new GridBagConstraints(0,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 92, 0, 0),0,0));

        rjp.add(rightArr, new GridBagConstraints(2,3,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0),0,0));
    }

    /**
     * function that sets the functionalities of the navigation arrows
     *
     * @param leftArr: arrow that leads to the previous page
     * @param rightArr: arrow that leads to the next page
     * @param currPage: current page
     * @param browseTable : table containing the beers
     * @param tableContainer : panel containing the table
     * @param frame : frame used by the application
     * @param user : logged user
     * @param rjp : right JPanel containing the table
     * @param tableType : type of table to show, for Beers or for Brewery
     * @param beerInput: user's search input
     */
    private static void setNavButtonFunctionalities(JButton leftArr, JButton rightArr, JTextField currPage, JTable browseTable, JPanel tableContainer, JFrame frame, GeneralUser user, JPanel rjp, Integer[] tableType, JTextField beerInput) {
        leftArr.setEnabled(false);
        leftArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            if(currNum == 2)
                leftArr.setEnabled(false);
            rightArr.setEnabled(true);
            currNum = currNum - 1;
            if(Objects.equals(tableType[0], BEER_TABLE)) {
                ArrayList<Beer> beerToShow = BeerManager.getInstance().browseBeers(currNum, beerInput.getText());
                prepareBrowseTable(browseTable, tableContainer, frame, user, beerToShow, rjp, tableType);
            }
            else{
                ArrayList<Brewery> breweryToShow = BreweryManager.getInstance().browseBreweries(currNum, beerInput.getText());
                prepareBrowseTableBrewery(browseTable, tableContainer, frame, user, breweryToShow, rjp, tableType);
            }
            currPage.setText(String.valueOf(currNum));
        });

        rightArr.addActionListener(e -> {
            int currNum = Integer.parseInt(currPage.getText());
            leftArr.setEnabled(true);
            currNum = currNum + 1;
            if(Objects.equals(tableType[0], BEER_TABLE)) {
                ArrayList<Beer> beerToShow = BeerManager.getInstance().browseBeers(currNum, beerInput.getText());
                if(beerToShow.size() <= 12)
                    rightArr.setEnabled(false);
                prepareBrowseTable(browseTable, tableContainer, frame, user, beerToShow, rjp, tableType);
            }
            else{
                ArrayList<Brewery> breweryToShow = BreweryManager.getInstance().browseBreweries(currNum, beerInput.getText());
                if(breweryToShow.size() <= 12)
                    rightArr.setEnabled(false);
                prepareBrowseTableBrewery(browseTable, tableContainer, frame, user, breweryToShow, rjp, tableType);
            }
            currPage.setText(String.valueOf(currNum));
        });
    }

    /**
     * function that creates the table inside the "browse beers" section
     *
     * @param browseTable : table containing beers
     * @param tableContainer : panel containing browseTable
     * @param frame : frame used by the application
     * @param user : logged user
     * @param beerToShow : Beer list to show in the table
     * @param rjp : right JPanel containing the table
     * @param tableType : type of table to show, Beer table or Brewery Table
     */
    private static void prepareBrowseTable(JTable browseTable, JPanel tableContainer, JFrame frame, GeneralUser user, ArrayList<Beer> beerToShow, JPanel rjp, Integer[] tableType) {
        tableContainer.removeAll();

        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setTableSettings(tableModel, browseTable, rjp, frame, user, tableType);

        if(beerToShow.size() > 12)
            beerToShow.remove(beerToShow.size() - 1);
        else{
            Component[] c = rjp.getComponents();
            for(Component comp: c){
                if(comp instanceof JButton && ((JButton) comp).getText().equals(">"))
                    comp.setEnabled(false);
            }
        }

        for (Beer beer : beerToShow) tableModel.addRow(beerToStringArray(beer));

        JScrollPane jsc = new JScrollPane(browseTable);
        tableContainer.add(jsc, new GridBagConstraints(0,0,0,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that creates the table inside the "browse beers" section
     *
     * @param browseTable : table containing beers
     * @param tableContainer : panel containing browseTable
     * @param frame : frame used by the application
     * @param user : logged user
     * @param breweryToShow: list of breweries to show in the table
     * @param rjp: right JPanel containing the table
     * @param tableType : type of the table to show, for beers of for breweries
     */
    private static void prepareBrowseTableBrewery(JTable browseTable, JPanel tableContainer, JFrame frame, GeneralUser user, ArrayList<Brewery> breweryToShow, JPanel rjp, Integer[] tableType) {
        tableContainer.removeAll();

        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setTableSettings(tableModel, browseTable, rjp, frame, user, tableType);
        if(breweryToShow.size() > 12){
            breweryToShow.remove(breweryToShow.size() - 1);
        }
        else{
            Component[] c = rjp.getComponents();
            for(Component comp: c){
                if(comp instanceof JButton && ((JButton) comp).getText().equals(">"))
                    comp.setEnabled(false);
            }
        }
        for (Brewery brewery : breweryToShow) tableModel.addRow(breweryToStringArray(brewery));


        JScrollPane jsc = new JScrollPane(browseTable);
        tableContainer.add(jsc, new GridBagConstraints(0,0,0,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that converts a brewery object into a String array
     *
     * @param b: beer info to put in an array
     * @return beerInfo: array of info
     */
    private static String[] breweryToStringArray(Brewery b) {
        String[] beerInfo = new String[4];
        beerInfo[0] = b.getUserID();
        beerInfo[1] = b.getUsername();
        beerInfo[2] = b.getLocation();
        beerInfo[3] = b.getTypes();

        return beerInfo;
    }
    /**
     * function that converts a brewery object into a String array
     *
     * @param b: beer info to put in an array
     * @return beerInfo: array of info
     */
    private static String[] beerToStringArray(Beer b) {
        String[] beerInfo = new String[5];
        beerInfo[0] = b.getBeerID();
        beerInfo[1] = b.getBeerName();
        beerInfo[2] = b.getStyle();
        beerInfo[3] = (Objects.equals(b.getAbv(), "-1.0"))?"-" : b.getAbv();
        beerInfo[4] = (Objects.equals(b.getScore(), "-1.0"))? "-" : b.getScore();

        return beerInfo;
    }

    /**
     * function that sets the table graphic settings and the actions that can be performed on it
     *
     * @param tableModel: table model
     * @param browseTable: JPanel with the table
     * @param rjp: JPanel that contains the section
     * @param frame: frame used by the application
     * @param user : logged user
     * @param tableType : type of the table to show, for Beer or for Brewery
     */
    private static void setTableSettings(DefaultTableModel tableModel, JTable browseTable, JPanel rjp, JFrame frame, GeneralUser user, Integer[] tableType) {
        tableModel.addColumn((Objects.equals(tableType[0], BEER_TABLE))?"BeerID":"BreweryID");
        tableModel.addColumn((Objects.equals(tableType[0], BEER_TABLE))?"Beer Name":"Brewery Name");
        tableModel.addColumn((Objects.equals(tableType[0], BEER_TABLE))?"Style":"Location");

        if(Objects.equals(tableType[0], BEER_TABLE)) {
            tableModel.addColumn("Abv");
            tableModel.addColumn("Rate");
        }

        browseTable.setModel(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        browseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        if(Objects.equals(tableType[0], BEER_TABLE)) {
            browseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            browseTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        }

        browseTable.setModel(tableModel);
        TableColumnModel tcm = browseTable.getColumnModel();
        browseTable.getColumnModel().getColumn(1).setPreferredWidth((Objects.equals(tableType[0], BEER_TABLE))?170:175);
        browseTable.getColumnModel().getColumn(2).setPreferredWidth((Objects.equals(tableType[0], BEER_TABLE))?160:165);
        if(Objects.equals(tableType[0], BEER_TABLE)) {
            browseTable.getColumnModel().getColumn(3).setPreferredWidth(10);
            browseTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        tcm.removeColumn(tcm.getColumn(0));
        browseTable.setRowHeight(30);
        browseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    String id = browseTable.getModel().getValueAt(browseTable.getSelectedRow(),0).toString();
                    if(Objects.equals(tableType[0], BEER_TABLE)) {
                        DetailedBeer b = BeerManager.getInstance().getDetailedBeer(id);
                        createBeerPage(rjp, frame, b, user);
                    }
                    else {
                        BreweryManagerGUI.createBreweryPage(rjp, frame, user, id, Objects.equals(user.getUserID(), id));

                    }
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
        JTextPane[] userInputs = new JTextPane[2];
        prepareRecipeText(recipeTexts, selBeer);
        beerFields.setBackground(BACKGROUND_COLOR);
        beerFields.setLayout(new GridBagLayout());
        createBeerFields("Beer Name", beerFields, 0, 0, false, userInputs);
        createBeerFields("Style", beerFields, 1, 0, false, userInputs);
        createBeerFields("Rating", beerFields, 2, 0, false, userInputs);
        createBeerFields("Num. of Ratings", beerFields, 3, 0, false, userInputs);
        createBeerFields(selBeer.getBeerName(), beerFields, 0, 1, (Objects.equals(selBeer.getBreweryID(), user.getUserID())), userInputs);
        createBeerFields(selBeer.getStyle(), beerFields, 1, 1, (Objects.equals(selBeer.getBreweryID(), user.getUserID())), userInputs);
        createBeerFields(selBeer.getScore(), beerFields, 2, 1, false, userInputs);
        createBeerFields(selBeer.getNumRating(), beerFields, 3, 1, false, userInputs);

        containerPanel.add(beerFields, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        JButton toBrewery = new JButton(selBeer.getBreweryID().equals("")?"No Corresponding Brewery":"Go to Brewery");
        toBrewery.setEnabled(!selBeer.getBreweryID().equals(""));
        if(Objects.equals(user.getType(), STANDARD_USER))
            containerPanel.add(toBrewery, new GridBagConstraints(0,1,2,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));

        toBrewery.addActionListener(e-> BreweryManagerGUI.createBreweryPage(containerPanel, frame, user, selBeer.getBreweryID(), Objects.equals(user.getUserID(), selBeer.getBreweryID())));
        JComboBox<String>[] recipeCB = new JComboBox[1];
        JTextArea[] inputArea = new JTextArea[1];
        createRecipeSection(containerPanel, 2, recipeTexts, user.getType(), (Objects.equals(selBeer.getBreweryID(), user.getUserID())), selBeer, recipeCB, inputArea, frame, userInputs, user);

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
        recipeTexts[13] = selBeer.getRetired();
        recipeTexts[14] = selBeer.getUrl();
        recipeTexts[15] = selBeer.getYeast();
    }

    /**
     * function used to add a generic description-input element
     *
     * @param containerPanel: JPanel that will contain the elements
     * @param description: description text
     * @param info: initial content of the input fields
     * @param row: row that will contain the elements
     * @param inputs: array containing the user input JTextPane
     * @param editable: tells if the JTextPane is editable
     */
    public static void addGenericFields(JPanel containerPanel, String description, String info, int row, JTextPane[] inputs, boolean editable) {
        JTextField desc = new JTextField(description);
        desc.setFont(new Font("Arial", Font.BOLD, 14));
        desc.setEditable(false);
        desc.setBackground(BACKGROUND_COLOR_LIGHT);
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
        infoPane.setEditable(editable);
        containerPanel.add(infoPane, new GridBagConstraints(1,row,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets((row == 0)?15:0, 0, 15, 15),0, 0));
    }

    /**
     * function that creates the fields of the beer page
     *
     * @param fieldName : Name of the field
     * @param containerPanel : panel containing the field
     * @param row: GridBagConstraint grid_y
     * @param column: GridBagConstraint grid_x
     * @param enabled: tells if the JTextPane is editable
     * @param userInput: array containing the user input JTextPane
     *
     */
    private static void createBeerFields(String fieldName, JPanel containerPanel, int row, int column, boolean enabled, JTextPane[] userInput) {
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
            description.setEditable(enabled);
            if(enabled)
                userInput[row] = description;

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
     * @param rjp : right Jpanel that contains the section
     * @param panelRow: row where the panel will be located
     * @param recipeTexts: array containing the recipe text
     * @param userType: type of user requesting the section
     * @param editable: tells if the user can modify the recipe
     * @param selBeer: selected beer
     * @param recipeCB: JComboBox
     * @param frame : frame used by the application
     * @param inputRecipe: recipe user input
     * @param userInputs: beer info user input
     * @param b: logged user
     */
    public static void createRecipeSection(JPanel rjp, int panelRow, String[] recipeTexts, Integer userType, boolean editable, DetailedBeer selBeer, JComboBox<String>[] recipeCB, JTextArea[] inputRecipe, JFrame frame, JTextPane[] userInputs, GeneralUser b) {
        JPanel recipePanel = new JPanel();
        recipePanel.setBackground(BACKGROUND_COLOR_LIGHT);
        recipePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        recipePanel.setLayout(new GridBagLayout());

        JTextField title = new JTextField("Recipe Section");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBackground(BACKGROUND_COLOR_LIGHT);
        title.setBorder(createEmptyBorder());
        recipePanel.add(title, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0),0,0));

        String[] choices = {"Choose an option", "Abv", "Availability", "Color", "Fermentables", "Fg", "Hops", "Ibu", "Method", "Notes", "Og",
                "Other", "Ph Mash", "Retired", "Url", "Yeast"};
        recipeCB[0] = new JComboBox<>(choices);
        recipeCB[0].setVisible(true);
        recipePanel.add(recipeCB[0], new GridBagConstraints(0, 1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));

        inputRecipe[0] = new JTextArea(4, 20);
        inputRecipe[0].setText((Objects.equals(userType, BREWERY_MANAGER))? "Choose an option, write the description and then press the 'Confirm' button"
                : "Choose an option and then press the 'Confirm' button");
        inputRecipe[0].setEditable(editable);
        inputRecipe[0].setLineWrap(true);
        inputRecipe[0].setWrapStyleWord(true);
        inputRecipe[0].setCaretPosition(0);
        JScrollPane jsc = new JScrollPane(inputRecipe[0]);
        recipePanel.add(jsc, new GridBagConstraints(0, 2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),0,0));

        int[] index = new int[1];
        JComboBox<String> finalRecipeCB = recipeCB[0];
        JTextArea finalInputRecipe = inputRecipe[0];
        JTextField errorMsg = new JTextField();
        recipeCB[0].addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                index[0] = finalRecipeCB.getSelectedIndex();
                finalInputRecipe.setForeground(Color.BLACK);
                finalInputRecipe.setText(recipeTexts[index[0]]);
                if(index[0] == 0){
                    finalInputRecipe.setEditable(editable);
                    finalInputRecipe.setText("'Choose an option' is not a valid recipe section");
                    finalInputRecipe.setForeground(Color.RED);
                }
                else
                    finalInputRecipe.setEditable(editable);
            }
            if(e.getStateChange() == ItemEvent.DESELECTED){
                if(index[0] != 0){
                    if(finalInputRecipe.getText().equals("Choose an option, write the description and then press the 'Confirm' button"))
                        finalInputRecipe.setText("");
                    recipeTexts[index[0]] = finalInputRecipe.getText();
                    finalInputRecipe.setText("");
                }
            }
        });
        if(editable && selBeer!= null){
            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(BACKGROUND_COLOR);
            btnPanel.setBorder(createEmptyBorder());
            JButton deleteBeer = new JButton("Delete Beer");
            deleteBeer.setEnabled(true);
            btnPanel.add(deleteBeer, new GridBagConstraints(0,0,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5),0,0));
            deleteBeer.addActionListener(e-> {
                Brewery br= (Brewery) b;
                BreweryManager.getInstance().removeBeer(selBeer,br);
                BreweryManagerGUI.createBreweryPage(rjp, frame, b, b.getUserID(), editable);
            });

            JButton updateBeer = new JButton("Update Beer");

            updateBeer.addActionListener(e-> {
                Brewery br = (Brewery) b;
                updateBeer(selBeer, recipeTexts, rjp, frame, userInputs, finalRecipeCB, finalInputRecipe, errorMsg, br);
            });
            btnPanel.add(updateBeer, new GridBagConstraints(1, 0,1,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 0),0,0));

            rjp.add(btnPanel, new GridBagConstraints(0,3,2,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10,0,10,0),0,0));
        }

        rjp.add(recipePanel, new GridBagConstraints(0, panelRow, 2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),30,10));
    }

    /**
     * function to update beer recipe
     *
     * @param selBeer: selected beer
     * @param recipeTexts: array containing the recipe informations
     * @param rjp : right Jpanel that contains the section
     * @param frame : frame used by the application
     * @param userInputs: beer info inserted by the user
     * @param recipeCB: JComboBox that switches between recipe sections
     * @param inputArea: recipe section user input
     * @param errorMsg: error message
     */
    private static void updateBeer(DetailedBeer selBeer, String[] recipeTexts, JPanel rjp, JFrame frame, JTextPane[] userInputs, JComboBox<String> recipeCB, JTextArea inputArea, JTextField errorMsg, Brewery br) {
        recipeTexts[recipeCB.getSelectedIndex()] = inputArea.getText();
        boolean recipeCorrect = BreweryManagerGUI.checkRecipe(recipeTexts);
        boolean inputsCorrect = BreweryManagerGUI.checkInfo(userInputs);
        boolean retiredCorrect = (recipeTexts[13].equalsIgnoreCase("Yes") || recipeTexts[13].equalsIgnoreCase("No"));
        if(!recipeCorrect){
            errorMsg.setText("OG - FG - IBU - COLOR - PHMASH - ABV must be numbers");
            errorMsg.setBackground(BACKGROUND_COLOR);
            errorMsg.setBorder(createEmptyBorder());
            errorMsg.setForeground(Color.RED);
            rjp.add(errorMsg, new GridBagConstraints(0,7,2,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
            frame.repaint();
            frame.setVisible(true);
        }
        else {
            if(!retiredCorrect){
                errorMsg.setText("Retired field must be 'Yes' or 'No'");
                rjp.remove(errorMsg);
                errorMsg.setForeground(Color.RED);
                errorMsg.setBackground(BACKGROUND_COLOR);
                errorMsg.setBorder(createEmptyBorder());
                rjp.add(errorMsg, new GridBagConstraints(0,7,2,1,0,0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
                frame.repaint();
                frame.setVisible(true);
            }
            else {
                errorMsg.setText("Beer Correctly Updated");
                errorMsg.setForeground(new Color(0, 59, 16));
                frame.repaint();
                frame.setVisible(true);
                if (inputsCorrect) {
                    selBeer.setBeerName(userInputs[0].getText());
                    selBeer.setStyle(userInputs[1].getText());
                    selBeer.setAbv(recipeTexts[1]);
                    selBeer.setAvailability(recipeTexts[2]);
                    selBeer.setColor(recipeTexts[3]);
                    selBeer.setFermentables(recipeTexts[4]);
                    selBeer.setFg(recipeTexts[5]);
                    selBeer.setHops(recipeTexts[6]);
                    selBeer.setIbu(recipeTexts[7]);
                    selBeer.setMethod(recipeTexts[8]);
                    selBeer.setNotes(recipeTexts[9]);
                    selBeer.setOg(recipeTexts[10]);
                    selBeer.setOther(recipeTexts[11]);
                    selBeer.setPhMash(recipeTexts[12]);
                    selBeer.setRetired(recipeTexts[13]);
                    selBeer.setUrl(recipeTexts[14]);
                    selBeer.setYeast(recipeTexts[15]);
                    BeerManager.getInstance().updateBeer(selBeer, br);
                }
            }
        }
    }

    /**
     * function that creates the input section components
     *
     * @param jp: JPanel that contains the login components
     * @param inputs: array that contains the JTextField objects inside jp
     */
    private static void createLoginInputField(JPanel jp, JTextField[] inputs) {
        JPanel loginInputs = new JPanel(new GridBagLayout());
        loginInputs.setBackground(BACKGROUND_COLOR_LIGHT);
        JTextField description = new JTextField("Email");
        description.setBackground(BACKGROUND_COLOR_LIGHT);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        loginInputs.add(description, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 45, 20, 30),0, 0));
        JTextField inputText = new JTextField();
        loginInputs.add(inputText, new GridBagConstraints(1,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 20, 20),200, 0));
        inputs[0] = inputText;

        description = new JTextField("Password");
        description.setBorder(createEmptyBorder());
        description.setBackground(BACKGROUND_COLOR_LIGHT);
        description.setEditable(false);
        loginInputs.add(description, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 20, 0),0, 0));
        JPasswordField pw = new JPasswordField();
        loginInputs.add(pw, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 20),200, 0));

        jp.add(loginInputs, new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 20),0, 0));
        inputs[1] = pw;
    }

    /**
     * function that reads the user's login informations and validates them
     *
     * @param inputs: array containing the JTextField inside the login section
     * @param inputData: array to be filled with the values in the JTextFields inside the inputs vector
     * @return correctData: value representing the correctness of the data
     */
    private static GeneralUser readLoginInputs(JTextField[] inputs, String[] inputData) {
        boolean correctData = true;
        GeneralUser gu = null;
        for (int i = 0; i < inputs.length; i++) {
            if (!inputs[i].getText().equals("")) {
                inputData[i] = inputs[i].getText();
                inputs[i].setBackground(Color.WHITE);
            } else {
                inputs[i].setBackground(Color.YELLOW);
                correctData = false;
            }
        }
        if (correctData) {
            //ask database for user existance
            gu = UserManager.getInstance().login(inputData[0], inputData[1]);
            if(gu == null){
                for(JTextField input: inputs){
                    input.setText("");
                    input.setBackground(Color.YELLOW);
                }
            }
        }
        return gu;
    }

    /**
     * function that prepares the register section
     *
     * @param frame: frame used by the application
     */
    private static void prepareRegisterSection(JFrame frame) {
        frame.setTitle("BeerZone - REGISTER");
        JTextField[] inputs = new JTextField[6];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        jp.setBackground(BACKGROUND_COLOR_LIGHT);
        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
            boolean registrationOk;
            boolean correctData = readRegisterInputs(rb1, rb2, inputs, inputData);
            if(correctData) {
                bg[0] = readUserType(rb1);
                frame.getContentPane().removeAll();
                frame.repaint();
                if(Objects.equals(bg[0], "Brewery Manager")){
                    Brewery b = new Brewery(null, inputData[EMAIL_ROW], inputData[USERNAME_ROW], inputData[PASSWORD_ROW], inputData[LOCATION_ROW], inputData[VARIABLE_ROW]);
                    registrationOk = BreweryManager.getInstance().addBrewery(b);
                    if(registrationOk)
                        BreweryManagerGUI.breweryManagerSection(frame, b);
                }
                else{
                    StandardUser s = new StandardUser(null, inputData[EMAIL_ROW], inputData[USERNAME_ROW], inputData[PASSWORD_ROW], Integer.parseInt(inputData[VARIABLE_ROW]), inputData[LOCATION_ROW]);
                    registrationOk = UserManager.getInstance().addUser(s);
                    if(registrationOk)
                        StandardUserGUI.standardUserSection(frame, s);
                }

                if(!registrationOk){
                    for (JTextField input : inputs) {
                        input.setText("");
                        input.setBackground(Color.YELLOW);
                    }
                }

            }
        });
    }

    /**
     * @param rb1 : RadioButton 1
     * @return String containing the correspnding type of user
     */
    private static String readUserType(JRadioButton rb1) {
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
            rb1.setBackground(Color.YELLOW);
            rb2.setBackground(Color.YELLOW);
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
                    inputs[i].setBackground(Color.YELLOW);
                    correctData = false;
                }
                if(i == VARIABLE_ROW && rb1.isSelected()){
                    try{
                        int age = Integer.parseInt(inputs[i].getText());
                        if(age < 18) {
                            inputs[i].setBackground(Color.YELLOW);
                            inputs[i].setText("You must be at least 18 years old");
                            correctData = false;
                        }
                    }catch(NumberFormatException nfe){
                        inputs[i].setText("Insert an integer");
                        correctData = false;
                    }
                }
                if (i == (PASS_CONFIRMATION_ROW) && !inputs[i - 1].getText().equals(inputs[i].getText())) {
                    inputs[i].setBackground(Color.YELLOW);
                    inputs[i].setText("");
                    inputs[i - 1].setBackground(Color.YELLOW);
                    inputs[i - 1].setText("");
                    correctData = false;
                }
                if(i == EMAIL_ROW){
                    int pos = inputs[i].getText().indexOf('@');
                    if((pos <= 0) || (pos == inputs[i].getText().length() - 1)){
                        inputs[i].setText("Invalid mail");
                        inputs[i].setBackground(Color.YELLOW);
                        correctData = false;
                    }
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
        rb1.setBackground(BACKGROUND_COLOR_LIGHT);
        rb2.setBackground(BACKGROUND_COLOR_LIGHT);
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
        description.setBackground(BACKGROUND_COLOR_LIGHT);
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
     * function that creates the login and register buttons
     *
     * @param frame: frame used by the application
     */
    public static void prepareLogRegister(JFrame frame){
        frame.getContentPane().removeAll();
        frame.setLayout(new GridBagLayout());
        frame.setTitle("BeerZone");
        setBeerZoneImage(frame);
        setLoginButton(frame);
        setRegisterButton(frame);
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * function that set the image of the application
     *
     * @param frame: frame used by the application
     */
    private static void setBeerZoneImage(JFrame frame) {
        try{
            BufferedImage myPicture = ImageIO.read(new File("C:/images/logobeerzone.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            /*JLabel picLabel = new JLabel(new ImageIcon(
                    BeerZoneGUI.class.getResource("/logobeerzone.png")));*/
            frame.getContentPane().add(picLabel, new GridBagConstraints(0,0,2,1,0,0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 40, 0),0,0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * function that prepares the register button
     *
     * @param frame: frame used by the application
     */
    private static void setRegisterButton(JFrame frame){
        JButton btn = new JButton("Register");
        GridBagConstraints gbc = new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10),100,10);
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            prepareRegisterSection(frame);
        });
        frame.getContentPane().add(btn, gbc);
    }

    /**
     * function that creates the login button
     *
     * @param frame: frame used by the application
     */
    private static void setLoginButton(JFrame frame) {
        JButton btn = new JButton("Login");
        GridBagConstraints gbc = new GridBagConstraints(0,1,1,1,0,0,
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
        frame.setTitle("BeerZone - USER LOGIN");

        JTextField[] inputs = new JTextField[2];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel(new GridBagLayout());
        jp.setBackground(BACKGROUND_COLOR_LIGHT);
        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel loginButtonPanel = new JPanel(new GridBagLayout());
        loginButtonPanel.setBackground(BACKGROUND_COLOR_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        frame.getContentPane().add(jp, gbc);
        createLoginInputField(jp, inputs);
        JButton loginButton = new JButton("Login");
        createLoginButton(loginButtonPanel, frame, loginButton, inputs);

        JButton returnButton = new JButton("Go Back");
        returnButton.addActionListener(e -> prepareLogRegister(frame));
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0,0,20,40);
        gbc.gridwidth = 1;
        loginButtonPanel.add(returnButton, gbc);

        jp.add(loginButtonPanel, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),0,0));
        frame.setVisible(true);
    }
    /**
     * functioon that creates the button used to log in
     *
     *  @param jp : JPanel that contains the login components
     * @param frame : frame used by the application
     * @param loginButton : login button
     * @param inputs: user inputs
     */
    private static void createLoginButton(JPanel jp, JFrame frame, JButton loginButton, JTextField[] inputs) {
        GridBagConstraints gbc = new GridBagConstraints();
        String[] inputData = new String[2];
        loginButton.addActionListener(e -> {
            GeneralUser loggedUser = readLoginInputs(inputs, inputData);
            if(loggedUser != null) {
                frame.getContentPane().removeAll();
                frame.repaint();
                //send query to see what type of user it is
                if(loggedUser.getType() == BREWERY_MANAGER){
                    Brewery b = (Brewery)loggedUser;
                    BreweryManagerGUI.breweryManagerSection(frame, b);
                }
                else{
                    StandardUser s = (StandardUser)loggedUser;
                    UserManager.getInstance().getFavorites(s);
                    StandardUserGUI.standardUserSection(frame, s);
                }
            }
        });
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.insets = new Insets(0,40,20,0);
        gbc.gridwidth = 1;
        jp.add(loginButton, gbc);
    }

    /**
     * function that initialize the frame used by the application
     */
    public void createAndShowGUI(){
        JFrame frame = new JFrame("BeerZone");
        try{
            BufferedImage myPicture = ImageIO.read(new File("C:/images/logobeerzone.png"));
            frame.setIconImage(myPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        frame.setSize((int)(width*0.60), (int)(height*0.75));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        prepareLogRegister(frame);
        frame.setVisible(true);
    }
}


