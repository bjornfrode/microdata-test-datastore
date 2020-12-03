package no.microdata.datastore.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public abstract class MicrodataException extends RuntimeException {

    protected Map errorMessage;

    public MicrodataException(Map errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage(){
        return toJsonString(errorMessage);
    }

    public Map getErrorMessage(){
        return errorMessage;
    }

    public static String toJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create json string for object", e);
        }
    }
}
