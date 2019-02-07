package ga.caturbate.test;

import ga.caturbate.core.mail.MailReader;
import ga.caturbate.core.SteamAccountCreator;

import javax.mail.MessagingException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SteamAccountCreator accountCreator = new SteamAccountCreator();
        //accountCreator.start();

        MailReader mailReader = new MailReader();
        //mailReader.getSteamVerifyUrl("cowsayb00st_29", "123456");

    }
}
