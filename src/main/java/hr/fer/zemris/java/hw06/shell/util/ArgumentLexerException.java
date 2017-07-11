package hr.fer.zemris.java.hw06.shell.util;

/**
 * Razred koji nasljeđuje {@link RuntimeException}. Razred se koristi u
 * implementaciji leksičkog analizatora {@link ArgumentLexer}. Ovaj razred je
 * neprovjeravana iznimka i koristi se kako bi korisnika obavijestio da je
 * leksička analiza naišla na pogrešku
 * 
 * @author Davor Češljaš
 */
public class ArgumentLexerException extends RuntimeException {

	/** Konstanta serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Korištenjem ovog
	 * konstruktora korisniku će prilikom pojave iznimke biti ispisan trag stoga
	 * bez ikakve dodatne poruke
	 */
	public ArgumentLexerException() {
		super();
	}

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Korištenjem ovog
	 * konstruktora korisniku će prilikom pojave iznimke biti ispisan trag stoga
	 * uz dodatnu poruku
	 *
	 * @param message
	 *            poruka koju treba ispisati korisniku prilikom bacanja iznimke
	 */
	public ArgumentLexerException(String message) {
		super(message);
	}

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Korištenjem ovog
	 * konstruktora korisniku će prilikom pojave iznimke biti ispisan trag stoga
	 * uz dodatan uzrok
	 *
	 * @param cause
	 *            Uzrok bacanja iznimke
	 */
	public ArgumentLexerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Korištenjem ovog
	 * konstruktora korisniku će prilikom pojave iznimke biti ispisan trag stoga
	 * uz dodatan uzrok i poruku
	 *
	 * @param message
	 *            poruka koju treba ispisati korisniku prilikom bacanja iznimke
	 * @param cause
	 *            Uzrok bacanja iznimke
	 */
	public ArgumentLexerException(String message, Throwable cause) {
		super(message, cause);
	}
}
