import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BeerZoneGUI {
    private final Integer USERTYPE_ROW = 0;
    private final Integer USERNAME_ROW = 1;
    private final Integer EMAIL_ROW = 2;
    private final Integer PASSWORD_ROW = 3;
    private final Integer PASS_CONFIRMATION_ROW = 4;
    private final Integer LOCATION_ROW = 5;

    private final Color BACKGROUND_COLOR = new Color(255, 170, 3);
    private final Color BACKGROUND_COLOR_RECIPE = new Color(255, 186, 51);
    /*
     * Brewery manager section
     */
    public void breweryManagerSection(JFrame frame, String[] inputData){
        frame.setTitle("BeerZone - BREWERY MANAGER");
        frame.setLayout(new GridLayout(1,2));

        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        rjp.setLayout(new GridBagLayout());
        ljp.setLayout(new GridBagLayout());
        JButton b1 = new JButton("Add beer");
        b1.addActionListener(e -> generateAddBeerMenu(rjp, frame, inputData));
        JButton b2 = new JButton("Browse Beer");
        b2.addActionListener(e -> generateBrowseBeerMenu(rjp, frame, inputData));
        JButton b3 = new JButton("Extract Brewery Statistics");
        b3.addActionListener(e -> generateBreweryStatisticsMenu(rjp, frame, inputData));
        setLeftBreweryManagerButton(b1, b2, b3, ljp);
        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);

        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    private void setLeftBreweryManagerButton(JButton b1, JButton b2, JButton b3, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),99,30);
        jp.add(b1, gbc);
        gbc.ipadx = 75;
        gbc.gridy = 1;
        jp.add(b2, gbc);
        gbc = new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,30);
        jp.add(b3, gbc);
    }

    private void generateBreweryStatisticsMenu(JPanel containerPanel, JFrame frame, String[] inputData) {
        containerPanel.removeAll();
        Object[][] data = {{"Look", "--"}, {"Smell", "--"}, {"Taste", "--"}, {"Feel", "--"}};
        String[] colHeader = {"Feature", "Score"};
        JTextField breweryStatsTitle = new JTextField(inputData[USERNAME_ROW]);
        breweryStatsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        breweryStatsTitle.setBackground(BACKGROUND_COLOR);
        breweryStatsTitle.setBorder(createEmptyBorder());
        containerPanel.add(breweryStatsTitle, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 80, 0),0,0));

        JTextField responseField = new JTextField("Not yet computed");
        responseField.setHorizontalAlignment(JTextField.CENTER);
        responseField.setBackground(BACKGROUND_COLOR);
        responseField.setBorder(createEmptyBorder());
        containerPanel.add(responseField, new GridBagConstraints(1,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 60, 0),0,0));

        JButton breweryStatsBtn = new JButton("Compute brewery score");
        containerPanel.add(breweryStatsBtn, new GridBagConstraints(0,1,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 60, 0),0,0));
        breweryStatsBtn.addActionListener(e->{
            responseField.setText("4.5/5");
        });

        JTable breweryStatsTable = new JTable(data, colHeader);
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

        breweryStatsBtn = new JButton("Compute average score for features");
        containerPanel.add(breweryStatsBtn, new GridBagConstraints(0,2,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),0,0));
        breweryStatsBtn.addActionListener(e->{
            breweryStatsTable.getModel().setValueAt("4",0,1);
            breweryStatsTable.getModel().setValueAt("5",1,1);
            breweryStatsTable.getModel().setValueAt("2",2,1);
            breweryStatsTable.getModel().setValueAt("3",3,1);
        });
        frame.repaint();
        frame.setVisible(true);
    }

    private void generateBrowseBeerMenu(JPanel containerPanel, JFrame frame, String[] inputData) {
        containerPanel.removeAll();
        JCheckBox ownBeers = new JCheckBox("Select Own Beers",false);
        ownBeers.setBackground(BACKGROUND_COLOR);
        ownBeers.addItemListener(itemEvent -> {
            boolean test = ownBeers.isSelected();
            if(test)
                System.out.println("Selected");
            else
                System.out.println("Deselected");
        });
        containerPanel.add(ownBeers, new GridBagConstraints(0,0,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        JTable browseTable = new JTable();
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
        tableModel.addColumn("Rating");
        browseTable.setModel(tableModel);
        tableModel.addRow(new String[]{"1", "a", "a", "3"});
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        browseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        browseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableModel.addRow(new String[]{"2", "b", "b", "4"});
        browseTable.setModel(tableModel);
        browseTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        browseTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        browseTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        browseTable.getColumnModel().getColumn(3).setPreferredWidth(30);
        browseTable.setRowHeight(30);
        JScrollPane jsc = new JScrollPane(browseTable);
        containerPanel.add(jsc, new GridBagConstraints(0,1,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private void generateAddBeerMenu(JPanel containerPanel, JFrame frame, String[] inputData) {
        containerPanel.removeAll();
        JTextField[] inputs = new JTextField[5];
        createInputField("Beer Name", containerPanel, 1, inputs);
        JComboBox cb = createInputStyle(containerPanel);
        createInputRecipe(containerPanel);
        JButton btn = new JButton("Add Beer to Brewery");
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.addActionListener(e->{

        });
        containerPanel.add(btn, new GridBagConstraints(0,5,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),0,0));
        frame.repaint();
        frame.setVisible(true);
    }

    private void createInputRecipe(JPanel containerPanel) {
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
        JButton btn = new JButton("Confirm");
        gbc.gridy = 4;
        recipePanel.add(btn, gbc);
        JTextField rv = new JTextField();
        gbc = new GridBagConstraints(0,2,1,2,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0),220,80);
        recipePanel.add(rv, gbc);
        gbc = new GridBagConstraints(0,3,2,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0),50,0);
        containerPanel.add(recipePanel, gbc);
    }

    private JComboBox createInputStyle(JPanel containerPanel) {
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

    public void StandardUserSection(JFrame frame, String[] inputData){
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel ljp = new JPanel();
        JPanel rjp = new JPanel();
        ljp.setLayout(new GridBagLayout());
        rjp.setLayout(new GridBagLayout());
        JButton b1 = new JButton("Browse Favorites");
        b1.addActionListener(e -> generateAddBeerMenu(rjp, frame, inputData));
        JButton b2 = new JButton("View Suggestions");
        b2.addActionListener(e -> generateBreweryStatisticsMenu(rjp, frame, inputData));
        JButton b3 = new JButton("Browse Beer");
        b3.addActionListener(e -> generateBrowseBeerMenu(rjp, frame, inputData));
        setLeftStandardUserButton(b1, b2, b3, ljp);
        ljp.setBorder(BorderFactory.createLineBorder(Color.black));
        ljp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(ljp);
        rjp.setBorder(BorderFactory.createLineBorder(Color.black));
        rjp.setBackground(BACKGROUND_COLOR);
        frame.getContentPane().add(rjp);

        frame.setVisible(true);
    }

    private void setLeftStandardUserButton(JButton b1, JButton b2, JButton b3, JPanel jp) {
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 40, 0),22,30);
        jp.add(b1, gbc);
        gbc.ipadx = 22;
        gbc.gridy = 1;
        jp.add(b2, gbc);
        gbc = new GridBagConstraints(0,2,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),50,30);
        jp.add(b3, gbc);
    }

    /*
    *
    */
    private void setStandardUserButton(JFrame frame){
        JButton btn = new JButton("Login as Standard User");
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 40, 0),10,10);
        frame.getContentPane().add(btn, gbc);
    }

    /*
     *
     */
    private void setBreweryManagerButton(JFrame frame){
        JButton btn = new JButton("Login as Brewery Manager");
        GridBagConstraints gbc = new GridBagConstraints(3,0,1,1,0,0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 40, 0),10,10);
        btn.addActionListener(e -> {
        });
        frame.getContentPane().add(btn, gbc);
    }

    /*
     *
     */
    private void setRegisterButton(JFrame frame){
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

    private void prepareRegisterSection(JFrame frame) {
        frame.setTitle("BeerZone - REGISTER");
        String[] inputData = new String[6];
        JTextField[] inputs = new JTextField[5];
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        frame.getContentPane().add(jp, gbc);
        jp.setLayout(new GridBagLayout());
        JComboBox cbInput = createInputUserType(jp);
        createInputField("Username", jp, 1, inputs);
        createInputField("E-mail", jp, 2, inputs);
        createInputField("Password", jp, 3, inputs);
        createInputField("Repeat password", jp, 4, inputs);
        createInputField("Location", jp, 5, inputs);
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            Boolean correctData = readInputs(cbInput, inputs, inputData);
            if(correctData) {
                frame.getContentPane().removeAll();
                frame.repaint();
                //Data ready for being saved
                if(inputData[0].equals("Brewery manager"))
                    breweryManagerSection(frame, inputData);
                else
                    StandardUserSection(frame, inputData);
            }
            else
                    System.out.println("Missing data");
        });
        gbc.gridy = 6;
        gbc.insets = new Insets(0,140,20,0);
        jp.add(registerButton, gbc);
        frame.setVisible(true);
    }

    private Boolean readInputs(JComboBox cbInput, JTextField[] inputs, String[] inputData) {
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

    private JComboBox createInputUserType(JPanel panel) {
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

    private void createInputField(String type, JPanel panel, Integer row, JTextField[] inputs) {
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

    /*
     *  starting page with login and register buttons
     */
    public void prepareLogRegister(JFrame frame){
        setStandardUserButton(frame);
        setBreweryManagerButton(frame);
        setRegisterButton(frame);
    }

    public void createAndShowGUI(){
        JFrame frame = new JFrame("BeerZone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        GridBagLayout lay = new GridBagLayout();
        frame.setLayout(lay);
        prepareLogRegister(frame);
        //breweryManagerSection(frame);

        frame.setVisible(true);
    }
}


