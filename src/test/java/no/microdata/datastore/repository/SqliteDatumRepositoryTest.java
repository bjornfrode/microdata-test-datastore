package no.microdata.datastore.repository;

import no.microdata.datastore.MockApplication;
import no.microdata.datastore.MockConfig;
import no.microdata.datastore.model.Datum;
import no.microdata.datastore.model.EventQuery;
import no.microdata.datastore.model.FixedQuery;
import no.microdata.datastore.model.StatusQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {MockApplication.class, MockConfig.class})
class SqliteDatumRepositoryTest {

    @Autowired
    private DatumRepository repository;

    @Test
    void timeQueryShouldReturnTverrsnittOn1990_12_31() {
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimeQueryOn19901231();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(4, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timeQueryShouldReturnTverrsnittOn1990_12_31WithNoStartAndStopAttributes() {
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimeQueryOn19901231NoAttributes();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(4, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timeQueryShouldReturnTverrsnittOn1991_12_31() {
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimeQueryOn19911231();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(3, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timePeriodQueryShouldReturnDataFrom1991_01_01To1991_12_31() {
        QueryAndExpectedResults<EventQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimePeriodQueryFrom1991_01_01To1991_12_31();

        Collection<Datum> actualDatums = repository.findByTimePeriod(testData.query());

        assertNotNull(actualDatums);
        assertEquals(7, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timePeriodQueryShouldReturnDataFrom1991_01_01To1992_12_31() {
        QueryAndExpectedResults<EventQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimePeriodQueryFrom1991_01_01To1992_12_31();

        Collection<Datum> actualDatums = repository.findByTimePeriod(testData.query());

        assertNotNull(actualDatums);
        assertEquals(9, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timePeriodQueryShouldReturnDataFrom1991_01_01To1992_12_31WithNoStartAndStopAttributes() {
        QueryAndExpectedResults<EventQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimePeriodQueryFrom1991_01_01To1992_12_31NoAttributes();

        Collection<Datum> actualDatums = repository.findByTimePeriod(testData.query());

        assertNotNull(actualDatums);
        assertEquals(9, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timeQueryWithUnitIdFilter(){
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimeQueryWithUnitIdFilter();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(2, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timePeriodQueryWithUnitIdFilter(){
        QueryAndExpectedResults<EventQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimePeriodQueryWithUnitIdFilter();

        Collection<Datum> actualDatums = repository.findByTimePeriod(testData.query());

        assertNotNull(actualDatums);
        assertEquals(5, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void timeQueryWithNonExistingTable() {
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForNonExistingDataset();

        Assertions.assertThrows(RuntimeException.class, () -> {
            repository.findByTime(testData.query());
        });
    }

    @Test
    void fixedQueryShouldReturnAllDatums() {
        QueryAndExpectedResults<FixedQuery> testData =
                DatumRepositorySpecFixture.createTestDataForFixedQuery();

        Collection<Datum> actualDatums = repository.findByFixed(testData.query());

        assertNotNull(actualDatums);
        assertEquals(4, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void statusQueryWithValueFilter(){
        QueryAndExpectedResults<StatusQuery> testData =
                DatumRepositorySpecFixture.createTestDataForTimeQueryWithValueFilter();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(2, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void eventQueryWithValueFilter(){
        QueryAndExpectedResults<EventQuery> testData =
                DatumRepositorySpecFixture.createTestDataForEventQueryWithValueFilter();

        Collection<Datum> actualDatums = repository.findByTimePeriod(testData.query());

        assertNotNull(actualDatums);
        assertEquals(5, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

    @Test
    void fixedQueryWithValueFilter(){
        QueryAndExpectedResults<FixedQuery> testData =
                DatumRepositorySpecFixture.createTestDataForFixedQueryWithValueFilter();

        Collection<Datum> actualDatums = repository.findByFixed(testData.query());

        assertNotNull(actualDatums);
        assertEquals(3, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }

}