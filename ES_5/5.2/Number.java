public class Number extends Token {
    public int lexeme;

    public Number (int tag, int s) { 
    	super(tag); lexeme=s; 
    }

    public String toString() { 
    	return "<" + tag + ", " + lexeme + ">"; 
    }

    //public static final Number
	//numero = new Number (Tag.NUM, "300");
	  
}
