package com.budko.translatorproject.logic;

import com.budko.translatorproject.entities.Lexeme;
import com.budko.translatorproject.entities.PolizEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dBudko
 */
public class PolizGenerator {
    private List<Lexeme> lexemes;
    private Map<String, Integer> prioritetesTable;
    private List<PolizEntity> processList;

    public PolizGenerator(List<Lexeme> lexemes) {
        this.processList = new LinkedList<>();
        this.lexemes = lexemes;
        boolean isFullCode = lexemes.stream()
                .map(Lexeme::getLexemeName)
                .collect(Collectors.toList()).contains("begin");
        if (isFullCode) {
            boolean isDeclarationBlock = true;
            List<Lexeme> codeList = new ArrayList<>();
            for (Lexeme currentLexeme : lexemes) {
                if (!isDeclarationBlock && !currentLexeme.getLexemeName().equals("end")) {
                    codeList.add(currentLexeme);
                }
                if (currentLexeme.getLexemeName().equals("begin")) {
                    isDeclarationBlock = false;
                }
            }
            this.lexemes = codeList;
        }
    }

    {
        this.prioritetesTable = new HashMap<>();
        prioritetesTable.put("if",0);
        prioritetesTable.put("{",1);
        prioritetesTable.put("}",1);
        prioritetesTable.put("do",1);
        prioritetesTable.put("while",2);
        prioritetesTable.put("$",2);
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
        prioritetesTable.put("read", 10);
        prioritetesTable.put("write", 10);
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
                        Lexeme upl = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]УПЛ");
                        upl.setCommonLexemeName("label");
                        polizLexemes.add(upl);
                        Lexeme stackUPL = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]");
                        stackUPL.setCommonLexemeName("label");
                        stack.push(stackUPL);
                        labelCounter++;
                    }
                    if (lexeme.getLexemeName().equals("}") && stack.size() > 0 && stack.peek().getLexemeName().equals("if")) {
                        polizLexemes.add(new Lexeme(lexeme.getLineNumber(),":"));
                        stack.pop();
                    }
                    if (lexeme.getLexemeName().equals("do")) {
                        stack.push(new Lexeme(lexeme.getLineNumber(),"do"));
                        Lexeme upl = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]");
                        upl.setCommonLexemeName("label");
                        stack.push(upl);
                        Lexeme label = new Lexeme(lexeme.getLineNumber(),"m[" + labelCounter + "]");
                        label.setCommonLexemeName("label");
                        polizLexemes.add(label);
                        polizLexemes.add(new Lexeme(lexeme.getLineNumber(),":"));
                        labelCounter++;
                    }
                }
                if (!(lexeme.getLexemeName().equals(")") || lexeme.getLexemeName().equals("{") || lexeme.getLexemeName().equals("}") || lexeme.getLexemeName().equals(";") || lexeme.getLexemeName().equals("do") ||lexeme.getLexemeName().equals("while"))) {
                    stack.push(lexeme);
                }
                if (lexeme.getLexemeName().equals(";") && stack.size() > 0 && stack.peek().getCommonLexemeName() != null && stack.peek().getCommonLexemeName().equals("labelWhile")) {
                    Lexeme stackLexeme = stack.pop();
                    stackLexeme.setLexemeName(stackLexeme.getLexemeName() + "УП");
                    polizLexemes.add(stackLexeme);
                    stack.pop();
                }
                if (lexeme.getLexemeName().equals("while") && stack.size() > 0 && stack.peek().getCommonLexemeName().equals("label")) {
                    stack.peek().setCommonLexemeName("labelWhile");
                }
            }
            PolizEntity polizEntity = new PolizEntity();
            polizEntity.setInputLexeme(lexeme.getLexemeName());
            polizEntity.setStack(polizStringBuilder(stack));
            polizEntity.setPoliz(polizStringBuilder(polizLexemes));
            processList.add(polizEntity);
        }
        if (stack.size() > 0) {
            stack.pop();
        }
        return polizStringBuilder(polizLexemes);
    }

    private String polizStringBuilder(Collection<Lexeme> lexemes) {
        StringBuilder outputPoliz = new StringBuilder();
        for (Lexeme lexeme : lexemes) {
            outputPoliz.append(lexeme.getLexemeName());
            outputPoliz.append(" ");
        }
        return outputPoliz.toString();
    }

    private Integer getLimiterPriority(Lexeme lexeme) {
        Integer priority = prioritetesTable.get(lexeme.getLexemeName());
        if (lexeme.getCommonLexemeName() != null && (lexeme.getCommonLexemeName().equals("label") || lexeme.getCommonLexemeName().equals("labelWhile"))) {
            return 1;
        }
        if (priority == null) {
            throw new IllegalArgumentException("Нет такого ограничителя в таблице " + lexeme.getLexemeName());
        } else {
            return priority;
        }
    }

    public List<PolizEntity> getProcessList() {
        return processList;
    }
}
