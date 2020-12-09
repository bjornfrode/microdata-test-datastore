package no.microdata.datastore.adapters.api.dto;

public record DataStoreVersionQuery(
        String dataStructureName,
        String dataStoreVersion,
        String requestId) {}