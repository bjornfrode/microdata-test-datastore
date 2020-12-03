package no.microdata.datastore.services;

import no.microdata.datastore.DataStoreService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataStoreServiceImpl implements DataStoreService {
    @Override
    public Map findAllDataStoreVersions(String requestId) {
        return null;
    }

    @Override
    public Map findDatastructureVersion(String requestId, String name, String datastoreVersion) {
        return null;
    }
}
