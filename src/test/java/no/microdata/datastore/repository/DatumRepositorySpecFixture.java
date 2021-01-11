package no.microdata.datastore.repository;

import no.microdata.datastore.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatumRepositorySpecFixture {

    private static final String VERSION = "3.2.1.0";

    private static final Datum DATUM1 = new Datum(1000000001L, "3", LocalDate.of(1985, 1, 1), LocalDate.of(1991, 6, 30));
    private static final Datum DATUM2 = new Datum(1000000002L, "8", LocalDate.of(1974, 1, 1), LocalDate.of(1992, 1, 31));
    private static final Datum DATUM3 = new Datum(1000000003L, "12", LocalDate.of(1981, 1, 1), LocalDate.of(1991, 1, 31));
    private static final Datum DATUM4 = new Datum(1000000004L, "2", LocalDate.of(1979, 1, 1), LocalDate.of(1991, 2, 10));

    final static QueryAndExpectedResults<StatusQuery> createTestDataForTimeQueryOn19901231() {
        StatusQuery query = createTimeQuery("1990-12-31", "BOSTED", true, null, null, null);

        Collection<Datum> expectedDatums = List.of(DATUM1, DATUM2, DATUM3, DATUM4);
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

}
