package ga.caturbate.core;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.apache.http.HttpHeaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SteamConnectionWrapper extends WebConnectionWrapper {
    private List<String> responses;
    private WebClient client;
    private Map<String, CaptchaEmailState> captchaStates;

    public SteamConnectionWrapper(WebClient webClient) throws IllegalArgumentException {
        super(webClient);
        responses = new LinkedList<>();
        captchaStates = new HashMap<>();
        captchaStates.put(Config.STEAM_CAPTCHA_EMAIL_JSON, CaptchaEmailState.CAPTCHA_AND_MAIL);
        captchaStates.put(Config.STEAM_CAPTCHA_JSON, CaptchaEmailState.CAPTCHA);
        captchaStates.put(Config.STEAM_EMAIL_JSON, CaptchaEmailState.MAIL);
        captchaStates.put(Config.STEAM_NONE_JSON, CaptchaEmailState.NONE);
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        request.setAdditionalHeader(HttpHeaders.ACCEPT_ENCODING, "identity");
        WebResponse response = super.getResponse(request);
        if (request.getUrl().toExternalForm().contains(Config.STEAM_BASE_URL)) {
            String content = response.getContentAsString();

            System.out.println("Request: " + request.getRequestBody());
            if(!response.getContentType().contains("text/html")) {
                System.out.println("Response: " + content);
            }
            else {
                System.out.println("Response: too long");
            }

            responses.add(content);


            WebResponseData data = new WebResponseData(content.getBytes(),
                    response.getStatusCode(), response.getStatusMessage(), response.getResponseHeaders());
            response = new WebResponse(data, request, response.getLoadTime());
        }
        return response;
    }

    public CaptchaEmailState getCaptchaState() {
        for (String respons : responses) {
            for (Map.Entry<String, CaptchaEmailState> entry : captchaStates.entrySet()) {
                if(respons.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public boolean getEmailState() {
        for (String respons : responses) {
            if(respons.equalsIgnoreCase(Config.STEAM_EMAIL_VERIFIED)) {
                return true;
            }
        }
        return false;
    }
}
