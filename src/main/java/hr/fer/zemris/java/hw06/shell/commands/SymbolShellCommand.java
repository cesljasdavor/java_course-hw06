package hr.fer.zemris.java.hw06.shell.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hr.fer.zemris.java.hw06.shell.Environment;
import hr.fer.zemris.java.hw06.shell.MyShellEnvironment;
import hr.fer.zemris.java.hw06.shell.ShellCommand;
import hr.fer.zemris.java.hw06.shell.ShellStatus;
import hr.fer.zemris.java.hw06.shell.util.ShellCommandUtil;

/**
 * Razred koji predstavlja naredbu {@value #NAME}. Razred implementira sučelje
 * {@link ShellCommand} i nudi implementaciju svih njegovih metoda.Opis naredbe
 * dan je u nastavku:
 * 
 * <pre>
 * <i>Naredba koja se koristi kako bi se promijenili ili ispisali znakovi {@link MyShellEnvironment#PROMPT}, {@link MyShellEnvironment#MORELINES} ili {@link MyShellEnvironment#MULTILINE}.<i>,
 * <ul>
 * <li><i>{@link MyShellEnvironment#PROMPT}: znak koji se ispisuje prije nego korisnik upiše naredbu.</i></li>
 * <li><i>{@link MyShellEnvironment#MORELINES}: znak koji korisnik treba upisati ukoliko želi upisati višeredčanu naredbu.
 * Znak mora doći nakon svakog redka.</i></li>
 * <li><i>{@link MyShellEnvironment#MULTILINE}: znak koji će se ispisati prije svakog novog redka naredbe ukoliko je korisnik 
 * odlučio pisati višeredčanu naredbu.</i></li>
 * </ul>
 * <i>Ukoliko korisnik želi promijeniti znakove za {@link MyShellEnvironment#PROMPT}, 
 * {@link MyShellEnvironment#MORELINES} ili {@link MyShellEnvironment#MULTILINE}, </i>
 * mora unutar ove naredbe predati novi znak koji se nadalje poistovjećuje sa prvim argumentom.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 *
 * @author Davor Češljaš
 */
