package no.microdata.datastore.repository;

import no.microdata.datastore.model.DatasetRevision;
import no.microdata.datastore.model.Datum;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.StatusQuery;

import java.util.Collection;
import java.util.Map;

public interface DatumRepository {

    Collection<Datum> findByTime(StatusQuery query);

    Collection<Datum> findByFixed(FixedQuery query);

    Collection<Datum> findByTimePeriod(EventQuery query);

    Map<String, Object> findTemporalDates(DatasetRevision datasetRevision);

}
