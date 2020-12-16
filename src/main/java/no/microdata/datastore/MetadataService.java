package no.microdata.datastore;

import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.MetadataQuery;

import java.util.Map;

public interface MetadataService {

    Map getDataStructure(MetadataQuery metadataQuery);

    Map getDataStructureVersion(DataStoreVersionQuery query);

}
