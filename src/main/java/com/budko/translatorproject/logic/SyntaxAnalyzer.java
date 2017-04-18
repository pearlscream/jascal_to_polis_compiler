package com.budko.translatorproject.logic;

import com.budko.translatorproject.entities.Error;
import com.budko.translatorproject.entities.Lexeme;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DBudko.
 */
public class SyntaxAnalyzer {
    private List<Lexeme> lexemes;
    private List<Error> errors;
    private Lexeme currentLexeme;
    private int lexemeNumber;
    private boolean outOfRange = false;

    public SyntaxAnalyzer(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        this.errors = new ArrayList<>();
        currentLexeme = lexemes.get(0);
    }

    public List<Error> getErrors() {
        return errors;
    }

    public boolean analyze() {
        program();
        return true;
    }

    private boolean nextLexeme() {
        lexemeNumber++;
        if (lexemeNumber < lexemes.size()) {
            currentLexeme = lexemes.get(lexemeNumber);
            return true;
        } else {
            currentLexeme = lexemes.get(lexemes.size() - 1);
            outOfRange = true;
            return false;
        }
    }

    private boolean program() {
        if (!outOfRange && currentLexeme.getLexemeCode() == 0) { // program
            nextLexeme();
            if (!outOfRange && currentLexeme.getLexemeCode() == 31) { //IDN
                nextLexeme();
                if (!outOfRange && currentLexeme.getLexemeCode() == 21) nextLexeme(); // ;
                if (declarationList()) {
                    if (!outOfRange && currentLexeme.getLexemeCode() == 2) { // BEGIN
                        nextLexeme();
                        if (operatorList()) { // end lexeme - end
                            nextLexeme();
                            if (outOfRange && currentLexeme.getLexemeCode() == 3) { // END
                                System.out.println("Помилок немає");
                                return true;
                            } else {
                                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": відсутній закриваючий оператор END program()");
                                errors.add(new Error(currentLexeme.getLineNumber(), "відсутній закриваючий оператор END program()"));
                                return false;
                            }
                        } else {
                            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": невірний список операторів program()");
                            errors.add(new Error(currentLexeme.getLineNumber(), "невірний список операторів program()"));
                            return false;
                        }
                    } else {
                        System.out.println("Рядок " + currentLexeme.getLineNumber() + ": відсутній оператор BEGIN program()");
                        errors.add(new Error(currentLexeme.getLineNumber(), ": відсутній оператор BEGIN program()"));
                        return false;
                    }
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": невірний список оголошення program()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "невірний список оголошення program()"));
                    return false;
                }

            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": немає назви програми program()");
                errors.add(new Error(currentLexeme.getLineNumber(), "немає назви програми program()"));
                return false;
            }
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": програма має починатись зі слова program program()");
            errors.add(new Error(currentLexeme.getLineNumber(), "програма має починатись зі слова program program()"));
            return false;
        }
    }

    private boolean variableType() {
        if (!outOfRange && (currentLexeme.getLexemeCode() == 4 || currentLexeme.getLexemeCode() == 5)) {
            nextLexeme();
            return true;
        } else {
            if (currentLexeme.getLexemeCode() == 31) {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": тип може бути int або double variableType()");
                errors.add(new Error(currentLexeme.getLineNumber(), "тип може бути int або double variableType()"));
            }
            return false;
        }
    }

    private boolean idList() {
        if (currentLexeme.getLexemeCode() == 31) { // IDN
            nextLexeme();
            while (currentLexeme.getLexemeCode() == 22) { // ,
                nextLexeme();
                if (currentLexeme.getLexemeCode() == 31) { // IDN
                    nextLexeme();
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": не ідентифікатор idList()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "не ідентифікатор idList()"));
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути список ідентифікаторів idList()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути список ідентифікаторів idList()"));
            return false;
        }
    }


    private boolean declarationList() {
        if (declaration()) {
            while (!outOfRange && currentLexeme.getLexemeCode() == 21) { // ;
                nextLexeme();
                if (declaration()) {

                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": помилка в оголошенні declarationList()");
                    errors.add(new Error(currentLexeme.getLineNumber(), ": помилка в оголошенні declarationList()"));
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": помилка в оголошенні declarationList()");
            errors.add(new Error(currentLexeme.getLineNumber(), ": помилка в оголошенні declarationList()"));
            return false;
        }
    }

    private boolean declaration() {
        if (variableType()) {
            if (idList()) {
                return true;
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": оголосити список ід declaration()");
                errors.add(new Error(currentLexeme.getLineNumber(), "оголосити список ід declaration()"));
                return false;
            }
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": відсутній тип declaration()");
            errors.add(new Error(currentLexeme.getLineNumber(), ": відсутній тип declaration()"));
            return false;
        }
    }

    private boolean operatorList() {
        if (operator()) { // IDN
            while (currentLexeme.getLexemeCode() == 21) { // ;
                nextLexeme();
                if (!operator()) {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": не оператор operatorList()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "не оператор operatorList()"));
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": не оператор operatorList()");
            errors.add(new Error(currentLexeme.getLineNumber(), "не оператор operatorList()"));
            return false;
        }
    }

    private boolean operator() {
        //      ----------- ПРИСВОЄННЯ ---------------------------------
        if (!outOfRange && currentLexeme.getLexemeCode() == 31) { // IDN
            nextLexeme();
            if (!outOfRange && currentLexeme.getLexemeCode() == 23) { // =
                nextLexeme();
                if (expression()) {
                    return true;
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути вираз operator()");
                    errors.add(new Error(currentLexeme.getLineNumber(), ": має бути вираз operator()"));
                    return false;
                }
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": оператор присвоєння(має бути = ) operator()");
                errors.add(new Error(currentLexeme.getLineNumber(), ": оператор присвоєння(має бути = ) operator()"));
                return false;
            }
            //    ------------------   READ WRITE --------------
        } else if (currentLexeme.getLexemeCode() == 6 || currentLexeme.getLexemeCode() == 7) { // Read Write
            nextLexeme();
            if (!outOfRange && currentLexeme.getLexemeCode() == 19) { // (
                nextLexeme();
                idList();
                if (!outOfRange && currentLexeme.getLexemeCode() == 20) { // )
                    nextLexeme();
                    return true;
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ":в операторі Read Write(має бути закриваюча дужка) operator()");
                    errors.add(new Error(currentLexeme.getLineNumber(), ":в операторі Read Write(має бути закриваюча дужка) operator()"));
                    return false;
                }
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ":в операторі Read Write(має бути відкриваюча дужка) operator()");
                errors.add(new Error(currentLexeme.getLineNumber(), ":в операторі Read Write(має бути відкриваюча дужка) operator()"));
                return false;
            }
            //     -------------- DO WHILE -----------------------------------------
        } else if (!outOfRange && currentLexeme.getLexemeCode() == 8) { //do
            nextLexeme();
            if (operatorList()) {// end operator - while
                nextLexeme();
                if (logicalExpression()) { //IDN
                    return true;
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": після while має бути логічний вираз operator()");
                    errors.add(new Error(currentLexeme.getLineNumber(), ": після while має бути логічний вираз operator()"));
                    return false;
                }

            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": помилка в списку операторів циклу do while operator()");
                errors.add(new Error(currentLexeme.getLineNumber(), ": помилка в списку операторів циклу do while operator()"));
                return false;
            }
            //        -------------------- УМОВНИЙ IF -----------------------------------
        } else if (!outOfRange && currentLexeme.getLexemeCode() == 10) { //if
            nextLexeme();
            if (logicalExpression()) {
                if (!outOfRange && currentLexeme.getLexemeCode() == 11) { //{
                    nextLexeme();
                    if (operatorList()) { // end operator - }
                        if (!outOfRange && currentLexeme.getLexemeCode() == 12) { // }
                            nextLexeme();
                            return true;
                        } else {
                            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": умовний оператор повинен закінчуватися на } operator()");
                            errors.add(new Error(currentLexeme.getLineNumber(), ": умовний оператор повинен закінчуватся на } operator()"));
                            return false;
                        }
                    } else {
                        System.out.println("Рядок " + currentLexeme.getLineNumber() + ": помилка списку операторів в if operator()");
                        errors.add(new Error(currentLexeme.getLineNumber(), ":  помилка списку операторів в if operator()"));
                        return false;
                    }
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": умовний оператор повинен починатися на { operator()");
                    errors.add(new Error(currentLexeme.getLineNumber(), ": умовний оператор повинен починатися на { operator()"));
                    return false;
                }
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": помилка логічного виразу в операторі if operator()");
                errors.add(new Error(currentLexeme.getLineNumber(), ": помилка логічного виразу в операторі if operator()"));
                return false;
            }
        }
        System.out.println("Рядок " + currentLexeme.getLineNumber() + ": невідомий оператор operator()");
        errors.add(new Error(currentLexeme.getLineNumber(), ": невідомий оператор operator()"));
        return false;
    }

    private boolean logicalExpression() {
        if (logicalTerminal()) {
            while (!outOfRange && currentLexeme.getLexemeCode() == 14) { // ||
                nextLexeme();
                logicalTerminal();
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути логічний термінал logicalExpression()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути логічний термінал logicalExpression()"));
            return false;
        }
    }

    private boolean logicalTerminal() {
        if (logicalMultiplier()) {
            while (!outOfRange && currentLexeme.getLexemeCode() == 13) { // &&
                nextLexeme();
                logicalTerminal();
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути логічний множник logicalTerminal()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути логічний множник logicalTerminal()"));
            return false;
        }
    }

    private boolean logicalMultiplier() {
        if (!outOfRange && currentLexeme.getLexemeCode() == 19) { // (
            if (logicalExpression()) {
                if (!outOfRange && currentLexeme.getLexemeCode() == 20) { // )
                    nextLexeme();
                    return true;
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути закриваюча дужка logicalMultiplier()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "має бути закриваюча дужка logicalMultiplier()"));
                    return false;
                }
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути логічний вираз logicalMultiplier()");
                errors.add(new Error(currentLexeme.getLineNumber(), ": має бути логічний вираз logicalMultiplier()"));
                return false;
            }
        } else if (!outOfRange && currentLexeme.getLexemeCode() == 30) { // !
            if (logicalMultiplier()) {
                return true;
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути логічний множник після ! logicalMultiplier()");
                errors.add(new Error(currentLexeme.getLineNumber(), "має бути логічний множник після ! logicalMultiplier()"));
                return false;
            }
        } else if (relation()) {
            return true;
        }
        System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути логічний множник logicalMultiplier()");
        errors.add(new Error(currentLexeme.getLineNumber(), "має бути логічний множник logicalMultiplier()"));
        return false;
    }

    private boolean relation() {
        if (expression()) {
            if (tokenRelation()) {
                if (expression()) {
                    return true;
                } else {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути останній вираз relation()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "має бути останній вираз relation()"));
                    return false;
                }
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути знак відношення relation()");
                errors.add(new Error(currentLexeme.getLineNumber(), "має бути знак відношення relation()"));
                return false;
            }
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути перший вираз relation()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути перший вираз relation()"));
            return false;
        }
    }


    private boolean tokenRelation() { //знак відношення
        if (!outOfRange && currentLexeme.getLexemeCode() == 24 || currentLexeme.getLexemeCode() == 25 || currentLexeme.getLexemeCode() == 26
                || currentLexeme.getLexemeCode() == 27 || currentLexeme.getLexemeCode() == 28 || currentLexeme.getLexemeCode() == 29) {
            nextLexeme();
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути знак відношення tokenRelation()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути знак відношення tokenRelation()"));
            return false;
        }
    }

    private boolean expression() {
        if (!outOfRange && currentLexeme.getLexemeCode() == 16) { // -
            nextLexeme();
        }
        if (terminal()) {
            while (!outOfRange && (currentLexeme.getLexemeCode() == 15 || currentLexeme.getLexemeCode() == 16)) { // + -
                nextLexeme();
                if (!terminal()) {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути термінал expression()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "має бути термінал expression()"));
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути термінал expression()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути термінал expression()"));
            return false;
        }
    }

    private boolean terminal() {
        if (multiplier()) {
            while (!outOfRange && currentLexeme.getLexemeCode() == 17 || currentLexeme.getLexemeCode() == 18) { // * /
                nextLexeme();
                if (!multiplier()) {
                    System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути множник terminal()");
                    errors.add(new Error(currentLexeme.getLineNumber(), "має бути множник terminal()"));
                    return false;
                }
            }
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути множник terminal()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути множник terminal()"));
            return false;
        }
    }

    private boolean multiplier() {
        if (!outOfRange && currentLexeme.getLexemeCode() == 19) { // (
            nextLexeme();
            if (!expression()) {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути вираз multiplier()");
                errors.add(new Error(currentLexeme.getLineNumber(), ": має бути вираз multiplier()"));
                return false;
            }
            if (!outOfRange && currentLexeme.getLexemeCode() == 20) { // )
                nextLexeme();
                return true;
            } else {
                System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути закриваюча дужка multiplier()");
                errors.add(new Error(currentLexeme.getLineNumber(), "має бути закриваюча дужка multiplier()"));
                return false;
            }
        } else if (currentLexeme.getLexemeCode() == 31 || currentLexeme.getLexemeCode() == 32) { //IDN CON
            nextLexeme();
            return true;
        } else {
            System.out.println("Рядок " + currentLexeme.getLineNumber() + ": має бути IDN або CON або відкриваюча дужка для виразу multiplier()");
            errors.add(new Error(currentLexeme.getLineNumber(), "має бути IDN або CON multiplier()"));
            return false;
        }
    }
}