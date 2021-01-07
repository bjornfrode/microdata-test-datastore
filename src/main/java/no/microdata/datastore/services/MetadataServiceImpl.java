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
import java.util.Objects;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final static Logger log = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @Override
    public Map getDataStructure(MetadataQuery metadataQuery) {
        log.warn("Example implementation - hardcoded!");

        try {
            Map metadataAll = new ObjectMapper().readValue(
                    Files.readString(Paths.get(
                            "src/test/resources/no_ssb_test_datastore/metadata/metadata-all-test__1_0_0.json")), Map.class);
            List<Map> datasets = (List<Map>) metadataAll.get("dataStructures");

            for (Map dataset: datasets) {
                if (Objects.equals(metadataQuery.getNames().get(0), dataset.get("name"))) {
                    return dataset;
                }
            }

            throw new RuntimeException("Should not have happened");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        log.warn("Example implementation - hardcoded!");

        return Map.of(
                "datastructureName","GRUNNSTFDT_MOTTAK",
                "datastructureVersion", "1.0.0.0"
        );
    }

}
