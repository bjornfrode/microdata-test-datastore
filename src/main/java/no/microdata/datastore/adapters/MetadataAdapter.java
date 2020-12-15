package no.microdata.datastore.adapters;

import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;

import java.util.Collection;
import java.util.Map;

public interface MetadataAdapter {

    Map getDataStructure(MetadataQuery query);
    Map getDataStructureVersion(DataStoreVersionQuery query);

//  Not likely to use this method in test datastore
//  Collection<Revision> getRevisions(List<String> languages, String requestId);
    Collection<Map> getLanguages(String requestId);
    Map getDatastore(String languages, String version, String requestId);
}
