package no.microdata.datastore.services;

import no.microdata.datastore.AllMetadataService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AllMetadataServiceImpl implements AllMetadataService {
    @Override
    public Map find(MetadataQuery query) {
        return null;
    }
}
