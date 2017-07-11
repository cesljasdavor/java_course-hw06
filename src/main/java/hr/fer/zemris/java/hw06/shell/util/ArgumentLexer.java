package hr.fer.zemris.java.hw06.shell.util;

/**
 * Razred koji predstavlja leksički analizator koji se koristi prilikom
 * parsiranja primjerkom razreda {@link ArgumentParser} . Ovaj analizator ulazni
 * niz znakova analizira na sljedeća dva načina:
 * <ul>
 * <li>Ukoliko se kao prvi znak pročita znak {@value #QUOUTE} tada leksički
 * analizator nastoji izvaditi primjerak razreda {@link String} tako da makne
 * taj znak i čita sve znakove do sljedećeg nailaska na znak {@value #QUOUTE}.
 * Ujedino provjeravaju se 2 moguće escape sekvence:
 * <ol>
 * <li>escape sekvenca sastoji se od {@value #ESCAPE} i {@value #QUOUTE} te se
 * tumači kao {@value #QUOUTE}</li>
 * <li>escape sekvenca sastoji se od dva znaka {@value #ESCAPE}, a tumači se kao
 * jedan takav znak</li>
 * </ol>
 * Sve ostale sekvence od dva znaka koje prije imaju {@value #ESCAPE}, tumače se
 * kao dva znaka.</li>
 * <li>Inače se niz tumači kao primjerak razreda {@link String} sve do nailaska
 * na prazninu</li>
 * </ul>
 * 
 * @see ArgumentParser
 * 
 * @author Davor Češljaš
 */
public class ArgumentLexer {

	/** Konstanta koja predstavlja znak '\\'. */
	private static final char ESCAPE = '\\';

	/** Konstanta koja predstavlja znak ". */
	private static final char QUOUTE = '"';

	/** Trenutni pozicija u ulaznom nizu znakova. */
	private int currentIndex;

	/** Ulazni niz znakova koji se leksički analizira. */
	private char[] data;

	/** Zadnje izvađeni token. */
	private String currentToken;

	/**
	 * Konstruktor koji iz ulaznog teksta inicijalizira ulazni niz znakova.
	 *
	 * @param arguments
	 *            ulazni tekst koji je potrebno leksički analizirati
	 * @throws ArgumentLexerException
	 *             ukoliko je kao <b>arguments</b> predan <b>null</b>
	 */
	public ArgumentLexer(String arguments) {
		if (arguments == null) {
			throw new ArgumentLexerException("Ulazni niz u leksički analizator ne smije biti null");
		}

		data = arguments.trim().toCharArray();
	}

	/**
	 * Metoda koja dohvaća zadnje izvađeni token. Može vratiti <b>null</b>
	 * ukoliko analiza nije započela, odnosno nikada nije pozvana metoda
	 * {@link #nextToken()}
	 *
	 * @return zadnje izvađeni token ili <b>null</b>
	 */
	public String getCurrentToken() {
		return currentToken;
	}

	/**
	 * Analizator iz predanog ulaznog teksta pokušava izvaditi sljedeći token.
	 * Metoda ujedino ažurira trenutni token. Izvađeni token sada je ponovo
	 * moguće dohvatiti pozivom metode {@link #getToken()}
	 *
	 * @return sljedeći token iz ulaznog niza
	 * 
	 * @throws ArgumentLexerException
	 *             ukoliko sljedeći token nije moguće izvaditi, jer znakovi u
	 *             ulaznom nizu ne odgovaraju niti jednom tipu tokena
	 */
	public String nextToken() {
		extractToken();
		return currentToken;
	}

	/**
	 * Metoda koja ispituje jesmo li došli do kraja ulaznog niza.
	 *
	 * @return <b>true</b> ukoliko smo došli do kraj niza <b>false</b> inače
	 */
	public boolean isEOF() {
		return currentIndex >= data.length;
	}

