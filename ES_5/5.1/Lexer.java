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

    private char peekch(BufferedReader br) {
        char c = (char) -1;
        try {
            br.mark(1);
            c = (char) br.read();
            br.reset();
        } catch (IOException exc) {
            c = (char) -1; // ERROR
        }
        return c;
    }

    private void skipComment(BufferedReader br, boolean multiline) {
        if (multiline) {
            if (peek == '/' && peekch(br) == '*') {
                readch(br);
                readch(br);
            }

            while (!(peek == '*' && peekch(br) == '/') && peek != (char) -1) {
                if (peek == '\n')
                    line++;
                readch(br);
            }

            if (peek == (char) -1) {
                // Error
                throw new RuntimeException("Commento non chiuso!!");
            }

            readch(br);
            readch(br);
            skipSpaces(br);

        } else {
            while (peek != '\n' && peek != (char) -1) {
                readch(br);
            }
            skipSpaces(br);
        }
    }

    private void skipSpaces(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n')
                line++;
            readch(br);
        }
    }

    public Token lexical_scan(BufferedReader br) {

        skipSpaces(br);

        while (peek == '/' && (peekch(br) == '*' || peekch(br) == '/')) {
            if (peekch(br) == '*')
                skipComment(br, true);
            else if (peekch(br) == '/')
                skipComment(br, false);
        }

        switch (peek) {
        case '!':
            peek = ' ';
            return Token.not;

        // ... gestire i casi di (, ), +, -, *, /, ; ... //
        case '(':
            peek = ' ';
            return Token.lpt;

        case ')':
            peek = ' ';
            return Token.rpt;

        case '+':
            peek = ' ';
            return Token.plus;

        case '-':
            peek = ' ';
            return Token.minus;

        case '*':
            peek = ' ';
            return Token.mult;

        case '/':
            peek = ' ';
            return Token.div;

        case ';':
            peek = ' ';
            return Token.semicolon;

        case '&':
            readch(br);
            if (peek == '&') {
                peek = ' ';
                return Word.and;
            } else {
                System.err.println("Erroneous character" + " after & : " + peek);
                return null;
            }

            // ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //

        case '|':
            readch(br);
            if (peek == '|') {
                peek = ' ';
                return Word.or;
            } else {
                System.err.println("Erroneous character" + " after | : " + peek);
                return null;
            }

        case '<':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.le;
            } else if (peek == '>') {
                peek = ' ';
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

        case '=':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.eq;
            } else {
                return Word.assign;
            }

        case (char) -1:
            return new Token(Tag.EOF);

        default:
            if (Character.isLetter(peek)) {

                // ... gestire il caso degli identificatori e delle parole chiave //
                String lexeme = Character.toString(peek);
                readch(br);
                while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                    lexeme += Character.toString(peek);
                    readch(br);
                }

                if (lexeme.equals("if")) {
                    return Word.iftok;
                } else if (lexeme.equals("then")) {
                    return Word.then;
                } else if (lexeme.equals("else")) {
                    return Word.elsetok;
                } else if (lexeme.equals("for")) {
                    return Word.fortok;
                } else if (lexeme.equals("do")) {
                    return Word.dotok;
                } else if (lexeme.equals("print")) {
                    return Word.print;
                } else if (lexeme.equals("read")) {
                    return Word.read;
                } else if (lexeme.equals("begin")) {
                    return Word.begin;
                } else if (lexeme.equals("end")) {
                    return Word.end;
                } else {
                    return new Word(Tag.ID, lexeme);
                }
            } else if (peek == '_') {

                String lexeme = Character.toString(peek);
                readch(br);

                while (peek == '_') {
                    lexeme += Character.toString(peek);
                    readch(br);
                }

                if (Character.isLetter(peek) || Character.isDigit(peek)) {
                    lexeme += Character.toString(peek);
                    readch(br);
                } else {
                    System.err.println("Identificatore composto solo da underscore!!");
                    return null;
                }

                while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
                    lexeme += Character.toString(peek);
                    readch(br);
                }

                return new Word(Tag.ID, lexeme);

            } else if (Character.isDigit(peek)) {
                // ... gestire il caso dei numeri ... //
                String numb=""+peek;
                readch(br);
                while (Character.isDigit(peek)) {
                    numb+=peek;
                    readch(br);
                }
                if (Character.isLetter(peek) || peek == '_') {
                    System.err.println("Identifier cannot start with a digit");
                    return null;
                }
                return new Number(Tag.NUM, Integer.parseInt(numb));
            } else {
                System.err.println("Erroneous character: " + peek);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
