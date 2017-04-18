package com.budko.translatorproject.logic;

import com.budko.translatorproject.entities.Lexeme;

import java.util.*;

/**
 * @author dBudko
 */
public class PolizGenerator {
    private List<Lexeme> lexemes;
    private Map<String, Integer> prioritetesTable;
    private List<String> labelTable;

    public PolizGenerator(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        this.labelTable = new LinkedList<>();
    }

    {
        this.prioritetesTable = new HashMap<>();
        prioritetesTable.put("if",0);
        prioritetesTable.put("{",1);
        prioritetesTable.put("}",1);
        prioritetesTable.put(";",2);
        prioritetesTable.put("(", 3);
        prioritetesTable.put(")", 4);
        prioritetesTable.put("=", 5);
        prioritetesTable.put("||", 6);
        prioritetesTable.put("&&", 7);
        prioritetesTable.put("!", 8);
        prioritetesTable.put("<", 9);
        prioritetesTable.put(">", 9);
        prioritetesTable.put("<=", 9);
        prioritetesTable.put(">=", 9);
        prioritetesTable.put("==", 9);
        prioritetesTable.put("!=", 9);
        prioritetesTable.put("+", 10);
        prioritetesTable.put("-", 10);
        prioritetesTable.put("*", 11);
        prioritetesTable.put("/", 11);
    }

    public String generatePoliz() {
        Deque<Lexeme> stack = new ArrayDeque<>();
        List<Lexeme> polizLexemes = new LinkedList<>();
        int labelCounter = 0;
        for (Lexeme lexeme : lexemes) {
            if (lexeme.isConstant() || lexeme.isId()) {
                polizLexemes.add(lexeme);
            } else {
                if (!(lexeme.getLexemeName().equals("(") || lexeme.getLexemeName().equals("if"))) {
                    Integer currentLexemePriority = getLimiterPriority(lexeme);
                    while (stack.size() > 0 && (getLimiterPriority(stack.peek()) >= currentLexemePriority)) {
                        Lexeme stackLexeme = stack.pop();
                        polizLexemes.add(stackLexeme);
                    }
                    if (lexeme.getLexemeName().equals(")") && stack.peek().getLexemeName().equals("(")) {
                        stack.pop();
                    }
                    if (lexeme.getLexemeName().equals("{") && stack.peek().getLexemeName().equals("if")) {
//                        stack.pop();
                        Lexeme upl = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]УПЛ");
                        upl.setCommonLexemeName("label");
                        polizLexemes.add(upl);
//                        labelTable.add("m[" + labelCounter + "]");
//                        polizLexemes.add(new Lexeme(lexeme.getLineNumber(),"УПЛ"));
                        Lexeme stackUPL = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]");
                        stackUPL.setCommonLexemeName("label");
                        stack.push(stackUPL);
                        labelCounter++;
                    }
                    if (lexeme.getLexemeName().equals("}") && stack.size() > 0 && stack.peek().getLexemeName().equals("if")) {
                        polizLexemes.add(new Lexeme(lexeme.getLineNumber(),":"));
                        stack.pop();
                    }
                }
                if (!(lexeme.getLexemeName().equals(")") || lexeme.getLexemeName().equals("{") || lexeme.getLexemeName().equals("}") || lexeme.getLexemeName().equals(";"))) {
                    stack.push(lexeme);
                }
            }
        }
//        while (stack.size() > 0 && !stack.peek().getLexemeName().equals(";")) {
//            polizLexemes.add(stack.pop());
//        }
        if (stack.size() > 0) {
            stack.pop();
        }
        StringBuilder outputPoliz = new StringBuilder();
        for (Lexeme lexeme : polizLexemes) {
            outputPoliz.append(lexeme.getLexemeName());
        }
        return outputPoliz.toString();
    }

    private Integer getLimiterPriority(Lexeme lexeme) {
        Integer priority = prioritetesTable.get(lexeme.getLexemeName());
        if (lexeme.getCommonLexemeName() != null && lexeme.getCommonLexemeName().equals("label")) {
            return 1;
        }
        if (priority == null) {
            throw new IllegalArgumentException("Нет такого ограничителя в таблице");
        } else {
            return priority;
        }
    }
}
