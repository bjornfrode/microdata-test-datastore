package no.microdata.datastore.services;

import no.microdata.datastore.DataStructureService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataStructureServiceImpl implements DataStructureService {

    @Override
    public List<Map> find(MetadataQuery query) {
        return List.of(Map.of("key", "value"));
    }

    @Override
    public List<Map> findCurrent(MetadataQuery query) {
        return List.of(Map.of("key", "value"));
    }
}
