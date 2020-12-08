package no.microdata.datastore.model;

import java.util.Collection;
import java.util.stream.Stream;

interface DatumRepository {

    Collection<Datum> findByTime(StatusQuery query);
    Collection<Datum> findByFixed(FixedQuery query);
    Collection<Datum> findByTimePeriod(EventQuery query);

    //  Obsolete?
    //    Stream<Datum> findEventByStreaming(EventQuery query);
    //    Map<String, Object> findTemporalDates(DatasetRevision datasetRevision);
}