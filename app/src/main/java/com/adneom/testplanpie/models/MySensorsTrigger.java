package com.adneom.testplanpie.models;

/**
 * Created by gtshilombowanticale on 09-08-16.
 */
public class MySensorsTrigger {

    private int secondes;
    private TypesTrigger type;
    private int value;

    public MySensorsTrigger(int secondes, TypesTrigger type, int value){
        setType(type);
        setSecondes(secondes);
        setValue(value);
    }

    public int getSecondes() {
        return secondes;
    }

    public void setSecondes(int secondes) {
        this.secondes = secondes;
    }

    public TypesTrigger getType() {
        return type;
    }

    public void setType(TypesTrigger type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString(){
        String typeTrigger = "";
        if(type.equals(TypesTrigger.SCREEN)){
            typeTrigger = "SENSOR";
        }else{
            typeTrigger = "SCREEN";
        }

        return "For "+secondes+" sec. the trigger is "+typeTrigger+" with value : "+value+" ";
    }
}
