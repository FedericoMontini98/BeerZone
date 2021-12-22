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
            session.run("MATCH (U:User {Name: $Username})-[F:Favorites]-(B:Beer {Name: $BeerID) \n" +
                            "DELETE r",
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
            session.run("CREATE (U:User{name: $Username})",parameters("Username",Username));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
