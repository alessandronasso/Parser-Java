import java.io.*;

public class ExpressionTranslator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    CodeGenerator code = new CodeGenerator();

    public ExpressionTranslator(Lexer l, BufferedReader br) {
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

    public void prog() {        
        match(Tag.PRINT);
        match('(');
        expr();
        code.emit(OpCode.invokestatic,1);
        match(')');
        match(Tag.EOF);
        try {
            code.toJasmin();
        }
        catch(java.io.IOException e) {
            System.out.println("IO error\n");
        };
    }

    private void expr() {
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                term();
                exprp();
                break;

            default:
                error("Errore in EXPR");
        }
    }

    private void exprp() {
        switch(look.tag) {
            case '+':
                match('+');
                term();
                code.emit(OpCode.iadd);
                exprp();
                break;
	       
            case '-':
                match('-');
                term();
                code.emit(OpCode.isub);
                exprp();
                break;

            case ')':
                break;

            default:
                error("Errore in EXPRP");
        }
    }

    private void term() {
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;

            default:
                error("Errore in TERM");
        }
    }

    private void termp() {
        switch (look.tag) {
            case '*':
                match('*');
                fact();
                code.emit(OpCode.imul);
                termp();
                break;

            case '/':
                match('/');
                fact();
                code.emit(OpCode.idiv);
                termp();
                break;

            case '+':
            case '-':
            case ')':
                break;
                
            default:
                error("Errore in TERMP");
        }
    }

    private void fact () {
        switch(look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;

            case Tag.NUM:
                code.emit(OpCode.ldc,((Number)look).lexeme);
                match(Tag.NUM);
                break;

            default:
                error("Errore in FACT");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.pas"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ExpressionTranslator translator = new ExpressionTranslator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            System.out.println("Digita 'java -jar jasmin.jar Output.j' per il file Output.class e 'java Output' per eseguirlo.\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}