package no.microdata.datastore.adapters.api;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.adapters.api.dto.InputFixedQuery;
import no.microdata.datastore.adapters.api.dto.InputQuery;
import no.microdata.datastore.adapters.api.dto.InputTimePeriodQuery;
import no.microdata.datastore.adapters.api.dto.InputTimeQuery;
import no.microdata.datastore.exceptions.BadRequestException;
import no.microdata.datastore.exceptions.DataNotFoundException;
import no.microdata.datastore.exceptions.NotFoundException;
import no.microdata.datastore.exceptions.UnauthorizedException;
import no.microdata.datastore.model.DatasetRevision;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.IntervalFilter;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.model.UnitIdFilter;
import no.microdata.datastore.model.ValueFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static no.microdata.datastore.adapters.api.Constants.*;
import static no.microdata.datastore.adapters.api.RequestId.*;

@RestController
@RequestMapping( produces = {"application/json","application/x-msgpack"}, consumes = {"application/json","application/x-msgpack"} )
class DataAPI {

    private final static Logger log = LoggerFactory.getLogger(DataAPI.class);

    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/data/data-structure/event", method = RequestMethod.POST)
    Map getEvent(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                 @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                 @RequestBody InputTimePeriodQuery inputTimePeriodQuery, HttpServletResponse response) {

        log.info("Entering getEvent with languages = $languages and request body = $inputTimePeriodQuery");

        if (inputTimePeriodQuery.validate()) {

            final Stopwatch timer = Stopwatch.createStarted();

            String dataStructureName = inputTimePeriodQuery.getDataStructureName();
            String datastoreVersion = String.valueOf(inputTimePeriodQuery.getVersion());
            Long startDate = inputTimePeriodQuery.getStartDate();
            Long stopDate = inputTimePeriodQuery.getStopDate();

            requestId = verifyAndUpdateRequestId(requestId);

            DataStoreVersionQuery dataStoreVersionQuery =
                    new DataStoreVersionQuery(dataStructureName, datastoreVersion, requestId);

            String dataStructureVersion =
                    getDataStructureVersion(dataStoreVersionQuery);

            MetadataQuery metadataQuery = new MetadataQuery(
                    Map.of(
                            "names", List.of(dataStructureName),
                            "languages", languages,
                            "version", datastoreVersion,
                            "includeAttributes", inputTimePeriodQuery.getIncludeAttributes()
                    ));

            DatasetRevision datasetRevision =
                    new DatasetRevision(Map.of("datasetName", dataStructureName, "version", dataStructureVersion));

            EventQuery eventQuery = new EventQuery(
                    Map.of(
                            "datasetRevision",  datasetRevision,
                            "startDate", LocalDate.ofEpochDay(startDate),
                            "endDate", LocalDate.ofEpochDay(stopDate),
                            "requestId", requestId,
                            "valueFilter", createValueFilter(inputTimePeriodQuery),
                            "unitIdFilter",  createUnitIdFilter(inputTimePeriodQuery),
                            "intervalFilter", createIntervalFilter(inputTimePeriodQuery),
                            "includeAttributes", inputTimePeriodQuery.getIncludeAttributes()
                    ));

            response.setHeader(X_REQUEST_ID, requestId);
            response.setHeader(CONTENT_LANGUAGE, "no");

            log.info("getEvent query with metadata query = $metadataQuery and time period query = $eventQuery");
            Map dataStructure = dataService.getEvent(metadataQuery, eventQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getEvent with elapsed time : $elapsed seconds.");

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
        }
    }

    @RequestMapping(value = "/data/data-structure/status", method = RequestMethod.POST)
    Map getStatus(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                  @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                  @RequestBody InputTimeQuery inputTimeQuery, HttpServletResponse response) {

        log.info("Entering getStatus with languages = $languages and request body = $inputTimeQuery")

        if (inputTimeQuery.validate()) {

            final Stopwatch timer = Stopwatch.createStarted();

            String dataStructureName = inputTimeQuery.getDataStructureName();
            String datastoreVersion = String.valueOf(inputTimeQuery.getVersion());
            Long dateValue = inputTimeQuery.getDate();
            requestId = verifyAndUpdateRequestId(requestId);

            DataStoreVersionQuery dataStoreVersionQuery =
                    new DataStoreVersionQuery(dataStructureName, datastoreVersion, requestId);

            String dataStructureVersion =
                    getDataStructureVersion(dataStoreVersionQuery);

            MetadataQuery metadataQuery = new MetadataQuery(
                    Map.of(
                            "names", List.of(dataStructureName),
                            "languages", languages,
                            "requestId", requestId,
                            "version", datastoreVersion,
                            "includeAttributes", inputTimeQuery.getIncludeAttributes()
                    ));

            DatasetRevision datasetRevision =
                    new DatasetRevision(Map.of("datasetName", dataStructureName, "version", dataStructureVersion));

            StatusQuery statusQuery = new StatusQuery(  datasetRevision: datasetRevision,
                    date: LocalDate.ofEpochDay(dateValue),
                    requestId: requestId,
                    valueFilter: createValueFilter(inputTimeQuery),
                    unitIdFilter:  createUnitIdFilter(inputTimeQuery),
                    intervalFilter: createIntervalFilter(inputTimeQuery),
                    includeAttributes: inputTimeQuery.includeAttributes);

            response.setHeader(X_REQUEST_ID, requestId);
            response.setHeader(CONTENT_LANGUAGE, "no");

            if (inputTimeQuery.credentials?.password?.equals("InvalidPass")) {
                log.info("Tmp feature!! Have invalid password");
                response.setStatus(401);
                return Status.RESPONSE_AUTHENTICATION_FAILURE;
            }

            log.info("getStatus query with metadata query = $metadataQuery and time period query = $statusQuery");
            Map dataStructure = dataService.getStatus(metadataQuery, statusQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getStatus with elapsed time : $elapsed seconds.");

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
        }
    }

    @RequestMapping(value = "/data/data-structure/fixed", method = RequestMethod.POST)
    Map getFixed(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                 @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                 @RequestBody InputFixedQuery inputFixedQuery, HttpServletResponse response) {

        log.info("Entering getFixed with languages = $languages and request body = $inputFixedQuery");
        if (inputFixedQuery.validate()) {

            final Stopwatch timer = Stopwatch.createStarted();

            String dataStructureName = inputFixedQuery.getDataStructureName();
            String datastoreVersion = String.valueOf(inputFixedQuery.getVersion());

            requestId = verifyAndUpdateRequestId(requestId);

            DataStoreVersionQuery dataStoreVersionQuery =
                    new DataStoreVersionQuery(dataStructureName, datastoreVersion, requestId);

            String dataStructureVersion =
                    getDataStructureVersion(dataStoreVersionQuery);

            MetadataQuery metadataQuery = new MetadataQuery(
                    Map.of(
                            "names", List.of(dataStructureName),
                            "languages", languages,
                            "requestId", requestId,
                            "version", datastoreVersion,
                            // includeAttributes: inputFixedQuery.includeAttributes) ??
                            "includeAttributes", false
                    ));

            DatasetRevision datasetRevision =
                    new DatasetRevision(Map.of("datasetName", dataStructureName, "version", dataStructureVersion));

            FixedQuery fixedQuery = new FixedQuery( datasetRevision: datasetRevision,
                    requestId: requestId,
                    valueFilter: createValueFilter(inputFixedQuery),
                    unitIdFilter:  createUnitIdFilter(inputFixedQuery),
                    intervalFilter: createIntervalFilter(inputFixedQuery),
                    // includeAttributes: inputFixedQuery.includeAttributes) ??
                    includeAttributes: false);

            response.setHeader(X_REQUEST_ID, requestId);
            response.setHeader(CONTENT_LANGUAGE, "no");

            log.info("getStatus query with metadata query = $metadataQuery and query = $fixedQuery");
            Map dataStructure = dataService.getFixed(metadataQuery, fixedQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getFixed with elapsed time : $elapsed seconds.");

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
        }
    }

    String getDataStructureVersion(DataStoreVersionQuery query){
        var response = dataService.getDataStructureVersion(query);
        if (! response.datastructureName.equals(query.dataStructureName)) {
            throw new RuntimeException("Query data structure name $query.dataStructureName does not match name " +
                    "from metadata store $response.datastructureName");
        }
        log.info("$query.dataStructureName : dataStore version $query.dataStoreVersion, dataStructure version $response.datastructureVersion");
        response.datastructureVersion;
    }

    private static ValueFilter createValueFilter(InputQuery inputQuery) {
        return inputQuery.hasValueFilter() ? new ValueFilter((Set<String>) inputQuery.getValues()) : ValueFilter.noFilterInstance();
    }

    private static UnitIdFilter createUnitIdFilter(InputQuery inputQuery) {
        return UnitIdFilter.create(inputQuery.populationFilter());
    }

    private static IntervalFilter createIntervalFilter(InputQuery inputQuery) {
        return inputQuery.hasIntervalFilter() ?
                IntervalFilter.create(inputQuery.getIntervalFilter()) : IntervalFilter.fullIntervalInstance();
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<String> handleBadRequestException(BadRequestException e) throws IOException {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> handleNotFoundException(NotFoundException e) throws IOException {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotFoundException.class)
    ResponseEntity<String> handleDataNotFoundException(DataNotFoundException e) throws IOException {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) throws IOException {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
