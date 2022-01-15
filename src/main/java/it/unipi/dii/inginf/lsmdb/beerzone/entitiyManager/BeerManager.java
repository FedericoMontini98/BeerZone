package it.unipi.dii.inginf.lsmdb.beerzone.entitiyManager;

import com.mongodb.client.FindIterable;
import com.mongodb.lang.Nullable;
import it.unipi.dii.inginf.lsmdb.beerzone.entities.*;
import it.unipi.dii.inginf.lsmdb.beerzone.entityManagerDB.BeerManagerDB;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.MongoManager;
import it.unipi.dii.inginf.lsmdb.beerzone.managerDB.Neo4jManager;
import org.bson.Document;

import java.util.*;

public class BeerManager {
    //private final MongoCollection<Document> beersCollection;
    //private final Neo4jManager NeoDBMS;
    private static BeerManager beerManager;
    private final BeerManagerDB beerManagerDB;

    private BeerManager(){
        //beersCollection = MongoManager.getInstance().getCollection("beers");
        //NeoDBMS = Neo4jManager.getInstance();
        beerManagerDB = BeerManagerDB.getInstance();
    }

    public static BeerManager getInstance() {
        if (beerManager == null)
            beerManager = new BeerManager();
        return beerManager;
    }

    public void addNewBeer(DetailedBeer beer) {
        beerManagerDB.addNewBeerMongo(beer);
    }

    protected boolean removeBeer(Beer beer) {
        if(beerManagerDB.removeBeerMongo(beer)) {
            removeBeerFromNeo(beer);
            return true;
        }
        return false;
    }

    public ArrayList<Beer> browseBeers(int page, @Nullable String name) {
        ArrayList<Beer> beerList = new ArrayList<>();
        FindIterable<Document> iterable = beerManagerDB.browseBeers(page, name);

        for (Document beer: iterable) {
            beerList.add(new Beer(beer));
        }
        return beerList;
    }
    public Beer getBeer(String beerID) {
        Beer beer = null;
        Document doc = beerManagerDB.getBeer(beerID);
        if (doc != null)
            beer = new Beer(doc);

        return beer;
    }

    public DetailedBeer getDetailedBeer(String beerID) {
        DetailedBeer beer = null;
        Document doc = beerManagerDB.getDetailedBeer(beerID);

        if (doc != null)
            beer = new DetailedBeer(doc);

        return beer;
    }

    public long deleteBreweryFromBeers(String breweryID) {
        return beerManagerDB.deleteBreweryFromBeers(breweryID);
    }

