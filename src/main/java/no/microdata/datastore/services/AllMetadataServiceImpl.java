package no.microdata.datastore.services;

import no.microdata.datastore.AllMetadataService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AllMetadataServiceImpl implements AllMetadataService {

    // Hele metadata-all-test__1_0_0.json skal returneres her

    @Override
    public Map<String, Object> find(MetadataQuery query) {
        return Map.of("key", "AllMetadataServiceImpl");
    }
}
