package org.example.external.sfera.own;

public enum ResponseStatus {

    SUCCESS("success"),
    ERROR("fail");

    private String name;

    private ResponseStatus(String name){

        this.name = name;
    }

    @Override
    public String toString(){

        return name;
    }
}
