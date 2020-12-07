package no.microdata.datastore;

import no.microdata.datastore.model.MetadataQuery;

import java.util.Map;

public interface AllMetadataService {

    Map<String, Object> find(MetadataQuery query);

}