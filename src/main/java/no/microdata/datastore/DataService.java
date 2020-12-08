package no.microdata.datastore

import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.model.StatusQuery;

import java.util.Map;

interface DataService {

    Map getEvent(MetadataQuery metadataQuery, EventQuery dataQuery);
    Map getStatus(MetadataQuery metadataQuery, StatusQuery dataQuery);
    Map getFixed(MetadataQuery metadataQuery, FixedQuery dataQuery);

    Map getDataStructureVersion(DataStoreVersionQuery query);
}
