package no.microdata.datastore.repository;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;

// Example copied from Spark instalation dir
// /opt/spark/examples/src/main/java/org/apache/spark/examples/sql/JavaSQLDataSourceExample.java

public class SparkTest {

    @Test
    public void runBasicParquetExample(){
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL data sources example")
                .config("spark.master", "spark://192.168.56.111:7077")
                .getOrCreate();


        Dataset<Row> peopleDF = spark.read().json("spark/people.json");

        // DataFrames can be saved as Parquet files, maintaining the schema information
        peopleDF.write().parquet("spark/people.parquet");

        // Read in the Parquet file created above.
        // Parquet files are self-describing so the schema is preserved
        // The result of loading a parquet file is also a DataFrame
        Dataset<Row> parquetFileDF = spark.read().parquet("people.parquet");

        // Parquet files can also be used to create a temporary view and then used in SQL statements
        parquetFileDF.createOrReplaceTempView("parquetFile");
        Dataset<Row> namesDF = spark.sql("SELECT name FROM parquetFile WHERE age BETWEEN 13 AND 19");
        Dataset<String> namesDS = namesDF.map(
                (MapFunction<Row, String>) row -> "Name: " + row.getString(0),
                Encoders.STRING());
        namesDS.show();
    }
}
