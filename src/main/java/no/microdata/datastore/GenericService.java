package no.microdata.datastore;

import java.util.List;
import java.util.Map;

public interface GenericService {

    List<Map<String, Object>> findLanguages(String requestId);

}