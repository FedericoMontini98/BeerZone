import javax.swing.*;
import java.awt.*;
import static javax.swing.BorderFactory.createEmptyBorder;

public class BeerZoneGUI {
    private final Integer NUM_REGISTER_ROW = 5;

    private final Color backgroundColor = new Color(255, 170, 3);
    /*
     * Brewery manager section
     */
    /*public void BreweryManagerSection(JFrame frame){
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
    }*/

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
        frame.setLayout(new GridBagLayout());
        JPanel jp = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        //jp.setBorder(new RoundedBorder(50));
        frame.getContentPane().add(jp, gbc);
        jp.setLayout(new GridBagLayout());
        createInputUserType(jp);
        createInputField("Username", jp, 1);
        createInputField("E-mail", jp, 2);
        createInputField("Password", jp, 3);
        createInputField("Repeat password", jp, 4);
        createInputField("Location", jp, 5);
        JButton registerButton = new JButton("Register");
        gbc.gridy = 6;
        gbc.insets = new Insets(0,140,20,0);
        jp.add(registerButton, gbc);
        frame.setVisible(true);
    }

    private void createInputUserType(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 30, 0, 20);
        gbc.gridy = 0;
        gbc.gridx = 0;
        JTextField description = new JTextField("User type");
        description.setBorder(createEmptyBorder());
        description.setEditable(false);
        panel.add(description, gbc);
        String[] choices = {"Standard user", "Brewery manager"};
        final JComboBox<String> cb = new JComboBox<>(choices);
        cb.setVisible(true);
        gbc.insets = new Insets(20, 0, 0, 20);
        gbc.gridx = 1;
        panel.add(cb, gbc);
    }

    private void createInputField(String type, JPanel panel, Integer row) {
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
        JTextField inputSection = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.ipadx = 122;
        if(row == NUM_REGISTER_ROW)
            gbc.insets = new Insets(20,0,20,30);
        else
            gbc.insets = new Insets(20,0,0,30);

        panel.add(inputSection, gbc);
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


