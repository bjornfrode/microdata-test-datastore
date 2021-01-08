package no.microdata.datastore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.AllMetadataService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class AllMetadataServiceImpl implements AllMetadataService {

    @Override
    public Map<String, Object> find(MetadataQuery query) {
        try {
            return new ObjectMapper().readValue(
                    Files.readString(Paths.get(
                            "src/test/resources/no_ssb_test_datastore/metadata/metadata-all-test__1_0_0.json")), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
