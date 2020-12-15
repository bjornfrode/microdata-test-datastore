package no.microdata.datastore.services;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.DataService;
import no.microdata.datastore.adapters.MetadataAdapter;
import no.microdata.datastore.model.*;
import no.microdata.datastore.repository.DatumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
class DataServiceImpl implements DataService {

    private final static Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    @Autowired
    DatumRepository repository;

    @Autowired
    MetadataAdapter metadataAdapter;

    @Override
    public Map getEvent(MetadataQuery metadataQuery, EventQuery eventQuery) {
        log.debug("Entering getEvent() with metadata query = $metadataQuery and eventQuery query = $eventQuery");

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();
        final Stopwatch fourthTimer = Stopwatch.createStarted();

        Map dataStructures = metadataAdapter.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed " +
                "${secondTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.");

        Collection<Datum> datums = repository.findByTimePeriod(eventQuery);
        SplitDatums splitDatums = createSplitDatums(datums, eventQuery.includeAttributes);
        log.info("Call to repository.getEvent consumed " +
                "${thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.")

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, splitDatums,
                eventQuery.includeAttributes)

        log.info("Call to DataMappingFunctions.addDatumsToDataStructure consumed " +
                "${fourthTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.")

        log.debug("Found datastructure with name = ${dataStructure.name} that has ${splitDatums.ids.size()} datums")
        log.info("Leaving getEvent with total elapsed time : " +
                "${firstTimer.stop().elapsed(TimeUnit.SECONDS)} seconds.")

        dataStructure

    }

    @Override
    public Map getStatus(MetadataQuery metadataQuery, StatusQuery statusQuery) {
        log.debug("Entering getStatus() with metadata query = $metadataQuery and time query = $statusQuery");

        return Map.of(
                "getStatus", "dummy"
        );
    }

    @Override
    public Map getFixed(MetadataQuery metadataQuery, FixedQuery fixedQuery) {
        log.debug("Entering getFixed() with metadata query = $metadataQuery and data query = $fixedQuery");

        return Map.of(
                "getFixed", "dummy"
        );
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        return Map.of(
                "getDataStructureVersion", "dummy"
        );
    }

    private SplitDatums createSplitDatums(Collection<Datum> datums, Boolean includeAttributes) {

        List<Long> ids = []
        List<String> values = []
        List<LocalDate> startDates = []
        List<LocalDate> stopDates = []

        // You may feel tempted to replace 'for (datum in datums)' with 'datums.each{}'
        // This is strongly discouraged!
        // For- loop is chosen deliberately because of better performance!
        SplitDatums splitDatums
        if (includeAttributes){
            if (datums){
                for (datum in datums){
                    ids.add(datum.id)
                    values.add(datum.value)
                    startDates.add(datum.start)
                    stopDates.add(datum.stop)
                }
            }
            splitDatums = new SplitDatums([ids: ids, values: values, startDates: startDates, stopDates: stopDates])
        }else {
            if (datums){
                for (datum in datums){
                    ids.add(datum.id)
                    values.add(datum.value)
                }
            }
            splitDatums = new SplitDatums([ids: ids, values:values])
        }
        splitDatums
    }


}