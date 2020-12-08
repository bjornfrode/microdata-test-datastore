package no.microdata.datastore.services;

import no.microdata.datastore.DataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.MetadataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class DataServiceImpl implements DataService {

    private final static Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

//    @Autowired
//    DatumRepository repository
//
//    @Autowired
//    MetadataAdapter metadataAdapter

    @Override
    public Map getEvent(MetadataQuery metadataQuery, EventQuery eventQuery) {
        log.debug("Entering getEvent() with metadata query = $metadataQuery and eventQuery query = $eventQuery");

        return Map.of(
            "getEvent", "dummy"
        );
    }

    @Override
    public Map getStatus(MetadataQuery metadataQuery, StatusQuery statusQuery) {
        log.debug("Entering getStatus() with metadata query = $metadataQuery and time query = $statusQuery");

        return Map.of(
                "getStatus", "dummy"
        );
    }

    @Override
    public Map getFixed(MetadataQuery metadataQuery, FixedQuery fixedQuery) {
        log.debug("Entering getFixed() with metadata query = $metadataQuery and data query = $fixedQuery");

        return Map.of(
                "getFixed", "dummy"
        );
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        return Map.of(
                "getDataStructureVersion", "dummy"
        );
    }

}