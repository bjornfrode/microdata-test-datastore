package no.microdata.datastore.model;

public enum Datatype {

    LONG("Long"),
    STRING("String"),
    DOUBLE("Double");

    String text;
    Datatype(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}