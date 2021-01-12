package no.microdata.datastore.adapters.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

class DataAPITestFixture {

    static final Map datastructureFnr() throws JsonProcessingException {

        String json = "{\n" +
                "  \"attributeVariables\": [\n" +
                "    {\n" +
                "      \"name\": \"END\",\n" +
                "      \"label\": \"End\",\n" +
                "      \"dataType\": \"Instant\",\n" +
                "      \"representedVariables\": [\n" +
                "        {\n" +
                "          \"validPeriod\": {\n" +
                "            \"start\": 946681200\n" +
                "          },\n" +
                "          \"valueDomain\": {\n" +
                "            \"missingValues\": [\n" +
                "              \n" +
                "            ],\n" +
                "            \"codeList\": [\n" +
                "              {\n" +
                "                \"category\": \"0 - 19,9 timer\",\n" +
                "                \"code\": 1\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"20 - 29,9 timer\",\n" +
                "                \"code\": 2\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"30 timer og mer\",\n" +
                "                \"code\": 3\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"description\": \"Stoppdato for forløpsdata.\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"unitType\": {\n" +
                "        \"description\": \"Person\",\n" +
                "        \"label\": \"Person\",\n" +
                "        \"name\": \"PERSON\"\n" +
                "      },\n" +
                "      \"variableRole\": \"Stop\",\n" +
                "      \"datums\": [\n" +
                "        11323,\n" +
                "        11688,\n" +
                "        11323,\n" +
                "        11688,\n" +
                "        11323\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"dataType\": \"Instant\",\n" +
                "      \"label\": \"Start\",\n" +
                "      \"name\": \"START\",\n" +
                "      \"unitType\": {\n" +
                "        \"description\": \"Person\",\n" +
                "        \"label\": \"Person\",\n" +
                "        \"name\": \"PERSON\"\n" +
                "      },\n" +
                "      \"representedVariables\": [\n" +
                "        {\n" +
                "          \"validPeriod\": {\n" +
                "            \"start\": 946681200\n" +
                "          },\n" +
                "          \"valueDomain\": {\n" +
                "            \"missingValues\": [\n" +
                "              \n" +
                "            ],\n" +
                "            \"codeList\": [\n" +
                "              {\n" +
                "                \"category\": \"0 - 19,9 timer\",\n" +
                "                \"code\": 1\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"20 - 29,9 timer\",\n" +
                "                \"code\": 2\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"30 timer og mer\",\n" +
                "                \"code\": 3\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"description\": \"Startdato for forløpsdata eller måletidspunkt for statusdata/tverrsnittsdata.\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"variableRole\": \"Start\",\n" +
                "      \"datums\": [\n" +
                "        10957,\n" +
                "        11323,\n" +
                "        10957,\n" +
                "        11323,\n" +
                "        10957\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"identifierVariables\": [\n" +
                "    {\n" +
                "      \"label\": \"Fødselsnummer kryptert (HASH)\",\n" +
                "      \"dataType\": \"String\",\n" +
                "      \"name\": \"FNR_HASH\",\n" +
                "      \"unitType\": {\n" +
                "        \"description\": \"Person\",\n" +
                "        \"label\": \"Person\",\n" +
                "        \"name\": \"PERSON\"\n" +
                "      },\n" +
                "      \"representedVariables\": [\n" +
                "        {\n" +
                "          \"validPeriod\": {\n" +
                "            \"start\": 946681200\n" +
                "          },\n" +
                "          \"valueDomain\": {\n" +
                "            \"missingValues\": [\n" +
                "              \n" +
                "            ],\n" +
                "            \"codeList\": [\n" +
                "              {\n" +
                "                \"category\": \"0 - 19,9 timer\",\n" +
                "                \"code\": 1\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"20 - 29,9 timer\",\n" +
                "                \"code\": 2\n" +
                "              },\n" +
                "              {\n" +
                "                \"category\": \"30 timer og mer\",\n" +
                "                \"code\": 3\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          \"description\": \"Person identifisert med kryptert (HASH) fødselsnummer.\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"variableRole\": \"Identifier\",\n" +
                "      \"datums\": [\n" +
                "        1,\n" +
                "        2,\n" +
                "        2,\n" +
                "        3,\n" +
                "        4\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"measureVariable\": {\n" +
                "    \"dataType\": \"String\",\n" +
                "    \"label\": \"Fødselsnummer\",\n" +
                "    \"name\": \"FNR\",\n" +
                "    \"unitType\": {\n" +
                "      \"description\": \"Person\",\n" +
                "      \"label\": \"Person\",\n" +
                "      \"name\": \"PERSON\"\n" +
                "    },\n" +
                "    \"representedVariables\": [\n" +
                "      {\n" +
                "        \"validPeriod\": {\n" +
                "          \"start\": 946681200\n" +
                "        },\n" +
                "        \"valueDomain\": {\n" +
                "          \"missingValues\": [\n" +
                "            \n" +
                "          ],\n" +
                "          \"codeList\": [\n" +
                "            {\n" +
                "              \"category\": \"0 - 19,9 timer\",\n" +
                "              \"code\": 1\n" +
                "            },\n" +
                "            {\n" +
                "              \"category\": \"20 - 29,9 timer\",\n" +
                "              \"code\": 2\n" +
                "            },\n" +
                "            {\n" +
                "              \"category\": \"30 timer og mer\",\n" +
                "              \"code\": 3\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"description\": \"Variabelen viser fødselsnummer, det vil si fødselsdag, -måned og -år (6 siffer) og personnummer (5 siffer)\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"variableRole\": \"Measure\",\n" +
                "    \"datums\": [\n" +
                "      \"1\",\n" +
                "      \"2\",\n" +
                "      \"45\",\n" +
                "      \"3\",\n" +
                "      \"4\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"name\": \"FNR\",\n" +
                "  \"temporality\": \"Event\",\n" +
                "  \"temporalCoverage\": {\n" +
                "    \"start\": 946681200,\n" +
                "    \"stop\": 1419980400\n" +
                "  },\n" +
                "  \"subjectFields\": [\n" +
                "    \"ARBEID_LONN\"\n" +
                "  ],\n" +
                "  \"languageCode\": \"no\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(json, Map.class);

        return map;
    }
}