package ga.caturbate.core.mail;

import ga.caturbate.io.MailReader;

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

    /* TODO
     *
     * send GET to php script
     * script launsches sh script to all vmail users
     * script echoes usernames of said vmailusers to HTML
     * this program reads the HTML and reads the vmailusers
     */
    public String getSteamVerificationUrl() throws IOException, MessagingException {
        String user = "";
        String pw = "";
        return reader.getSteamVerifyUrl(server, user, pw);
    }
}
