package no.microdata.datastore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "NotInUse")
public class NotFoundException extends MicrodataException {

    public NotFoundException(Map errorMessage) {
        super(errorMessage);
    }

}