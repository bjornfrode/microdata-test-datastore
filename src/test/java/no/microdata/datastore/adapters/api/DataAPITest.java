package no.microdata.datastore.adapters.api;

import no.microdata.datastore.DataService;
import no.microdata.datastore.MockApplication;
import no.microdata.datastore.MockConfig;
import no.microdata.datastore.adapters.api.dto.Credentials;
import no.microdata.datastore.adapters.api.dto.DataStoreVersionQuery;
import no.microdata.datastore.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

//import org.springframework.restdocs.JUnitRestDocumentation;
//import org.springframework.restdocs.headers.HeaderDocumentation;
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//import org.springframework.restdocs.operation.OperationRequest;
//import org.springframework.restdocs.operation.OperationRequestFactory;
//import org.springframework.restdocs.operation.OperationResponse;
//import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
//import org.springframework.restdocs.operation.preprocess.Preprocessors;

import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {MockApplication.class, MockConfig.class})
public class DataAPITest {

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MSGPACK = "application/x-msgpack";
    public static final String REQUESTID_DESCRIPTION = "The request id. Optional.";
    public static final String DATASTRUCTURE_NAME_DESCRIPTION = "The name of the data structure.";
    public static final String ACCEPT_LANGUAGE_DESCRIPTION = "ISO 639-1 language code. Optional.";
    public static final String CONTENT_TYPE_DESCRIPTION = "The content type of the request body. We expect \"application/json\".";
    public static final String ACCEPT_DESCRIPTION = "The API support both \"application/json\" and \"application/x-msgpack\"";
    public static final String DATASTORE_VERSION = "1.1.0.0";
    public static final String DATASTRUCTURE_VERSION = "1.0.0.0";
    public static final String VERSION_DESCRIPTION = "The version of the datastore.";
    public static final String VALUE_FILTER_DESCRIPTION = "The value filter. It is a collection of Strings";
    public static final String POPULATION_DESCRIPTION = "The population filter. It is an object with a key named 'unitIds' where the value is an array of unit IDs.";
    public static final String POPULATION_UNITIDS_DESCRIPTION = "The unit ids in the population.";
    public static final String INTERVALFILTER_DESCRIPTION = "An interval of integers between 0 and 999. Format: '[10, 100]'. This can be used by the http client in order to" +
                                                            " run parallel requests.  Optional.";
    public static final String INCLUDE_ATTRIBUTES_DESCRIPTION = "No attributes will be returned if 'includeAttributes' is false or not provided. Optional.";
    public static final String CREDENTIALS_USERNAME = "The username";
    public static final String CREDENTIALS_PASSWORD = "The password";
    public static final Map AUTHENTICATION_FAILURE = Map.of(
                                                            "type", "AUTHENTICATION_FAILURE",
                                                            "code", 104,
                                                            "service", "data-store",
                                                            "message", "Wrong username or password"
                                                        );
    public static final Map REQUEST_VALIDATION_ERROR = Map.of(
                                                            "code", 101,
                                                            "service", "data-store",
                                                            "type", "REQUEST_VALIDATION_ERROR",
                                                            "message", "child \"version\" fails because [\"version\" is required]"
                                                        );

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext

