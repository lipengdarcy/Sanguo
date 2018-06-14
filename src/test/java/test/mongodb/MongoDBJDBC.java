package test.mongodb;

import org.bson.Document;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBJDBC {

	public static void main(String args[]) {
		MongoClient mongoClient = null;
		try {
			// To connect to mongodb server
			mongoClient = new MongoClient("localhost", 27017);
			// Now connect to your databases
			MongoDatabase db = mongoClient.getDatabase("test");
			System.out.println("Connect to database successfully");
			// boolean auth = db.authenticate("mongo_user", "mongo_pass");
			// System.out.println("Authentication: " + auth);

			MongoCollection<Document> table = db.getCollection("test_table");
			if (table != null)
				System.out.println("Collection test_table selected successfully");
			else {
				db.createCollection("test_table");
				System.out.println("Collection created successfully");
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			mongoClient.close();
		}
	}
}
