package no.microdata.datastore;

import java.util.Map;

public interface DataStoreService {

    Map findAllDataStoreVersions(String requestId);

}