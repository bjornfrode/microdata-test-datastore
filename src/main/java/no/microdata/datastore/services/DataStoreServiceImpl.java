package no.microdata.datastore.services;

import no.microdata.datastore.DataStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataStoreServiceImpl implements DataStoreService {

    // Lese og sette sammen data-store.json og versions.json

    @Override
    public Map<String, Object> findAllDataStoreVersions(String requestId) {
        return Map.of("key", "DataStoreServiceImpl");
    }

}
