package no.microdata.datastore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.DataStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class DataStoreServiceImpl implements DataStoreService {

    @Value("${datastore.metadata.data-store}")
    private String datastoreFile;

    @Value("${datastore.metadata.versions}")
    private String versionsFile;

    @Override
    public Map<String, Object> findAllDataStoreVersions(String requestId) {
        try {
            Map<String, Object> datastore = new ObjectMapper().readValue(
                                Files.readString(Paths.get(datastoreFile)), Map.class);
            Map<String, Object> versions = new ObjectMapper().readValue(
                                Files.readString(Paths.get(versionsFile)), Map.class);
            datastore.putAll(versions);
            return datastore;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}