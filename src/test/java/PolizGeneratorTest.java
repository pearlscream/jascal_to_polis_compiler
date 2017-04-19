import com.budko.translatorproject.entities.Constant;
import com.budko.translatorproject.entities.Identifier;
import com.budko.translatorproject.entities.Lexeme;
import com.budko.translatorproject.logic.LexicalAnalyzer;
import com.budko.translatorproject.logic.PolizGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * @author dBudko
 */
public class PolizGeneratorTest {
    @Test
    public void firstExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a + b * c / d;"));
        assertEquals("abc*d/+",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void secondExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a + b * c + (d / e) / f;"));
        assertEquals("abc*+de/f/+",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void thirdExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a - 3 > c * d / e;"));
        assertEquals("a3-cd*e/>",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void fourthExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("! a + b > 3 && c == 0 || (a - q) == 1;"));
        assertEquals("ab+3>!c0==&&aq-1==||",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void fifthExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a > b || c == d && a + 2 * b > c - 1;"));
        assertEquals("ab>cd==a2b*+c1->&&||",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void assignmentExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a = c - b + 20;"));
        assertEquals("acb-20+=",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void firstConditionalExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if a > b {a = 0;}"));
        assertEquals("ab>m[0]УПЛa0=m[0]:",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void secondConditionalExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if a > b { a = 0; if c < d {e = 100; r = 200} }"));
        assertEquals("ab>m[0]УПЛa0=cd<m[1]УПЛe100=r200=m[1]:m[0]:",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void firstLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("do write(a); while c > d ;"));
        assertEquals("m[0]:awritecd>m[0]УПЛ",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }


    @Test
    public void secondLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("do if e > m {h = 100;} while c > d ;"));
        assertEquals("m[0]:em>m[1]УПЛh100=m[1]:cd>m[0]УПЛ",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void thirdLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if e > m { do h = 100; while c > d ;}"));
        assertEquals("em>m[0]УПЛm[0]m[1]:h100=cd>m[1]УПЛ:",polizGenerator.generatePoliz());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    private List<Lexeme> getLexemes(String expression) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(expression);
        Lexeme.setLexemesToZero();
        Identifier.setIdentifiersToZero();
        Constant.setConstantsToZero();
        lexicalAnalyzer.analyze();
        return lexicalAnalyzer.getOutputLexemeTable();
    }
}
