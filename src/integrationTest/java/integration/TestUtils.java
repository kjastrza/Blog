package integration;

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
    public static final String HOST = BUNDLE.getString("test.host");
    public static final String PORT = BUNDLE.getString("test.port");

    public static String getUrl(String restPath) {
        return String.format("http://%s:%s/" + APP_CONTEXT + restPath, HOST, PORT);
    }
}
