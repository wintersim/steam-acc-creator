package ga.caturbate.core.mail;

import ga.caturbate.core.Config;
import ga.caturbate.io.MailIO;

import javax.mail.MessagingException;
import java.io.IOException;

public class Mailer {
    private String server;
    private MailIO reader;

    public Mailer(String server) {
        this.server = server;
        this.reader = new MailIO();
    }

    public String getSteamVerificationUrl(String user, String pw) throws IOException, MessagingException {
        return reader.getSteamVerifyUrl(server, user, pw);
    }
    public String getSteamVerificationUrl(String user) throws IOException, MessagingException {
        return reader.getSteamVerifyUrl(server, user, Config.MAIL_PASSWD);
    }
}
