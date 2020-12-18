package no.microdata.datastore.model;

import com.google.common.base.Strings;

import java.util.Map;
import java.util.Objects;

public class DatasetRevision {

    String datasetName;
    String version;

    public DatasetRevision(Map inputFields) {
        String tempDatasetName = (String) inputFields.get("datasetName");
        String tempVersion = (String) inputFields.get("version");

        if (!hasRequiredFields(tempDatasetName, tempVersion)) {
            throw new AssertionError(
                    String.format("Missing required field. Fields datasetName = %s, version = %s", tempDatasetName, tempVersion));
        }

        this.datasetName = tempDatasetName;
        this.version = tempVersion;
    }

    private boolean hasRequiredFields(String datasetName, String version) {
        return !Strings.isNullOrEmpty(datasetName) && !Strings.isNullOrEmpty(version);
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "DatasetRevision["
                + "datasetName=" + datasetName
                + ", version=" + version
                + ']';
    }

    @Override
    public int hashCode() {
        return Objects.hash(datasetName, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatasetRevision other = (DatasetRevision) obj;
        return Objects.equals(datasetName, other.datasetName)
                && Objects.equals(version, other.version);
    }

}
