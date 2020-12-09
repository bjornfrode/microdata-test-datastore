package no.microdata.datastore;

import no.microdata.datastore.model.MetadataQuery;

import java.util.List;
import java.util.Map;

public interface DataStructureService {

    List<Map<String, Object>> find(MetadataQuery query);

}