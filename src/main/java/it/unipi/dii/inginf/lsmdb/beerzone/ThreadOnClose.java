package it.unipi.dii.inginf.lsmdb.beerzone;

import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.*;

class ThreadOnClose extends Thread {
    public void run() {
        MongoManager.closeConnection();
        Neo4jManager.getInstance().close();
    }
}

