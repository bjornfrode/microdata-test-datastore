package no.microdata.datastore.model;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataQuery {

    String languages;
    String requestId;
    List<String> names;
    String version;

    public MetadataQuery(Map inputFields) {

        if(!hasRequiredField((String) inputFields.get("version"))) {
            throw new AssertionError("Missing required field. Field version = " + inputFields.get("version"));
        }

        if (inputFields.get("names") == null){
            this.names = List.of();
        } else if (inputFields.get("names") instanceof List){
            this.names = (List<String>) inputFields.get("names");
        } else if (inputFields.get("names") instanceof String){
            String [] names = ((String)inputFields.get("names")).split(",");

            this.names = Stream.of(names).map(String::strip).collect(Collectors.toList());
        } else {
            this.names = List.of();
        }

        this.version = (String) inputFields.get("version");
        this.requestId = (String) inputFields.get("requestId");
        this.languages = (String) inputFields.get("languages");
    }

    private boolean hasRequiredField(String field) {
        return field != null && !field.isEmpty();
    }

    public String getLanguages() {
        return languages;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<String> getNames() {
        return names;
    }

    public String getVersion() {
        return version;
    }
}