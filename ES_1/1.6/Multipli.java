//Esercizio 1.6
import java.util.*;
public class Multipli {
	public static boolean scan(String s) {
		int state = 0; int i = 0, x=0;
		while (state >= 0 && i < s.length()) { 
			final char ch = s.charAt(i++);
			switch (state) { 
				case 0:
					if (ch=='1')
						state=1;
					else if (ch!='0')
						state=-1;
					break;

				case 1: 
					if (ch=='1')
						state=0;
					else if (ch=='0')
						state=2;
					else
						state=-1;
					break;

				case 2: 
					if (ch=='0')
						state=1;
					else if (ch!='1')
						state=-1;
					break;

		} 				
	}
	return state == 0;  
}
public static void main(String[] args) {
		Scanner tastiera=new Scanner (System.in);
		System.out.println("Inserisci la stringa:");
		String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}
