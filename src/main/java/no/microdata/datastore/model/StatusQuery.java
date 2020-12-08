package no.microdata.datastore.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.StringJoiner;

public class StatusQuery {

    DatasetRevision datasetRevision;
    LocalDate date;
    String requestId;
    UnitIdFilter unitIdFilter;
    IntervalFilter intervalFilter;
    ValueFilter valueFilter;
    Boolean includeAttributes;

    public StatusQuery(Map inputFields) {

        if(!hasRequiredFields(
                (DatasetRevision)inputFields.get("datasetRevision"),
                (LocalDate)inputFields.get("date"))) {
            throw new AssertionError(
                    String.format(
                            "Missing required field. Fields datasetRevision = %s, date = %s",
                            inputFields.get("datasetRevision"), inputFields.get("date")));
        }

        datasetRevision = (DatasetRevision)inputFields.get("datasetRevision");
        date = (LocalDate)inputFields.get("date");
        requestId = (String)inputFields.get("requestId");
        intervalFilter = (IntervalFilter)inputFields.getOrDefault("intervalFilter", IntervalFilter.fullIntervalInstance());
        unitIdFilter = (UnitIdFilter)inputFields.getOrDefault("unitIdFilter", UnitIdFilter.noFilterInstance());
        valueFilter = (ValueFilter)inputFields.getOrDefault("valueFilter", ValueFilter.noFilterInstance());
        includeAttributes = (Boolean)inputFields.getOrDefault("includeAttributes", false);
    }

    private boolean hasRequiredFields(DatasetRevision datasetRevision, LocalDate date) {
        return datasetRevision != null && date != null;
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
                .add("date=" + date)
                .add("requestId='" + requestId + "'")
                .add("datasetRevision=" + datasetRevision)
                .add("unitIdFilter=" + unitIdFilter)
                .add("intervalFilter=" + intervalFilter)
                .add("valueFilter=" + valueFilter)
                .add("includeAttributes=" + includeAttributes)
                .toString();
    }

}
