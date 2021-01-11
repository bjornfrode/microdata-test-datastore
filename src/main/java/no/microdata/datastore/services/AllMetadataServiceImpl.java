package no.microdata.datastore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.microdata.datastore.AllMetadataService;
import no.microdata.datastore.model.MetadataQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class AllMetadataServiceImpl implements AllMetadataService {

    @Value("${datastore.metadata.metadata-all}")
    private String metadataAllFile;

    @Override
    public Map<String, Object> find(MetadataQuery query) {
        try {
            return new ObjectMapper().readValue(Files.readString(Paths.get(metadataAllFile)), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}