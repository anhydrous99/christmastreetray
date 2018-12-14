import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;
import java.net.URI;

import javax.imageio.ImageIO;

class ApiEngine {
    private String _key; // will store the api key
    private String _cx;  // will store the custom search id

    // Constructor
    ApiEngine(String key, String cx) {
        _key = key;
        _cx = cx;
    }

    // Uses apache's httpclient to send & receive JSON from google search api
    private String sendStringRequest(String query) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Builds the Uri for the request
        // e.x. https://www.googleapis.com/customsearch/v1?cx=stuff&key=stuff ...
        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("www.googleapis.com")
                .setPath("/customsearch/v1")
                .setParameter("cx", _cx)
                .setParameter("key", _key)
                .setParameter("q", query)
                .setParameter("dateRestrict", "m1")
                .setParameter("safe", "active")
                .setParameter("searchType", "image")
                .setParameter("imgType", "photo")
                .setParameter("imgSize", "xlarge")
                .setParameter("num", "10")
                .build();
        HttpGet httpget = new HttpGet(uri); // Dictates our intention on using a GET http header
        CloseableHttpResponse response = httpclient.execute(httpget); // Executes
        if (response.getStatusLine().getStatusCode() >= 300) { // if error
            throw new Exception(String.format("failure - received a %d for %s.",
                    response.getStatusLine().getStatusCode(), httpget.getURI().toString()));
        }

        HttpEntity entity = response.getEntity(); // Gets response and returns it as string
        return EntityUtils.toString(entity, "UTF-8");
    }

    // Interprets a JSON format google search and randomly picks the link of an image from it
    private String getRandomImageLink(String json_txt) throws JSONException {
        JSONObject o = new JSONObject(json_txt);
        JSONArray items = o.getJSONArray("items");

        // Creates random number
        int rand = ThreadLocalRandom.current().nextInt(0, 9);

        // Gets random item
        JSONObject item = items.getJSONObject(rand);
        return item.getString("link"); // returns said item's image link
    }

    // Gets an image using it's link and returns it a buffered image
    private BufferedImage getImage(String path) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        URI uri = new URI(path);
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        return ImageIO.read(is);
    }

    // Puts it all together
    BufferedImage getRandomChristmasTreeImage() throws Exception {
        String json_txt = sendStringRequest("Christmas Tree");
        String img_link = getRandomImageLink(json_txt);
        return getImage(img_link);
    }
}
