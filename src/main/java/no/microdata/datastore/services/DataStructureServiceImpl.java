package no.microdata.datastore.services;

import no.microdata.datastore.DataStructureService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataStructureServiceImpl implements DataStructureService {

    // Slå opp i metadata-all-test__1_0_0.json og finne datasetet med gitt navn
    // Husk at det kan være behov for flere enn 1 dataset, request kan se slik ut:
    //
    // http://{{metadatastore-api}}/metadata/data-structures?names=BOFORHOLD_EIEFORM,BOFORHOLD_HUSHTYPE&version=4.0.0.0
    //

    @Override
    public List<Map<String, Object>> find(MetadataQuery query) {
        return List.of(Map.of("key", "DataStructureServiceImpl"));
    }

}
