package integration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static integration.TestUtils.getUrl;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 10/26/13
 * Time: 9:59 PM
 */
public class ResponseCodeTest {

    @Test
    public void shouldReceive200Response() throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet(getUrl("/rest/posts/"));
        HttpResponse httpResponse = httpClient.execute(request);
        Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReceive404Response() throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet(getUrl("/rest/posts/123"));
        HttpResponse httpResponse = httpClient.execute(request);
        Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
    }
}
