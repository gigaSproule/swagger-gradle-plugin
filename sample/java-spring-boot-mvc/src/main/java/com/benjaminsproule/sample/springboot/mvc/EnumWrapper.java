package com.benjaminsproule.sample.springboot.mvc;

public class EnumWrapper<T extends Enum<T>> {

    private String name;

    private T enumeration;

    public EnumWrapper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(T enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public String toString() {
        return "EnumWrapper{" +
            "name='" + name + '\'' +
            ", enumeration=" + enumeration +
            '}';
    }
}
