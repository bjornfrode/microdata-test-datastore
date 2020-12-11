package no.microdata.datastore.services;

import org.apache.parquet.HadoopReadOptions;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.filter2.compat.FilterCompat;
import org.apache.parquet.filter2.predicate.FilterPredicate;
import org.apache.parquet.filter2.predicate.Operators;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.api.Binary;
import org.junit.jupiter.api.Test;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.ColumnIOFactory;
import org.apache.parquet.io.MessageColumnIO;
import org.apache.parquet.io.RecordReader;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.apache.parquet.filter2.predicate.FilterApi.*;

public class ParquetServiceTest {

    @Test
    void read() throws IOException {
        Configuration conf = new Configuration();

        try {
            Path path = new Path("file:/home/pbu/wk_test_performance/microdata-test-datastore/src/test/resources/no_ssb_test_datastore/dataset/TEST_PERSON_INCOME/TEST_PERSON_INCOME__1_0.parquet");
            ParquetMetadata readFooter = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER);
            MessageType schema = readFooter.getFileMetaData().getSchema();
            ParquetFileReader r = new ParquetFileReader(conf, path, readFooter);

            PageReadStore pages = null;
            try {
                while (null != (pages = r.readNextRowGroup())) {
                    final long rows = pages.getRowCount();
                    System.out.println("Number of rows: " + rows);

                    final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                    final RecordReader<Group> recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
                    for (int i = 0; i < rows; i++) {
                        final Group g = recordReader.read();
                        printGroup(g);

                        // TODO Compare to System.out.println(g);
                    }
                }
            } finally {
                r.close();
            }
        } catch (IOException e) {
            System.out.println("Error reading parquet file.");
            e.printStackTrace();
        }

    }

    @Test
    void readUsingNewerAPIs() throws IOException {
        File f = Paths.get("src/test/resources/no_ssb_test_datastore/dataset/TEST_PERSON_INCOME/TEST_PERSON_INCOME__1_0.parquet").toFile();
        final Path path = new Path(f.toURI());
        final HadoopInputFile hif = HadoopInputFile.fromPath(path, new Configuration());
        final ParquetReadOptions opts = HadoopReadOptions.builder(hif.getConfiguration())
                .withMetadataFilter(ParquetMetadataConverter.NO_FILTER).build();

        try (final ParquetFileReader r = ParquetFileReader.open(hif, opts))
        {
            final ParquetMetadata readFooter = r.getFooter();
            final MessageType schema = readFooter.getFileMetaData().getSchema();

            while (true)
            {
                final PageReadStore pages = r.readNextRowGroup();

                if (pages == null)
                    return;

                final long rows = pages.getRowCount();

                System.out.println("Number of rows: " + rows);

                final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema));
                for (int i = 0; i < rows; i++)
                {
                    final Group g = (Group) recordReader.read();
                    //System.out.println(g);
                    printGroup(g);
                }
            }
        }
    }

    //value == 51000
    @Test
    void filterUsingNewerAPIs() throws IOException {
        File f = Paths.get("src/test/resources/no_ssb_test_datastore/dataset/TEST_PERSON_INCOME/TEST_PERSON_INCOME__1_0.parquet").toFile();
        final Path path = new Path(f.toURI());
        final HadoopInputFile hif = HadoopInputFile.fromPath(path, new Configuration());
        final ParquetReadOptions opts = HadoopReadOptions.builder(hif.getConfiguration())
                .withMetadataFilter(ParquetMetadataConverter.NO_FILTER).build();

        Operators.BinaryColumn value = binaryColumn("value");

        FilterPredicate filterPredicate = eq(value, Binary.fromString("51000"));

        try (final ParquetFileReader r = ParquetFileReader.open(hif, opts))
        {
            final ParquetMetadata readFooter = r.getFooter();
            final MessageType schema = readFooter.getFileMetaData().getSchema();

            while (true)
            {
                final PageReadStore pages = r.readNextRowGroup();

                if (pages == null)
                    return;

                final long rows = pages.getRowCount();

                System.out.println("Number of rows: " + rows);

                final MessageColumnIO columnIO = new ColumnIOFactory().getColumnIO(schema);
                final RecordReader recordReader = columnIO.getRecordReader(pages, new GroupRecordConverter(schema), FilterCompat.get(filterPredicate));
                for (int i = 0; i < rows; i++)
                {
                    final Group g = (Group) recordReader.read();
                    System.out.println(g);
                }
            }
        }
    }

    private static void printGroup(Group g) {
        final int fieldCount = g.getType().getFieldCount();

        for (int field = 0; field < fieldCount; field++)
        {
            final int valueCount = g.getFieldRepetitionCount(field);
            final Type fieldType = g.getType().getType(field);
            final String fieldName = fieldType.getName();

            for (int index = 0; index < valueCount; index++)
            {
                if (fieldType.isPrimitive())
                {
                    System.out.println(fieldName + " " + g.getValueToString(field, index));
                }
            }
        }

        System.out.println("");
    }

}
