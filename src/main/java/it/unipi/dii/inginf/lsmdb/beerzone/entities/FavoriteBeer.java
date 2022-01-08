package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FavoriteBeer extends Beer {
    private String favoriteDate;

    public FavoriteBeer(String beerID, String beerName, String favoriteDate) {
        super(beerID, beerName);
        this.favoriteDate = favoriteDate;
    }

    public FavoriteBeer(String BeerID, String favoriteDate){
        super(BeerID, BeerManager.getInstance().getBeer(BeerID).getBeerName());
        this.favoriteDate =favoriteDate;
    }

    public FavoriteBeer(Beer beer, String date) {
        this(beer.getBeerID(), beer.getBeerName(), date);
    }

    //public FavoriteBeer(String beerID, String beerName) {
    //     this(beerID, beerName, new Date());
    //  }

    public String getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(String favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
}
