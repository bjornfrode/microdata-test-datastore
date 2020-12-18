package no.microdata.datastore.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.common.base.Strings;

import java.time.LocalDate;
import java.util.Objects;

public class Datum {

    public Datum(Long id, String value, LocalDate start, LocalDate stop) {
        validateInput(id, value);

        this.id = id;
        this.value = value;

        this.start = start;
        this.stop = stop;
    }

    public Datum(Long id, String value) {
        validateInput(id, value);

        this.id = id;
        this.value = value;
        this.start = null;
        this.stop = null;
    }

    private void validateInput(Long id, String value) {
        if (id == null)
            throw new IllegalArgumentException("'ID' should not be null");
        if (Strings.isNullOrEmpty(value))
            throw new IllegalArgumentException("'Value' should not be null or empty");
    }

    public String getValue() {
        return value;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getStop() {
        return stop;
    }

    public Long getId() {
        return id;
    }

    private final Long id;
    private final String value;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate start;
    private final LocalDate stop;

    @Override
    public String toString() {
        return "Datum{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", start=" + start +
                ", stop=" + stop +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Datum datum = (Datum) o;
        return Objects.equals(id, datum.id) &&
                Objects.equals(value, datum.value) &&
                Objects.equals(start, datum.start) &&
                Objects.equals(stop, datum.stop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, start, stop);
    }
}