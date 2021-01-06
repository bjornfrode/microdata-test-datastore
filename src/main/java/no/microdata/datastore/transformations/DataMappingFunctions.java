package no.microdata.datastore.transformations;

import no.microdata.datastore.model.Datatype;
import no.microdata.datastore.model.SplitDatums;

import java.util.*;
import java.util.stream.Collectors;

public class DataMappingFunctions {

    public static Map addDatumsToDataStructure(Map dataStructure, SplitDatums datums, Boolean includeAttributes){
        if(dataStructure == null)
            throw new IllegalArgumentException("DataStructure argument can't be null.");
        if(datums == null)
            throw new IllegalArgumentException("SplitDatums argument can't be null");

        dataStructure = addMeasureDatumsToDataStructure(dataStructure, datums);
        dataStructure = addIdentifierDatumsToDataStructure(dataStructure, datums);

        if (includeAttributes){
            dataStructure = addStartTimeDatumsToDataStructure(dataStructure, datums);
            dataStructure = addEndTimeDatumsToDataStructure(dataStructure, datums);
        }
        return dataStructure;
    }

    public static Map addMeasureDatumsToDataStructure(Map dataStructure, SplitDatums datums) {

        if (validMeasureVariableExists(dataStructure)){

            if( ((Map)dataStructure.get("measureVariable")).get("dataType").equals(Datatype.LONG.toString())) {
                ((Map)dataStructure.get("measureVariable")).put("datums", stringListToLong(datums.getValues()));

            } else if ( ((Map)dataStructure.get("measureVariable")).get("dataType").equals(Datatype.DOUBLE.toString())) {
                ((Map)dataStructure.get("measureVariable")).put("datums", stringListToDouble(datums.getValues()));

            } else
                ((Map)dataStructure.get("measureVariable")).put("datums", datums.getValues());
        } else
            throw new IllegalArgumentException(
                    "The DataStructure map has illegal measureVariable object. DataStructure = " + dataStructure);

        return dataStructure;
    }

    private static List<Long> stringListToLong(List<String> list){
        return list.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private static List<Double> stringListToDouble(List<String> list){
        return list.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    private static boolean validMeasureVariableExists(Map dataStructure) {
        if (dataStructure!=null && dataStructure.get("measureVariable")!=null &&
                        ((Map)dataStructure.get("measureVariable")).containsKey("label")){
            return true;
        }
        return false;
    }

    private static boolean validIdentifierVariableExists(Map identifierVariable) {
        if (identifierVariable!=null && identifierVariable.containsKey("label")){
            return true;
        }
        return false;
    }

    private static boolean validAttributeVariableExists(Map attributeVariable) {
        if (attributeVariable!=null && attributeVariable.containsKey("label")){
            return true;
        }
        return false;
    }

    public static Map addIdentifierDatumsToDataStructure(Map dataStructure, SplitDatums datums) {

        Map identifierVariable = (Map) ((List)dataStructure.get("identifierVariables")).get(0);

        if (validIdentifierVariableExists(identifierVariable)){
                identifierVariable.put("datums", datums.getIds());
        } else
            throw new IllegalArgumentException(
                    "The DataStructure map has illegal identifier object. DataStructure = " + dataStructure);

        return dataStructure;
    }

    public static Map addStartTimeDatumsToDataStructure(Map dataStructure, SplitDatums datums) {

        Map startAttributeVariable=null;
        if (dataStructure!=null) {
            List<Map> attributeVariables = (List) dataStructure.getOrDefault("attributeVariables", new ArrayList<>());
            Optional<Map> first = attributeVariables.stream()
                    .filter(attributeVariable -> Objects.equals(attributeVariable.get("variableRole"),"Start"))
                    .findFirst();
            startAttributeVariable = first.orElse(null);
        }

        if (validAttributeVariableExists(startAttributeVariable) && datums.getStartDates().size() > 0){
            startAttributeVariable.put("datums", datums.startDatesAsDays());
        }else {
            startAttributeVariable.put("datums", new ArrayList<>());
        }
        return dataStructure;
    }

    public static Map addEndTimeDatumsToDataStructure(Map dataStructure, SplitDatums datums) {

        Map endAttributeVariable=null;
        if (dataStructure!=null) {
            List<Map> attributeVariables = (List) dataStructure.getOrDefault("attributeVariables", new ArrayList<>());
            Optional<Map> first = attributeVariables.stream()
                    .filter(attributeVariable -> Objects.equals(attributeVariable.get("variableRole"),"Stop"))
                    .findFirst();
            endAttributeVariable = first.orElse(null);
        }

        if (validAttributeVariableExists(endAttributeVariable) && datums.getStopDates().size() > 0){
            endAttributeVariable.put("datums", datums.stopDatesAsDays());
        }else {
            endAttributeVariable.put("datums", new ArrayList<>());
        }
        return dataStructure;
    }
}