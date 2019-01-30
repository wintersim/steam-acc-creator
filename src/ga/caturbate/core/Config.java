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

    public static final String DEFAULT_COUNTRY_CODE = "AT";

    public static final String EMAI_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String USERNAME_REGEX = "[\\w]+";
    public static final String PASSWORD_REGEX = "[\\w]+";

    public static final String MAIL_SERVER_BASE_URL = "http://192.168.0.208/webmail/";
}

enum CaptchaEmailState {
    CAPTCHA_AND_MAIL, MAIL, CAPTCHA, NONE
}

