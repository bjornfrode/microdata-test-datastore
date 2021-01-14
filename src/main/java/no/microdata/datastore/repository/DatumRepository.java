package no.microdata.datastore.repository;

import no.microdata.datastore.model.Datum;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.StatusQuery;

import java.util.Collection;

public interface DatumRepository {

    Collection<Datum> findByTime(StatusQuery query);

    Collection<Datum> findByFixed(FixedQuery query);

    Collection<Datum> findByTimePeriod(EventQuery query);
}
