package com.budko.translatorproject.entities;

/**
 * @author DBudko.
 */
public class Constant {
    private int code;
    private String value;
    private static int count = 0;
    public Constant(String value) {
        code = count;
        this.value = value;
        count++;
    }

    public static void setConstantsToZero() {
        count = 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        Constant constant = (Constant) o;
        if (constant.value.equals(this.value)) {
            return true;
        } else {
            return false;
        }
    }


    public static void subtractCount() {
        count--;
    }
    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + count;
        return result;
    }

    @Override
    public String toString() {
        return "Constant{" +
                "code=" + code +
                ", value='" + value + '\'' +
                '}';
    }
}
