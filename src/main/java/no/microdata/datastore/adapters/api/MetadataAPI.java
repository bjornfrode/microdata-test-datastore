package no.microdata.datastore.adapters.api;

import no.microdata.datastore.AllMetadataService;
import no.microdata.datastore.DataStoreService;
import no.microdata.datastore.DataStructureService;
import no.microdata.datastore.GenericService;
import no.microdata.datastore.model.MetadataQuery;
import no.microdata.datastore.transformations.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static no.microdata.datastore.adapters.api.Constants.*;
import static no.microdata.datastore.adapters.api.RequestId.*;

@RestController
@RequestMapping(produces = {"application/json;charset=UTF-8", "application/x-msgpack"})
class MetadataAPI {

    private final static Logger log = LoggerFactory.getLogger(MetadataAPI.class);

    @Autowired
    DataStructureService dataStructureService;

    @Autowired
    DataStoreService dataStoreService;

    @Autowired
    AllMetadataService allMetadataService;

    @Autowired
    GenericService genericService;

    @RequestMapping(value = "/metadata/data-store", method = RequestMethod.GET)
    Map getDataStore(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                           @RequestHeader(value = ACCEPT_LANGUAGE, required = false) String language,
                           HttpServletResponse response) {

        log.info("Entering getDataStore() with requestId = {} and language {}", requestId, language);

        var verifiedRequestId = verifyAndUpdateRequestId(requestId);

        response.setHeader(X_REQUEST_ID, verifiedRequestId);
        response.setHeader(CONTENT_LANGUAGE, "no");

        return dataStoreService.findAllDataStoreVersions(verifiedRequestId);
    }

    @RequestMapping(value = "/metadata/data-structures", method = RequestMethod.GET)
    List<Map<String, Object>> getDataStructures(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                                @RequestHeader(value = ACCEPT_LANGUAGE, required = false) List<String> languages,
                                @RequestParam(value = NAMES, required = false) List names,
                                @RequestParam(value = VERSION, required = true) String version,
                                HttpServletResponse response) {

        MetadataQuery query = new MetadataQuery(
                Map.of(
                        "names", names,
                        "languages", joinToString(languages),
                        "requestId", verifyAndUpdateRequestId(requestId),
                        "version", VersionUtils.toThreeLabelsIfNotDraft(version)
                ));

        log.info("Entering getDataStructures() where query = {}", query);

        response.setHeader(X_REQUEST_ID, query.getRequestId());
        response.setHeader(CONTENT_LANGUAGE, "no");

        return dataStructureService.find(query);
    }

    @RequestMapping(value = "/metadata/all", method = RequestMethod.GET)
    Map<String, Object> getAllMetadata(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                       @RequestHeader(value = ACCEPT_LANGUAGE, required = false) List<String> languages,
                       @RequestParam(value = VERSION, required = true) String version,
                       HttpServletResponse response) {

        MetadataQuery query = new MetadataQuery(
                Map.of(
                        "languages", joinToString(languages),
                        "requestId", verifyAndUpdateRequestId(requestId),
                        "version", VersionUtils.toThreeLabelsIfNotDraft(version)
                ));

        log.info("Entering getAllMetadata() where query = {}", query);

        response.setHeader(X_REQUEST_ID, query.getRequestId());
        response.setHeader(CONTENT_LANGUAGE, "no");

        return allMetadataService.find(query);
    }

    @RequestMapping(value = "/metadata/data-structure/version", method = RequestMethod.GET)
    Map<String, String> getDatastructureVersion(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                                @RequestHeader(value = ACCEPT_LANGUAGE, required = false) List<String> languages,
                                @RequestParam(value = DATASTRUCTURE_NAME, required = true) String name,
                                @RequestParam(value = DATASTORE_VERSION, required = true) String datastoreVersion,
                                HttpServletResponse response) {

        log.info("Entering getDatastructureVersion with name = {} and datastoreVersion = {}", name, datastoreVersion);

        var verifiedRequestId = verifyAndUpdateRequestId(requestId);

        response.setHeader(X_REQUEST_ID, verifiedRequestId);
        response.setHeader(CONTENT_LANGUAGE, "no");

        var datastructureVersion =
                dataStoreService.findDatastructureVersion(verifiedRequestId, name, VersionUtils.toThreeLabelsIfNotDraft(datastoreVersion));

        log.info("Leaving getDatastructureVersion with name = {} and datastoreVersion = {}",
                datastructureVersion.get("datastructureName"), datastructureVersion.get("datastructureVersion"));

        return datastructureVersion;
    }

    @RequestMapping(value = "/languages", method = RequestMethod.GET)
    List<Map<String, Object>> getLanguages(@RequestHeader(value = X_REQUEST_ID, required = false) String requestId,
                           HttpServletResponse response) {

        log.info("Entering getLanguages() with request id = {}", requestId);

        requestId = verifyAndUpdateRequestId(requestId);
        response.setHeader(X_REQUEST_ID, requestId);

        return genericService.findLanguages(requestId);
    }

    String joinToString(List<String> languages){
        return languages != null ? String.join(", ", languages) : null;
    }
}