package it.unipi.dii.inginf.lsmdb.beerzone.managerDB;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

public class Neo4jManager {
    private final Driver driver;

    /* Class Creator, it creates an instance of GraphDatabase that connects to the Cluster */
    public Neo4jManager( String uri, String user, String password){
        driver=GraphDatabase.driver(uri,
                                    AuthTokens.basic(user,password));
    }

    public void close()
    {
        driver.close();
    }

    /* Function used to add a specific Beer (Given its ID) to the list of favorites of a specific User (Given its
    * Username). This function has to be available only if the beer isn't already in the favorites list */
    public boolean addFavorite(String Username, String BeerID){
        try(Session session = driver.session()){
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            String str = formatter.format(date);
            session.run("MATCH\n" +
                    "  (B:Beer),\n" +
                    "  (U:User)\n" +
                    "WHERE U.username = $Username AND B.id = $BeerID'\n" +
                    "CREATE (U)-[F:Favorites{InDate:$Date}]->(B)\n",
                        parameters( "Username", Username, "BeerID", BeerID,"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove a favorite beer from the users favorites list. To identify a relationship we need the
    *  Username and the BeerID, this functionality has to be available on a specific beer only if a User has it in its
    *  favorites */
    public boolean removeFavorite(String Username, String BeerID){
        try(Session session = driver.session()){
            session.run("MATCH (U:User {username: $Username})-[F:Favorites]-(B:Beer {id: $BeerID}) \n" +
                            "DELETE F",
                        parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to add StandardUser Nodes in the graph, the only property that they have is Username which is common
    *  Both to reviews and User's files */
    public boolean AddStandardUser(String Username){
        try(Session session = driver.session()){
            session.run("CREATE (U:User{username: $Username})",parameters("Username",Username));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to add Beer Nodes in the graph, the only property that they have is id which is common
     *  Both to reviews and beer's files */
    public boolean AddBeer (String BeerID){
        try(Session session = driver.session()){
            session.run("CREATE (B:Beer{id: $BeerID})",parameters("BeerID",BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to add the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer hasn't been reviewed from this user yet to avoid multiple
     * reviews from the same user which can lead to inconsistency or fake values of the avg. score */
    public boolean addReview(String Username, String BeerID){
        try(Session session = driver.session()){
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            String str = formatter.format(date);
            session.run("MATCH\n" +
                            "  (B:Beer),\n" +
                            "  (U:User)\n" +
                            "WHERE U.username = $Username AND B.id = $BeerID\n" +
                            "CREATE (U)-[R:Reviewed{InDate:$Date}]->(B)\n",
                    parameters( "Username", Username, "BeerID", BeerID,"Date", str));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to remove the relationship of 'reviewed' between a beer and a specific User.
     * This function has to be available only if the beer has been reviewed from this user */
    public boolean removeReview(String Username, String BeerID){
        try(Session session = driver.session()){
            session.run("MATCH (U:User {username: $Username})-[R:Reviewed]-(B:Beer {id: $BeerID}) \n" +
                            "DELETE R",
                    parameters( "Username", Username, "BeerID", BeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /* Function used to add the relationship of "SameStyle" between two beers.
    *  It's used to suggest beers based on the common styles between the beers that can be suggested and the beers
    *  in their favorites */
    public boolean addSameStyle(String firstBeerID,String secondBeerID){
        try(Session session = driver.session()){
            session.run("MATCH\n" +
                                "(B1:Beer),\n" +
                                "(B2:Beer)\n " +
                            "WHERE B1.id = $BeerID AND B2.id = $BeerID \n" +
                            "CREATE (B1)-[S:SameStyle]->(B2)\n" +
                            "CREATE (B2)-[S:SameStyle]->(B1)\n",
                    parameters( "firstBeerID", firstBeerID, "secondBeerID", secondBeerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBeer(String beerID){
        try(Session session = driver.session()){
            session.run("MATCH (B {id: $beerID})\n" +
                            "DETACH DELETE B",
                    parameters( "beerID", beerID));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeUser(String username){
        try(Session session = driver.session()){
            session.run("MATCH (U {id: $username})\n" +
                            "DETACH DELETE U",
                    parameters( "username", username));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
