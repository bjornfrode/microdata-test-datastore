package no.microdata.datastore.adapters.api.dto;

import java.util.Map;

public class Status {

    public static Map<String, Object> RESPONSE_AUTHENTICATION_FAILURE =
            Map.of(
                    "type", "AUTHENTICATION_FAILURE",
                    "code", 104,
                    "service", "data-store",
                    "message", "Wrong username or password"
            );
}
