package hr.fer.zemris.java.hw06.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Collections;
import java.util.SortedMap;

/**
 * Sučelje koje oblikuje okolinu unutar koje se naredbe izvode. Razredi koji
 * implementiraju ovo sučelje moraju ponoditi sljedeće metode:
 * <ul>
 * <li>{@link #readLine()}</li>
 * <li>{@link #write(String)}</li>
 * <li>{@link #writeln(String)}</li>
 * <li>{@link #commands()}</li>
 * <li>{@link #getMultilineSymbol()}</li>
 * <li>{@link #setMultilineSymbol(Character)}</li>
 * <li>{@link #getMorelinesSymbol()}</li>
 * <li>{@link #setMorelinesSymbol(Character)}</li>
 * <li>{@link #getPromptSymbol()}</li>
 * <li>{@link #setPromptSymbol(Character)}</li>
 * </ul>
 * 
 * NAPOMENA: Simboli MULTILINE, PROMPT i MORELINES ne bi trebali imati nikakvu
 * semantičku vrijednost
 * 
 * @author Davor Češljaš
 */
@SuppressWarnings("unused")
public interface Environment {

	/**
	 * Metoda koja se koristi za čitanje jednog redka unosa. Savjetuje se da se
	 * za unos koristi {@link BufferedReader} koji omata proizvoljni tok znakova
	 *
	 * @return redak unosa koji je pročitan
	 * @throws ShellIOException
	 *             ukoliko se ne može čitati iz ulaznog toka znakova
	 */
	String readLine() throws ShellIOException;

	/**
	 * Metoda koja se koristi za pisanje proizvoljnog niz znakova <b>text</b> u
	 * izlazni tok znakova. Savjetuje se da se za pisanje koristi
	 * {@link BufferedWriter} koji omata proizvoljni tok znakova.
	 *
	 * @param text
	 *            proizvoljan niz znakova koji se želi upisati u izlazni tok
	 * @throws ShellIOException
	 *             ukoliko se ne može pisati u izlazni tok znakova
	 */
	void write(String text) throws ShellIOException;

	/**
	 * Metoda koja se koristi za pisanje jednog redka koji sadrži proizvoljan
	 * niz znakova <b>text</b> u izlazni tok znakova. Savjetuje se da se za
	 * pisanje koristi {@link BufferedWriter} koji omata proizvoljni tok
	 * znakova. Također savjetuje se delegiranje pisanja u izlazni tok metodi
	 * {@link #write(String)}.
	 *
	 * @param text
	 *            redak koji sadrži proizvoljan niz znakova koji se želi upisati
	 *            u izlazni tok
	 * @throws ShellIOException
	 *             ukoliko se ne može pisati u izlazni tok znakova
	 */
	void writeln(String text) throws ShellIOException;

	/**
	 * Metoda koja dohvaća sve naredbe podržane unutar ljuske koja koristi ovo
	 * okruženje. Sve naredbe vraćaju se mapirane po nazivu naredbe unutar
	 * nepromijenjive soritrane mape oblikovane sučeljem {@link SortedMap}. Za
	 * implementaciju savjetuje se da korisnik pogleda metodu
	 * {@link Collections#unmodifiableSortedMap(SortedMap).}
	 *
	 * @return naredbe ljuske koja koristi ovo okruženje mapirane po nazivu
	 *         naredbe unutar nepromijenjive soritrane mape oblikovane sučeljem
	 *         {@link SortedMap}
	 */
	SortedMap<String, ShellCommand> commands();

	/**
	 * Metoda koja dohvaća primjerak razreda {@link Character} koji unutar ovog
	 * okruženja predstavlja znak koji se ispisuje prije svakog redka
	 * višeredčane naredbe.
	 *
	 * @return primjerak razreda {@link Character} koji unutar ovog okruženja
	 *         predstavlja znak koji se ispisuje prije svakog redka višeredčane
	 *         naredbe.
	 */
	Character getMultilineSymbol();

	/**
	 * Metoda koja postavlja primjerak razreda {@link Character} koji unutar
	 * ovog okruženja predstavlja znak koji se ispisuje prije svakog redka
	 * višeredčane naredbe.
	 *
	 * @param symbol
	 *            novi primjerak razreda {@link Character} koji unutar ovog
	 *            okruženja predstavlja znak koji se ispisuje prije svakog redka
	 *            višeredčane naredbe.
	 */
	void setMultilineSymbol(Character symbol);

	/**
	 * Metoda koja dohvaća primjerak razreda {@link Character} koji unutar ovog
	 * okruženja predstavlja znak koji se ispisuje prije upisa svake naredbe.
	 *
	 * @return primjerak razreda {@link Character} koji unutar ovog okruženja
	 *         predstavlja znak koji se ispisuje prije upisa svake naredbe.
	 */
	Character getPromptSymbol();

	/**
	 * Metoda koja postavlja primjerak razreda {@link Character} koji unutar
	 * ovog okruženja predstavlja znak koji se ispisuje prije upisa svake
	 * naredbe.
	 *
	 * @param symbol
	 *            novi primjerak razreda {@link Character} koji unutar ovog
	 *            okruženja predstavlja znak koji se ispisuje prije upisa svake
	 *            naredbe.
	 */
	void setPromptSymbol(Character symbol);

	/**
	 * Metoda koja dohvaća primjerak razreda {@link Character} koji unutar ovog
	 * okruženja predstavlja znak koji je potrebno upisati ukoliko se želi
	 * napisati višeredčana naredba. Savjetuje se da ovaj znak ne bude sastavni
	 * dio naredbe
	 *
	 * @return primjerak razreda {@link Character} koji unutar ovog okruženja
	 *         predstavlja znak koji je potrebno upisati ukoliko se želi
	 *         napisati višeredčana naredba
	 */
	Character getMorelinesSymbol();

	/**
	 * Metoda koja postavlja primjerak razreda {@link Character} koji unutar
	 * ovog okruženja predstavlja znak koji je potrebno upisati ukoliko se želi
	 * napisati višeredčana naredba Savjetuje se da ovaj znak ne bude sastavni
	 * dio naredbe
	 *
	 * @param symbol
	 *            novi primjerak razreda {@link Character} koji unutar ovog
	 *            okruženja predstavlja znak koji se ispisuje prije upisa svake
	 *            naredbe. Savjetuje se da ovaj znak ne bude sastavni dio
	 *            naredbe
	 */
	void setMorelinesSymbol(Character symbol);

}
