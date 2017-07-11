package hr.fer.zemris.java.hw06.shell;

/**
 * Razred koji nasljeđuje {@link RuntimeException}. Razred se koristi u
 * implementaciji okoline koja implementira sučelje {@link Environment}. Ovaj
 * razred je neprovjeravana iznimka i koristi se kako bi korisnika obavijestio
 * da sa {@link System#in} nije moguće čitati i u {@link System#out} nije moguće
 * pisati
 * 
 * @see System
 * @see RuntimeException
 * 
 * @author Davor Češljaš
 */
public class ShellIOException extends RuntimeException {

	/** Konstanta serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Korištenjem ovog
	 * konstruktora korisniku će prilikom pojave iznimke biti ispisan trag stoga
	 * bez ikakve dodatne poruke
	 */
	public ShellIOException() {
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
	public ShellIOException(String message) {
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
	public ShellIOException(Throwable cause) {
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
	public ShellIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
