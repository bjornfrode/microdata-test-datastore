package no.microdata.datastore.transformations;

import no.microdata.datastore.adapters.api.ErrorMessage;
import no.microdata.datastore.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(VersionUtils.class);
    private static final String SEMVER_4_PARTS_REG_EXP = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)$";
    private static final Pattern PATTERN4PARTS = Pattern.compile(SEMVER_4_PARTS_REG_EXP);

    static final String period = ".";
    static final String lastZero = ".0";
    static final String threeZeros = "0.0.0";

    public static String toThreeLabelsIfNotDraft(String version) {
        if (!is4PartSemanticVersion(version)) {
            var message = ErrorMessage.versionValidationError(version);
            LOG.error("Version validation error with version " + version + " Error message: " + message);
            throw new BadRequestException(message);
        }

        var numberOfPeriods = StringUtils.countOccurrencesOf(version, period);
        if (!version.startsWith(threeZeros) && numberOfPeriods == 3) {
            version = version.substring(0, version.lastIndexOf(period));
        }

        return version;
    }

    static String toFourLabelsIfNotDraft(String version) {
        if (! version.startsWith(threeZeros)){
            version = version + lastZero;
        }

        return version;
    }

    static final boolean is4PartSemanticVersion(String version) {
        Matcher matcher = PATTERN4PARTS.matcher(version);
        return matcher.find();
    }
}
