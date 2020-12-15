package no.microdata.datastore.model;

import java.util.Map;
import java.util.StringJoiner;

public class FixedQuery {

    DatasetRevision datasetRevision;
    String requestId;
    UnitIdFilter unitIdFilter;
    IntervalFilter intervalFilter;
    ValueFilter valueFilter;
    Boolean includeAttributes;

    public FixedQuery(Map inputFields) {

        if(!hasRequiredFields(
                (DatasetRevision)inputFields.get("datasetRevision"))) {
            throw new AssertionError(
                    String.format(
                            "Missing required field. Fields datasetRevision = %s",
                            inputFields.get("datasetRevision")));
        }

        datasetRevision = (DatasetRevision)inputFields.get("datasetRevision");
        requestId = (String)inputFields.get("requestId");
        intervalFilter = (IntervalFilter)inputFields.getOrDefault("intervalFilter", IntervalFilter.fullIntervalInstance());
        unitIdFilter = (UnitIdFilter)inputFields.getOrDefault("unitIdFilter", UnitIdFilter.noFilterInstance());
        valueFilter = (ValueFilter)inputFields.getOrDefault("valueFilter", ValueFilter.noFilterInstance());
        includeAttributes = (Boolean)inputFields.getOrDefault("includeAttributes", false);
    }

    private boolean hasRequiredFields(DatasetRevision datasetRevision) {
        return datasetRevision != null;
    }

    boolean hasValueFilter(){
        return valueFilter.valueFilter().size() > 0;
    }

    boolean hasUnitIdFilter() {
        return unitIdFilter.unitIds().size() > 0;
    }

    public UnitIdFilter getUnitIdFilter() {
        return unitIdFilter;
    }

    public Boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public DatasetRevision getDatasetRevision() {
        return datasetRevision;
    }

    public IntervalFilter getIntervalFilter() {
        return intervalFilter;
    }

    public ValueFilter getValueFilter() {
        return valueFilter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FixedQuery.class.getSimpleName() + "[", "]")
                .add("requestId='" + requestId + "'")
                .add("datasetRevision=" + datasetRevision)
                .add("unitIdFilter=" + unitIdFilter)
                .add("intervalFilter=" + intervalFilter)
                .add("valueFilter=" + valueFilter)
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

}
