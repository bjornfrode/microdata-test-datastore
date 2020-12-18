package no.microdata.datastore.model;

import java.util.HashSet;
import java.util.Set;

public record UnitIdFilter(Set<Long> unitIds){

    public static UnitIdFilter create(Set<Long> unitIds) {
        if (unitIds == null){
            unitIds = noFilterInstance().unitIds();
        }
        return new UnitIdFilter(unitIds);
    }

    public static UnitIdFilter noFilterInstance() {
        return new UnitIdFilter(new HashSet<>());
    }
}

