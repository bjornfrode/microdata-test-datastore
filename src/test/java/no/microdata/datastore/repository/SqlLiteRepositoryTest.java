package no.microdata.datastore.repository;

import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SqlLiteRepositoryTest {

    @Test
    void originalExample() {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next())
            {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    @Test
    void ifItWorks() throws ParseException {
        Connection connection = null;
        try
        {
            SQLiteConfig config = new SQLiteConfig();
            //config.setReadOnly(true);
            //config.setSharedCache(true);

            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:BEFOLKNING_HUSHNR__4_0.db", config.toProperties());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists BEFOLKNING_HUSHNR__4_0");
            //date is stored as INTEGER type as Unix Time, the number of seconds since 1970-01-01 00:00:00 UTC
            statement.executeUpdate("create table BEFOLKNING_HUSHNR__4_0 (unit_id integer, value string, start integer, stop integer, attributes string)");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = dateFormat.parse("2020-01-01");
            Date stop = dateFormat.parse("2020-12-31");
            long startStopwatch = System.currentTimeMillis();
            int rowNo = 100000;

            statement.execute("PRAGMA synchronous = OFF");
            statement.execute("BEGIN TRANSACTION");
            for (int i = 0; i <= rowNo; i++) {
                statement.addBatch("insert into BEFOLKNING_HUSHNR__4_0 values("
                        + i + ", '0000" + i +"'," + start.getTime() + ","  + stop.getTime() + ", null)");
                if (i % 10000 == 0) {
                    statement.executeBatch();
                }
            }
            statement.executeBatch();
            statement.execute("END TRANSACTION");
            System.out.println("Done inserting " + rowNo + " rows in " + (System.currentTimeMillis() - startStopwatch) + "ms");

            ResultSet rs = statement.executeQuery("select * from BEFOLKNING_HUSHNR__4_0");
            System.out.println("Reading first 10 rows:");
            for (int i = 0; i < 10; i++) {
            //while(rs.next())
                rs.next();
                System.out.println("unit_id = " + rs.getInt("unit_id"));
                System.out.println("value = " + rs.getString("value"));
                System.out.println("start = " + rs.getInt("start") + " as date = " + rs.getDate("start"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
