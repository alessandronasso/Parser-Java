//Esercizio 1.5
import java.util.*;
public class MatricolaContrario {
	public static boolean scan(String s) {
		int state = 0; int i = 0, x=0;
		while (state >= 0 && i < s.length()) { 
			final char ch = s.charAt(i++);
			switch (state) { 
				case 0:
					if (Character.isLetter(ch))
						if (ch>='a' && ch<='k')
							state=1;
						else 
							state=2;
					else 
						state=-1;
					break;

				case 1: //lettera compresa tra a-k
					if (Character.isDigit(ch))
						if ((int)ch%2==0)
							state=3;
						else
							state=5;
					else if (!(Character.isLetter(ch)))
						state=-1;
					break;

				case 2: //lettera compresa tra l-z
					if (Character.isDigit(ch))
						if ((int)ch%2!=0)
							state=4;
						else
							state=6;
					else if (!(Character.isLetter(ch)))
						state=-1;
					break;

				case 3: 
					if (Character.isDigit(ch)) 
						if ((int)ch%2!=0)
							state=4;
						else ;
					else
						state=-1;
					break;

				case 4:
					if (Character.isDigit(ch)) 
						if ((int)ch%2==0)
							state=6;
						else ;
					else
						state=-1;
					break;

				case 5:
					if (Character.isDigit(ch))
						if ((int)ch%2==0)
							state=3;
						else ;
					else
						state=-1;
					break;

				case 6:
					if (Character.isDigit(ch)) 
						if ((int)ch%2!=0)
							state=4;
						else ;
					else
						state=-1;
					break;

		} 				
	}
	return state == 3 || state == 4;  
}
public static void main(String[] args) {
		Scanner tastiera=new Scanner (System.in);
		System.out.println("Inserisci la stringa:");
		String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}
