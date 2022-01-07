package it.unipi.dii.inginf.lsmdb.beerzone.entities;

import it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager.BeerManager;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FavoriteBeer extends Beer {
    private Date favoriteDate;

    public FavoriteBeer(String beerID, String beerName, Date favoriteDate) {
        super(beerID, beerName);
        this.favoriteDate = favoriteDate;
    }

    public FavoriteBeer(String BeerID, String favoriteDate){
        super(BeerID, BeerManager.getInstance().getBeer(BeerID).getBeerName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        try {
            date = sdf.parse(favoriteDate);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        this.favoriteDate =date;
    }

    public FavoriteBeer(Beer beer, Date date) {
        this(beer.getBeerID(), beer.getBeerName(), date);
    }

    //public FavoriteBeer(String beerID, String beerName) {
    //     this(beerID, beerName, new Date());
    //  }

    public Date getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(Date favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
}
