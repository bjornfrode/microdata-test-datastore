package no.microdata.datastore.transformations;

public class Utils {

    public static boolean isNullOrEmpty(String string){
        return (string != null && !string.trim().isEmpty()) ? false : true;
    }

    public static boolean isNullOrEmpty(Long value){
        return (value != null && value>0) ? false : true;
    }
}
