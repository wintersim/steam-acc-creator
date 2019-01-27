package ga.caturbate.core;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamAccountCreator {


    private SteamConnectionWrapper wrapper;

    public SteamAccountCreator() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public void downloadSteamLoginPage() {
        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setDownloadImages(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getCache().setMaxSize(0);
            webClient.getOptions().setProxyConfig(new ProxyConfig("127.0.0.1", 8080));
            webClient.getOptions().setUseInsecureSSL(true);

            HtmlPage page = webClient.getPage(Config.STEAM_LOGIN_URL);
            wrapper = new SteamConnectionWrapper(webClient, this);

            System.out.println(webClient.getOptions().getSSLClientCertificateStore());

            sendRegistrationForm(page, webClient);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendRegistrationForm(HtmlPage page, WebClient webClient) throws IOException {

        /* Fill form*/

        HtmlForm form = page.getFormByName("create_account");

        form.getInputByName("email").setValueAttribute("cowsayb00st_34@caturbate.ga");
        form.getInputByName("reenter_email").setValueAttribute("cowsayb00st_34@caturbate.ga");
        HtmlSelect country = form.getSelectByName("country");
        country.setSelectedAttribute("AT", true);
        /*form.getInputByName("lt").setValueAttribute("0");
        form.getInputByName("lt").setAttribute("hidden","true" );*/

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
        btn.setAttribute("type", "submit");
        form.appendChild(btn);

        btn.click();

        page.executeJavaScript("StartCreationSession();"); //evtl nur ausführen, wenn captcha matcht
        webClient.waitForBackgroundJavaScript(60000);

        page = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();

        sendUsernameAndPassword(webClient, page);

        // System.out.println("Enter email verification url");
        // openEmailVerificationWindow(webClient, s.nextLine());
    }

    private void openEmailVerificationWindow(WebClient client, String url) {
        try {
            client.openWindow(new URL(url), "verify");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void sendUsernameAndPassword(WebClient client, HtmlPage page) { //callback from connection wrapper
        for (Cookie cookie : client.getCookieManager().getCookies()) {
            System.out.println(cookie);
        }
        HtmlForm form = page.getFormByName("create_account");
        form.getInputByName("accountname").setValueAttribute("cowsayb00st__34");

        form.getInputByName("password").setValueAttribute("aX4LlUknAg88");

        form.getInputByName("reenter_password").setValueAttribute("aX4LlUknAg88");

        form.getInputByName("lt").setValueAttribute("0");

        HtmlInput count = (HtmlInput) page.createElement("input");
        count.setAttribute("name", "count");
        count.setValueAttribute("6");
        form.appendChild(count);

        //Extract sessionID from raw html
        Pattern pattern = Pattern.compile("'(\\d+)'");
        Matcher matcher = pattern.matcher(page.asXml());
        String sessID = "123";
        if (matcher.find()) {
            sessID = matcher.group(1);
        }

        System.out.println(sessID);

        HtmlInput sessionID = (HtmlInput) page.createElement("input");
        sessionID.setAttribute("name", "creation_sessionid");
        sessionID.setValueAttribute(sessID);
        form.appendChild(sessionID);

        System.out.println(page.executeJavaScript("ReallyCreateAccount();"));
        client.waitForBackgroundJavaScript(60000);
        page = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();
    }

    private void deactivateSteamGuard(WebClient client, HtmlPage page) {

    }
}
