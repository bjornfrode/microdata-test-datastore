package no.microdata.datastore.repository;

import no.microdata.datastore.model.*;

import java.time.LocalDate;
import java.util.*;

public class DatumRepositorySpecFixture {

    private static final String VERSION = "3.2.1.0";

    private static final Datum DATUM1 = new Datum(1000000001L, "3", LocalDate.of(1985, 1, 1), LocalDate.of(1991, 6, 30));
    private static final Datum DATUM2 = new Datum(1000000002L, "8", LocalDate.of(1974, 1, 1), LocalDate.of(1992, 1, 31));
    private static final Datum DATUM3 = new Datum(1000000003L, "12", LocalDate.of(1981, 1, 1), LocalDate.of(1991, 1, 31));
    private static final Datum DATUM4 = new Datum(1000000004L, "2", LocalDate.of(1979, 1, 1), LocalDate.of(1991, 2, 10));
    private static final Datum DATUM5 = new Datum(1000000003L, "2", LocalDate.of(1991, 2, 1), LocalDate.of(1991, 10, 14));
    private static final Datum DATUM6 = new Datum(1000000003L, "12", LocalDate.of(1991, 10, 15), null);
    private static final Datum DATUM7 = new Datum(1000000001L, "16", LocalDate.of(1991, 7, 1), LocalDate.of(1992, 3, 31));
    private static final Datum DATUM8 = new Datum(1000000002L, "8", LocalDate.of(1992, 2, 1), null);
    private static final Datum DATUM9 = new Datum(1000000001L, "3", LocalDate.of(1992, 4, 1), null);

    private static final Datum DATUM_1_3 = new Datum(1000000001L, "3");
    private static final Datum DATUM_2_8 = new Datum(1000000002L, "8");
    private static final Datum DATUM_3_12 = new Datum(1000000003L, "12");
    private static final Datum DATUM_4_2 = new Datum(1000000004L, "2");
    private static final Datum DATUM_3_2 = new Datum(1000000003L, "2");
    private static final Datum DATUM_1_16 = new Datum(1000000001L, "16");

    private static final Set<String> VALUE_FILTER = Set.of("2", "8", "16");

