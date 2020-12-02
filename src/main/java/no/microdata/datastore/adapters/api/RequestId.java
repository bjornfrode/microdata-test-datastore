package no.microdata.datastore.adapters.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.UUID.randomUUID;

public class RequestId {

    private final static Logger log = LoggerFactory.getLogger(RequestId.class);

    public static String verifyAndUpdateRequestId(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            requestId = "metadatastore-api-" + randomUUID().toString();
            log.debug("Has created new request id = {}", requestId);
        }
        return requestId;
    }
}
