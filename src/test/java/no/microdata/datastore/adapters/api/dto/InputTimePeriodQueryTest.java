package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.adapters.api.BadRequestException;
import static no.microdata.datastore.adapters.api.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class InputTimePeriodQueryTest {

    static final String VERSION = "1.1.0.0";
    static final String KJONN = "KJONN";

    @DisplayName("should validate time period query with all required fields set")
    @Test
    void testOne(){

        Credentials credentials = new Credentials();
        credentials.setUsername(Credentials.VALID_USERNAME);
        credentials.setPassword(Credentials.VALID_PASSWORD);

        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion(VERSION);
        inputQuery.setCredentials(credentials);

        boolean isValid = inputQuery.validate();

        assertTrue (isValid);
    }

    @DisplayName("should NOT validate when required field dataStructureName is missing")
    @Test
    void testTwo(){

        Credentials credentials = new Credentials();
        credentials.setUsername(Credentials.VALID_USERNAME);
        credentials.setPassword(Credentials.VALID_PASSWORD);

        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery();
        inputQuery.setDataStructureName("KJONN");
        inputQuery.setStartDate(1259649003000L);
        inputQuery.setStopDate(1346394603000L);
        inputQuery.setVersion(VERSION);
        inputQuery.setCredentials(credentials);

        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(startDate: 1259649003000, stopDate: 1346394603000,credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])

        inputQuery.validate()

        BadRequestException e = thrown()
        e.errorMessage == requestValidationError(INPUT_FIELD_DATASTRUCTURE_NAME)
    }

    def "should NOT validate when required field start is missing"() {
        setup:
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(dataStructureName: KJONN,
                version: VERSION, stopDate: 1346394603000,credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])
        when:
        inputQuery.validate()
        then:
        BadRequestException e = thrown()
        e.errorMessage == requestValidationError(INPUT_FIELD_START_DATE)
    }

    def "should NOT validate when required field end is missing"() {
        setup:
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(dataStructureName: KJONN,
                version: VERSION, startDate: 1259649003000,credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])
        when:
        inputQuery.validate()
        then:
        BadRequestException e = thrown()
        e.errorMessage == requestValidationError(INPUT_FIELD_STOP_DATE)
    }

    def "should NOT validate when version is not 4 level numeric version"() {
        setup:
        def version = '1.1.0'
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(dataStructureName: 'kjonn',
                version: version, startDate: 1259649003000,
                stopDate: 1346394603000, credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])
        when:
        inputQuery.validate()
        then:
        BadRequestException e = thrown()
        e.errorMessage == versionValidationError(version)
    }

    def "should validate when version is 4 level numeric version"() {
        setup:
        def version = '1.1.0.0'
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(dataStructureName: 'kjonn',
                version: version, startDate: 1259649003000,
                stopDate: 1346394603000, credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])
        when:
        boolean isValid = inputQuery.validate()
        then:
        noExceptionThrown()
        isValid == true
    }

    def "should validate when version is a draft version"() {
        setup:
        def version = '0.0.0.127'
        InputTimePeriodQuery inputQuery = new InputTimePeriodQuery(dataStructureName: 'kjonn',
                version: version, startDate: 1259649003000,
                stopDate: 1346394603000, credentials: [
        username: Credentials.VALID_USERNAME,
                password: Credentials.VALID_PASSWORD
            ])
        when:
        boolean isValid = inputQuery.validate()
        then:
        noExceptionThrown()
        isValid == true
    }


}
