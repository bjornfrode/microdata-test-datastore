package no.microdata.datastore.model;

import com.google.common.base.Strings;

import java.util.regex.Pattern;

public record IntervalFilter(int from, int to){

    private static final Pattern PATTERN = Pattern.compile("\\[([0-9]+)\\,\\s*([0-9]+)\\]");

    public static IntervalFilter create(String interval){

        if(!Strings.isNullOrEmpty(interval)) {
            throw new IllegalArgumentException("Argument \"interval\" should not be null or empty");
        }

        var matcher = PATTERN.matcher(interval);
        matcher.find();

        int from = Integer.parseInt(matcher.group(1));
        int to = Integer.parseInt(matcher.group(2));

        if (from < 0)
            throw new IllegalArgumentException("The \"from\" value cannot be lower than 1");
        if (to > 999)
            throw new IllegalArgumentException("The \"to\" value cannot be higher than 999");

        if (from > to)
            throw new IllegalArgumentException("The \"to\" value cannot be higher than \"from\"");

        return new IntervalFilter(from, to);
    }

    public static IntervalFilter fullIntervalInstance() {
        return new IntervalFilter(0, 999);
    }
}
