package com.budko.translatorproject.entities;


public class AscendingEntity {
    private Integer step;
    private String stack;
    private String sign;
    private String input;
    private String poliz;

    public AscendingEntity(Integer step, String stack, String sign, String input, String poliz) {
        this.step = step;
        this.stack = stack;
        this.sign = sign;
        this.input = input;
        this.poliz = poliz;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getPoliz() {
        return poliz;
    }

    public void setPoliz(String poliz) {
        this.poliz = poliz;
    }

    @Override
    public String toString() {
        return "AscendingEntity{" +
                "step=" + step +
                ", stack='" + stack + '\'' +
                ", sign='" + sign + '\'' +
                ", input='" + input + '\'' +
                '}';
    }
}
