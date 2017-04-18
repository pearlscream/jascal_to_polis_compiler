package com.budko.translatorproject.entities;

/**
 * @author DBudko.
 */
public class Lexeme {
    private Integer lexemeNumber;
    private int lineNumber;
    private String lexemeName;
    private int lexemeCode;
    private Constant constant;
    private Identifier identifier;
    private String commonLexemeName;

    private static Integer count = 0;

    public Lexeme(int lineNumber, int lexemeCode,String lexemeName) {
        this.lexemeNumber = count;
        count++;
        this.lineNumber = lineNumber;
        this.lexemeName = lexemeName;
        this.lexemeCode = lexemeCode;
    }

    public Lexeme(int lineNumber, String lexemeName) {
        this.lexemeNumber = count;
        count++;
        this.lineNumber = lineNumber;
        this.lexemeName = lexemeName;
    }

    public static void setLexemesToZero() {
        count = 0;
    }


    public Lexeme(int lineNumber, String lexemeName, int lexemeCode, Constant constant) {
        this.lexemeNumber = count;
        count++;
        this.lineNumber = lineNumber;
        this.lexemeName = lexemeName;
        this.lexemeCode = lexemeCode;
        this.constant = constant;
    }

    public Lexeme(int lineNumber, String lexemeName, int lexemeCode, Identifier identifier) {
        this.lexemeNumber = count;
        count++;
        this.lineNumber = lineNumber;
        this.lexemeName = lexemeName;
        this.lexemeCode = lexemeCode;
        this.identifier = identifier;
    }


    public Integer getIdnCode() {
        if (identifier != null) {
            return identifier.getCode();
        } else {
            if (constant != null) {
                return constant.getCode();
            } else {
                return null;
            }
        }
    }
    public Integer getLexemeNumber() {
        return lexemeNumber;
    }

    public void setLexemeNumber(Integer lexemeNumber) {
        this.lexemeNumber = lexemeNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLexemeName() {
        return lexemeName;
    }

    public void setLexemeName(String lexemeName) {
        this.lexemeName = lexemeName;
    }

    public int getLexemeCode() {
        return lexemeCode;
    }

    public void setLexemeCode(int lexemeCode) {
        this.lexemeCode = lexemeCode;
    }

    public Constant getConstant() {
        return constant;
    }

    public void setConstant(Constant constant) {
        this.constant = constant;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public boolean isConstant() {
        return this.constant != null;
    }

    public boolean isId() {
        return this.identifier != null;
    }

    public String getCommonLexemeName() {
        return commonLexemeName;
    }

    public void setCommonLexemeName(String commonLexemeName) {
        this.commonLexemeName = commonLexemeName;
    }

    @Override
    public String toString() {
        return "Lexeme{" +
                "lineNumber=" + lineNumber +
                ", lexemeName='" + lexemeName + '\'' +
                ", lexemeCode=" + lexemeCode +
                ", constant=" + constant +
                ", identifier=" + identifier +
                '}';
    }
}