public class SymbolShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "symbol";

	/** Konstanta koja predstavlja da naredba ispisuje simbol */
	private static final int GET_SYMBOL = 1;

	/**
	 * Konstanta koja predstavlja da naredba mijenja simbol
	 */
	private static final int SET_SYMBOL = 2;

	/** Konstanta koja predstavlja poziciju na kojoj se nalazi ime simbola */
	private static final int SYMBOL_NAME_INDEX = 0;

	/**
	 * Konstanta koja predstavlja poziciju na kojoj se nalazi novi simbol
	 */
	private static final int NEW_SYMBOL_INDEX = 1;
	
	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] {
				"Naredba koja se koristi kako bi se promijenili ili ispisali znakovi PROMPT, MORELINES ili MULTILINE.",
				"\tPROMPT: znak koji se ispisuje prije nego korisnik upiše naredbu. Po defaultu jednak "
						+ MyShellEnvironment.DEFAULT_PROMPT_SYMBOL,
				"\tMORELINES: znak koji korisnik treba upisati ukoliko želi upisati višeredčanu naredbu.",
				"\tZnak mora doći nakon svakog redka. Po defaultu jednak "
						+ MyShellEnvironment.DEFAULT_MORELINES_SYMBOL,
				"\tMULTILINE: znak koji će se ispisati prije svakog novog redka naredbe ukoliko je korisnik odlučio pisati višeredčanu naredbu.",
				"\tPo defaultu jednak " + MyShellEnvironment.DEFAULT_MULTILINE_SYMBOL,
				"Ukoliko korisnik želi promijeniti znakove za PROMPT, MORELINES ili MULTILINES, ",
				"mora unutar ove naredbe predati novi znak koji se nadalje poistovjećuje sa prvim argumentom." 
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments == null) {
			env.writeln(
					"Naredbi 'symbol' potreban je minimalno jedan argument. Za više informacija upišite 'help symbol'");
		} else {
			List<String> separatedArguments = ShellCommandUtil.parseArguments(env, arguments);
			switch (separatedArguments.size()) {
			case GET_SYMBOL:
				writeSymbol(env, separatedArguments.get(SYMBOL_NAME_INDEX));
				break;
			case SET_SYMBOL:
				changeSymbol(env, separatedArguments);
				break;
			default:
				env.writeln("Predali ste pogrešan broj argumenata. Broj argumenata: " + separatedArguments.size());
				break;
			}
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja se korisit za promijenu simbola predstavljenog kao
	 * pozicija 0 unutar {@link List} <b>sepearatedArguments</b>. Simbol može
	 * biti jedan od sljedećih {@link MyShellEnvironment#PROMPT},
	 * {@link MyShellEnvironment#MORELINES} ili
	 * {@link MyShellEnvironment#MULTILINE}. Ukoliko simbol nije predstavljen sa
	 * jednim od navedenih, metoda će ispisati poruku o pogrešci. Ukoliko se
	 * pozivom metode {@link #extractNewSymbol(Environment, String)} ustvrdi
	 * ispravnost novog simbola, taj simbol postaje nova vrijednost koja se veže
	 * uz predano ime. Novi simbol se prenosi u <b>sepearatedArguments</b> na
	 * poziciji 1.
	 * 
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param separatedArguments
	 *            {@link List} separiranih ulaznih argumenata. U ovom slučaju
	 *            trebala bi se sastojati od točno dva argumenta
	 */
	private void changeSymbol(Environment env, List<String> separatedArguments) {
		Character newSymbol = extractNewSymbol(env, separatedArguments.get(NEW_SYMBOL_INDEX));
		if (newSymbol == null) {
			return;
		}

		String firstArgument = separatedArguments.get(SYMBOL_NAME_INDEX);
		Character oldSymbol = null;
		switch (firstArgument) {
		case MyShellEnvironment.PROMPT:
			oldSymbol = env.getPromptSymbol();
			env.setPromptSymbol(newSymbol);
			break;
		case MyShellEnvironment.MULTILINE:
			oldSymbol = env.getMultilineSymbol();
			env.setMultilineSymbol(newSymbol);
			break;
		case MyShellEnvironment.MORELINES:
			oldSymbol = env.getMorelinesSymbol();
			env.setMorelinesSymbol(newSymbol);
			break;
		default:
			env.writeln("Simbol '" + firstArgument + "' ne postoji!");
			return;
		}

		env.writeln(String.format("Simbol za %s promijenjen iz '%c' u '%c'", firstArgument, oldSymbol, newSymbol));
	}

	/**
	 * Pomoćna metoda koja je zadužena za vađenje novog simbola iz predanog
	 * primjerka razreda {@link String} <b>secondArgument</b>. Metoda će vratiti
	 * novi primjerak razreda {@link Character}, ako je {@link String#length()}
	 * jednak 1. Inače metoda vraća <code>null</code>
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualne pogreške
	 * @param secondArgument
	 *            argument iz kojeg se vadi novi simbol
	 * @return novi primjerak razreda {@link Character}, ako je
	 *         {@link String#length()} jednak 1. Inače metoda vraća
	 *         <code>null</code>
	 */
	private Character extractNewSymbol(Environment env, String secondArgument) {
		if (secondArgument.length() != 1) {
			env.writeln(String.format(
					"Predali ste niz znakova '%s' ,a očekivao sam jedan znak. Za više informacija upišite 'help symbol'",
					secondArgument));
			return null;
		}

		return secondArgument.charAt(0);
	}

	/**
	 * Pomoćna metoda koja se korisit ispis simbola predstavljenog kao primjerak
	 * razreda {@link String} <b>arguments</b>. Simbol može biti jedan od
	 * sljedećih {@link MyShellEnvironment#PROMPT},
	 * {@link MyShellEnvironment#MORELINES} ili
	 * {@link MyShellEnvironment#MULTILINE}. Ukoliko simbol nije predstavljen sa
	 * jednim od navedenih, metoda će ispisati poruku o pogrešci.
	 * 
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            simbola koji se veže uz jedno od imena simbola
	 * @param arguments
	 *            primjerak razreda {@link String} za koji se očekuje da je
	 *            jedan od: {@link MyShellEnvironment#PROMPT},
	 *            {@link MyShellEnvironment#MORELINES} ili
	 *            {@link MyShellEnvironment#MULTILINE}
	 */
	private void writeSymbol(Environment env, String arguments) {
		Character symbol = null;
		switch (arguments) {
		case MyShellEnvironment.PROMPT:
			symbol = env.getPromptSymbol();
			break;
		case MyShellEnvironment.MULTILINE:
			symbol = env.getMultilineSymbol();
			break;
		case MyShellEnvironment.MORELINES:
			symbol = env.getMorelinesSymbol();
			break;
		default:
			env.writeln("Simbol '" + arguments + "' ne postoji!");
			return;
		}
		env.writeln(String.format("Simbol za %s je '%c'", arguments, symbol));
	}

	@Override
	public String getCommandName() {
		return NAME;
	}

	@Override
	public List<String> getCommandDescription() {
		return DESCRIPTION;
	}

}
