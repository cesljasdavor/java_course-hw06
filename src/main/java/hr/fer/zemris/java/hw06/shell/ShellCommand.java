package hr.fer.zemris.java.hw06.shell;

import java.util.List;

/**
 * Sučelje koje predstavlja apstraknu naredbu ljuske. Sučelje nudi tri metode
 * koje svaka konkretna naredba ljuske treba ponuditi:
 * <ul>
 * <li>{@link #executeCommand(Environment, String)}</li>
 * <li>{@link #getCommandName()}</li>
 * <li>{@link #getCommandDescription()}</li>
 * </ul>
 * 
 * @see ShellStatus
 * @see Environment
 * 
 * @author Davor Češljaš
 */
public interface ShellCommand {

	/**
	 * Metoda od koje kreće izvođenje naredbe. Metoda prima primjerak razreda
	 * koji implementira sučelje {@link Environment} <b>env</b> i sve metode
	 * unosa i ispisa mora delegirati upravo tom primjerku razreda. Kao drugi
	 * argument metoda prima argumente koji su potrebni za izvršavanje naredbe
	 * putem primjerka razreda {@link String} <b>arguments</b>
	 *
	 * @param env
	 *            primjerak razreda koji implementira sučelje
	 *            {@link Environment}
	 * @param arguments
	 *            argumenti koji su potrebni za izvršavanje naredbe
	 * @return jedan od mogućih status {@link ShellStatus#CONTINUE} ili
	 *         {@link ShellStatus#TERMINATE} ovisno o semantici naredbe
	 */
	ShellStatus executeCommand(Environment env, String arguments);

	/**
	 * Metoda koja vraća ime naredbe. Ime naredbe je nepromijenjivo. Savjetuje
	 * se da se unutar ljuske poziv naredbe poistovjeti s ovim imenom
	 *
	 * @return primjerak razreda {@link String} koji reprezentira ime ove
	 *         naredbe
	 */
	String getCommandName();

	/**
	 * Metoda koja vraća opis naredbe. Opis naredbe je nepromijenjiv. Ukoliko
	 * korisnik zatraži opis naredbe unutar ljuske, savjetuje se korištenje
	 * upravo ovog opisa.
	 *
	 * @return {@link List} primjeraka razreda {@link String}. Svaki primjerak
	 *         razreda {@link String} predstavlja jedan redak opisa.
	 */
	List<String> getCommandDescription();

}
