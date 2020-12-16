package no.microdata.datastore.transformations;

import no.microdata.datastore.model.Datatype;
import no.microdata.datastore.model.SplitDatums;

import java.util.Map;

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
            if(dataStructure.measureVariable.dataType.equals(Datatype.LONG.toString())) {
                dataStructure.measureVariable.put('datums', datums.values.collect({ new Long(it) }))
            } else if (dataStructure.measureVariable.dataType.equals(Datatype.DOUBLE.toString())) {
                dataStructure.measureVariable.put('datums', datums.values.collect({ new Double(it) }))
            } else
                dataStructure.measureVariable.put('datums', datums.values)
        } else
            throw new IllegalArgumentException("The DataStructure map has illegal measureVariable object. DataStructure = $dataStructure")

        dataStructure
    }

    private static boolean validMeasureVariableExists(Map dataStructure) {
        dataStructure?.measureVariable?.containsKey('label')
    }

    private static boolean validIdentifierVariableExists(Map identifierVariable) {
        identifierVariable?.containsKey('label')
    }

    private static boolean validAttributeVariableExists(Map attributeVariable) {
        attributeVariable?.containsKey('label')
    }

    public static Map addIdentifierDatumsToDataStructure(Map dataStructure, SplitDatums datums) {

        Map identifierVariable = dataStructure.identifierVariables[0]

        if (validIdentifierVariableExists(identifierVariable)){
            if(identifierVariable.dataType.equals(Datatype.LONG.toString())){
                identifierVariable.put('datums', datums.ids.collect({new Long(it)}))
            } else
                identifierVariable.put('datums', datums.ids)

        } else
            throw new IllegalArgumentException("The DataStructure map has illegal identifier object. DataStructure = $dataStructure")

        dataStructure
    }

    public static Map addStartTimeDatumsToDataStructure(Map dataStructure, SplitDatums datums) {
        Map startAttributeVariable = dataStructure?.attributeVariables?.find{
            it?.variableRole == "Start"
        }

        if (validAttributeVariableExists(startAttributeVariable) && datums.startDates.size() > 0){
            startAttributeVariable.put('datums', datums.startDatesAsDays())
        }else {
            startAttributeVariable.put('datums', [])
        }
        dataStructure
    }

    public static Map addEndTimeDatumsToDataStructure(Map dataStructure, SplitDatums datums) {
        Map endAttributeVariable = dataStructure?.attributeVariables?.find{
            it?.variableRole == "Stop"
        }

        if (validAttributeVariableExists(endAttributeVariable) && datums.stopDates.size() > 0){
            endAttributeVariable.put('datums', datums.stopDatesAsDays())
        }else {
            endAttributeVariable.put('datums', [])
        }
        dataStructure
    }
}