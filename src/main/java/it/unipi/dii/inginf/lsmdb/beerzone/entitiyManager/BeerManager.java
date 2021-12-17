package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import it.unipi.dii.inginf.lsmdb.beerzone.entities.Beer;

public class BeerManager {
    private Beer beer;

    public BeerManager(){}

    public BeerManager (Beer beer) {
        this.beer = beer;
    }

    /*
    * public Beer createBeerFromDoc(Document beerDoc)
    *
    * public List<Beer> readBeers(String beerName)  // also a substring of the name
    *
    * public void updateRating(...)
    *
    * public void deleteBeer(Beer beer);
    *
    * */
}
