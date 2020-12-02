package no.microdata.datastore;

import no.microdata.datastore.model.MetadataQuery;

import java.util.Map;

public interface AllMetadataService {

    Map find(MetadataQuery query);

}