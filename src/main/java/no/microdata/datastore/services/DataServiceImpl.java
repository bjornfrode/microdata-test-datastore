package no.microdata.datastore.services;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.DataService;
import no.microdata.datastore.MetadataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.*;
import no.microdata.datastore.repository.DatumRepository;
import no.microdata.datastore.transformations.DataMappingFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
class DataServiceImpl implements DataService {

    private final static Logger log = LoggerFactory.getLogger(DataServiceImpl.class);

    @Autowired
    DatumRepository repository;

    @Autowired
    MetadataService metadataService;

    @Override
    public Map getEvent(MetadataQuery metadataQuery, EventQuery eventQuery) {
        log.debug("Entering getEvent() with metadata query = $metadataQuery and eventQuery query = $eventQuery");

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();
        final Stopwatch fourthTimer = Stopwatch.createStarted();

        Map dataStructures = metadataService.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed " +
                "${secondTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.");

        Collection<Datum> datums = repository.findByTimePeriod(eventQuery);
        SplitDatums splitDatums = createSplitDatums(datums, eventQuery.getIncludeAttributes());
        log.info("Call to repository.getEvent consumed " +
                "${thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.");

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, splitDatums,
                eventQuery.getIncludeAttributes());

        log.info("Call to DataMappingFunctions.addDatumsToDataStructure consumed " +
                "${fourthTimer.stop().elapsed(TimeUnit.MILLISECONDS)} miliseconds.");

        log.debug("Found datastructure with name = ${dataStructure.name} that has ${splitDatums.ids.size()} datums");
        log.info("Leaving getEvent with total elapsed time : " +
                "${firstTimer.stop().elapsed(TimeUnit.SECONDS)} seconds.");

        return dataStructure;

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
        log.debug("Entering getFixed() with metadata query = {} and data query = {}", metadataQuery, fixedQuery);

        final Stopwatch firstTimer = Stopwatch.createStarted();
        final Stopwatch secondTimer = Stopwatch.createStarted();
        final Stopwatch thirdTimer = Stopwatch.createStarted();
        final Stopwatch fourthTimer = Stopwatch.createStarted();

        Map dataStructures = metadataService.getDataStructure(metadataQuery);
        log.info("Call to metadataAdapter.getDataStructure consumed {} miliseconds.",
                secondTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Collection<Datum> datums = repository.findByFixed(fixedQuery);
        SplitDatums splitDatums = createSplitDatums(datums, fixedQuery.getIncludeAttributes());
        log.info("Call to dataAdapter.getFixed consumed {} miliseconds.", thirdTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        Map dataStructure = DataMappingFunctions.addDatumsToDataStructure(dataStructures, splitDatums,
                fixedQuery.getIncludeAttributes());

        log.info("Call to DataMappingFunctions.addDatumsToDataStructure consumed {} miliseconds.",
                fourthTimer.stop().elapsed(TimeUnit.MILLISECONDS));

        log.info("Leaving getFixed with total elapsed time : {} seconds.", firstTimer.stop().elapsed(TimeUnit.SECONDS));

        return dataStructure;
    }

    @Override
    public Map getDataStructureVersion(DataStoreVersionQuery query) {
        return metadataService.getDataStructureVersion(query);
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