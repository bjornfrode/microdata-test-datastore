package no.microdata.datastore.adapters.api;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.DataService;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.adapters.api.dto.InputFixedQuery;
import no.microdata.datastore.adapters.api.dto.InputQuery;
import no.microdata.datastore.adapters.api.dto.InputTimePeriodQuery;
import no.microdata.datastore.adapters.api.dto.InputTimeQuery;
import no.microdata.datastore.adapters.api.dto.Status;
import no.microdata.datastore.exceptions.BadRequestException;
import no.microdata.datastore.exceptions.DataNotFoundException;
import no.microdata.datastore.exceptions.NotFoundException;
import no.microdata.datastore.exceptions.UnauthorizedException;
import no.microdata.datastore.model.DatasetRevision;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.IntervalFilter;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.model.StatusQuery;
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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static no.microdata.datastore.adapters.api.Constants.*;
import static no.microdata.datastore.adapters.api.RequestId.*;

@RestController
@RequestMapping( produces = {"application/json","application/x-msgpack"}, consumes = {"application/json","application/x-msgpack"} )
class DataAPI {

    private final static Logger log = LoggerFactory.getLogger(DataAPI.class);

    private final DataService dataService;

    public DataAPI(DataService dataService) {
        this.dataService = dataService;
    }

    @RequestMapping(value = "/data/data-structure/event", method = RequestMethod.POST)
    Map getEvent(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                 @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                 @RequestBody InputTimePeriodQuery inputTimePeriodQuery, HttpServletResponse response) {

        log.info("Entering getEvent with languages = {} and request body = {}", languages, inputTimePeriodQuery);

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

            log.info("getEvent query with metadata query = {} and time period query = {}", metadataQuery, eventQuery);
            Map dataStructure = dataService.getEvent(metadataQuery, eventQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getEvent with elapsed time : {} seconds.", elapsed);

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
            throw new RuntimeException("Should not be possible");
        }
    }

    @RequestMapping(value = "/data/data-structure/status", method = RequestMethod.POST)
    Map getStatus(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                  @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                  @RequestBody InputTimeQuery inputTimeQuery, HttpServletResponse response) {

        log.info("Entering getStatus with languages = {} and request body = {}", languages, inputTimeQuery);

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

            StatusQuery statusQuery = new StatusQuery(
                    Map.of(
                            "datasetRevision",  datasetRevision,
                            "date", LocalDate.ofEpochDay(dateValue),
                            "requestId", requestId,
                            "valueFilter", createValueFilter(inputTimeQuery),
                            "unitIdFilter",  createUnitIdFilter(inputTimeQuery),
                            "intervalFilter", createIntervalFilter(inputTimeQuery),
                            "includeAttributes", inputTimeQuery.getIncludeAttributes()
                    ));

            response.setHeader(X_REQUEST_ID, requestId);
            response.setHeader(CONTENT_LANGUAGE, "no");

            if (inputTimeQuery.getCredentials() != null
                    && Objects.equals(inputTimeQuery.getCredentials().getPassword(), "InvalidPass")) {
                log.info("Tmp feature!! Have invalid password");
                response.setStatus(401);
                return Status.RESPONSE_AUTHENTICATION_FAILURE;
            }

            log.info("getStatus query with metadata query = {} and time period query = {}", metadataQuery, statusQuery);
            Map dataStructure = dataService.getStatus(metadataQuery, statusQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getStatus with elapsed time : {} seconds.", elapsed);

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
            throw new RuntimeException("Should not be possible");
        }
    }

    @RequestMapping(value = "/data/data-structure/fixed", method = RequestMethod.POST)
    Map getFixed(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                 @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String languages,
                 @RequestBody InputFixedQuery inputFixedQuery, HttpServletResponse response) {

        log.info("Entering getFixed with languages = {} and request body = {}", languages, inputFixedQuery);

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

            FixedQuery fixedQuery = new FixedQuery(
                    Map.of(
                            "datasetRevision",  datasetRevision,
                            "requestId", requestId,
                            "valueFilter", createValueFilter(inputFixedQuery),
                            "unitIdFilter",  createUnitIdFilter(inputFixedQuery),
                            "intervalFilter", createIntervalFilter(inputFixedQuery),
                            "includeAttributes", false
                    ));

            response.setHeader(X_REQUEST_ID, requestId);
            response.setHeader(CONTENT_LANGUAGE, "no");

            log.info("getStatus query with metadata query = {} and query = {}", metadataQuery, fixedQuery);
            Map dataStructure = dataService.getFixed(metadataQuery, fixedQuery);

            long elapsed = timer.stop().elapsed(TimeUnit.SECONDS);
            log.info("Leaving getFixed with elapsed time : {} seconds.", elapsed);

            return dataStructure;
        } else {
            log.warn("Should not be possible to have invalid input without having a exception!");
            throw new RuntimeException("Should not be possible");
        }
    }

    String getDataStructureVersion(DataStoreVersionQuery query){
        var response = dataService.getDataStructureVersion(query);
        if (! Objects.equals(response.get("datastructureName"), query.dataStructureName())) {
            throw new RuntimeException(
                    String.format("Query data structure name %s does not match name from metadata store %s",
                            query.dataStructureName(), response.get("datastructureName")));
        }
        log.info("{} : dataStore version {}, dataStructure version {}",
                query.dataStructureName(), query.dataStoreVersion(), response.get("datastructureVersion"));

        return (String) response.get("datastructureVersion");
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
    ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataNotFoundException.class)
    ResponseEntity<String> handleDataNotFoundException(DataNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
