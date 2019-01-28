package ga.caturbate.core;

public class Config {
    public static final String STEAM_LOGIN_URL = "https://store.steampowered.com/join/";
    public static final String STEAM_BASE_URL = "https://store.steampowered.com/";
    public static final String STEAM_NO_GUARD_URL = "https://store.steampowered.com/twofactor/manage/";
    public static final String STEAM_CAPTCHA_EMAIL_JSON= "{\"bCaptchaMatches\":true,\"bEmailAvail\":true}";
    public static final String STEAM_EMAIL_JSON = "{\"bCaptchaMatches\":false,\"bEmailAvail\":true}";
    public static final String STEAM_CAPTCHA_JSON = "{\"bCaptchaMatches\":true,\"bEmailAvail\":false}";
    public static final String STEAM_NONE_JSON = "{\"bCaptchaMatches\":false,\"bEmailAvail\":false}";
    public static final String STEAM_EMAIL_VERIFIED = "1";

    public static final String MAIL_SERVER_BASE_URL = "http://192.168.0.208/webmail/";
}

enum CaptchaEmailState {
    CAPTCHA_AND_MAIL, MAIL, CAPTCHA, NONE
}

