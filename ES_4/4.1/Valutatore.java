//Esercizio 4.1
import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
	lex = l; 
	pbr = br;
	move(); 
    }
   
    void move() { 
	   look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) { 
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
           if (look.tag != Tag.EOF) move();
       } else error("syntax error");
    }

    public void start() { 
	    int expr_val;
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                System.out.println(expr_val);
                break;

            default:
                error("Stringa non accettata");
        }
    }

    private int expr() { 
	    int term_val, exprp_val=0;
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                break;

            default:
                error("Errore in EXPR"); 
        }
        return exprp_val;
    }

    private int exprp(int exprp_i) {
	    int term_val, exprp_val=0;
	    switch (look.tag) {
	        case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;

            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;

            case Tag.EOF:
            case ')':
                exprp_val=exprp_i;
                break;

            default:
                error("Errore in EXPRP"); 
	    }
        return exprp_val;
    }

    private int term() { 
	   int fact_val, termp_val=0;
       switch (look.tag) {
            case '(':
            case Tag.NUM:
                fact_val=fact();
                termp_val=termp(fact_val);
                break;

            default:
                error("Errore in TERM"); 
            }
        return termp_val;
    }
    
    private int termp(int termp_i) { 
	   int fact_val, termp_val=0;
       switch (look.tag) {
            case '*':
                match('*');
                fact_val=fact();
                termp_val=termp(termp_i*fact_val);
                break;

            case '/':
                match('/');
                fact_val=fact();
                termp_val=termp(termp_i/fact_val);
                break;

            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                termp_val=termp_i;
                break;

            default:
                error("Errore in TERMP"); 
        }
        return termp_val;
    }
    
    private int fact() { 
	   int expr_val=0;
       switch (look.tag) {
            case '(': 
                match('(');
                expr_val=expr();
                match(')');
                break;

            case Tag.NUM:
                expr_val=((Number)look).lexeme;
                match(Tag.NUM);
                break;

            default:
                error("Errore in FACT"); 
        }
        return expr_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}