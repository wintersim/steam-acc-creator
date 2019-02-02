package ga.caturbate.io;

import com.gargoylesoftware.htmlunit.CookieManager;
import ga.caturbate.core.Config;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CookieFileWriter {

    public void writeCookies(CookieManager cookieManager) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(Config.COOKIE_FILE)))) {
            oos.writeObject(cookieManager);
        }
    }
}