    DataService dataService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        dataService = (DataService) webApplicationContext.getBean("dataService");
        reset(dataService);
    }

    @Test
    public void testGetEvent() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                                version: DATASTORE_VERSION, includeAttributes: true)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                                                version: DATASTRUCTURE_VERSION)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision: datasetRevision,
                                                    startDate: startDate,
                                                    endDate: endDate,
                                                    requestId: '56',
                                                    valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                                                    unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                                                    intervalFilter: IntervalFilter.create('[0, 99]'),
                                                    includeAttributes: true)

        def queryAsMap = [
                version:  DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                                'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ],
                includeAttributes: true
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))
                .andDo(MockMvcRestDocumentation.document("getEvent-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(MockMvcRestDocumentation.document("getEvent",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                org.springframework.restdocs.payload.PayloadDocumentation.requestFields(
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("version").description(VERSION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("startDate").description("The start date of the time period"),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("stopDate").description("The end date of the time period"),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population").description(POPULATION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("includeAttributes").description(INCLUDE_ATTRIBUTES_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                ),
                HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION)),
        ))

        verify(dataService).getEvent(metadataQuery, timePeriodQuery)
    }

    @Test
    void testGetEventWithMalformedUrl() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                version:  DATASTORE_VERSION, includeAttributes: true)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision:  new DatasetRevision(datasetName: metadataQuery.names[0],
                version:  DATASTRUCTURE_VERSION),
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                intervalFilter: IntervalFilter.create('[0, 99]'),
                includeAttributes: true)

        def queryAsMap = [
                version:  DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                        'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ],
                includeAttributes: true
        ]


        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/THISDOESNOTEXIST/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())

        verifyNoInteractions(dataService)
    }

    @Test
    void testGetEventWithInvalidUsername() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '58',
                version: DATASTORE_VERSION, includeAttributes: true)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision:  new DatasetRevision(datasetName: metadataQuery.names[0],
                version: DATASTRUCTURE_VERSION),
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                intervalFilter: IntervalFilter.create('[0, 99]'),
                includeAttributes: true)

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                        'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: 'INVALID_USERNAME',
                        password: Credentials.VALID_PASSWORD
                ],
                includeAttributes: true
        ]

        
        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(AUTHENTICATION_FAILURE).toString()))

        verifyNoInteractions(dataService)
    }

    @Test
    void testGetEventWithInvalidPassword() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '59',
                version: DATASTORE_VERSION, includeAttributes: true)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision:  new DatasetRevision(datasetName: metadataQuery.names[0],
                version: DATASTRUCTURE_VERSION),
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                intervalFilter: IntervalFilter.create('[0, 99]'),
                includeAttributes: true)

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                        'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: 'INVALID_PASSWORD'
                ],
                includeAttributes: true
        ]

        
        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(AUTHENTICATION_FAILURE).toString()))

        verifyNoInteractions(dataService)
    }

    @Ignore
    @Test
    public void testGetEventWithMissingMandatoryKey() throws Exception{
        //  Missing mandatory key: version
        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                version: DATASTORE_VERSION, includeAttributes: true)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision:  new DatasetRevision(datasetName: metadataQuery.names[0],
                version: DATASTRUCTURE_VERSION),
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                intervalFilter: IntervalFilter.create('[0, 99]'),
                includeAttributes: true)

        def queryAsMap = [
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                        'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ],
                includeAttributes: true
        ]

        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                 .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(REQUEST_VALIDATION_ERROR).toString()))
        verifyNoInteractions(dataService)
    }

    @Test
    public void testGetEventWithWrongUnitIdFilterKey() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery =
                new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56', version: DATASTORE_VERSION)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision:  new DatasetRevision(datasetName: metadataQuery.names[0],
                version: DATASTRUCTURE_VERSION),
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]))

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [
                        'PersonID_1' : populationFilter,
                ],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId).header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))

        verify(dataService).getEvent(metadataQuery, timePeriodQuery)
    }

    @Test
    public void testGetStatus() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnr()

        Long date = 14579;

        def populationFilter = [1, 2, 2, 3, 4]

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                                version: DATASTORE_VERSION, includeAttributes: true)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                                                                    version: DATASTRUCTURE_VERSION)

        StatusQuery timeQuery = new StatusQuery(datasetRevision: datasetRevision,
                                                date: LocalDate.parse("2009-12-01"),
                                                requestId: '56',
                                                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                                                unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                                                intervalFilter: IntervalFilter.create('[0, 99]'),
                                                includeAttributes: true)

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                date: date,
                values: timeQuery.valueFilter.valueFilter,
                population: [
                        'unitIds' : populationFilter
                ],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ],
                includeAttributes: true
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getStatus(metadataQuery, timeQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/status')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))
                .andDo(MockMvcRestDocumentation.document("getStatus-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(MockMvcRestDocumentation.document("getStatus",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                org.springframework.restdocs.payload.PayloadDocumentation.requestFields(
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("version").description(VERSION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("date").description("The date of the metadataQuery"),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population").description(POPULATION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("includeAttributes").description(INCLUDE_ATTRIBUTES_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                ),
                HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION)),
        ))

        verify(dataService).getStatus(metadataQuery, timeQuery)
    }

    @Test
    public void testGetStatusWithWrongUnitIdFilterKey() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnr()
        Long date = 14579;

        def populationFilter = [1, 2, 2, 3, 4]

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56', version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                version: DATASTRUCTURE_VERSION)

        StatusQuery timeQuery = new StatusQuery(datasetRevision: datasetRevision,
                date: LocalDate.parse("2009-12-01"),
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"])
        )

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                date: date,
                values: timeQuery.valueFilter.valueFilter,
                population: [
                        'PersonID_1' : populationFilter,
                ],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getStatus(metadataQuery, timeQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/status')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId).header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))

        verify(dataService).getStatus(metadataQuery, timeQuery)
    }

    @Test
    public void testGetFixed() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnrForFixed()

        def populationFilter = [1, 2, 2, 3, 4]

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                                                            version: DATASTRUCTURE_VERSION)

        FixedQuery fixedQuery = new FixedQuery(datasetRevision: datasetRevision,
                                               requestId: '56',
                                               valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                                               unitIdFilter: new UnitIdFilter(populationFilter as HashSet),
                                               intervalFilter: IntervalFilter.create('[0, 99]'),)

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                values: fixedQuery.valueFilter.valueFilter,
                population: ['unitIds' : populationFilter],
                intervalFilter: '[0, 99]',
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getFixed(metadataQuery, fixedQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/fixed')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))
                .andDo(MockMvcRestDocumentation.document("getFixed-request", Preprocessors.preprocessRequest(maskPassword())))
                .andDo(MockMvcRestDocumentation.document("getFixed",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                org.springframework.restdocs.payload.PayloadDocumentation.requestFields(
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("version").description(VERSION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("dataStructureName").description(DATASTRUCTURE_NAME_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("values").description(VALUE_FILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population").description(POPULATION_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("population.unitIds").description(POPULATION_UNITIDS_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("intervalFilter").description(INTERVALFILTER_DESCRIPTION),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.username").description(CREDENTIALS_USERNAME),
                        org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath("credentials.password").description(CREDENTIALS_PASSWORD)
                ),
                HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName(Constants.X_REQUEST_ID).description(REQUESTID_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT_LANGUAGE).description(ACCEPT_LANGUAGE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.CONTENT_TYPE).description(CONTENT_TYPE_DESCRIPTION),
                        HeaderDocumentation.headerWithName(Constants.ACCEPT).description(ACCEPT_DESCRIPTION)),
        ))
        verify(dataService).getFixed(metadataQuery, fixedQuery)
    }

    @Test
    public void testGetFixedWithWrongUnitIdFilterKey() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnrForFixed()

        def populationFilter = [1, 2, 2, 3, 4]

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                            version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                            version: DATASTRUCTURE_VERSION)

        FixedQuery fixedQuery = new FixedQuery(datasetRevision: datasetRevision,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]))

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                values: fixedQuery.valueFilter.valueFilter,
                population: [ 'PersonID_1' : populationFilter],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getFixed(metadataQuery, fixedQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/fixed')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId).header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))

        verify(dataService).getFixed(metadataQuery, fixedQuery)
    }


    @Test
    public void testThatContentTypeIsMsgPack() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnr()
        Long date = 14579;
        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                        version: DATASTORE_VERSION)
        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                        version: DATASTRUCTURE_VERSION)
        StatusQuery timeQuery = new StatusQuery(datasetRevision: datasetRevision,
                                        date: LocalDate.parse("2009-12-01"), requestId: '56')
        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                date: date,
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getStatus(metadataQuery, timeQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/status')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages)
                .header(Constants.ACCEPT, Constants.ACCEPT_MSGPACK))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_MSGPACK))

        verify(dataService).getStatus(metadataQuery, timeQuery)
    }

    @Test
    public void testThatContentTypeDefaultsToJson() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnr()
        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                            version: DATASTORE_VERSION)
        DatasetRevision datasetRevision = new DatasetRevision(datasetName:  metadataQuery.names[0],
                                            version: DATASTRUCTURE_VERSION)
        StatusQuery timeQuery = new StatusQuery(datasetRevision: datasetRevision,
                                                date: LocalDate.parse("2009-12-01"), requestId: '56')
        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                date: timeQuery.date.toEpochDay(),
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getStatus(metadataQuery, timeQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/status')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))

        verify(dataService).getStatus(metadataQuery, timeQuery)
    }

    @Test
    public void testGetFixedWithNoFiltering() throws Exception{

        def expectedEvent = DataAPITestFixture.datastructureFnrForFixed()

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                            version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                            version: DATASTRUCTURE_VERSION)

        FixedQuery fixedQuery = new FixedQuery(datasetRevision: datasetRevision, requestId: '56')

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getFixed(metadataQuery, fixedQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/fixed')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))

        verify(dataService).getFixed(metadataQuery, fixedQuery)
    }

    @Test
    public void testGetEventWithOnlyUseOfUnitIdKey() throws Exception{

        def populationFilter = [1, 2, 2, 3, 4]
        def expectedEvent = DataAPITestFixture.datastructureFnr()

        LocalDate startDate = LocalDate.parse("2009-12-01")
        LocalDate endDate = LocalDate.parse("2012-08-31")

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                            version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                                        version: DATASTRUCTURE_VERSION)

        EventQuery timePeriodQuery = new EventQuery(datasetRevision: datasetRevision,
                startDate: startDate,
                endDate: endDate,
                requestId: '56',
                valueFilter: ValueFilter.create(["1", "2", "45", "3", "4"]),
                unitIdFilter: new UnitIdFilter(populationFilter as HashSet))

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                startDate: startDate.toEpochDay(),
                stopDate: endDate.toEpochDay(),
                values: timePeriodQuery.valueFilter.valueFilter,
                population: [ 'unitIds' : populationFilter],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getEvent(metadataQuery, timePeriodQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/event')
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonOutput.toJson(queryAsMap))
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId).header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_JSON))
                .andExpect(MockMvcResultMatchers.content().json(JsonOutput.toJson(expectedEvent).toString()))

        verify(dataService).getEvent(metadataQuery, timePeriodQuery)
    }

    @Test
    public void testGetFixedWithMsgPackPayload() throws Exception {
        def expectedEvent = DataAPITestFixture.datastructureFnrForFixed()

        MetadataQuery metadataQuery = new MetadataQuery(names: ['FNR'], languages: 'no', requestId: '56',
                                                        version: DATASTORE_VERSION)

        DatasetRevision datasetRevision = new DatasetRevision(datasetName: metadataQuery.names[0],
                                                        version: DATASTRUCTURE_VERSION)

        FixedQuery fixedQuery = new FixedQuery(datasetRevision: datasetRevision, requestId: '56')

        def queryAsMap = [
                version: DATASTORE_VERSION,
                dataStructureName: metadataQuery.names[0],
                credentials: [
                        username: Credentials.VALID_USERNAME,
                        password: Credentials.VALID_PASSWORD
                ]
        ]
        def keys = queryAsMap.keySet() as String[]

        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

        packer.packMapHeader(3); // the number of (key, value) pairs
        packer.packString(keys[0])
        packer.packString(queryAsMap.get(keys[0]))
        packer.packString(keys[1])
        packer.packString(queryAsMap.get(keys[1]))
        packer.packString(keys[2])
        packer.packMapHeader(2)
        packer.packString('username')
        packer.packString(Credentials.VALID_USERNAME)
        packer.packString('password')
        packer.packString(Credentials.VALID_PASSWORD)
        packer.close()

        when(dataService.getDataStructureVersion(any(DataStoreVersionQuery.class))).thenReturn([
                "datastructureName" : "FNR",
                "datastructureVersion" : DATASTRUCTURE_VERSION
        ])

        when(dataService.getFixed(metadataQuery, fixedQuery)).thenReturn(expectedEvent)

        mockMvc.perform(RestDocumentationRequestBuilders.post('/data/data-structure/fixed')
                .content(packer.toByteArray())
                .header(Constants.X_REQUEST_ID, metadataQuery.requestId)
                .header(Constants.ACCEPT, Constants.ACCEPT_MSGPACK)
                .header(Constants.CONTENT_TYPE, Constants.ACCEPT_MSGPACK)
                .header(Constants.ACCEPT_LANGUAGE, metadataQuery.languages))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(CONTENT_TYPE_MSGPACK))

        verify(dataService).getFixed(metadataQuery, fixedQuery)
    }

    @Test
    public void testGetDataStructureVersion() throws Exception{
        DataAPI api = new DataAPI()
        DataService dataService = mock(DataService.class);
        api.dataService = dataService

        DataStoreVersionQuery query = new DataStoreVersionQuery('TEST_INNTEKT_HUSHNR', '4.1.0.0', '123')
        when(dataService.getDataStructureVersion(query)).thenReturn([
                                                "datastructureName" : "TEST_INNTEKT_HUSHNR",
                                                "datastructureVersion" : "4.1.0.0"
                                        ])

        String datastructureVersion = api.getDataStructureVersion(query)
        assert datastructureVersion.equals("4.1.0.0")
    }

    @Test(expected = RuntimeException.class)
    public void testGetDataStructureVersionError() throws Exception{
        DataAPI api = new DataAPI()
        DataService dataService = mock(DataService.class);
        api.dataService = dataService

        DataStoreVersionQuery query = new DataStoreVersionQuery('TEST_INNTEKT_HUSHNR', '4.1.0.0', '123')
        when(dataService.getDataStructureVersion(query)).thenReturn([
                "datastructureName" : "ANOTHER_NAME",
                "datastructureVersion" : "4.1.0.0"
        ])
        api.getDataStructureVersion(query)
    }

    private OperationPreprocessor maskPassword() {
        return new PasswordMaskingPreprocessor();
    }

    static class PasswordMaskingPreprocessor implements OperationPreprocessor {

        @Override
        public OperationRequest preprocess(OperationRequest request) {
            String contentAsString = request.getContentAsString()
            def contentAsJson = new JsonSlurper().parseText(contentAsString)
            contentAsJson?.credentials?.password = 'XXX'
            String resultingContentAsString = JsonOutput.toJson(contentAsJson)

            return new OperationRequestFactory().create(request.getUri(),
                    request.getMethod(), resultingContentAsString.bytes, request.getHeaders(),
                    request.getParameters(), request.getParts());
        }

        @Override
        public OperationResponse preprocess(OperationResponse response) {
            return response;
        }

    }
}