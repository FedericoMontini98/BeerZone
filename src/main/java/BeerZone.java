import it.unipi.dii.inginf.lsmdb.beerzone.gui.BeerZoneGUI;

public class BeerZone {
    public static void main(String[] args) {
        BeerZoneGUI beerGUI = new BeerZoneGUI();
        javax.swing.SwingUtilities.invokeLater(beerGUI::createAndShowGUI);
    }
}
