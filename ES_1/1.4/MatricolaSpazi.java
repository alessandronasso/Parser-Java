//Esercizio 1.4
import java.util.*;
public class MatricolaSpazi {
	public static boolean scan(String s) {
		int state = 0; int i = 0, x=0;
		while (state >= 0 && i < s.length()) { 
			final char ch = s.charAt(i++);
			switch (state) { 
				case 0:
					if (Character.isDigit(ch))
						if ((int)ch%2!=0) 
							state=1;
						else
							state=2;
					else if (ch!=' ')
						state=-1;
					break;

				case 1: //ultima cifra inserita=dispari
					if (Character.isWhitespace(ch))
						state=4;
					else if (Character.isDigit(ch)) 
						if ((int)ch%2==0)
							state=2;
					else
                        state=-1;
					break;

				case 2: //ultima cifra inserita=pari
					if (Character.isWhitespace(ch))
						state=4;
					else if (Character.isDigit(ch)) 
						if ((int)ch%2!=0)
							state=1;
					else
						state=-1;
					break;

				case 3: //caso "Spazio"--dispari
					if (Character.isLetter(ch))
						if (ch>='l' && ch<='z')
							state=5;
						else
                            state=-1;
					else if (ch!=' ')
						state=-1;
					break;

				case 4: //caso "Spazio"--pari
					if (Character.isLetter(ch))
						if (ch>='a' && ch<='k')
							state=5;
						else
                            state=-1;
					else if (ch!=' ')
						state=-1;
					break;

				case 5:
					if (!(Character.isLetter(ch) || ch==' '))
						state=-1;
					break;

		} 				
	}
	return state == 5;  
}
public static void main(String[] args) {
		Scanner tastiera=new Scanner (System.in);
		System.out.println("Inserisci la stringa:");
		String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}
