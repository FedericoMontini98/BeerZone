import javax.swing.*;
import java.awt.*;

public class BeerZoneGUI {

    /*
     * Divisione verticale in due metà
     */
    public static void prepareRegisterSection(JFrame frame){
        frame.setTitle("BeerZone - REGISTER");

        JPanel jp = new JPanel();
        jp.setBackground(new Color(1,255,255));
        frame.getContentPane().add(jp);

        jp = new JPanel();
        jp.setBackground(new Color(255,255,1));
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
        GridBagLayout lay = new GridBagLayout();
        frame.setLayout(lay);
        prepareLogRegister(frame);

        frame.setVisible(true);
    }
}
