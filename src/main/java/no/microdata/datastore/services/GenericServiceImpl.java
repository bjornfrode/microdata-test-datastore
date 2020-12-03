package no.microdata.datastore.services;

import no.microdata.datastore.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenericServiceImpl implements GenericService {
    @Override
    public List<Map> findLanguages(String requestId) {
        return List.of(Map.of("key", "value"));
    }
}
