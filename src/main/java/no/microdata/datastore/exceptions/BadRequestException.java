package no.microdata.datastore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "NotInUse")
public class BadRequestException extends MicrodataException {

    public BadRequestException(Map errorMessage) {
        super(errorMessage);
    }

}
