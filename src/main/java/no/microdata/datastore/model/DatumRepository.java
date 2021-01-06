package no.microdata.datastore.model;

import java.util.Collection;

interface DatumRepository {

    Collection<Datum> findByTime(StatusQuery query);
    Collection<Datum> findByFixed(FixedQuery query);
    Collection<Datum> findByTimePeriod(EventQuery query);

}