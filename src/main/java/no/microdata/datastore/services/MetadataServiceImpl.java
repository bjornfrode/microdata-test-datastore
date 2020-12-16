package no.microdata.datastore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.MetadataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.MetadataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final static Logger log = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @Override
    public Map getDataStructure(MetadataQuery metadataQuery) {
        log.warn("Example implementation - hardcoded!");

        try {
            return (Map) new ObjectMapper().readValue(
                    Files.readString(Paths.get(
                            "src/test/resources/fixtures/dataStructures.json")), List.class).get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        log.warn("Example implementation - hardcoded!");

        return Map.of(
                "datastructureName","AKT_ARBAP",
                "datastructureVersion", "4.1.0.0"
        );
    }

}
