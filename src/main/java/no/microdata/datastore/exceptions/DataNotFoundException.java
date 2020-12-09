package no.microdata.datastore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "NotInUse")
public class DataNotFoundException extends RuntimeException {

    private String dataSetName;

    public DataNotFoundException(String dataSetName){
        super();
        this.dataSetName = dataSetName;
    }

    @Override
    public String getMessage(){
        return MicrodataException.toJsonString(getMessageAsMap());
    }

    public Map getMessageAsMap() {
        String msg = "No data structure named '" + dataSetName + "' was found.";

        return Map.of(
                "type",  "DATA_NOT_FOUND",
                "code",  105,
                "service",  "data-store",
                "data",  dataSetName,
                "message", msg
        );
    }
}
