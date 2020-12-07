package no.microdata.datastore.adapters.api.dto;

public class DataStoreVersionQuery {
    String dataStructureName;
    String dataStoreVersion;
    String requestId;

    public DataStoreVersionQuery(String dataStructureName, String dataStoreVersion, String requestId) {
        this.dataStructureName = dataStructureName;
        this.dataStoreVersion = dataStoreVersion;
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "DataStoreVersionQuery["
                + "dataStructureName=" + dataStructureName
                + ", dataStoreVersion=" + dataStoreVersion
                + ", requestId=" + requestId
                + ']';
    }
}
