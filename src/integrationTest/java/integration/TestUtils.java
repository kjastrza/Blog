package integration;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 11/17/13
 * Time: 12:31 PM
 */
public class TestUtils {
    private static final String BUNDLE_FILE_NAME = "test";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_FILE_NAME);
    private static final String APP_CONTEXT = "Blog-1.0";
    static final String HOST = BUNDLE.getString("test.host");
    static final String PORT = BUNDLE.getString("test.port");
    static final String DB_HOST = BUNDLE.getString("mongodb.host");
    static final String DB_PORT = BUNDLE.getString("mongodb.port");
    static final String DB_NAME = BUNDLE.getString("mongodb.name");
    static final String POSTS_COLLECTION = "posts";


    public static String getUrl(String restPath) {
        return String.format("http://%s:%s/" + APP_CONTEXT + restPath, HOST, PORT);
    }

    public static void clearDB() throws UnknownHostException {
        DB db = new MongoClient(DB_HOST, Integer.parseInt(DB_PORT)).getDB(DB_NAME);
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        collection.remove(new BasicDBObject());
    }
}
