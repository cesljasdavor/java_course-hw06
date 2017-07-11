package hr.fer.zemris.java.hw06.shell.util;

import java.util.ArrayList;
import java.util.List;

/**
 * /** Razred koji predstavlja sintaksni analizator. Razred sadrži primjerak
 * razreda {@link ArgumentLexer} te nad njim poziva
 * {@link ArgumentLexer#nextToken()} i gradi generativno stablo. Prilikom
 * parsiranja tokeni mogu biti samo primjerci razreda {@link String} te se na
 * svaki token gleda jednako. Razred nudi jednu metodu i jedan konstruktor:
 * <ul>
 * <li>{@link #getSeparatedArguments()}</li>
 * <li>{@link #ArgumentParser(String)}</li>
 * </ul>
 * 
 * Ukoliko parsiranje ne uspije , zbog svoje implementacije razred baca
 * {@link ArgumentLexerException}, jer se pogreška ne može dogoditi prilikom
 * parsiranja.
 * 
 * @see ArgumentLexer
 * @see ArgumentLexerException
 * 
 * @author Davor Češljaš
 */
public class ArgumentParser {

	/**
	 * Članska varijabla koja je referenca na primjerak razreda
	 * {@link ArgumentLexer}, s kojim se vrši leksička analiza
	 */
	private ArgumentLexer lexer;

	/**
	 * članska varijabla koja predstavlja referencu na {@link List} primjeraka
	 * razreda {@link String} koji su nastali parsiranjem
	 */
	private List<String> separatedArguments;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Konstruktor će
	 * stvoriti primjerak razreda {@link ArgumentLexer} i predati mu
	 * <b>arguments</b> bez ikakve dodatne provjere. Po stvaranju leksičkog
	 * analizatora prijerak ovog razreda počinje sa sintaksnom analizom. Ukoliko
	 * leksička analiza pođe po zlu baca se {@link ArgumentLexerException}
	 * 
	 * @param arguments
	 *            primjerak razreda {@link String} koji se sintaksno analizira
	 * 
	 * @throws ArgumentLexerException
	 *             ukoliko je leksička analiza naišla na pogrešku
	 * 
	 * @see ArgumentLexer
	 * 
	 */
	public ArgumentParser(String arguments) {
		lexer = new ArgumentLexer(arguments);
		parse();
	}

	/**
	 * Metoda koja dohvaća sve tokene koje je vratila metoda
	 * {@link ArgumentLexer#nextToken()} kao referencu na {@link List}
	 * primjeraka razreda {@link String}
	 *
	 * @return sve tokene koje je vratila metoda
	 *         {@link ArgumentLexer#nextToken()} kao referencu na {@link List}
	 *         primjeraka razreda {@link String}
	 */
	public List<String> getSeparatedArguments() {
		return separatedArguments;
	}

	/**
	 * Pomoćna metoda koja vrši parsiranje i dodavanje u
	 * {@link #separatedArguments}. Metoda doslovno poziva
	 * {@link ArgumentLexer#nextToken()} dok god metoda
	 * {@link ArgumentLexer#isEOF()} vraća <b>false</b>, odnosno dok ima tokena.
	 * 
	 * @throws ArgumentLexerException
	 *             ukoliko leksička analiza nije uspjela
	 */
	private void parse() {
		separatedArguments = new ArrayList<>();
		while (!lexer.isEOF()) {
			separatedArguments.add(lexer.nextToken().trim());
		}
	}

}
