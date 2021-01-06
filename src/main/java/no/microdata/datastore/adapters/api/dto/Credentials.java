package no.microdata.datastore.adapters.api.dto;

import java.util.Map;

public class Credentials {

    final static String VALID_USERNAME = "StatCommandRunner";
    final static String VALID_PASSWORD = "ValidPass";
    final static Map ERROR_MESSAGE =
            Map.of(
                    "type", "AUTHENTICATION_FAILURE",
                    "code", 104,
                    "service", "data-store",
                    "message", "Wrong username or password"
            );

    String username;
    String password;

    boolean isValid(){
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