    public ArrayList<Beer> getHighestAvgScoreBeers() {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: beerManagerDB.getHighestAvgScoreBeers()) {
            Document idDoc = (Document) doc.get("_id");
            if (idDoc != null) {
                Beer b = new Beer(idDoc);
                b.setScore(doc.get("monthly_score") != null ? Double.parseDouble(doc.get("monthly_score").toString()) : -1);
                beers.add(b);
            }
        }
        return beers;
    }

    public ArrayList<Beer> getBeersUnderAvgFeatureScore(Brewery brewery, String feature) {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: beerManagerDB.getBeersUnderAvgFeatureScore(brewery.getUserID(), feature,
                getBreweryScore(brewery.getUserID()))) {
            Document idDoc = (Document) doc.get("_id");
            if (idDoc != null) {
                Beer b = new Beer(idDoc);
                b.setScore(doc.get("feature_score") != null ? Double.parseDouble(doc.get("feature_score").toString()) : -1);
                beers.add(b);
            }
        }
        return beers;
    }

    public boolean updateBeer(DetailedBeer beer) {
        return beerManagerDB.updateBeer(beer);
    }

    public void recomputeBeerRating(DetailedBeer beer) {
        Document docRating = beerManagerDB.updateBeerRating(beer);

        if (docRating != null) {
            if (docRating.get("rating") != null)
                beer.setScore(Double.parseDouble(docRating.get("rating").toString()));
            if (docRating.get("num_rating") != null)
                beer.setNumRating(docRating.getInteger("num_rating"));
        }
    }

    protected double getBreweryScore(String breweryID) {
        Document doc = beerManagerDB.getBreweryScore(breweryID);

        if (doc != null)
            return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;

        return -1;
    }

    protected double getWeightedBreweryScore(String breweryID) {
        Document doc = beerManagerDB.getWeightedBreweryScore(breweryID);

        if (doc != null) {
            return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;
        }
        return -1;
    }

    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean addBeer(Beer beer) {
        return beerManagerDB.addBeer(beer);
    }

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
     *  others users */
    public ArrayList<String> getSuggested(StandardUser user) {
        return beerManagerDB.getSuggested(user);
    }

    /* Function that calculate the most favorite beers in the past month */
    public ArrayList<FavoriteBeer> getMostFavoriteThisMonth () {
        return beerManagerDB.getMostFavoriteThisMonth();
    }

    protected void removeBeerFromNeo(Beer beer) {
        beerManagerDB.removeBeerFromNeo(beer);
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Reviews manager ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/*
    protected boolean deleteUserFromReviews(String username) {
        return beerManagerDB.deleteUserFromReviews(username);
    }

 */

    /* ************************************************************************************************************/
    /* *************************************  MongoDB Section  ****************************************************/
    /* ************************************************************************************************************/

 /*    protected boolean removeBeerMongoV(Beer beer){
        DeleteResult deleteResult = beersCollection.deleteOne(eq("_id", new ObjectId(beer.getBeerID())));
        return (deleteResult.getDeletedCount() == 1);

    }

   private void addNewBeerM(DetailedBeer beer) {
        try {
            Document beerDoc = beer.getBeerDoc();
            beersCollection.insertOne(beerDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */

    /*
    protected double computeNewBeerRatingV(Review review, DetailedBeer beer, boolean add) {
        try {
            Document doc = beerManagerDB.getDetailedBeer(beer.getBeerID());//beersCollection.find(eq("beer_id", new ObjectId(review.getBeerID()))).first();
            if (doc != null) {
                double rating = doc.get("rating") != null ? Double.parseDouble(doc.get("rating").toString()) : 0;
                int num_rating = doc.get("num_rating") != null ? doc.getInteger("num_rating") : 0;
                double oldTotalRating = rating * num_rating;
                double newRating;
                if (add) {
                    newRating = (oldTotalRating + review.getNumericScore()) / (++num_rating);
                }
                else {
                    if (num_rating <= 0)
                        return -1;
                    newRating = (oldTotalRating - review.getNumericScore()) / (--num_rating);
                }
                //double new_rating = oldTotalRating + Double.parseDouble(review.getScore()) / (num_rating);
                beer.setNumRating(num_rating);
                beer.setScore(newRating);

                return newRating;

               // UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(review.getBeerID())),
                    //    combine(set("rating", newRating), set("num_rating", num_rating),
                  //              addToSet("reviews", review.getReviewDoc())));
                //return updateResult.getMatchedCount() == 1;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

     */

/*

    public ArrayList<Beer> browseBeers(int page, @Nullable String name) {
        //check string
        name = name != null ? name : "";
        int limit = 13;
        int n = (page-1) * limit;

        FindIterable<Document> iterable = beersCollection.find(or(
                regex("name", "^" + name + ".*", "i"),
                regex("style", "^" + name + ".*", "i")))
                .skip(n).limit(limit+1)
                .projection(include("name", "style", "abv", "rating"));

        ArrayList<Beer> beerList = new ArrayList<>();
        for (Document beer: iterable) {
            beerList.add(new Beer(beer));
        }
        return beerList;
    }

    // never used
    public ArrayList<Beer> browseBeersByBreweryID(int page, String breweryID) {
        if (breweryID == null)
            return null;
        int limit = 3;
        int n = (page-1) * limit;

        ArrayList<Beer> beerList = new ArrayList<>();
        //ArrayList<String> beers = BreweryManager.getInstance().getBeerList(page, breweryID);
        try {
            for (Document beerDoc : beersCollection.find(eq("brewery_id", new ObjectId(breweryID)))
                    .skip(n).limit(limit+1)) {
                beerList.add(new Beer(beerDoc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beerList;
    }

    public ArrayList<Beer> getBeersFromBrewery(Brewery brewery) {
        ArrayList<Beer> beers = new ArrayList<>();
        try {
            for (Document beer : beersCollection.find(in("_id", brewery.getBeersID()))) {
                beers.add(new Beer(beer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beers;
    }

    public Beer getBeerv(String beerID) {
        Beer beer = null;
        try {
            Document doc = beersCollection.find(eq("_id", new ObjectId(beerID)))
                    .projection(include("name", "style", "abv", "rating")).first();
            if (doc != null)
                beer = new Beer(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }

    public DetailedBeer getDetailedBeerV(String beerID) {
        DetailedBeer beer = null;
        try {
            Document doc = beersCollection.find(eq("_id", new ObjectId(beerID))).first();
            if (doc != null)
                beer = new DetailedBeer(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beer;
    }
    */

    /* return value: matched beers in the beersCollection
    public long deleteBreweryFromBeersv(String breweryID) {
        UpdateResult updateResult = beersCollection.updateMany(eq("brewery_id", new ObjectId(breweryID)),
        //UpdateResult updateResult = beersCollection.updateMany(eq("brewery", breweryID),
                combine(unset("brewery_id"), set("retired", "t")));
        return updateResult.getMatchedCount();
    }

     */
/*
    private ArrayList<Beer> getHighestAvgScoreBeersOld() {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: ReviewManager.getInstance().getHighestAvgScoreBeers()) {
            Beer b = new Beer(doc);
            b.setScore(doc.get("monthly_score") != null ? Double.parseDouble(doc.get("monthly_score").toString()) : -1);
            beers.add(b);
        }
        return beers;
    }

 */
/*
    public ArrayList<Beer> getBeersUnderAvgFeatureScoreOld(Brewery brewery, String feature) {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: ReviewManager.getInstance().getBeersUnderAvgFeatureScore(brewery, feature,
                getBreweryScore(new ObjectId(brewery.getUserID())))) {
            Beer b = new Beer(doc);
            b.setScore(doc.get("feature_score") != null ? Double.parseDouble(doc.get("feature_score").toString()) : -1);
            beers.add(b);
        }
        return beers;
    }

 */

 /*   public ArrayList<Beer> getBeersUnderAvgFeatureScoreV(Brewery brewery, String feature) {
        ArrayList<Beer> beers = new ArrayList<>();
        for (Document doc: beerManagerDB.getBeersUnderAvgFeatureScore(brewery.getUserID(), feature,
                getBreweryScore(brewery.getUserID()))) {
            Document idDoc = (Document) doc.get("_id");
            if (idDoc != null) {
                Beer b = new Beer(idDoc);
                b.setScore(doc.get("feature_score") != null ? Double.parseDouble(doc.get("feature_score").toString()) : -1);
                beers.add(b);
            }
        }
        return beers;
    }

    public boolean updateBeerv(DetailedBeer beer) {
        try {
            UpdateResult updateResult = beersCollection.replaceOne(eq("_id", new ObjectId(beer.getBeerID())),
                    (beer.getBeerDoc()));
            if (updateResult.getMatchedCount() == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean addReviewToBeersCollectionV(Review review, Beer beer) {
        try {
            UpdateResult updateResult = beersCollection.updateOne(eq("_id", new ObjectId(beer.getBeerID())),
                    addToSet("reviews", review.getReviewDoc()));
            return updateResult.getMatchedCount() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
*/

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/*
    private AggregateIterable<Document> getHighestAvgScoreBeersMongoV() {
        //ArrayList<Beer> beers = new ArrayList<>();
        AggregateIterable<Document> list = null;
        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime last_month = LocalDateTime.now().minusMonths(1);
            Bson matchDate = match(and(lt("reviews.date", today), gt("reviews.date", last_month)));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeer = new Document("$group", new Document("_id",
                    new Document("_id", "$_id")
                            .append("name", "$name")
                            .append("style", "$style")
                            .append("abv", "$abv")
                            .append("rating", "$rating"))
                    .append("monthly_score", new Document("$avg", "$reviews.score")));
            Bson projectRoundScore = project(new Document("monthly_score",
                    new Document("$round", Arrays.asList("$monthly_score", 2))));
            Bson sortScore = sort(descending("monthly_score"));
            Bson limitResult = limit(8);

            list = beersCollection.aggregate(Arrays.asList(matchDate, unwindReviews, matchDate, groupBeer, sortScore,
                    limitResult, projectRoundScore));
            for (Document d: list) {
                System.out.println(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private AggregateIterable<Document> getBeersUnderAvgFeatureScoreNestedV(String breweryID, String feature, double breweryScore){
        AggregateIterable<Document> list = null;
        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime past = LocalDateTime.now().minusMonths(6);
            Bson initialMatch = match(and(eq("brewery_id", new ObjectId(breweryID)),
                    lt("reviews.date", today), gt("reviews.date", past)));
            Bson unwindReviews = unwind("$reviews");
            Bson groupBeer = new Document("$group", new Document("_id",
                    new Document("_id", "$_id")
                            .append("name", "$name")
                            .append("style", "$style")
                            .append("abv", "$abv")
                            .append("rating", "$rating"))
                    .append("feature_score", new Document("$avg", "$reviews."+ feature)));
            Bson projectRoundScore = project(new Document("feature_score",
                    new Document("$round", Arrays.asList("$feature_score", 2))));
            Bson matchBreweryScore = match(lt("feature_score", breweryScore));
            Bson sortResult = sort(ascending("feature_score"));
            list = beersCollection.aggregate(Arrays.asList(initialMatch, unwindReviews, groupBeer, projectRoundScore,
                    matchBreweryScore, sortResult));
            for (Document d: list) {
                System.out.println(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



    protected double getWeightedBreweryScoreV(String breweryID) {
        try {
            Bson initialMatch = match(and(eq("brewery_id", new ObjectId(breweryID)), gt("num_rating", 0)));
            Bson groupBrewery = group(new Document("_id", "$brewery_id")
                    .append("rating_sum", new Document("$sum",
                            new Document("$multiply", Arrays.asList("$rating", "$num_rating"))))
                    .append("tot_num_rating",
                            new Document("$sum", "$num_rating")));
            Bson projectRound = project(new Document("brewery_score", new Document("$round",
                    Arrays.asList(new Document("$divide", Arrays.asList("$rating_sum", "$tot_num_rating")), 2))));

            Document doc = beersCollection.aggregate(
                    Arrays.asList(initialMatch, groupBrewery, projectRound)).first();
            if (doc != null) {
                return doc.get("brewery_score") != null ? Double.parseDouble(doc.get("brewery_score").toString()) : -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

*/
    /* ************************************************************************************************************/
    /* *************************************  Neo4J Section  ******************************************************/
    /* ************************************************************************************************************/



    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
  /*     public boolean addBeer(Beer beer){
        return beerManagerDB.addBeer(beer);
     try(Session session = NeoDBMS.getDriver().session()){
            //I First have to see if the style node for this beer is already in the graph
            session.run("MERGE (S:Style{nameStyle: $Style})\n" +
                    "ON CREATE\n" +
                    "SET S.nameStyle= $Style",parameters("Style",beer.getStyle()));
            //I then create the node for the new beer
            session.run("MERGE (B:Beer{ID: $BeerID})",parameters("BeerID",beer.getBeerID()));
            //I create the relationship between the style node and the beer node
            session.run("MATCH\n" +
                            "(B:Beer{ID:$BeerID}),\n" +
                            "(S:Style{nameStyle:$style})\n " +
                            "MERGE (B)-[Ss:SameStyle]->(S)\n",
                    parameters( "BeerID", beer.getBeerID(), "style", beer.getStyle()));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }


    }   */

    /* Function that based on the user current research find some beers to suggest him based on the beer style and favorites of
    *  others users */
   /* public ArrayList<String> getSuggested(StandardUser user){
        return beerManagerDB.getSuggested(user);
        //Looking for how many style this user have in his favorites
 /*       try(Session session = NeoDBMS.getDriver().session()) {
            int n_style=0;
            Result Style_records= session.run("match (U:User{Username:$Username})-[F:Favorite]->(B:Beer)-[Ss:SameStyle]->(S:Style) \n" +
                    "return  distinct S.nameStyle as Style",parameters("Username",user.getUsername()));
            String Style_1="",Style_2="";
            if(Style_records.hasNext()) {
                Style_1= Style_records.next().get("Style").asString();
                if (Style_records.hasNext()) {
                    Style_2 = Style_records.next().get("Style").asString();
                    n_style = 2;
                }
                else{
                    n_style=1;
                }
            }
            if(n_style==0){ //If the user haven't any favorites I return an empty list
                return new ArrayList<>();
            }
            //If less than 4 I return two suggestions for that style
            if(n_style==1){
                String finalStyle_ = Style_1;
                return session.readTransaction(tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 4", parameters("Style", finalStyle_));
                    ArrayList<String> Suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        Suggested.add(r.get("ID").asString());
                    }
                    return Suggested;
                });
            }
            //If more than 2 I take the two with most records and return suggestions on those two
            else{
                String finalStyle_1 = Style_1;
                String finalStyle_2 = Style_2;
                return session.readTransaction(tx -> {
                    Result result = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Style", finalStyle_1));
                    ArrayList<String> suggested = new ArrayList<>();
                    while (result.hasNext()) {
                        Record r = result.next();
                        suggested.add(r.get("ID").asString());
                    }
                    Result result_2 = tx.run("MATCH (B:Beer)-[Ss:SameStyle]->(S:Style{nameStyle:$Style})\n" +
                            "WITH COLLECT(B) as BeersWithSameStyle\n" +
                            "MATCH ()-[F:Favorite]->(B1:Beer)\n" +
                            "WHERE (B1) in BeersWithSameStyle\n" +
                            "RETURN B1.ID as ID,COUNT(DISTINCT F) as FavoritesCount \n" +
                            "ORDER BY FavoritesCount DESC LIMIT 2", parameters("Style", finalStyle_2));
                    while (result_2.hasNext()) {
                        Record r = result_2.next();
                        suggested.add(r.get("ID").asString());
                    }
                    return suggested;
                });
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
  */

    /* Function that calculate the most favorite beers in the past month */
 /*     public ArrayList<FavoriteBeer> getMostFavoriteThisMonth (){
        return beerManagerDB.getMostFavoriteThisMonth();
      try(Session session = NeoDBMS.getDriver().session()){
            //Get the current date
            LocalDateTime MyLDTObj = LocalDateTime.now();
            //Subtract a month
            MyLDTObj=MyLDTObj.minus(Period.ofMonths(1));
            DateTimeFormatter myFormatObj  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //Convert it into a string with the chosen format
            String Starting_date = MyLDTObj.format(myFormatObj);
            //I commit the query and return the value
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH ()-[F:Favorite]->(B:Beer)\n" +
                                "WHERE F.date>=date($starting_Date)\n" +
                                "WITH collect(B) as Fv\n" +
                                "MATCH ()-[F1:Favorite]->(B1:Beer)\n" +
                                "WHERE (B1) in Fv AND F1.date>=date($starting_Date)\n" +
                                "RETURN COUNT(DISTINCT F1) AS Count,B1.ID AS ID ORDER BY Count DESC LIMIT 8",
                        parameters( "starting_Date", Starting_date));
                ArrayList<FavoriteBeer> MostLiked = new ArrayList<>();
                //Saving the results in a List before returning it
                while (result.hasNext()) {
                    Record r = result.next();
                    MostLiked.add(new FavoriteBeer(r.get("ID").asString(),null));
                }
                return MostLiked;
            });
        }
        catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
  */

     /*    protected boolean removeBeerFromNeo(Beer beer){
        return beerManagerDB.removeBeerFromNeo(beer);

        try(Session session = NeoDBMS.getDriver().session()){
            session.run("MATCH (B:Beer {ID: $ID})\n" +
                            "DELETE B;",
                    parameters( "ID", beer.getBeerID()));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }
         */

}
