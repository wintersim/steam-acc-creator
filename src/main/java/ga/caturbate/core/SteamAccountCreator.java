package ga.caturbate.core;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import ga.caturbate.core.mail.Mailer;
import ga.caturbate.io.AccountIO;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * TODO:
 *
 * Transform into JavaFX application
 * Automatically solve captchas (maybe use external service)
 * Clean up code
 * Add support for creating multiple accounts at once (more threads)
 */

public class SteamAccountCreator {


    private SteamConnectionWrapper wrapper;
    private AccountIO accountIO;

    public SteamAccountCreator() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        accountIO = new AccountIO();
    }


    //TODO: only continue if captcha matches and other errorchecks
    public void start() {
        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
            webclientDefaulSettings(webClient);
            //webClient.getOptions().setProxyConfig(new ProxyConfig("127.0.0.1", 8080));
            //webClient.getOptions().setUseInsecureSSL(true);

            wrapper = new SteamConnectionWrapper(webClient);

            for (int i = 0; i < 5; i++) {


                SteamAccount acc = fetchNewSteamAccount(webClient);

                if (acc == null) {
                    System.out.println("Failed to fetch account from server");
                    return;
                }

                sendRegistrationForm(webClient, acc);

                deactivateSteamGuard(webClient, new WebRequest(new URL(Config.STEAM_NO_GUARD_URL)));

                accountIO.writeToAccountFile("out.txt", acc);

                webClient.getCookieManager().clearCookies();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void webclientDefaulSettings(WebClient webClient) {
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getCache().setMaxSize(0);
    }

    private void sendRegistrationForm(WebClient webClient, SteamAccount account) throws IOException, MessagingException {
        HtmlPage page = webClient.getPage(Config.STEAM_LOGIN_URL);

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
        for (int i = 0; i < 5; i++) {
            state = wrapper.getCaptchaState();
            if (state == CaptchaEmailState.CAPTCHA_AND_MAIL) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i == 4) {
                System.out.println("Error[" + state + "] Stopping...");
                webClient.close();
                System.exit(1);
            }
        }

        //Age check dialog
        page.executeJavaScript("StartCreationSession();"); //evtl nur ausführen, wenn captcha matcht

        //Give Email a Chance to arrive
        //Todo: wait in loop until arrived
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Mailer m = new Mailer("192.168.0.208");
        String url = m.getSteamVerificationUrl(account.getEmail());
        openEmailVerificationWindow(url);

        //Wait for 'email verified' packet arrival
        for (int i = 0; i < 50000; i++) {
            if (wrapper.getEmailState()) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i == 49999) {
                System.out.println("Email cannot be verified. Try another one. Exiting...");
                webClient.close();
                System.exit(1);
            }
        }

        webClient.waitForBackgroundJavaScript(60000);

        //Save redirected page
        page = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();

        sendUsernameAndPassword(webClient, page, account);
    }

    private void openEmailVerificationWindow(String url) {

        Thread one = new Thread() {
            public void run() {
                try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
                    webclientDefaulSettings(webClient);

                    webClient.getPage(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        one.start();

    }

    public void sendUsernameAndPassword(WebClient client, HtmlPage page, SteamAccount account) {
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

        HtmlInput sessionID = (HtmlInput) page.createElement("input");
        sessionID.setAttribute("name", "creation_sessionid");
        sessionID.setValueAttribute(sessID);
        form.appendChild(sessionID);

        page.executeJavaScript("ReallyCreateAccount();");
        client.waitForBackgroundJavaScript(20000);
        page = (HtmlPage) page.getEnclosingWindow().getEnclosedPage();
    }

    //TODO login
    private void deactivateSteamGuard(WebClient client, WebRequest request) throws IOException {
        HtmlPage page = client.getPage(request);

        HtmlForm form = (HtmlForm) page.getElementById("none_authenticator_form");
        form.getInputByName("action").setValueAttribute("actuallynone");
        form.getInputByName("none_authenticator_check").remove(); //Prevent going to confirmation page

        HtmlButton btn = (HtmlButton) page.createElement("button");
        btn.setAttribute("type", "submit");
        form.appendChild(btn);

        btn.click();
    }

    private SteamAccount fetchNewSteamAccount(WebClient client) throws IOException {
        String login;
        String mail;
        String pw;

        HtmlPage page = client.getPage(Config.ACCOUNT_CREATE_SERVER);

        HtmlPreformattedText pre = (HtmlPreformattedText) page.getElementById("user");
        String[] parts = pre.getTextContent().split(":");

        if (parts.length != 3) {
            return null;
        }

        login = parts[0];
        pw = parts[1];
        mail = parts[2] + "@" + Config.DEFAULT_MAIL_SERVER;

        return new SteamAccount(login, mail, pw);
    }
}
