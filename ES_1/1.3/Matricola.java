//Esercizio 1.3
import java.util.*;
public class Matricola {
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
					else
						state=-1;
					break;

				case 1: //ultima cifra inserita=dispari
					if (Character.isLetter(ch))
						if (ch>='l' && ch<='z') //se e' dispari e compreso tra l-z
							state=3; //allora procedo
						else
							state=-1; //se la matricola non coincide con il cognome ->errore
					else if(Character.isDigit(ch))
						if ((int)ch%2==0) //controllo se mi devo spostare in q2 (cifra pari)
							state=2;
					else 
						state=-1;
					break;

				case 2: //ultima cifra inserita=pari
					if (Character.isLetter(ch))
						if (ch>='a' && ch<='k') //se e' pari e compreso tra a-k
							state=3; //allora procedo
						else
							state=-1; //se la matricola non coincide con il cognome ->errore
					else if(Character.isDigit(ch))
						if ((int)ch%2!=0) //controllo se mi devo spostare in q1 (cifra dispari)
							state=1;
					else 
						state=-1;
					break;

				case 3: //ho finito di inserire i numeri della matricola e sto inserendo le lettere
					if (!(Character.isLetter(ch)))
						state=-1;
					break;
		} 				
	}
	return state == 3;  
}
    public static void main(String[] args) {
        Scanner tastiera=new Scanner (System.in);
        System.out.println("Inserisci la stringa:");
        String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}

