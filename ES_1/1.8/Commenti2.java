//Esercizio 1.8
import java.util.*;
public class Commenti2 {
	public static boolean scan(String s) {
		int state = 0; int i = 0, x=0;
		while (state >= 0 && i < s.length()) { 
			final char ch = s.charAt(i++);
			switch (state) { 
				case 0:
					if (ch=='/')
						state=1;
					else if (ch!='*' && ch!='a')
						state=-1;
					break;

				case 1: 
					if (ch=='*')
						state=2;
					else if (ch=='a')
						state=0;
					else if (ch!='/')
						state=-1;
					break;

				case 2: 
					if (ch=='*')
						state=3;
					else if (!(ch=='/' || ch=='a'))
						state=-1;
					break;

				case 3:
					if (ch=='/')
						state=0;
					else if (ch=='a')
						state=2;
					else if (!(ch=='*'))
						state=-1;
					break;

		} 				
	}
	return state == 0 || state == 1;  
}
public static void main(String[] args) {
		Scanner tastiera=new Scanner (System.in);
		System.out.println("Inserisci la stringa:");
		String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}
