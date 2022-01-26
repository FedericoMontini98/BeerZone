# BeerZone
Project for Large-Scale and Multi-Structured Databases course at University of Pisa (CE &amp; AIDE Master Degree)

The application provides a service named “BeerZone”, in which a registered user can for example browse for beers, look for recipes, add new beer and review them.
This application was developed using Java and Java Swing.

Evaluating the needs of our project, we chose to use Neo4j and MongoDB as non-relational databases. 
Neo4J was used to leverage data in a way that would provide users with suggestions based on the user's tastes, namely it was used to develop the social part, taking advantage of its main feature of memorizing relationships between entities.
MongoDB on the other hand was used as main storage, thanks to its sharding capability, and to provide various features such as computing Beers or Brewery statistics.
