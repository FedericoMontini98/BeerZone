package it.unipi.dii.inginf.lsmdb.beerzone.managerDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import static org.neo4j.driver.Values.parameters;

public class Neo4jManager implements AutoCloseable{
    private static Neo4jManager neoInstance = null;
    private final Driver driver;

    /* Class Creator, it creates an instance of GraphDatabase that connects to the Cluster */
    private Neo4jManager() {
        driver = GraphDatabase.driver("bolt://172.16.4.58:7687", AuthTokens.basic("neo4j", "beer"));
    }

    public Driver getDriver() {
        if(neoInstance == null)
            throw new RuntimeException("Connection not created yet.");
        else
            return neoInstance.driver;
    }

    public static Neo4jManager getInstance() {
        if(neoInstance == null)
            neoInstance = new Neo4jManager();

        return neoInstance;
    }

    public void close() {
        if(neoInstance == null)
            throw new RuntimeException("Connection not created yet.");
        else {
            neoInstance.driver.close();
            System.out.println("Neo4j connection closed");
        }
    }
}
