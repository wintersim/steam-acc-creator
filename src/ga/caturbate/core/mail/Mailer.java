package ga.caturbate.core.mail;

import javax.mail.MessagingException;
import java.io.IOException;

public class Mailer {
    private String server;
    private MailReader reader;

    public Mailer(String server) {
        this.server = server;
        this.reader = new MailReader();
    }

    public String getSteamVerificationUrl(String user, String pw) throws IOException, MessagingException {
        return reader.getSteamVerifyUrl(server, user, pw);
    }

    //Creates new mail user first
    public String getSteamVerificationUrl() throws IOException, MessagingException {
        String user = "";
        String pw = "";
        return reader.getSteamVerifyUrl(server, user, pw);
    }
}
