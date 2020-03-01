import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {        
		switch(look.tag) {
			case Tag.ID:
			case Tag.READ:
			case Tag.PRINT:
			case Tag.IF:
			case Tag.FOR:
			case Tag.BEGIN:
				int lnext_prog = code.newLabel();
				statlist(lnext_prog);
				code.emitLabel(lnext_prog);
				match(Tag.EOF);
				try {
					code.toJasmin();
				}
				catch(java.io.IOException e) {
					System.out.println("IO error\n");
				};
				break;

			default:
				error("stringa non accettata - Procedura: prog");
		}
    }

    public void stat(int lnext) {
        switch(look.tag) {
	        case Tag.ID:
	            int id_addr_id = st.lookupAddress(((Word)look).lexeme); //Verifico se la variabile è presente nella tabella dei simboli
	            if(id_addr_id == -1){ //Non è presente, aggiungo la var
	            	id_addr_id = count; 
	            	st.insert(((Word)look).lexeme, count++); 
	            }
	            match(Tag.ID);
	            if(look.tag == '=') {
	            	match('=');
	            	expr();
	            	code.emit(OpCode.istore, id_addr_id);
	            } else {
	            	error("stringa non accettata, \'=\' non presente in stat");
	            }
        		break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                expr();
                code.emit(OpCode.invokestatic, 1); //determina num argomenti e cerca la lista dei metodi statici collegati ad esso
                match(')');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr == -1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme, count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore,read_id_addr);   
                } else error("Error in grammar (stat) after read( with " + look);
                break;

            case Tag.IF:
           	 	match(Tag.IF);
            	int if_true = code.newLabel(), if_false = code.newLabel();                
                b_expr(if_true, if_false);
                code.emitLabel(if_true); //Vado qui se la condizione è vera
                match(Tag.THEN);
                stat(lnext);
                statelse(lnext, if_false);
            	break;

            case Tag.FOR:
                match(Tag.FOR);
                int bexpr_true = code.newLabel();
                int bexpr_false = lnext; //Se la condizione non è verificata continuo con le successive istruzioni
                int stat_next = code.newLabel();
                match('(');
                int id_addr = -1;
                if (look.tag == Tag.ID) {
	                id_addr = st.lookupAddress(((Word)look).lexeme);
	                if (id_addr == -1) {
	                    id_addr = count;
	                    st.insert(((Word)look).lexeme, count++);
	               	}
                	match(Tag.ID);
                	if(look.tag == '=') {
                		match('=');
                		expr();
                		code.emit(OpCode.istore, id_addr);
                	} else error("stringa non accettata, \'=\' non presente in stat (FOR)");
           		}
                match(';');
                code.emitLabel(stat_next); //Ogni volta che termino il ciclo devo verificare la condizione, ovvero tornare qui
                b_expr(bexpr_true, bexpr_false);
                match(')');
                match(Tag.DO);
                code.emitLabel(bexpr_true); //Se la condizione è verificata eseguo il contenuto del do     
 				int incremento = code.newLabel();
                stat(incremento);
                code.emitLabel(incremento); //Istruzioni per l'incremento della variabile i
                code.emit(OpCode.ldc, 1);
                code.emit(OpCode.iload, id_addr);
                code.emit(OpCode.iadd);
                code.emit(OpCode.istore, id_addr);
                code.emit(OpCode.GOto, stat_next);
            	break;

            case Tag.BEGIN:
                match(Tag.BEGIN);
                statlist(lnext);
                match(Tag.END);
            	break;

			default:
				error("stringa non accettata - Procedura: stat");
   	 	}
 	}

    private void statelse(int lnext, int cond_bool) {
        switch (look.tag) {
            case Tag.ELSE:
                match(Tag.ELSE);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(cond_bool);
                stat(lnext);
                break;

            case Tag.EOF:
            case Tag.END:
            case ';':
            	code.emitLabel(cond_bool);
                break;

           default:
                error("stringa non accettata - Procedura: statelse");
        }
    }

 	private void statlist(int next) {
	    switch (look.tag) {
	        case Tag.ID:
            case Tag.READ:
            case Tag.PRINT:
            case Tag.FOR:
            case Tag.IF:
            case Tag.BEGIN:
            	int stat_next = code.newLabel();
                stat(stat_next);
                code.emitLabel(stat_next);
                statlistp(next);
            	break;

            default:
                error("stringa non accettata - Procedura: statlist");
	   }
    }

    private void statlistp(int next) {
        switch(look.tag) {
            case ';':
                match(';');
                int stat_next = code.newLabel();
                stat(stat_next);
                code.emitLabel(stat_next);
                statlistp(next);
           		break;

            case Tag.EOF:
            case Tag.END:
                //do nothing
            break;

            default:
                error("stringa non accettata - Procedura: statlistp");
        } 
    }

    private void b_expr(int ltrue, int lfalse) {
		switch(look.tag){
	        case '(' :
	        case Tag.NUM :
	        case Tag.ID :
		        expr();
		        if(look == Word.eq) { //==
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmpeq, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	         	} else if (look == Word.le) { // <=
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmple, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	          	} else if (look == Word.lt) { // <
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmplt, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	          	} else if (look == Word.ne) { // <>
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmpne, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	           	} else if (look == Word.ge) { // >=
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmpge, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	          	} else if (look == Word.gt) { // >
	                match(Tag.RELOP);
	                expr();
	                code.emit(OpCode.if_icmpgt, ltrue);
	                code.emit(OpCode.GOto, lfalse);
	          	} else error("operatore booleano non valido");
          break;

	          default :
	            error("stringa non accettata - Procedura: bexpr");
	    }
	}

    private void expr() {
        switch(look.tag) {
            case '(':
            case Tag.ID:
            case Tag.NUM: 
                term();
                exprp();
           	 	break;

           	default:
               error("stringa non accettata - Procedura: expr");
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

            case Tag.ELSE:
            case Tag.EOF:
            case Tag.END:
            case Tag.RELOP:
            case Tag.THEN:
            case ';':
            case ')':
                //do nothing;
            	break;

           	default:
                error("stringa non accettata - Procedura: exprp");
        }
    }

    private void term() {
        switch (look.tag) {
            case '(':
            case Tag.ID:
            case Tag.NUM:
                fact();
                termp();
            	break;

           default:
                error("stringa non accettata - Procedura: term");
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

            case Tag.ELSE:
            case Tag.EOF:
            case Tag.END:
            case Tag.RELOP:
            case Tag.THEN:
            case ';':
            case ')':
            case '+':
            case '-':
                //do nothing;
            	break;

           default:
                error("stringa non accettata - Procedura: termp");
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
            	break;

            case Tag.NUM:
            	code.emit(OpCode.ldc, ((Number)look).lexeme);
                match(Tag.NUM);
            	break;

            case Tag.ID:
             	String key = ((Word)look).lexeme;
                int read_id_addr = st.lookupAddress(key);
				if(read_id_addr == -1) { //variabile non presente             
		            throw new IllegalArgumentException("errore: variabile '"+ key + "' non dichiarata!");
		            /* read_id_addr = count;
		            st.insert(key, count++); */
		        }
		        code.emit(OpCode.iload, read_id_addr);
	            match(Tag.ID);
            	break;

           default:
                error("stringa non accettata - Procedura: fact");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.pas";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator et = new Translator(lex, br);
            et.prog();
            System.out.println("\nFile Output.j generato!");
            System.out.println("Digita 'java -jar jasmin.jar Output.j' per il file Output.class e 'java Output' per eseguirlo.\n");
            br.close();
        } catch (IOException e) {e.printStackTrace(); }
    }
}
