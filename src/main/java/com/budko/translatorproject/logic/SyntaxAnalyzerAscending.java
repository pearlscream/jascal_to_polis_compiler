package com.budko.translatorproject.logic;


import com.budko.translatorproject.abstraction.DataSource;
import com.budko.translatorproject.entities.AscendingEntity;
import com.budko.translatorproject.entities.Lexeme;
import com.budko.translatorproject.utils.FileDataSource;

import java.io.FileNotFoundException;
import java.util.*;


public class SyntaxAnalyzerAscending {
    private LinkedHashMap<String, String[][]> grammarMap;
    private String[][] grammarTable;
    private Map<String, String> reversedGrammarMap;
    private List<AscendingEntity> ascendingEntities = new ArrayList<>();
    private Deque<Lexeme> stack = new LinkedList<>();
    private Deque<Lexeme> poliz = new LinkedList<>();

    public List<AscendingEntity> getAscendingEntities() {
        return ascendingEntities;
    }

    public SyntaxAnalyzerAscending(List<Lexeme> lexemes, String grammarPath) throws FileNotFoundException {
        Lexeme firstStackElement = new Lexeme(0, 100, "#");
        firstStackElement.setCommonLexemeName("#");
        stack.push(firstStackElement);


        GrammarTree tree = new GrammarTree(grammarPath);
        grammarTable = tree.getPrecedenceTable();
        grammarMap = tree.getGrammarMap();

        lexemes.add(new Lexeme(0, 99, "#"));
        initCommonLexemesNames(lexemes);
        createReversedGrammar();
        analyze(lexemes);
    }



    private void analyze(List<Lexeme> lexemes) {
        Lexeme stackLexeme;
        String sign;
        Lexeme currentLexeme;
        do {
            stackLexeme = stack.peek();
            currentLexeme = lexemes.get(0);

            sign = getSign(stackLexeme, currentLexeme);

            if (sign.equals("<") || sign.equals("=")) {
                ascendingEntities.add(new AscendingEntity(ascendingEntities.size(), getStackString(stack), sign, getInputString(lexemes), getPolizString()));
                if (currentLexeme.isConstant() || currentLexeme.isId()) {
                    poliz.push(currentLexeme);
                }
                stack.push(currentLexeme);
                lexemes.remove(0);
            } else if (sign.equals(">")) {
                ascendingEntities.add(new AscendingEntity(ascendingEntities.size(), getStackString(stack), sign, getInputString(lexemes), getPolizString()));
                findBase();
            } else if (!stackLexeme.getCommonLexemeName().equals("#") && !currentLexeme.getLexemeName().equals("#")) {
                throw new NoSuchElementException("Помилка при синтаксичному аналізі " + currentLexeme.getLineNumber() + ". Відношення між "
                        + stackLexeme.getLexemeName() + " та " + currentLexeme.getLexemeName() + "не знайдено");
            }
        }
        while (!(currentLexeme.getCommonLexemeName().equals("#") && stackLexeme.getCommonLexemeName().equals("#")));
    }

    private String getPolizString() {
        String polizString = "";
        for (Lexeme string : poliz) {
            polizString = string.getLexemeName() + " " + polizString;
        }
        return polizString;
    }

    private String getSign(Lexeme stackLexeme, Lexeme lexeme) {
        int firstRelation = getFirstElementRelation(stackLexeme.getCommonLexemeName(), grammarTable);
        int secondRelation = getSecondElementRelation(lexeme.getCommonLexemeName(), grammarTable);
        return grammarTable[firstRelation][secondRelation];
    }

    private String getStackString(Deque<Lexeme> stack) {
        String stackString = "";
        for (Lexeme string : stack) {
            stackString = string.getLexemeName() + " " + stackString;
        }
        return stackString;
    }

    private String getInputString(List<Lexeme> input) {
        String inputString = "";
        for (Lexeme string : input) {
            inputString += string.getLexemeName() + " ";
        }
        return inputString;
    }

    private void createReversedGrammar() {
        reversedGrammarMap = new HashMap<>();
        for (String key : grammarMap.keySet()) {
            for (String[] key1 : grammarMap.get(key)) {
                StringBuilder builder = new StringBuilder();
                for (String word : key1) {
                    builder.append(word);
                }
                reversedGrammarMap.put(builder.toString(), key);
            }
        }
    }

    private void findBase() {
        StringBuilder newLexeme = new StringBuilder();
        Deque<Lexeme> localStack = new LinkedList();
        Lexeme firstStackElement = stack.pop();

        while (getRelation(stack.peek(), firstStackElement).equals("=")) {
            localStack.push(firstStackElement);
            firstStackElement = stack.pop();
        }
        localStack.push(firstStackElement);
        int line = firstStackElement.getLineNumber();

        while (!localStack.isEmpty()) {
            Lexeme localStackPop = localStack.pop();
            newLexeme.append(localStackPop.getCommonLexemeName());
        }
        System.out.println(newLexeme.toString());
        if (reversedGrammarMap.containsKey(newLexeme.toString())) {
            addPolizOperation(newLexeme);
            Lexeme newValue = new Lexeme(0, 100, reversedGrammarMap.get(newLexeme.toString()));
            newValue.setCommonLexemeName(reversedGrammarMap.get(newLexeme.toString()));
            stack.push(newValue);
        } else if (!newLexeme.toString().equals("<вираз1>")) {
            throw new IllegalArgumentException("Помилка при синтаксичному аналізі " + line + ". Не можна перетворити ланцюжок " + newLexeme.toString());
        }
    }

    private void addPolizOperation(StringBuilder newLexeme) {
        switch (newLexeme.toString()) {
            case "<вираз>+<терм1>": poliz.push(new Lexeme(0, 26, "+"));break;
            case "<вираз>-<терм1>": poliz.push(new Lexeme(0, 26, "-"));break;
            case "<терм>*<множник>": poliz.push(new Lexeme(0, 26, "*"));break;
            case "<терм>/<множник>": poliz.push(new Lexeme(0, 26, "/"));break;
        }
    }

    private String getRelation(Lexeme elem1, Lexeme elem2) {
        return grammarTable[getFirstElementRelation(elem1.getCommonLexemeName(), grammarTable)][getSecondElementRelation(elem2.getCommonLexemeName(), grammarTable)];
    }

    private int getFirstElementRelation(String lexeme, String[][] table) {
        for (int i = 1; i < table.length; i++) {
            if (Objects.equals(table[i][0], lexeme)) {
                return i;
            }
        }
        return -1;
    }

    private int getSecondElementRelation(String lexeme, String[][] table) {
        for (int j = 1; j < table.length; j++) {
            if (Objects.equals(table[0][j], lexeme)) {
                return j;
            }
        }
        return -1;
    }

    private void initCommonLexemesNames(List<Lexeme> lexemes) {
        for (Lexeme lexeme : lexemes) {
            if (lexeme.isConstant()) {
                lexeme.setCommonLexemeName("const");
            } else if (lexeme.isId()) {
                lexeme.setCommonLexemeName("idn");
            } else lexeme.setCommonLexemeName(lexeme.getLexemeName());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        String grammarPath = "d:\\labs_6_semestr\\sapr\\2nd_lab\\TranslatorProject\\grammarTable.txt";
        DataSource dataSource = new FileDataSource("d:\\labs_6_semestr\\sapr\\my_1_lab\\TranslatorProject\\JascalExample.jas");
        String file = dataSource.getProgramText();
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(file);
        lexicalAnalyzer.analyze();
        new SyntaxAnalyzerAscending(lexicalAnalyzer.getOutputLexemeTable(), grammarPath);
    }
}
