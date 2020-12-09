package no.microdata.datastore.model;

enum Datatype {

    LONG("Long"),
    STRING("String"),
    DOUBLE("Double");

    String text;
    private Datatype(String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }
}