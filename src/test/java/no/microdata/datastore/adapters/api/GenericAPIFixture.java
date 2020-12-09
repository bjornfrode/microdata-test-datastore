package no.microdata.datastore.adapters.api;

import java.util.List;
import java.util.Map;

public class GenericAPIFixture {

    public static List<Map<String, Object>> LANGUAGES =
            List.of(
                    Map.of(
                            "code", "no",
                            "label", "Norsk"
                    ),
                    Map.of(
                            "code", "de",
                            "label", "Deutsch")
            );
}
