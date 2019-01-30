package ga.caturbate.core;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
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


    //TODO: only continue if captcha matches and other errorchecks
    public void start() {
        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setDownloadImages(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            webClient.getCache().setMaxSize(0);
            //webClient.getOptions().setProxyConfig(new ProxyConfig("127.0.0.1", 8080));
            //webClient.getOptions().setUseInsecureSSL(true);

            HtmlPage page = webClient.getPage(Config.STEAM_LOGIN_URL);
            wrapper = new SteamConnectionWrapper(webClient);

            sendRegistrationForm(page, webClient, new SteamAccount("wh4tAn3picG4mer420", "deadmigner@yandex.com", "asjkdhf74kh4"));

            //deactivateSteamGuard(webClient);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendRegistrationForm(HtmlPage page, WebClient webClient, SteamAccount account) throws IOException {

        /* Fill out form*/

        HtmlForm form = page.getFormByName("create_account");

        form.getInputByName("email").setValueAttribute(account.getEmail());
        form.getInputByName("reenter_email").setValueAttribute(account.getEmail());
        HtmlSelect country = form.getSelectByName("country");
        country.setSelectedAttribute(account.getCountryCode(), true);

        HtmlCheckBoxInput checkBoxInput = form.getInputByName("i_agree_check");
        checkBoxInput.setChecked(true);

        //captcha
        HtmlImage img = (HtmlImage) page.getElementById("captchaImg");
        String captchaSrc = img.getAttribute("src");
        System.out.println(captchaSrc);
        Scanner s = new Scanner(System.in);
        String id = captchaSrc.split("=")[1];
        System.out.println("captcha");
        String cap = s.nextLine();

        form.getInputByName("captchagid").setValueAttribute(id);
        form.getInputByName("captcha_text").setValueAttribute(cap);

        //fake submit button
        HtmlButton btn = (HtmlButton) page.createElement("button");
        btn.setAttribute("type", "submit");
        form.appendChild(btn);

        btn.click();

        CaptchaEmailState state;

        //Try 5 times if 'captcha matches' packet arrived
        for(int i = 0; i < 5; i++) {
            state = wrapper.getCaptchaState();
            if (state == CaptchaEmailState.CAPTCHA_AND_MAIL) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i == 4) {
                System.out.println("Error[" + state + "] Stopping...");
                webClient.close();
                System.exit(1);
            }
        }

        //Age check dialog
        page.executeJavaScript("StartCreationSession();"); //evtl nur ausführen, wenn captcha matcht

        //Wait for 'email verified' packet arrival
        // TODO Enum for packet states(matching, notmatching,waiting)
        for(int i = 0; i < 50000; i++) {
            if (wrapper.getEmailState()) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i == 49999) {
                System.out.println("Email cannot be verified. Try another one. Exiting...");
                webClient.close();
                System.exit(1);
            }
        }

        webClient.waitForBackgroundJavaScript(60000);

        //Save redirected page
        page = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();

        sendUsernameAndPassword(webClient, page, account);

        //TODO read email from local server or from webmail
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

    public void sendUsernameAndPassword(WebClient client, HtmlPage page, SteamAccount account) {
        for (Cookie cookie : client.getCookieManager().getCookies()) {
            System.out.println(cookie);
        }

        //Fill out form

        HtmlForm form = page.getFormByName("create_account");
        form.getInputByName("accountname").setValueAttribute(account.getLogin());

        form.getInputByName("password").setValueAttribute(account.getPassword());

        form.getInputByName("reenter_password").setValueAttribute(account.getPassword());

        form.getInputByName("lt").setValueAttribute("0");

        HtmlInput count = (HtmlInput) page.createElement("input");
        count.setAttribute("name", "count");
        count.setValueAttribute("6");
        form.appendChild(count);

        //Extract sessionID from raw html
        Pattern pattern = Pattern.compile("'(\\d+)'");
        Matcher matcher = pattern.matcher(page.asXml());
        String sessID = "0";
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


    //TODO WIP
    private void deactivateSteamGuard(WebClient client) throws IOException {
        HtmlPage page = client.getPage(Config.STEAM_NO_GUARD_URL);

        HtmlForm form = (HtmlForm) page.getElementById("none_authenticator_form");
        form.getInputByName("action").setValueAttribute("actuallynone");
        form.removeChild(page.getElementById("none_authenticator_check"));

        HtmlButton btn = (HtmlButton) page.createElement("button");
        btn.setAttribute("type", "submit");
        form.appendChild(btn);

        HtmlPage p2 = btn.click();
        System.out.println(p2.asXml());
    }
}
