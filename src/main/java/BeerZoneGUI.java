import javax.swing.*;
import java.awt.*;
import static javax.swing.BorderFactory.createEmptyBorder;

public class BeerZoneGUI {
    private final Integer NUM_REGISTER_ROW = 5;
    private final Integer PASS_CONFIRMATION_ROW = 4;

    private final Color backgroundColor = new Color(255, 170, 3);
    /*
     * Brewery manager section
     */
    public void BreweryManagerSection(JFrame frame, String[] inputData){
        frame.setTitle("BeerZone - BREWERY MANAGER");
        frame.setLayout(new GridLayout(1,2));
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createLineBorder(Color.black));
        jp.setBackground(backgroundColor);
        frame.getContentPane().add(jp);
        jp = new JPanel();
        jp.setBorder(BorderFactory.createLineBorder(Color.black));
        jp.setBackground(backgroundColor);
        frame.getContentPane().add(jp);

        frame.setVisible(true);
    }

    public void StandardUserSection(JFrame frame, String[] inputData){
        frame.setTitle("BeerZone - STANDARD USER");
        frame.setLayout(new GridLayout(1,2));
        JPanel jp = new JPanel();
        jp.setBorder(BorderFactory.createLineBorder(Color.black));
        jp.setBackground(backgroundColor);
        frame.getContentPane().add(jp);
        jp = new JPanel();
        jp.setBorder(BorderFactory.createLineBorder(Color.black));
        jp.setBackground(backgroundColor);
        frame.getContentPane().add(jp);

        frame.setVisible(true);
    }

    /*
    *
    */
    private void setStandardUserButton(JFrame frame){
        JButton btn = new JButton("Login as Standard User");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        gbc.ipadx = 10;
        gbc.insets.bottom = 40;
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.getContentPane().add(btn, gbc);
    }

    /*
     *
     */
    private void setBreweryManagerButton(JFrame frame){
        JButton btn = new JButton("Login as Brewery Manager");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        gbc.ipadx = 10;
        gbc.insets.bottom = 40;
        gbc.gridx = 3;
        gbc.gridy = 0;
        btn.addActionListener(e -> {
        });
        frame.getContentPane().add(btn, gbc);
    }

    /*
     *
     */
    private void setRegisterButton(JFrame frame){
        JButton btn = new JButton("Register");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        gbc.ipadx = 100;
        gbc.insets.left = gbc.insets.right = 10;
        gbc.gridx = 2;
        gbc.gridy = 1;
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
        //jp.setBorder(new RoundedBorder(50));
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
                    BreweryManagerSection(frame, inputData);
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
            if(i == PASS_CONFIRMATION_ROW && !inputs[i - 1].getText().equals(inputs[i].getText())) {
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 30, 0, 20);
        gbc.gridy = 0;
        gbc.gridx = 0;
        JTextField description = new JTextField("User type");
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
        if(row == NUM_REGISTER_ROW)
            gbc.insets = new Insets(20,30,20,20);
        else
            gbc.insets = new Insets(20,30,0,20);

        gbc.ipady = 8;
        gbc.gridx = 0;
        gbc.gridy = row;
        JTextField description = new JTextField(type);
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.ipadx = 122;
        if(row == NUM_REGISTER_ROW)
            gbc.insets = new Insets(20,0,20,30);
        else
            gbc.insets = new Insets(20,0,0,30);
        JPasswordField inputSectionPw = new JPasswordField();
        JTextField inputSection = new JTextField();
        if(row == PASS_CONFIRMATION_ROW - 1|| row == PASS_CONFIRMATION_ROW){
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
        frame.setSize(800, 600);
        frame.getContentPane().setBackground(backgroundColor);
        GridBagLayout lay = new GridBagLayout();
        frame.setLayout(lay);
        prepareLogRegister(frame);

        frame.setVisible(true);
    }

    /*private static class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x,y,width-1,height-1,radius,radius);
        }
    }*/
}


