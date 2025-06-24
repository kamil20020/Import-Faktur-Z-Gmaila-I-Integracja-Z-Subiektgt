package org.example.gui;

public enum BooleanSelectOptions {

    NOT_GIVEN(""),
    YES("Tak"),
    NO("Nie");

    private final String value;

    private BooleanSelectOptions(String value){

        this.value = value;
    }

    @Override
    public String toString(){

        return value;
    }

    public static BooleanSelectOptions getValue(String value) throws IllegalArgumentException{

        if(value.equals(BooleanSelectOptions.YES.value)){
            return BooleanSelectOptions.YES;
        }

        for(BooleanSelectOptions option : values()){

            if(option.value.equals(value)){
                return option;
            }
        }

        throw new IllegalArgumentException("Could not find good option");
    }

}
