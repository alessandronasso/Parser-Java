//Esercizio 3.1
import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
        //move();
        expr();
        match(Tag.EOF);
        if(look.tag == Tag.EOF) System.out.println("Stringa accettata");
        else error("Stringa non accettata");
        match(Tag.EOF);
    }

    private void expr() { 
	   switch (look.tag) {
        case '(':
            //move();
            term(); 
            exprp();
            break;

        case Tag.NUM:
            //move();
            term(); 
            exprp();
            break;

        default:
            error("Stringa non accettata EXPR");
       }
    }

    private void exprp() { 
	   switch (look.tag) {
	       case '+':
	            move();
                term(); 
                exprp();
                break;

            case '-':
                move();
                term();
                exprp();
                break;

            default:
                if (look.tag != Tag.EOF && look.tag != ')') 
                    error("Stringa non accettata EXPRP");
	   }
    }

    private void term() { 
        switch (look.tag) {
            case '(':
                //move();
                fact(); 
                termp();
                break;

            case Tag.NUM:
                //move();
                fact(); 
                termp();
                break;

            default:
                error("Stringa non accettata TERM");
            }
        }

    private void termp() { 
        switch (look.tag) {
           case '*':
                move();
                fact(); 
                termp();
                break;

            case '/':
                move();
                fact(); 
                termp();
                break;

            default:
                if (look.tag!='+' && look.tag!='-' && look.tag!= ')' && look.tag!=Tag.EOF)
                    error("Stringa non accettata TERMP"); 
        }
    }

    private void fact() { 
        switch (look.tag) {
            case '(': 
                move();
                expr();
                match(')');
                break;

            case Tag.NUM:
                move();
                break;

            default:
                error("Stringa non accettata FACT");
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}