package hr.fer.zemris.java.hw06.shell.commands;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import hr.fer.zemris.java.hw06.shell.Environment;
import hr.fer.zemris.java.hw06.shell.ShellCommand;
import hr.fer.zemris.java.hw06.shell.ShellStatus;
import hr.fer.zemris.java.hw06.shell.util.ShellCommandUtil;

/**
 * Razred koji predstavlja naredbu {@value #NAME}. Razred implementira sučelje
 * {@link ShellCommand} i nudi implementaciju svih njegovih metoda.Opis naredbe
 * dan je u nastavku:
 * 
 * <pre>
 * <i>Naredba koja prima točno jedan argument.</i>
 * <i>Taj argument mora biti valjana putanja do datoteke.</i>
 * <i>Naredba će u istom retku ispisati 16 okteta u heksadekadskom obliku, </i>
 * <i>a odmah pored toga ispisati će ispisati što ti okteti predstavljaju u ASCII tablici.</i>
 * <i>Svi znakovi koji nisu unutar ASCII tablice između 32 i 127 pozicije biti će zamijenjeni s '.' </i>
 * <i>Izvršavanjem ove naredbe ljuska nastavlja sa radom.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class HexdumpShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "hexdump";

	/**
	 * Konstanta koja predstavlja broj argumenata potrebnih za normalno izvođnje
	 * ove naredbe.
	 */
	private static final int ARGUMENTS_SIZE = 1;

	/** Konstanta koja predstavlja poziciju jedinog argumenta naredbe */
	private static final int ARGUMENT_INDEX = 0;

	/**
	 * Konstanta koja predstavlja veličinu pomoćnog spremnika
	 */
	private static final int BUFFER_SIZE = 16;

	/**
	 * Konstanta koja predstavlja jedan razmak
	 */
	private static final String WHITESPACE = " ";

	/**
	 * Konstanta koja predstavlja točno dva razmaka
	 */
	private static final String TWO_WHITESPACES = "  ";

	/**
	 * Konstanta koja predstavlja razdjeljivač između pojedinih dijelova
	 * formatiranog ispisa
	 */
	private static final String SPLITTER = "|";
	
	/**
	 * Konstanta koja predstavlja točku
	 */
	private static final String DOT = ".";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba koja prima točno jedan argument.",
				"Taj argument mora biti valjana putanja do datoteke.",
				"Naredba će u istom retku ispisati 16 okteta u heksadekadskom obliku, ",
				"a odmah pored toga ispisati će ispisati što ti okteti predstavljaju u ASCII tablici.",
				"Svi znakovi koji nisu unutar ASCII tablice između 32 i 127 pozicije biti će zamijenjeni s '.' ",
				"Izvršavanjem ove naredbe ljuska nastavlja sa radom." 
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
					"Potrebno je predati točno jedan argument (putanju do datoteke). Za više informacija upišite 'help hexdump'");
		} else {
			List<String> separatedArguments = ShellCommandUtil.parseArguments(env, arguments);
			if (separatedArguments.size() == ARGUMENTS_SIZE) {
				readFile(env, separatedArguments.get(ARGUMENT_INDEX));
			} else {
				env.writeln("Predali ste pogrešan broj argumenata. Tražio sam 1 ,a Vi ste predali "
						+ separatedArguments.size());
			}
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja vrši čitanje iz datoteke te za svakih 16 okteta
	 * poziva metodu {@link #formatedOutput(Environment, int, int, byte[])}.
	 * Ukoliko se iz datoteke ne može čitati metoda će ispisati odgovarajuću
	 * poruku. Za ispis se koristi primjerak sučelja {@link Environment}.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param fileName
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do datoteke iz koje čitamo oktete
	 */
	private void readFile(Environment env, String fileName) {
		Path filePath = ShellCommandUtil.extractSingleFile(env, fileName);
		if (filePath == null) {
			return;
		}

		try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(filePath))) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			int row = 0;
			while ((length = bis.read(buffer)) != -1) {
				formatedOutput(env, row, length, buffer);
				row++;
			}
		} catch (IOException e) {
			env.writeln(String.format("Iz datoteke '%s' se ne može čitati!", fileName));
		}

	}

	/**
	 * Pomoćna metoda koja se koristi za formatirani ispis maksimalno 16 okteta
	 * u jednom redku. Svaki redak sadrži broj poziciju okteta u datoteci od
	 * koje se redak ispisuje. Dodatno svaki redak sadrži 2 grupe po 8 okteta u
	 * heksadekadskom obliku koji predstavljaju vrijednost znakova unutar ASCII
	 * tablice. Nakon ispisa okteta slijede konkretni znakovi. Znakovi koji
	 * reprezentiraju sami sebe nalaze se unutar raspona [32,127] u ASCII
	 * tablici. Svi ostali znakovi van tog raspona reprezentirani su sa '.'
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param row
	 *            broj redka koji se ispisuje
	 * @param length
	 *            broj pročitanih znakova iz datoteke <= 16
	 * @param buffer
	 *            pomoćni spremnik iz kojeg se čitaju pročitani okteti iz
	 *            datoteke
	 */
	private void formatedOutput(Environment env, int row, int length, byte[] buffer) {
		StringJoiner rowJoiner = new StringJoiner(WHITESPACE);
		rowJoiner.add(String.format("%08x:", row * buffer.length));
		StringBuilder textBuilder = new StringBuilder();
		for (int i = 0; i < buffer.length; i++) {
			if (i < length) {
				rowJoiner.add(String.format("%02X", buffer[i]));
				byte b = buffer[i];
				if (b >= 32 && b <= 127) {
					textBuilder.append((char) b);
				} else {
					textBuilder.append(DOT);
				}
			} else {
				rowJoiner.add(TWO_WHITESPACES);
			}
			if (i == (buffer.length / 2 - 1)) {
				rowJoiner.add(SPLITTER);
			}
		}
		rowJoiner.add(SPLITTER).add(textBuilder.toString());
		env.writeln(rowJoiner.toString());
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
