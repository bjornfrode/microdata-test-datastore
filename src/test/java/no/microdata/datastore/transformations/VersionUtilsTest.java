package no.microdata.datastore.transformations;

import no.microdata.datastore.exceptions.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionUtilsTest {

    @Test
    void shouldConvertFrom4To3Labels() {
        assertEquals("22.266.3", VersionUtils.toThreeLabelsIfNotDraft("22.266.3.0"));
    }

    @Test
    void shouldConvertFrom3To4Labels() {
        assertEquals("1.23.345.0", VersionUtils.toFourLabelsIfNotDraft("1.23.345"));
    }

    @Test
    void shouldConvertFrom4To2Labels() {
        assertEquals("22.266", VersionUtils.toTwoLabels("22.266.3.0"));
    }

    @Test
    void shouldNotConvertANullVersionTo3LabelsVersion() {
        assertEquals("0.0.0.148", VersionUtils.toThreeLabelsIfNotDraft("0.0.0.148"));
    }

    @Test
    void shouldNotConvertANullVersionTo4LabelsVersion() {
        assertEquals("0.0.0.148", VersionUtils.toFourLabelsIfNotDraft("0.0.0.148"));
    }

    @Test
    void shouldThrowExceptionWhenWrongFormat() {
        BadRequestException exceptionThatWasThrown = Assertions.assertThrows(BadRequestException.class, () -> {
            VersionUtils.toThreeLabelsIfNotDraft("1");
        });

        assertEquals("1", exceptionThatWasThrown.getErrorMessage().get("version"));
    }
}