package it.unipi.dii.inginf.lsmdb.beerzone;

import it.unipi.dii.inginf.lsmdb.beerzone.gui.BeerZoneGUI;

public class BeerZone {
    public static void main(String[] args) {
        Runtime current = Runtime.getRuntime();
        current.addShutdownHook(new ThreadOnClose());

        BeerZoneGUI beerGUI = new BeerZoneGUI();
        javax.swing.SwingUtilities.invokeLater(beerGUI::createAndShowGUI);
    }
}
