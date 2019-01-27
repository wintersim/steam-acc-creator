package ga.caturbate.core;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SteamConnectionWrapper extends WebConnectionWrapper {
    private List<String> responses;
    private SteamAccountCreator creator;
    private WebClient client;

    public SteamConnectionWrapper(WebClient webClient, SteamAccountCreator creator) throws IllegalArgumentException {
        super(webClient);
        responses = new LinkedList<>();
        this.creator = creator;
        this.client = webClient;
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        request.setAdditionalHeader(HttpHeaders.ACCEPT_ENCODING, "identity");
        WebResponse response = super.getResponse(request);
        if (request.getUrl().toExternalForm().contains(Config.STEAM_BASE_URL)) {
            String content = response.getContentAsString();

            System.out.println("Request: " + request.getRequestBody());
            System.out.println("Response: " + content);

            responses.add(content);

          //  if(content.contains(Config.STEAM_ACCOUNT_FINISH_STRING)) { //If response is username/password page --> callback
              //  creator.sendUsernameAndPassword(client, content, request.getUrl());
          //  }

            WebResponseData data = new WebResponseData(content.getBytes(),
                    response.getStatusCode(), response.getStatusMessage(), response.getResponseHeaders());
            response = new WebResponse(data, request, response.getLoadTime());
        }
        return response;
    }
}
