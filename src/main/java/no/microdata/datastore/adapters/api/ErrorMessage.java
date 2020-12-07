package no.microdata.datastore.adapters.api;

import java.util.Map;

public class ErrorMessage {

    public static final String INPUT_FIELD_START_DATE = "startDate";
    public static final String INPUT_FIELD_STOP_DATE = "stopDate";
    public static final String INPUT_FIELD_DATASTRUCTURE_NAME = "datastructureName";
    public static final String INPUT_FIELD_VERSION = "version";

    public static Map requestValidationError(String field) {
        String msg = String.format("child \"%s\" fails because [\"%s\" is required]", field, field);

        return Map.of(
                "code", 101,
                "service", "data-store",
                "type", "REQUEST_VALIDATION_ERROR",
                "message", msg
        );
    }

    public static Map requestDataNotFoundError(String dataSetName) {
        String msg = String.format("No data structure named '%s' was found.", dataSetName);

        return Map.of(
                "type", "DATA_NOT_FOUND",
                "code", 105,
                "service", "data-store",
                "data", dataSetName,
                "message", msg
        );
    }

    static Map requestDataNotFoundError(String dataSetName, String version) {
        String msg = String.format("No data structure named '%s' with version '%s' was found.", dataSetName, version);

        return Map.of(
                "type", "DATA_NOT_FOUND",
                "code", 105,
                "service", "data-store",
                "data", dataSetName,
                "message", msg
        );
    }

    public static Map versionValidationError(String version) {
        return Map.of(
                "type", "REQUEST_VALIDATION_ERROR",
                "code", 106,
                "service", "data-store",
                "version", version,
                "message", String.format("Version is in incorrect format: %s. Should consist of 4 parts, e.g. 2.0.0.0", version)
        );
    }

}
