package com.budko.translatorproject.logic;

import com.budko.translatorproject.entities.Constant;
import com.budko.translatorproject.entities.Identifier;
import com.budko.translatorproject.entities.Lexeme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author DBudko.
 */
public class LexicalAnalyzer {
    private List<Lexeme> outputLexemeTable;
    private List<Identifier> identifiers;
    private List<Constant> constants;
    private final List<String> inputLexemTable;
    private final List<String> delimitersTable;
    private String programText;

    public LexicalAnalyzer(String programText) {
        inputLexemTable = new ArrayList<>();
        delimitersTable = new ArrayList<>();
        outputLexemeTable = new ArrayList<>();
        constants = new ArrayList<>();
        identifiers = new ArrayList<>();
        createLexemeDataBase();
        this.programText = programText;
    }

    public void analyze() {
        String delimiters = "\\-\\n\\s;/(/)/+/*/{}/</>/=,";
        String regularExpression = "(?=[" + delimiters + "])|(?<=[" + delimiters + "])";
        String[] result = programText.split(regularExpression);
        result = parseDoubleDelimiters(result);
        outputLexemeTable = parseLexemsToTable(result);
    }

    private String[] parseDoubleDelimiters(String[] lexemesArray) {
        List<String> lexemesList = new ArrayList<>();
        int i = 0;
        for (String lexeme : lexemesArray) {
            if (lexeme.equals("=")) {
                if (i != 0 & isLogicalOperator(lexemesList.get(i - 1))) {
                    lexemesList.set(i - 1, lexemesList.get(i - 1) + lexeme);
                } else {
                    lexemesList.add(lexeme);
                    i++;
                }
            } else {
                    lexemesList.add(lexeme);
                    i++;
            }
        }
        return lexemesList.stream().toArray(String[]::new);
    }

    private boolean isLogicalOperator(String element) {
        List<String> logicalOperators = Arrays.asList(">", "<", "=", "!");
        return logicalOperators.contains(element);
    }

    public void printOutputLexemeTable() {
        System.out.println("|№ |№ рядка|    Лексема    |Код лексеми|Код іdn/con|");
        String adding = "";
        for (Lexeme lexeme : outputLexemeTable) {
            if (lexeme.getConstant() != null) {
                adding = String.valueOf(lexeme.getConstant().getCode());
            } else {
                if (lexeme.getIdentifier() != null) {
                    adding = String.valueOf(lexeme.getIdentifier().getCode());
                } else {
                    adding = "";
                }
            }
            System.out.format("|%2d|%8d|%15s|%11d|%11s|\n", lexeme.getLexemeNumber(), lexeme.getLineNumber(), lexeme.getLexemeName(), lexeme.getLexemeCode(), adding);
        }
    }

    private List<Lexeme> parseLexemsToTable(String[] lexemes) {
        int lineNumber = 1;
        Lexeme lexeme;
        List<Lexeme> resultList = new ArrayList<>();
        String previousLexeme = null;
        for (String str : lexemes) {
            if (!str.equals(" ") && !str.equals("\n")) {
                int lexemeNumber = inputLexemTable.indexOf(str);
                if (inputLexemTable.contains(str)) {
                    lexeme = new Lexeme(lineNumber, lexemeNumber,str);
                } else {
                    if (isConstant(str)) {
                        Constant constant = new Constant(str);
                        if (constants.contains(constant)) {
                            constant = constants.get(constants.indexOf(constant));
                            Constant.subtractCount();
                        } else {
                            constants.add(constant);
                        }
                        lexeme = new Lexeme(lineNumber, str, inputLexemTable.indexOf("const"), constant);
                    } else {
                        if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
                            throw new IllegalArgumentException("Помилкова лексема у строці " + lineNumber + ": " + str);
                        } else {
                            String type = null;
                            if (previousLexeme != null && (previousLexeme.equals("int") || previousLexeme.equals("double") || previousLexeme.equals("program") || previousLexeme.equals(","))) {
                                if (previousLexeme.equals("int") || previousLexeme.equals("double") || previousLexeme.equals("program")) {
                                    for (Identifier elem : identifiers) {
                                        if (elem.getName().equals(str)) {
//                                            throw new IllegalArgumentException("Дублювання оголошення у строці");
                                        }
                                    }
                                    type = previousLexeme;
                                }
                                if (previousLexeme.equals(",")) {
                                    for (Identifier elem : identifiers) {
                                        if (elem.getName().equals(str)) {
//                                            throw new IllegalArgumentException("Дублювання оголошення у строці");
                                        }
                                    }
                                    type = identifiers.get(identifiers.size() - 1).getType();
                                }
                            } else {
                                boolean flag = false;
                                for(Identifier elem : identifiers) {
                                    if (elem.getName().equals(str)) {
                                        flag = true;
                                    }
                                }
//                                if (!flag) {
//                                    throw new IllegalArgumentException("Необ'явлений ідентифікатор");
//                                }
                            }
                            Identifier identifier = new Identifier(str, type);
                            if (identifiers.contains(identifier)) {
                                identifier = identifiers.get(identifiers.indexOf(identifier));
                                Identifier.subtractCount();
                            } else {
                                identifiers.add(identifier);
                            }
                            lexeme = new Lexeme(lineNumber, str, inputLexemTable.indexOf("idn"), identifier);
                        }
                    }

                }
                resultList.add(lexeme);
                System.out.println(str);
                previousLexeme = str;
            }
            if (str.equals("\n")) {
                lineNumber++;
            }

        }
        return resultList;
    }

    private boolean isConstant(String lexeme) {
        if (lexeme.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
            return true;
        }
        return false;
    }

    private void createLexemeDataBase() {
        inputLexemTable.add("program");
        inputLexemTable.add("var");
        inputLexemTable.add("begin");
        inputLexemTable.add("end");
        inputLexemTable.add("int");
        inputLexemTable.add("double");
        inputLexemTable.add("read");
        inputLexemTable.add("write");
        inputLexemTable.add("do");
        inputLexemTable.add("while");
        inputLexemTable.add("if");
        inputLexemTable.add("{");
        inputLexemTable.add("}");
        inputLexemTable.add("&&");
        inputLexemTable.add("||");
        inputLexemTable.add("+");
        inputLexemTable.add("-");
        inputLexemTable.add("*");
        inputLexemTable.add("/");
        inputLexemTable.add("(");
        inputLexemTable.add(")");
        inputLexemTable.add(";");
        inputLexemTable.add(",");
        inputLexemTable.add("=");
        inputLexemTable.add(">");
        inputLexemTable.add("<");
        inputLexemTable.add(">=");
        inputLexemTable.add("<=");
        inputLexemTable.add("==");
        inputLexemTable.add("!=");
        inputLexemTable.add("!");
        inputLexemTable.add("idn");
        inputLexemTable.add("const");
        inputLexemTable.add("$");
        inputLexemTable.add("[");
        inputLexemTable.add("]");
    }

    public List<Lexeme> getOutputLexemeTable() {
        return outputLexemeTable;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public List<Constant> getConstants() {
        return constants;
    }

    public List<String> getInputLexemTable() {
        return inputLexemTable;
    }

    private String getDelimitersSimpleString() {
        StringBuilder simpleString = new StringBuilder();
        for (String delimiter : delimitersTable) {
            simpleString.append(delimiter);
        }
        return simpleString.toString();
    }
}


