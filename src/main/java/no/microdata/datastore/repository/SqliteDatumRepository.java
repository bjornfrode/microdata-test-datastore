package no.microdata.datastore.repository;

import com.google.common.base.Stopwatch;
import no.microdata.datastore.model.*;
import no.microdata.datastore.transformations.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.sqlite.SQLiteConfig;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Repository
public class SqliteDatumRepository implements DatumRepository {
    private static final Logger log = LoggerFactory.getLogger(SqliteDatumRepository.class);

    private SQLiteConfig sqLiteConfig;

    @Value("${datastore.root}")
    private String datastoreRoot;

    @PostConstruct
    private void postSetup() {
        sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.setReadOnly(true);
    }

    private Connection getConnection(DatasetRevision datasetRevision) throws SQLException {
        String fileName = String.format(
                "%s/dataset/%s/%s__%s.db",
                datastoreRoot,
                datasetRevision.getDatasetName(),
                datasetRevision.getDatasetName(),
                VersionUtils.toTwoLabels(datasetRevision.getVersion()).replace(".", "_"));

        log.debug("Database file: {}", fileName);
        return DriverManager.getConnection("jdbc:sqlite:" + fileName, sqLiteConfig.toProperties());
    }

    @Override
    public Collection<Datum> findByTimePeriod(EventQuery query) {
        if (query == null) {
            throw new NullPointerException("Event query object is null");
        }
        log.debug("Entering findByTimePeriod() with query = {}", query);

        final Stopwatch timer = Stopwatch.createStarted();
        try (Connection con = getConnection(query.getDatasetRevision())) {
            PreparedStatement stmt = findByTimePeriodStatement(con, query);
            logDBPropertiesAndSQL(con, stmt);

            ResultSet rs = stmt.executeQuery();

            final Collection<Datum> results = collectResult(query.getUnitIdFilter(), query.getIncludeAttributes(), rs);
            logResultsAndElapsedTime(timer, results);
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Datum> findByTime(StatusQuery query) {
        if (query == null) {
            throw new NullPointerException("Status query object is null");
        }
        log.debug("Entering findByTime() with query = {}", query);

        final Stopwatch timer = Stopwatch.createStarted();
        try (Connection con = getConnection(query.getDatasetRevision())) {
            PreparedStatement stmt = findByTimeStatement(con, query);
            logDBPropertiesAndSQL(con, stmt);

            ResultSet rs = stmt.executeQuery();

            final Collection<Datum> results = collectResult(query.getUnitIdFilter(), query.getIncludeAttributes(), rs);

            logResultsAndElapsedTime(timer, results);
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Datum> findByFixed(FixedQuery query) {
        if (query == null) {
            throw new NullPointerException("Fixed query object is null");
        }
        log.debug("Entering findByFixed() with query = {}", query);

        final Stopwatch timer = Stopwatch.createStarted();
        try (Connection con = getConnection(query.getDatasetRevision())) {
            PreparedStatement stmt = findByFixedStatement(con, query);

            logDBPropertiesAndSQL(con, stmt);

            ResultSet rs = stmt.executeQuery();
            final Collection<Datum> results = collectResult(query.getUnitIdFilter(), query.getIncludeAttributes(), rs);

            logResultsAndElapsedTime(timer, results);
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Datum> collectResult(UnitIdFilter unitIdFilter, Boolean includeAttributes, ResultSet rs) throws SQLException {
        final Collection<Datum> results = new ArrayList<>();
        if (unitIdFilter != null && unitIdFilter.unitIds().size() > 0) {
            while (rs.next()) {
                Long id = rs.getLong("unit_id");
                if (isInFilter(unitIdFilter, id)) {
                    addDatum(results, rs, id, includeAttributes);
                }
            }
        } else {
            while (rs.next()) {
                addDatum(results, rs, rs.getLong("unit_id"), includeAttributes);
            }
        }
        log.info("The sql query resulted in {} datums", results.size());
        return results;
    }

    private void addDatum(Collection<Datum> result, ResultSet rs, Long id, Boolean includeAttributes) throws SQLException {
        if (includeAttributes == null || !includeAttributes) {
            result.add(new Datum(id, rs.getString("value")));
        } else {
            result.add(
                    new Datum(
                            id,
                            rs.getString("value"),
                            rs.getString("start") != null ?
                                    LocalDate.parse(rs.getString("start")) : null,
                            rs.getString("stop") != null ?
                                    LocalDate.parse(rs.getString("stop")) : null
                    ));
        }
    }

    private PreparedStatement findByTimeStatement(Connection con, StatusQuery query) throws SQLException {
        StringBuilder select = new StringBuilder();
        if (query.getIncludeAttributes() != null && query.getIncludeAttributes()) {
            select.append("SELECT unit_id, value, start, stop ");
        } else {
            select.append("SELECT unit_id, value ");
        }
        select.append("FROM `")
                .append(getTableName(query.getDatasetRevision()))
                .append("` ")
                .append("WHERE part_num BETWEEN ? AND ? ")
                .append(" AND ( (start <= ? AND stop IS NULL) OR (start <= ? AND stop >= ? ) ) ");
        ValueFilter valueFilter = query.getValueFilter();
        if (valueFilter.hasValues()) {
            createINSqlClause(select, valueFilter.valueFilter().size());
        }

        String time = query.getDate().toString();
        PreparedStatement stmt = con.prepareStatement(select.toString());
        stmt.setInt(1, query.getIntervalFilter().from());
        stmt.setInt(2, query.getIntervalFilter().to());
        stmt.setString(3, time);
        stmt.setString(4, time);
        stmt.setString(5, time);

        if (valueFilter.hasValues()) {
            insertValueFilterInStatement(6, valueFilter, stmt);
        }

        return stmt;
    }

    static void createINSqlClause(StringBuilder select, long numberOfElements) {
        select.append("AND value IN (");
        for (int i = 0; i < numberOfElements; i++) {
            select.append(" ?");
            if (i != numberOfElements - 1) select.append(",");
        }

        select.append(")");
    }

    private static void insertValueFilterInStatement(int startIndex, ValueFilter valueFilter, PreparedStatement stmt) throws SQLException {
        Iterator<String> iterator = valueFilter.valueFilter().iterator();
        for (int i = startIndex; i < valueFilter.size() + startIndex; i++) {
            stmt.setString(i, iterator.next());
        }
    }

    private PreparedStatement findByFixedStatement(Connection con, FixedQuery query) throws SQLException {
        StringBuilder select = new StringBuilder();
        select.append("SELECT unit_id, value " + "FROM `")
                .append(getTableName(query.getDatasetRevision()))
                .append("` ")
                .append("WHERE part_num BETWEEN ? AND ? ");

        ValueFilter valueFilter = query.getValueFilter();
        if (valueFilter.hasValues()) {
            createINSqlClause(select, valueFilter.valueFilter().size());
        }

        PreparedStatement stmt = con.prepareStatement(select.toString());
        stmt.setInt(1, query.getIntervalFilter().from());
        stmt.setInt(2, query.getIntervalFilter().to());
        int nextIndex = 3;

        if (valueFilter.hasValues()) {
            insertValueFilterInStatement(nextIndex, valueFilter, stmt);
        }

        return stmt;
    }

    private static String getTableName(DatasetRevision datasetRevision) {
        if (datasetRevision == null)
            throw new NullPointerException("Dataset revision object should not be null.");
        if (datasetRevision.getVersion() == null)
            throw new NullPointerException("Dataset revision does not have a version.");

        String[] array = datasetRevision.getVersion().split("\\.");
        String datasetName = datasetRevision.getDatasetName().toUpperCase();
        return String.format("%1$s__%2$s_%3$s", datasetName, array[0], array[1]);
    }

    static PreparedStatement findByTimePeriodStatement(Connection con, EventQuery query) throws SQLException {
        StringBuilder select = new StringBuilder();
        if (query.getIncludeAttributes() != null && query.getIncludeAttributes()) {
            select.append("SELECT unit_id, value, start, stop ");
        } else {
            select.append("SELECT unit_id, value ");
        }
        select.append("FROM `")
                .append(getTableName(query.getDatasetRevision()))
                .append("` ")
                .append("WHERE (part_num BETWEEN ? AND ?) ")
                .append("AND ( ( (start <= ? AND stop IS NULL) OR (start <= ? AND stop >= ?) )")
                .append("OR")
                .append("( (start BETWEEN ? AND ?) OR (start > ? AND stop <= ?) ) ) ");

        if (query.getValueFilter().hasValues()) {
            createINSqlClause(select, query.getValueFilter().size());
        }

        PreparedStatement stmt = con.prepareStatement(select.toString());
        stmt.setInt(1, query.getIntervalFilter().from());
        stmt.setInt(2, query.getIntervalFilter().to());
        stmt.setString(3, query.getStartDate().toString());
        stmt.setString(4, query.getStartDate().toString());
        stmt.setString(5, query.getStartDate().toString());
        stmt.setString(6, query.getStartDate().toString());
        stmt.setString(7, query.getEndDate().toString());
        stmt.setString(8, query.getStartDate().toString());
        stmt.setString(9, query.getEndDate().toString());

        if (query.getValueFilter().hasValues()) {
            insertValueFilterInStatement(10, query.getValueFilter(), stmt);
        }

        return stmt;
    }

    static boolean isInFilter(UnitIdFilter unitIdFilter, Long id) {
        return unitIdFilter.unitIds().contains(id);
    }

    static void logDBPropertiesAndSQL(Connection con, PreparedStatement stmt) throws SQLException {
        log.debug("Request has connection URL {}", stmt.getConnection().getMetaData().getURL());
        log.debug("Request is using host {}", stmt.getConnection().toString());
        log.debug("Request has read only = {}", con.isReadOnly());
        log.info("Sql = {}", logFriendlySqlString(stmt));
    }

    static void logResultsAndElapsedTime(Stopwatch timer, Collection<Datum> results) {
        log.info("#results = {}, time = {}s ({}ms)",
                results.size(), timer.stop().elapsed(TimeUnit.SECONDS), timer.elapsed(TimeUnit.MILLISECONDS));
    }

    static String logFriendlySqlString(PreparedStatement stmt) {
        String[] array = stmt != null ? stmt.toString().split("SELECT") : new String[0];
        return array.length > 0 ? "SELECT" + array[array.length - 1] : "";
    }

}
