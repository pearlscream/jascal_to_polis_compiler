package com.budko.translatorproject.entities;

/**
 * @author DBudko.
 */
public class Error {
    private int lineNumber;
    private String text;

    public Error(int lineNumber, String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Error{" +
                "lineNumber=" + lineNumber +
                ", text='" + text + '\'' +
                '}';
    }
}