	/**
	 * Pomoćna metoda koja vrši vađenje sljedećeg tokena . Ako uspije izvađeni
	 * token će biti postavljen kao trenutni token
	 * 
	 * @throws ArgumentLexerException
	 *             ukoliko vađenje sljedećeg tokena nije uspjelo
	 */
	private void extractToken() {
		skipWhitespaces();
		
		if(isEOF()) {
			throw new ArgumentLexerException("Nemam više tokena!");
		}
		
		// promjena stanja
		if (data[currentIndex] == QUOUTE) {
			currentIndex++;
			nextQuoutedStringToken();
		} else {
			nextStringToken();
		}
	}

	/**
	 * Pomoćna metoda koja niz tumači kao primjerak razreda {@link String} sve
	 * do nailaska na prazninu ili kraj niza
	 */
	private void nextStringToken() {
		StringBuilder sb = new StringBuilder();
		
		while (!isEOF() && !isWhitespace(data[currentIndex])) {
			sb.append(data[currentIndex++]);
		}
		currentToken = sb.toString();
	}

	/**
	 * Pomoćna metoda koja nastoji izvaditi primjerak razreda {@link String}
	 * tako da čita sve znakove do sljedećeg nailaska na znak {@value #QUOUTE}.
	 * Ujedino provjeravaju se 2 moguće escape sekvence:
	 * <ol>
	 * <li>escape sekvenca sastoji se od {@value #ESCAPE} i {@value #QUOUTE} te
	 * se tumači kao {@value #QUOUTE}</li>
	 * <li>escape sekvenca sastoji se od dva znaka {@value #ESCAPE}, a tumači se
	 * kao jedan takav znak</li>
	 * </ol>
	 */
	private void nextQuoutedStringToken() {
		StringBuilder sb = new StringBuilder();

		while (true) {
			if (isEOF()) {
				throw new ArgumentLexerException("Niste zatvorili navodnike!");
			} 
			if (data[currentIndex] == QUOUTE) {
				currentIndex++;
				break;
			}
			
			if (data[currentIndex] == ESCAPE) {
				extractEscapeSequence(sb);
			} else {
				sb.append(data[currentIndex++]);
			}
		}
		currentToken = sb.toString();
	}

	/**
	 * Pomoćna metoda koja vadi escape sekvencu. Postoje 2 moguće escape
	 * sekvence.
	 * <ol>
	 * <li>escape sekvenca sastoji se od {@value #ESCAPE} i {@value #QUOUTE} te
	 * se tumači kao {@value #QUOUTE}</li>
	 * <li>escape sekvenca sastoji se od dva znaka {@value #ESCAPE}, a tumači se
	 * kao jedan takav znak</li>
	 * </ol>
	 * Sve ostale sekvence od dva znaka koje prije imaju {@value #ESCAPE},
	 * tumače se kao dva znaka.
	 *
	 * @param sb
	 *            primjerak razreda {@link StringBuilder} kojem se nadodaje znak
	 * @throws ArgumentLexerException
	 *             ako znak nakon '\' nije jednak <b>specialChar</b>
	 */
	private void extractEscapeSequence(StringBuilder sb) {
		char c = data[++currentIndex];

		if (c != ESCAPE && c != QUOUTE) {
			sb.append(ESCAPE);
		}
		sb.append(data[currentIndex++]);
	}

	/**
	 * Pomoćna metoda koja se koristi za preskakanje praznina u ulaznom nizu.
	 */
	private void skipWhitespaces() {
		while (!isEOF() && isWhitespace(data[currentIndex])) {
			currentIndex++;
		}
	}

	/**
	 * Pomoćna metoda koja ispituje je li predani znak praznina. Kao praznine se
	 * podrazumjevaju znakovi :
	 * <ul>
	 * <li>'\t'</li>
	 * <li>'\r'</li>
	 * <li>' '</li>
	 * </ul>
	 * 
	 *
	 * @param c
	 *            znak koji se provjerava
	 * @return <b>true </b> ukoliko je <b>c</b> praznina, inače vraća
	 *         <b>false</b>
	 */
	private boolean isWhitespace(char c) {
		return c == '\t' || c == '\r' || c == ' ';
	}
}
