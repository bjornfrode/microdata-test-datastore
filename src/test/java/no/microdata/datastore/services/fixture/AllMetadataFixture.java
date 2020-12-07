package no.microdata.datastore.services.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.adapters.api.GenericAPIFixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static no.microdata.datastore.transformations.DatastoreUtils.*;

public class AllMetadataFixture {

    public static Map<String, Object> allMetadata() throws IOException {
        List<Map<String, Object>> dataStructures = new ObjectMapper().readValue(
                Files.readString(Paths.get(
                        "src/test/resources/fixtures/dataStructures.json")), List.class);

        return Map.of(
                "dataStore", Map.of(
                        "name", DATASTORE_NAME,
                        "label", DATASTORE_LABEL,
                        "description", DATASTORE_DESCRIPTION,
                        "languageCode", "no"
                ),
                "languages", GenericAPIFixture.LANGUAGES,
                "dataStructures", dataStructures
        );
    }

}