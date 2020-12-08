package no.microdata.datastore.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.StringJoiner;

public class EventQuery {

    LocalDate startDate;
    LocalDate endDate;
    String requestId;
    DatasetRevision datasetRevision;
    UnitIdFilter unitIdFilter;
    IntervalFilter intervalFilter;
    ValueFilter valueFilter;
    Boolean includeAttributes;

    EventQuery(LocalDate startDate){
        this.startDate = startDate;
    }

    public EventQuery(Map inputFields) {

        if(!hasRequiredFields(
                (DatasetRevision)inputFields.get("datasetRevision"),
                (LocalDate)inputFields.get("startDate"),
                (LocalDate)inputFields.get("endDate"))) {
            throw new AssertionError(
                    String.format(
                            "Missing required field. Fields datasetRevision = %s, startDate = %s and endDate = %s",
                            inputFields.get("datasetRevision"), inputFields.get("startDate"), inputFields.get("endDate")));
        }

        datasetRevision = (DatasetRevision)inputFields.get("datasetRevision");
        startDate = (LocalDate)inputFields.get("startDate");
        endDate = (LocalDate)inputFields.get("endDate");
        requestId = (String)inputFields.get("requestId");
        intervalFilter = (IntervalFilter)inputFields.getOrDefault("intervalFilter", IntervalFilter.fullIntervalInstance());
        unitIdFilter = (UnitIdFilter)inputFields.getOrDefault("unitIdFilter", UnitIdFilter.noFilterInstance());
        valueFilter = (ValueFilter)inputFields.getOrDefault("valueFilter", ValueFilter.noFilterInstance());
        includeAttributes = (Boolean)inputFields.getOrDefault("includeAttributes", false);
    }

    private boolean hasRequiredFields(DatasetRevision datasetRevision, LocalDate startDate, LocalDate endDate) {
        return datasetRevision != null && startDate != null && endDate != null;
    }

    boolean hasValueFilter(){
        return valueFilter.valueFilter().size() > 0;
    }

    boolean hasUnitIdFilter() {
        return unitIdFilter.unitIds().size() > 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EventQuery.class.getSimpleName() + "[", "]")
                .add("startDate=" + startDate)
                .add("endDate=" + endDate)
                .add("requestId='" + requestId + "'")
                .add("datasetRevision=" + datasetRevision)
                .add("unitIdFilter=" + unitIdFilter)
                .add("intervalFilter=" + intervalFilter)
                .add("valueFilter=" + valueFilter)
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

}
