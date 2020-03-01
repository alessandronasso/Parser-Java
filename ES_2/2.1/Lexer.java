//Esercizio 2.1
import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek=' ';
                return Token.rpt;

            case '+':
                peek=' ';
                return Token.plus;

            case '-':
                peek=' ';
                return Token.minus;

            case '*':
                peek=' ';
                return Token.mult;

            case '/':
                peek=' ';
                return Token.div;

            case ';':
                peek=' ';
                return Token.semicolon;
                
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"+ " after & : "  + peek );
                    return null;
                }

            case '=':
                readch(br);
                if (peek== '=') {
                    peek=' ';
                    return Word.eq;
                } else {
                    return Token.assign;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"+ " after | : "  + peek );
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek=='>') {
                    peek= ' ';
                    return Word.ne;
                } else {
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }

            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {
                    String lect=""+peek;
                    readch(br);
                    while (Character.isLetter(peek)) {
                        lect+=peek;
                        readch(br);
                    }

                    if (lect.equals("if"))
                        return Word.iftok;
                    else if (lect.equals("then"))
                        return Word.then;
                    else if (lect.equals("else"))
                        return Word.elsetok;
                    else if (lect.equals("for"))
                        return Word.fortok;
                    else if (lect.equals("do"))
                        return Word.dotok;
                    else if (lect.equals("print"))
                        return Word.print;
                    else if (lect.equals("read"))
                        return Word.read;
                    else if (lect.equals("begin"))
                        return Word.begin;
                    else if (lect.equals("end"))
                        return Word.end;
                    else 
                        return new Word(Tag.ID, lect);

                } else if (Character.isDigit(peek)) {
                    String numb=""+peek;
                    readch(br);
                    while (Character.isDigit(peek)) {
                        numb+=peek;
                        readch(br);
                    }
                    return new Number (Tag.NUM, numb);
                } else {
                        System.err.println("Erroneous character: " + peek );
                        return null;
                }
         }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }
}
