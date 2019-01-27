package ga.caturbate.core;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;

public class WebMailReader {

    public WebMailReader() {

    }

    public String getSteamVerifyUrl(String mailLogin, String mailPw) {
        try(final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setDownloadImages(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getCache().setMaxSize(0);

            HtmlPage page = webClient.getPage(Config.MAIL_SERVER_BASE_URL);

            HtmlForm form = page.getFormByName("form");

            form.getInputByName("_user").setValueAttribute(mailLogin);
            form.getInputByName("_pass").setValueAttribute(mailPw);

            page = form.getInputByValue("Login").click();

            HtmlTableRow email = null;


           // System.out.println(page.asText());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
