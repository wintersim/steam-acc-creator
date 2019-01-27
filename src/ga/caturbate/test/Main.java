package ga.caturbate.test;

import ga.caturbate.core.SteamAccountCreator;
import ga.caturbate.core.WebMailReader;

public class Main {
    public static void main(String[] args) {
        SteamAccountCreator accountCreator = new SteamAccountCreator();
        //accountCreator.start();

        WebMailReader mailReader = new WebMailReader();
        mailReader.getSteamVerifyUrl("cowsayb00st_29", "123456");
    }
}
