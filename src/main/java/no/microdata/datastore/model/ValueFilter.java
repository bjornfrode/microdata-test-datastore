package no.microdata.datastore.model;

import java.util.HashSet;
import java.util.Set;

public record ValueFilter (Set<String> valueFilter){

    public static  ValueFilter noFilterInstance(){
        return new ValueFilter(new HashSet<String>());
    }

    boolean hasValues(){
        return this.valueFilter.size() > 0;
    }

    long size(){
        return this.valueFilter.size();
    }
}
