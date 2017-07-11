package hr.fer.zemris.java.hw06.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import hr.fer.zemris.java.hw06.shell.commands.CatShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.CharsetsShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.CopyShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.ExitShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.HelpShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.HexdumpShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.LsShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.MkdirShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.SymbolShellCommand;
import hr.fer.zemris.java.hw06.shell.commands.TreeShellCommand;

/**
 * Razred koji implementira sučelje {@link Environment} i sve njegove metode.
 * Razred se korisi kao okruženje programa {@link MyShell}. Kao vrijednosti za
 * MULTILINE, PROMPT i MORELINES znakove po defaultu se koriste '|', '>' i '\\'.
 * 
 * Čemu služe simboli propisano je unutar sučelja {@link Environment}
 * 
 * @see Environment
 * @see MyShell
 * 
 * @author Davor Češljaš
 */
public class MyShellEnvironment implements Environment {

	/**
	 * Konstanta koja predstavlja defaultni MULTILINE simbol
	 * 
	 * @see {@link Environment}
	 */
	public static final Character DEFAULT_MULTILINE_SYMBOL = '|';

	/**
	 * Konstanta koja predstavlja defaultni PROMPT simbol
	 * 
	 * @see {@link Environment}
	 */
	public static final Character DEFAULT_PROMPT_SYMBOL = '>';

	/**
	 * Konstanta koja predstavlja defaultni MORELINES simbol
	 * 
	 * @see {@link Environment}
	 */
	public static final Character DEFAULT_MORELINES_SYMBOL = '\\';

	/** Konstanta koja predstavlja niz znakova "PROMPT" */
	public static final String PROMPT = "PROMPT";

	/** Konstanta koja predstavlja niz znakova "MULTILINE" */
	public static final String MULTILINE = "MULTILINE";

	/** Konstanta koja predstavlja niz znakova "MORELINES" */
	public static final String MORELINES = "MORELINES";

	/**
	 * Nepromijenjiva referenca na {@link SortedMap} svih naredbi podržanih
	 * unutar programa {@link MyShell}. Svaki unos mape mapiran je po nazivu
	 * naredbe.
	 */
	private static final SortedMap<String, ShellCommand> commands;

	static {
		SortedMap<String, ShellCommand> initCommands = new TreeMap<>();
		initCommands.put(HelpShellCommand.NAME, new HelpShellCommand());
		initCommands.put(ExitShellCommand.NAME, new ExitShellCommand());
		initCommands.put(SymbolShellCommand.NAME, new SymbolShellCommand());
		initCommands.put(CharsetsShellCommand.NAME, new CharsetsShellCommand());
		initCommands.put(CatShellCommand.NAME, new CatShellCommand());
		initCommands.put(LsShellCommand.NAME, new LsShellCommand());
		initCommands.put(TreeShellCommand.NAME, new TreeShellCommand());
		initCommands.put(MkdirShellCommand.NAME, new MkdirShellCommand());
		initCommands.put(CopyShellCommand.NAME, new CopyShellCommand());
		initCommands.put(HexdumpShellCommand.NAME, new HexdumpShellCommand());

		commands = Collections.unmodifiableSortedMap(initCommands);
	}

	/** Članska varijabla koja predstavlja referencu na ulazni tok znakova */
	private BufferedReader reader;

	/** Članska varijabla koja predstavlja referencu na izlazni tok znakova */
	private BufferedWriter writer;

	/**
	 * Članska varijabla koja predstavlja trenutnu vrijednost koja se
	 * poistovjećuje sa {@value #MULTILINE}
	 * 
	 * @see Environment
	 */
	private Character multilineSymbol;

	/**
	 * Članska varijabla koja predstavlja trenutnu vrijednost koja se
	 * poistovjećuje sa {@value #PROMPT}
	 * 
	 * @see Environment
	 */
	private Character promptSymbol;

	/**
	 * Članska varijabla koja predstavlja trenutnu vrijednost koja se
	 * poistovjećuje sa {@value #MORELINES}
	 * 
	 * @see Environment
	 */
	private Character morelinesSymbol;

	/**
	 * Konstruktor koji inicijalizira primjerak ovog razreda. Konstruktor
	 * postavlja članske varijable {@link #promptSymbol},
	 * {@link #multilineSymbol} i {@link #morelinesSymbol} na defaultne
	 * vrijednosti {@link #DEFAULT_PROMPT_SYMBOL},
	 * {@link #DEFAULT_MULTILINE_SYMBOL} i {@link #DEFAULT_MORELINES_SYMBOL}.
	 * 
	 * Konstruktor također stvara primjerke razreda {@link BufferedReader} sa
	 * {@link System#in} i {@link BufferedWriter} sa {@link System#out} te ih
	 * pridjeljuje članskim varijablama. Charset koji se koristi je
	 * {@link StandardCharsets#UTF_8}
	 */
	public MyShellEnvironment() {
		reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

		multilineSymbol = DEFAULT_MULTILINE_SYMBOL;
		promptSymbol = DEFAULT_PROMPT_SYMBOL;
		morelinesSymbol = DEFAULT_MORELINES_SYMBOL;
	}

	@Override
	public String readLine() throws ShellIOException {
		try {
			String line = reader.readLine();
			if (line == null || line.trim().isEmpty()) {
				return null;
			}
			return line.trim();
		} catch (IOException e) {
			throw new ShellIOException("Ne mogu čitati iz ulaznog toka!");
		}
	}

	@Override
	public void write(String text) throws ShellIOException {
		try {
			if (text != null) {
				writer.write(text);
				writer.flush();
			}
		} catch (IOException e) {
			throw new ShellIOException("Ne mogu pisati u izlazni tok!");
		}
	}

	@Override
	public void writeln(String text) throws ShellIOException {
		write(text + "\n");
	}

	@Override
	public SortedMap<String, ShellCommand> commands() {
		return commands;
	}

	@Override
	public Character getMultilineSymbol() {
		return multilineSymbol;
	}

	@Override
	public void setMultilineSymbol(Character symbol) {
		if (symbol != null) {
			multilineSymbol = symbol;
		}
	}

	@Override
	public Character getPromptSymbol() {
		return promptSymbol;
	}

	@Override
	public void setPromptSymbol(Character symbol) {
		if (symbol != null) {
			promptSymbol = symbol;
		}
	}

	@Override
	public Character getMorelinesSymbol() {
		return morelinesSymbol;
	}

	@Override
	public void setMorelinesSymbol(Character symbol) {
		if (symbol != null) {
			morelinesSymbol = symbol;
		}
	}
}
