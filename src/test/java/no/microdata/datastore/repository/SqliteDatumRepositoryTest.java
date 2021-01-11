package no.microdata.datastore.repository;

import no.microdata.datastore.MockApplication;
import no.microdata.datastore.MockConfig;
import no.microdata.datastore.model.Datum;
import no.microdata.datastore.model.StatusQuery;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
    }

    @Test
    void timeQueryShouldReturnTverrsnittOn1990_12_31() {
        QueryAndExpectedResults<StatusQuery> testData = DatumRepositorySpecFixture.createTestDataForTimeQueryOn19901231();

        Collection<Datum> actualDatums = repository.findByTime(testData.query());

        assertNotNull(actualDatums);
        assertEquals(4, actualDatums.size());
        assertEquals(testData.expected(), actualDatums);
    }
}