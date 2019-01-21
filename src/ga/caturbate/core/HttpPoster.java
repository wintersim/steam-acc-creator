package ga.caturbate.core;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

public class HttpPoster {
    HttpClient httpClient;
    HttpPost httpPost;

    public HttpPoster() {
        httpClient = HttpClients.createDefault();
    }

    public void postRegistration(String email, String country, String captcha) {
        
    }
}
