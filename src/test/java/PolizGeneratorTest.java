import com.budko.translatorproject.entities.Constant;
import com.budko.translatorproject.entities.Identifier;
import com.budko.translatorproject.entities.Lexeme;
import com.budko.translatorproject.entities.PolizEntity;
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
        assertEquals("a b c * d / + ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void secondExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a + b * c + (d / e) / f;"));
        assertEquals("a b c * + d e / f / + ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void thirdExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a - 3 > c * d / e;"));
        assertEquals("a 3 - c d * e / > ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void fourthExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("! a + b > 3 && c == 0 || (a - q) == 1;"));
        assertEquals("a b + 3 > ! c 0 == && a q - 1 == || ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void fifthExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a > b || c == d && a + 2 * b > c - 1;"));
        assertEquals("a b > c d == a 2 b * + c 1 - > && || ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void assignmentExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("a = c - b + 20;"));
        assertEquals("a c b - 20 + = ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void firstConditionalExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if a > b {a = 0;}"));
        assertEquals("a b > m[0]УПЛ a 0 = m[0] : ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void secondConditionalExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if a > b { a = 0; if c < d {e = 100; r = 200} }"));
        assertEquals("a b > m[0]УПЛ a 0 = c d < m[1]УПЛ e 100 = r 200 = m[1] : m[0] : ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void firstLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("do write(a); while c > d ;"));
        assertEquals("m[0] : a write c d > m[0]УП " ,polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }


    @Test
    public void secondLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("do if e > m {h = 100;} while c > d ;"));
        assertEquals("m[0] : e m > m[1]УПЛ h 100 = m[1] : c d > m[0]УП ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
        System.out.println("Your poliz: " + polizGenerator.generatePoliz());
    }

    @Test
    public void thirdLoopExpression() {
        PolizGenerator polizGenerator = new PolizGenerator(getLexemes("if e > m { do h = 100; while c > d ;}"));
        assertEquals("e m > m[0]УПЛ m[0] m[1] : h 100 = c d > m[1]УП : ",polizGenerator.generatePoliz());
        printProcessDynamic(polizGenerator.getProcessList());
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

    private void printProcessDynamic(List<PolizEntity> polizEntities) {
        for (PolizEntity polizEntity : polizEntities) {
            System.out.println("Input Lexeme " + polizEntity.getInputLexeme());
            System.out.println("Stack " + polizEntity.getStack());
            System.out.println("Poliz " + polizEntity.getPoliz());
        }
    }
}
