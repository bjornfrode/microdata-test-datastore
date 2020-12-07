package no.microdata.datastore.adapters.api.dto;

import no.microdata.datastore.adapters.api.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.microdata.datastore.transformations.Utils.*;

//import static no.microdata.datastore.adapters.api.ErrorMessage.*;

class InputTimeQuery extends InputQuery{

    final static Logger log = LoggerFactory.getLogger(InputTimeQuery.class);
    Long date;

    /**
     * Validates the input query.
     *
     * @return true if time query is valid, otherwise throws BadRequestException
     * @throws BadRequestException if not valid
     */
    boolean validate() {
        super.validate();
        if (isNullOrEmpty(date))
            throw  new BadRequestException(no.microdata.datastore.adapters.api.ErrorMessage.requestValidationError(no.microdata.datastore.adapters.api.ErrorMessage.INPUT_FIELD_START_DATE));
        return true;
    }

    @Override
    public String toString(){
        String string = String.format("{ dataStructureName: %1$s, date: %2$d, version: %3$s", dataStructureName, date, version);
        if(hasValueFilter()){
            string.concat(String.format(", values.size(): %1$d", values.size()));
        }
        if(hasUnitIdFilter()){
            string.concat(String.format(", population filter size:  %1$d", populationFilter().size()));
        }
        if (hasIntervalFilter()){
            string.concat(String.format(", interval filter: %1$s", intervalFilter));
        }
        if (includeAttributes){
            string.concat(String.format(", includeAttributes: %1$b", includeAttributes));
        }
        return string.concat(" }");
    }
}