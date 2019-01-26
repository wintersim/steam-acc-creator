package ga.caturbate.core;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.logging.Level;

public class SteamAccountCreator {

    private static final String TEST = "http://caturbate.ga/";

    private SteamConnectionWrapper wrapper;

    public SteamAccountCreator() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public void downloadSteamLoginPage() {
        try(final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setDownloadImages(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCookieManager().setCookiesEnabled(true);

            wrapper = new SteamConnectionWrapper(webClient);
            HtmlPage page = webClient.getPage(Config.STEAM_LOGIN_URL);



            sendRegistrationForm(page, webClient);


         /*  HtmlPage page = webClient.getPage("http://127.0.0.1/");

           HtmlForm form = page.getFormByName("test");
           form.getInputByName("userName").setValueAttribute("tester");
           form.getInputByName("userName").setValueAttribute("tester");
           form.getInputByName("userName").setValueAttribute("tester");
           HtmlSubmitInput button = form.getInputByName("submitbtn");

           HtmlPage p2 = button.click();

            System.out.println(p2.getWebResponse().getContentAsString().contains("NIGGER"));
*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void sendRegistrationForm(HtmlPage page, WebClient webClient) throws IOException {

        /* Fill form*/

        //TODO Hier fehler, evtl input vergessen o.Ä.

        HtmlForm form = page.getFormByName("create_account");

        form.getInputByName("email").setValueAttribute("testr@gmail.com");
        form.getInputByName("reenter_email").setValueAttribute("testr@gmail.com");
        HtmlSelect country = form.getSelectByName("country");
        country.setSelectedAttribute("AT",true );
        form.getInputByName("lt").setValueAttribute("0");

        HtmlCheckBoxInput checkBoxInput = form.getInputByName("i_agree_check");
        checkBoxInput.setChecked(true);

        //captcha
        HtmlImage img = (HtmlImage) page.getElementById("captchaImg");
        System.out.println(img.getAttribute("src"));
        System.out.println("id");
        Scanner s = new Scanner(System.in);
        String id = s.nextLine();
        System.out.println("captcha");
        String cap = s.nextLine();

        form.getInputByName("captchagid").setValueAttribute(id);
        form.getInputByName("captcha_text").setValueAttribute(cap);

        //fake submit button
        HtmlButton btn = (HtmlButton) page.createElement("button");
        btn.setAttribute("type","submit" );
        form.appendChild(btn);

        btn.click();

        webClient.waitForBackgroundJavaScript(3000);

        page.executeJavaScript("StartCreationSession();"); //evtl nur ausführen, wenn captcha matcht
    }
}
