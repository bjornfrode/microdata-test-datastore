package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.adapters.api.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputTimePeriodQueryTest {

    Credentials credentials;

    @BeforeAll
    void setup(){
        credentials = new Credentials();
        credentials.setUsername(Credentials.VALID_USERNAME);
        credentials.setPassword(Credentials.VALID_PASSWORD);
    }

    @DisplayName("should validate time period query with all required fields set")
    @Test
    void testSunnyDay(){
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setCredentials(credentials);

        boolean isValid = inputQuery.validate();
        assertTrue (isValid);
    }

    @DisplayName("should validate when version is a draft version")
    @Test
    void testDraftVersion() {
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion("0.0.0.127");
        inputQuery.setCredentials(credentials);

        boolean isValid = inputQuery.validate();
        assertTrue (isValid);
    }


    @DisplayName("should NOT validate when required field dataStructureName is missing")
    @Test
    void testMissingDataStructureName(){
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setCredentials(credentials);

        Assertions.assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        // Hvordan kan vi teste innholdet i exception?
//        e.errorMessage == requestValidationError(INPUT_FIELD_DATASTRUCTURE_NAME)
    }

    @DisplayName("should NOT validate when required field start is missing")
    @Test
    void testMissingStart() {
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setCredentials(credentials);

        Assertions.assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        // Hvordan kan vi teste innholdet i exception?
//        e.errorMessage == requestValidationError(INPUT_FIELD_START_DATE)

    }

    @DisplayName("should NOT validate when required field end is missing")
    @Test
    void testMissingEnd() {
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setVersion("1.1.0.0");
        inputQuery.setCredentials(credentials);

        Assertions.assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        // Hvordan kan vi teste innholdet i exception?
//        e.errorMessage == requestValidationError(INPUT_FIELD_STOP_DATE)

    }

    @DisplayName("should NOT validate when version is not 4 level numeric version")
    @Test
    void testWrongVersionFormat() {
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion("1.1.0");
        inputQuery.setCredentials(credentials);

        Assertions.assertThrows(BadRequestException.class, () -> {
            inputQuery.validate();
        });

        // Hvordan kan vi teste innholdet i exception?
//        e.errorMessage == versionValidationError(version)

    }
}