    static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryOn19901231() {
        StatusQuery query = createTimeQuery("1990-12-31", "BOSTED", true, null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM1, DATUM2, DATUM3, DATUM4);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryOn19901231NoAttributes() {
        StatusQuery query = createTimeQuery("1990-12-31", "BOSTED", false, null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM_1_3, DATUM_2_8, DATUM_3_12, DATUM_4_2);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryOn19911231() {
        StatusQuery query = createTimeQuery("1991-12-31", "BOSTED", true, null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM7, DATUM2, DATUM6);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<EventQuery> createTestDataForTimePeriodQueryFrom1991_01_01To1991_12_31() {
        EventQuery query = createEventQuery("1991-01-01", "1991-12-31", null, "BOSTED", null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM1, DATUM7, DATUM2, DATUM3, DATUM5, DATUM6, DATUM4);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<EventQuery> createTestDataForTimePeriodQueryFrom1991_01_01To1992_12_31(){
        EventQuery query = createEventQuery("1991-01-01", "1992-12-31", null, "BOSTED", null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM1, DATUM7, DATUM9, DATUM2, DATUM8, DATUM3, DATUM5, DATUM6, DATUM4);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<EventQuery> createTestDataForTimePeriodQueryFrom1991_01_01To1992_12_31NoAttributes(){
        EventQuery query = createEventQuery("1991-01-01", "1992-12-31", false, "BOSTED", null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM_1_3, DATUM_1_16, DATUM_1_3, DATUM_2_8, DATUM_2_8,
                DATUM_3_12, DATUM_3_2, DATUM_3_12, DATUM_4_2);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryWithUnitIdFilter(){
        StatusQuery query = createTimeQuery("1991-12-31", "BOSTED", true, "[0,999]",
                UnitIdFilter.create(Set.of(1000000002L, 1000000003L)), null);

        Collection<Datum> expectedDatums = List.of(DATUM2, DATUM6);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<EventQuery> createTestDataForTimePeriodQueryWithUnitIdFilter(){
        EventQuery query = createEventQuery("1991-01-01", "1992-12-31", true, "BOSTED",
                "[0, 999]", UnitIdFilter.create(Set.of(1000000002L, 1000000003L)), null);

        Collection<Datum> expectedDatums = List.of(DATUM2, DATUM8, DATUM3, DATUM5, DATUM6);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<StatusQuery> createTestDataForNonExistingDataset() {
        StatusQuery query = createTimeQuery("1990-12-31", "Non-existing", true, null, null, null);

        return new QueryAndExpectedResults<>(query, null);
    }

    static QueryAndExpectedResults<FixedQuery> createTestDataForFixedQuery() {
        FixedQuery query = createFixedQuery("AFIXEDDATASET", null, null, null);

        Datum datum1 = new Datum(DATUM1.getId(), DATUM1.getValue());
        Datum datum2 = new Datum(DATUM2.getId(), DATUM2.getValue());
        Datum datum3 = new Datum(DATUM3.getId(), DATUM3.getValue());
        Datum datum4 = new Datum(DATUM4.getId(), DATUM4.getValue());

        Collection<Datum> expectedDatums = List.of(datum1, datum2, datum3, datum4);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryWithValueFilter(){
        StatusQuery query = createTimeQuery("1991-12-31", "BOSTED", true, "[0,999]",
                UnitIdFilter.noFilterInstance(), new ValueFilter(VALUE_FILTER));

        Collection<Datum> expectedDatums = List.of(DATUM7, DATUM2);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<EventQuery> createTestDataForEventQueryWithValueFilter(){
        EventQuery query = createEventQuery("1991-01-01", "1992-12-31", true, "BOSTED",
                "[0, 999]", UnitIdFilter.noFilterInstance(), new ValueFilter(VALUE_FILTER));

        Collection<Datum> expectedDatums = List.of(DATUM7, DATUM2, DATUM8, DATUM5, DATUM4);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    static QueryAndExpectedResults<FixedQuery> createTestDataForFixedQueryWithValueFilter(){
        FixedQuery query = createFixedQuery("AFIXEDDATASET", "[0,999]", UnitIdFilter.noFilterInstance(),
                new ValueFilter(Set.of("2", "3", "8")));

        Datum datum1 = new Datum(DATUM1.getId(), DATUM1.getValue());
        Datum datum2 = new Datum(DATUM2.getId(), DATUM2.getValue());
        Datum datum3 = new Datum(DATUM4.getId(), DATUM4.getValue());

        Collection<Datum> expectedDatums = List.of(datum1, datum2, datum3);
        return new QueryAndExpectedResults<>(query, expectedDatums);
    }

    private static StatusQuery createTimeQuery(String date,
                                               String datasetName,
                                               Boolean includeAttributes,
                                               String filter,
                                               UnitIdFilter unitIdFilter,
                                               ValueFilter valueFilter) {
        if (includeAttributes == null) {
            includeAttributes = true;
        }
        if (unitIdFilter == null) {
            unitIdFilter = UnitIdFilter.noFilterInstance();
        }
        if (valueFilter == null) {
            valueFilter = ValueFilter.noFilterInstance();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("datasetRevision", new DatasetRevision(Map.of("datasetName", datasetName, "version", VERSION)));
        map.put("date", LocalDate.parse(date));
        map.put("valueFilter", valueFilter);
        map.put("unitIdFilter", unitIdFilter);
        map.put("intervalFilter", filter != null ? IntervalFilter.create(filter) : IntervalFilter.fullIntervalInstance());
        map.put("includeAttributes", includeAttributes);

        return new StatusQuery(map);
    }

    private static EventQuery createEventQuery(String startDate,
                                               String endDate,
                                               Boolean includeAttributes,
                                               String variableName,
                                               String filter,
                                               UnitIdFilter unitIdFilter,
                                               ValueFilter valueFilter) {
        if (includeAttributes == null) {
            includeAttributes = true;
        }
        if (unitIdFilter == null) {
            unitIdFilter = UnitIdFilter.noFilterInstance();
        }
        if (valueFilter == null) {
            valueFilter = ValueFilter.noFilterInstance();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("datasetRevision", new DatasetRevision(Map.of("datasetName", variableName, "version", VERSION)));
        map.put("startDate", LocalDate.parse(startDate));
        map.put("endDate", LocalDate.parse(endDate));
        map.put("valueFilter", valueFilter);
        map.put("unitIdFilter", unitIdFilter);
        map.put("intervalFilter", filter != null ? IntervalFilter.create(filter) : IntervalFilter.fullIntervalInstance());
        map.put("includeAttributes", includeAttributes);

        return new EventQuery(map);
    }

    private static FixedQuery createFixedQuery(String variableName,
                                               String filter,
                                               UnitIdFilter unitIdFilter,
                                               ValueFilter valueFilter) {
        if (unitIdFilter == null) {
            unitIdFilter = UnitIdFilter.noFilterInstance();
        }
        if (valueFilter == null) {
            valueFilter = ValueFilter.noFilterInstance();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("datasetRevision", new DatasetRevision(Map.of("datasetName", variableName, "version", VERSION)));
        map.put("valueFilter", valueFilter);
        map.put("unitIdFilter", unitIdFilter);
        map.put("intervalFilter", filter != null ? IntervalFilter.create(filter) : IntervalFilter.fullIntervalInstance());
        map.put("includeAttributes", false);

        return new FixedQuery(map);
    }
}
