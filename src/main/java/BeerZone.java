import java.awt.*;
import javax.swing.*;

public class BeerZone {

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
     *  starting page with login and register buttons
    */
    public static void prepareLogRegister(JFrame frame){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        gbc.ipadx = 10;
        gbc.insets.bottom = 40;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton btn = new JButton("Login as Standard User");
        frame.getContentPane().add(btn, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        btn = new JButton("Login as Brewery Manager");
        btn.addActionListener(e -> {
        });
        frame.getContentPane().add(btn, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.ipadx = 100;
        gbc.insets.left = gbc.insets.right = 10;
        btn = new JButton("Register");
        btn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.repaint();
            prepareRegisterSection(frame);
        });
        frame.getContentPane().add(btn, gbc);
    }

    private static void createAndShowGUI(){
        JFrame frame = new JFrame("BeerZone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        GridBagLayout lay = new GridBagLayout();
        frame.setLayout(lay);
        prepareLogRegister(frame);

        frame.setVisible(true);

    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(BeerZone::createAndShowGUI);
    }
}
