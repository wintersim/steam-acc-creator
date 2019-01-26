package ga.caturbate.core;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SteamConnectionWrapper extends WebConnectionWrapper {
    private List<String> responses;

    public SteamConnectionWrapper(WebClient webClient) throws IllegalArgumentException {
        super(webClient);
        responses = new LinkedList<>();
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        WebResponse response = super.getResponse(request);
        if (request.getUrl().toExternalForm().contains(Config.STEAM_BASE_URL)) {
            String content = response.getContentAsString();
            
            responses.add(content);

            WebResponseData data = new WebResponseData(content.getBytes(),
                    response.getStatusCode(), response.getStatusMessage(), response.getResponseHeaders());
            response = new WebResponse(data, request, response.getLoadTime());
        }
        return response;
    }
}
