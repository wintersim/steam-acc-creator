package ga.caturbate.io;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MailReader {

    public MailReader() {

    }

    public String getSteamVerifyUrl(String server,String mailUser, String mailPasswd) throws MessagingException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore("imap");
        store.connect(server, 143, mailUser, mailPasswd);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();

        // Sort messages from recent to oldest
        Arrays.sort(messages, (m1, m2) -> {
            try {
                return m2.getSentDate().compareTo(m1.getSentDate());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        String msg = "";
        String regex = "((<https:\\/\\/store.steampowered.com\\/email\\/AccountCreationEmailVerification\\?).*>)";

        //TODO ugly
        for (Message message : messages) {
            if (message.isMimeType("multipart/*")) {
                MimeMultipart tmp = (MimeMultipart) message.getContent();
                for (int i = 0; i < tmp.getCount(); i++) {
                    BodyPart ibp = tmp.getBodyPart(i);
                    if(((String)ibp.getContent()).contains("Verify your email address to complete creating your Steam account.")) {
                        if (ibp.isMimeType("text/plain")) {
                            msg = (String) ibp.getContent();
                        }
                    }
                }
            }
        }

        if(msg.equalsIgnoreCase("")) {
            System.out.println("noo");
        }

        //find url from msg

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        String url = "0";
        if (matcher.find()) {
            url = matcher.group(1);
        }

        //remove brackets
        url = url.replace("<", "");
        url = url.replace(">", "");
        return url;
    }

}
