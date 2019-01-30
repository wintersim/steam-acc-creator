package ga.caturbate.io;

import ga.caturbate.core.Config;
import ga.caturbate.core.SteamAccount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*File format:
 *
 * email:login:password
 *
 * */

public class AccountFileReader {

    public AccountFileReader() {
    }

    public Set<SteamAccount> readAccountFile(String filename) throws IOException {
        Set<SteamAccount> accs = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String currentline, email, login, password;
            String[] splitted;
            Pattern email_rgx = Pattern.compile(Config.EMAI_REGEX);
            Pattern login_rgx = Pattern.compile(Config.USERNAME_REGEX);
            Pattern passwd_rgx = Pattern.compile(Config.PASSWORD_REGEX);
            Matcher m;

            while((currentline = br.readLine()) != null) {
                splitted = currentline.split(":");
                if(splitted.length == 3) {
                    m = email_rgx.matcher(splitted[0]);
                    email = m.matches() ? splitted[0] : null;

                    m = login_rgx.matcher(splitted[1]);
                    login = m.matches() ? splitted[1] : null;

                    m = passwd_rgx.matcher(splitted[2]);
                    password = m.matches() ? splitted[2] : null;

                    accs.add(new SteamAccount(login,email , password));
                }
            }
        }
        return accs;
    }
}
