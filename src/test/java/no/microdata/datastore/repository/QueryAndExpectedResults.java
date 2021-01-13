package no.microdata.datastore.repository;

import no.microdata.datastore.model.Datum;

import java.util.Collection;

public record QueryAndExpectedResults<T>(T query, Collection<Datum> expected) {
}
