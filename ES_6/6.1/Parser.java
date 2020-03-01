import java.io.*;
import java.util.*;


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
	   throw new Error("alla riga " + lex.line + ": " + s);
    }

    void match(int t) {
    	if (look.tag == t) {
    	   if (look.tag != Tag.EOF) move();
    	} else error("errore di sintassi");
    }

    public void start() {
        int stato = 0;
        Stack<Integer> st = new Stack<>();
        st.push(stato); //aggiungo stato 0 sullo stack
        while(true) {
            System.out.println("Input: "+look.tag);
            System.out.println("Stack: " + st);

            if(azione(stato, look.tag) == 0) { // shift
                int shift = shift(stato, look.tag);
                stato = (int)st.push(shift); //aggiungo stato su stack
                System.out.println("Shift"+shift);
                move();
            } else if(azione(stato, look.tag) == 1) { //reduce 
                ReduceInfo info = reduce(stato, look.tag); //recupero le informazioni
                popstack(st, info.getNumero()); //rimuovo dallo stack tanti stati quanti ritornati da getNumero
                System.out.println("Reduce"+info.getProduzione() + " Lunghezza produzione: "+ info.getNumero());
                stato = (int)st.peek(); //aggiorno con nuovo tos dopo la rimozione
                stato = (int)st.push(gotoStato(stato, info.getProduzione())); //inserisco nuovo stato 
            } else if(azione(stato, look.tag) == 2) break; // accetta
            else error("la stringa non e' corretta");
        }
    }

    public static int azione(int stato, int cc) {
        int shift = 0, reduce = 1, accetta = 2, errore = -1;
        switch(stato) {
            case 0:
                switch(cc) {
                    case '(':
                    case Tag.NUM:
                        return shift;
                    default: 
                        return errore;
                }

            case 1:
                switch(cc) {
                    case '+':
                    case '-':
                        return shift;
                    case Tag.EOF:
                        return accetta;
                    default: 
                        return errore;
                }

            case 2:
                switch(cc) {
                    case '+':
                    case '-':
                        return reduce;
                    case '*':
                    case '/':
                        return shift;
                    case ')':
                    case Tag.EOF:
                        return reduce;
                    default:
                        return errore;
                }
            case 3:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return reduce;
                    default:
                        return errore;
                }

            case 4:
                switch(cc) {
                    case '(':
                    case Tag.NUM:
                        return shift;
                    default:
                        return errore;
                }

            case 5:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return reduce;
                    default:
                        return errore;
                }
            case 6:
            case 7:
            case 8:
            case 9:
                switch(cc) {
                    case '(':
                    case Tag.NUM:
                        return shift;
                }
            case 10:
                switch(cc) {
                    case '+':
                    case '-':
                    case ')':
                        return shift;
                    default:
                        return errore;

                }
            case 11:
            case 12:
                switch(cc) {
                    case '+':
                    case '-':
                        return reduce;
                    case '*':
                    case '/':
                        return shift;
                    case ')':
                    case Tag.EOF:
                        return reduce;
                    default:
                        return errore;
                }
            case 13:
            case 14:
            case 15:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return reduce;
                    default:
                        return errore;
                }          
        }
        return errore;                  
    }

    public static int shift(int stato, int cc) {
        switch(stato) {
            case 0:
                switch(cc) {
                    case '(':
                        return 4;
                    case Tag.NUM:
                        return 5;
                }

            case 1:
                switch(cc) {
                    case '+':
                        return 6;
                    case '-':
                        return 7;
                }

            case 2:
                switch(cc) {
                    case '*':
                        return 8;
                    case '/':
                        return 9;
                }

            case 4:
                switch(cc) {
                    case '(':
                        return 4;
                    case Tag.NUM:
                        return 5;
                }

            case 6:
            case 7:
            case 8:
            case 9:
                switch(cc) {
                    case '(':
                        return 4;
                    case Tag.NUM:
                        return 5;
                }
            case 10:
                switch(cc) {
                    case '+':
                        return 6;
                    case '-':
                        return 7;
                    case ')':
                        return 15;
                }
            case 11:
            case 12:
                switch(cc) {
                    case '*':
                        return 8;
                    case '/':
                        return 9;
                }       
        }
        return -1;              
    }
	
    public static ReduceInfo reduce(int stato, int cc) {
        int numero;
        char produzione;
        switch(stato) {
            case 2:
                switch(cc) {
                    case '+':
                    case '-':
                    case ')':
                    case Tag.EOF:
                        numero = 1;
                        produzione = 'E';
                        return new ReduceInfo(1, 'E');
                }
            case 3:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(1, 'T');
                }

            case 5:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(1, 'F');
                }

            case 11:
                switch(cc) {
                    case '+':
                    case '-':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(3, 'E');
                }

            case 12:
                switch(cc) {
                    case '+':
                    case '-':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(3, 'E');
                }
            case 13:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(3, 'T');  
                } 

            case 14:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(3, 'T');
                } 

            case 15:
                switch(cc) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case ')':
                    case Tag.EOF:
                        return new ReduceInfo(3, 'F');
                }          
        }          
        return null;      
    }

    public static int gotoStato(int stato, char cc) {
        switch(stato) {
            case 0:
                switch(cc) {
                    case 'E':
                        return 1;
                    case 'T':
                        return 2;
                    case 'F':
                        return 3;
                }

            case 4:
                switch(cc) {
                    case 'E':
                        return 10;
                    case 'T':
                        return 2;
                    case 'F':
                        return 3;
                }

            case 6:
                switch(cc) {
                    case 'T':
                        return 11;
                    case 'F':
                        return 3;
                } 

            case 7:
                switch(cc) {
                    case 'T':
                        return 12;
                    case 'F':
                        return 3;
                }
            case 8:
                switch(cc) {
                    case 'F':
                        return 13;
                }

            case 9:
                switch(cc) {
                    case 'F':
                        return 14;
                }         
        }  
        return -1;                 
    }
    static void popstack(Stack st, int num) { 
        for(int i=0; i<num; i++) {
            st.pop();
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Stringa accettata");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}

final class ReduceInfo {
    private final int numero; //numero di elementi da rimuovere dallo stack dopo
    private final char produzione;

    public ReduceInfo(int numero, char produzione) {
        this.numero = numero;
        this.produzione = produzione;
    }

    public int getNumero() {
        return numero;
    }

    public char getProduzione() {
        return produzione;
    }
}