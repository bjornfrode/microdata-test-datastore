package no.microdata.datastore.model;

import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a set of FDBDataClient splitted into separated lists for each property. A single datum can be recreated by
 * collecting values in each list on one given index.
 */
public class SplitDatums {

    List<Long> ids = new ArrayList<Long>();
    List<String> values = new ArrayList<String>();
    List<LocalDate> startDates = new ArrayList<LocalDate>();
    List<LocalDate> stopDates = new ArrayList<LocalDate>();

    public SplitDatums(Map args) {
        ids = (List<Long>) args.get("ids");
        values = (List<String>) args.get("values");

        List<LocalDate> tmpStartDates = (List<LocalDate>)args.get("startDates");
        if ( ! CollectionUtils.isEmpty(tmpStartDates) ){
            startDates = tmpStartDates;
        }

        List<LocalDate> tmpStopDates = (List<LocalDate>)args.get("stopDates");
        if ( ! CollectionUtils.isEmpty(tmpStopDates) ){
            stopDates = tmpStopDates;
        }
    }

    public List<Long> getIds() {
        return ids;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString(){
        return "Datum size = " + ids.size();
    }

    static List<Long> datesAsDays(List<LocalDate> dates) {
        return dates.stream().map(LocalDate -> LocalDate.toEpochDay()).collect(Collectors.toList());
    }

    List<Long> stopDatesAsDays() {
        return datesAsDays(stopDates);
    }

    List<Long> startDatesAsDays() {
        return datesAsDays(startDates);
    }
}