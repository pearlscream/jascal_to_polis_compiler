package com.budko.translatorproject.test;

import com.budko.translatorproject.entities.Constant;
import com.budko.translatorproject.entities.Identifier;
import com.budko.translatorproject.entities.Lexeme;
import com.budko.translatorproject.logic.LexicalAnalyzer;
import com.budko.translatorproject.logic.PolizGenerator;

/**
 * @author dBudko
 */
public class PolizMain {
    public static void main(String[] args) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("if a > b { a = 0; if c < d {e = 100; r = 200} }");
//        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("    step=0;\n" +
//                "    do\n" +
//                "        read(a);\n" +
//                "        result=(a/2.5E4)+20;\n" +
//                "        step=step-1;\n" +
//                "    while result<=1000 $\n" +
//                "    if step<1000 {\n" +
//                "        write(a);\n" +
//                "    };\n" +
//                "    write(a);\n");
        Lexeme.setLexemesToZero();
        Identifier.setIdentifiersToZero();
        Constant.setConstantsToZero();
        lexicalAnalyzer.analyze();
        PolizGenerator polizGenerator = new PolizGenerator(lexicalAnalyzer.getOutputLexemeTable());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }
}
