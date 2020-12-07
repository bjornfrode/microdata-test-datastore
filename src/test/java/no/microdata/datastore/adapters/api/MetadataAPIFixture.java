package no.microdata.datastore.adapters.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.services.fixture.AllMetadataFixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class MetadataAPIFixture {

    static Map<String, Object> allMetadata() throws IOException {
        return AllMetadataFixture.allMetadata();
    }

    static List<Map<String, Object>> dataStructures() throws IOException {
        return new ObjectMapper().readValue(
                Files.readString(Paths.get(
                        "src/test/resources/fixtures/dataStructures.json")), List.class);
    }

    static Map<String, Object> dataStoreVersions() {
        return
                Map.of(
                        "name", "SSB-RAIRD",
                        "label", "SSB datastore",
                        "description", "Inneholder SSB databaser med data siden 1970 :-)",
                        "versions", List.of(
                                Map.of(
                                        "version", "3.2.0",
                                        "description", "Denne versjonen handler om nye inntektsvariabler",
                                        "releaseTime", 123456,
                                        "languageCode", "no",
                                        "dataStructureUpdates", List.of(
                                                Map.of(
                                                        "description", "string",
                                                        "name", "INNTEKT_TJENPEN",
                                                        "operation", "ADD"
                                                ),
                                                Map.of(
                                                        "description", "string",
                                                        "name", "INNTEKT_BANKINNSK",
                                                        "operation", "ADD"
                                                )
                                        ),
                                        "updateType", "MINOR"
                                )
                        ),
                        "languageCode", "no"
                );
    }
}
