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
    private String _key;
    private String _cx;

    ApiEngine(String key, String cx) {
        _key = key;
        _cx = cx;
    }

    private String sendStringRequest(String query) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

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
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() >= 300) {
            throw new Exception(String.format("failure - received a %d for %s.",
                    response.getStatusLine().getStatusCode(), httpget.getURI().toString()));
        }

        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, "UTF-8");
    }

    private String getRandomImageLink(String json_txt) throws JSONException {
        JSONObject o = new JSONObject(json_txt);
        JSONArray items = o.getJSONArray("items");

        int rand = ThreadLocalRandom.current().nextInt(0, 9);

        JSONObject item = items.getJSONObject(rand);
        return item.getString("link");
    }

    private BufferedImage getImage(String path) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        URI uri = new URI(path);
        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        return ImageIO.read(is);
    }

    BufferedImage getRandomChristmasTreeImage() throws Exception {
        String json_txt = sendStringRequest("Christmas Tree");
        String img_link = getRandomImageLink(json_txt);
        return getImage(img_link);
    }
}
