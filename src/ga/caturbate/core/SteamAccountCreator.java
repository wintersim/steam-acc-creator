package ga.caturbate.core;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class SteamAccountCreator {

    Document doc;

    private static final String STEAM_LOGIN_URL = "https://store.steampowered.com/join/";

    public SteamAccountCreator() {

    }

    public void downloadSteamLoginPage() {
        try {
            doc = Jsoup.connect(STEAM_LOGIN_URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element captchaImg = doc.getElementById("captchaImg");
        String url = captchaImg.attr("src");
        System.out.println(url);
    }

    private void sendRegistrationForm() {

    }
}
