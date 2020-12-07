package no.microdata.datastore;

import java.util.Map;

public interface DataStoreService {

    Map findAllDataStoreVersions(String requestId);

    Map<String, String> findDatastructureVersion(String requestId, String name, String datastoreVersion);

}