package no.microdata.datastore.services.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.adapters.api.GenericAPIFixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class AllMetadataFixture {

    public static Map<String, Object> allMetadata() throws IOException {
        List<Map<String, Object>> dataStructures = new ObjectMapper().readValue(
                Files.readString(Paths.get(
                        "src/test/resources/fixtures/dataStructures.json")), List.class);

        return Map.of(
                "dataStore", Map.of(
                        "name", "dummy datastore name",
                        "label", "dummy datastore label",
                        "description", "dummy datastore description",
                        "languageCode", "no"
                ),
                "languages", GenericAPIFixture.LANGUAGES,
                "dataStructures", dataStructures
        );
    }

}