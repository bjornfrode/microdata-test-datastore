package no.microdata.datastore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "NotInUse")
public class UnauthorizedException extends MicrodataException {

    public UnauthorizedException(Map errorMessage) {
        super(errorMessage);
    }

}