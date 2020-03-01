//Esercizio 1.2
import java.util.*;
public class Identif {
	public static boolean scan(String s) {
		int state = 0; int i = 0;
		while (state >= 0 && i < s.length()) { 
			final char ch = s.charAt(i++);
			switch (state) { 
				case 0:
					if (Character.isLetter(ch)) //Controllo se il carattere corrisponde ad una lettera
						state = 2; //se lo e' vado gia' nello stato finale (2)
					else if (ch=='_') 
						state = 1; //Se e' un _ allora vado nello stato 1
					else
						state = -1; //altrimenti se e' qualcos'altro so gia' che deve andare in errore (-1)
					break;

				case 1:
					if (Character.isLetter(ch) || Character.isDigit(ch)) 
						state = 2; //Se il char e' una lettera o un numero allora vado nello stato finale
					else if (ch != '_')
						state=-1; //Se invece arriva in input qualcosa diverso da _ allora deve andare in errore
					break;

				case 2:
					if (!(Character.isDigit(ch) || Character.isLetter(ch) || ch=='_'))
						state=-1; //Se nello stato 3 gli arriva qualsiasi cosa non compresa nel liguaggio dell'automa allora
						break;     //deve dare errore 
			} 				
		}
	return state == 2; 
}
public static void main(String[] args) {
		Scanner tastiera=new Scanner (System.in);
		System.out.println("Inserisci la stringa:");
		String st=tastiera.nextLine();
        System.out.println(scan(st) ? "OK" : "NOPE");
    }
}
