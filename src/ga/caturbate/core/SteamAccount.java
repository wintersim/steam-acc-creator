package ga.caturbate.core;

public class SteamAccount implements Comparable<SteamAccount>{
    private String login;
    private String email;
    private String password;
    private String countryCode;
    private boolean isCreated;

    public SteamAccount(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.countryCode = Config.DEFAULT_COUNTRY_CODE;
        this.isCreated = false;
    }

    public SteamAccount(String login, String email, String password, String countryCode) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
        this.isCreated = false;
    }

    public SteamAccount(String login, String password) {
        this.login = login;
        this.password = password;
        this.email = "";
        this.countryCode = Config.DEFAULT_COUNTRY_CODE;
        this.isCreated = true;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SteamAccount)) return false;

        SteamAccount that = (SteamAccount) o;

        if (isCreated != that.isCreated) return false;
        if (getLogin() != null ? !getLogin().equals(that.getLogin()) : that.getLogin() != null) return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return getCountryCode() != null ? getCountryCode().equals(that.getCountryCode()) : that.getCountryCode() == null;
    }

    @Override
    public int hashCode() {
        int result = getLogin() != null ? getLogin().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getCountryCode() != null ? getCountryCode().hashCode() : 0);
        result = 31 * result + (isCreated ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(SteamAccount o) {
        int ret = -1;

        if (o != null) {
            ret = login.compareTo(o.getLogin());

            if(ret == 0) {
                ret = email.compareTo(o.getEmail());

                if(ret == 0) {
                    ret = countryCode.compareTo(o.getCountryCode());

                    if (ret == 0) {
                        ret = password.compareTo(o.getPassword());

                        if(ret == 0) {
                            ret = Boolean.compare(o.isCreated, isCreated);
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "SteamAccount{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", isCreated=" + isCreated +
                '}';
    }
}
