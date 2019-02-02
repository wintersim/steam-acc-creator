package ga.caturbate.io;

import com.gargoylesoftware.htmlunit.CookieManager;
import ga.caturbate.core.Config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CookieFileReader {

    public CookieManager readCookies() throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(Config.COOKIE_FILE)))) {
            return (CookieManager) ois.readObject();
        }
    }
}
