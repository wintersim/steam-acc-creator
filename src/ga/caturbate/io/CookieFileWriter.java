package ga.caturbate.io;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.util.Cookie;
import ga.caturbate.core.Config;

import java.io.*;

public class CookieFileWriter {

    public void writeCookies(CookieManager cookieManager) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(Config.COOKIE_FILE)))) {
            oos.writeObject(cookieManager);
        }
    }

    public void writeCookiesAsText(CookieManager mgr) throws Exception {
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Config.COOKIE_TXT_FILE)))) {
            for(Cookie c : mgr.getCookies()) {
                pw.printf("Set-Cookie: %s=%s; path=%s%n", c.getName(), c.getValue(), c.getPath());
            }
        }
    }
}
