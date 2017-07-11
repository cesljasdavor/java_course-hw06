package hr.fer.zemris.java.hw06.shell.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 * <i>Naredba koja prima jedan ili dva argumenta.</i>
 * <i>Prvi argument predstavlja datoteku koja se treba ispisati.</i>
 * <i>Drugi argument koji je opcionalan predstavlja skup znakova koje je potrebno koristiti za ispis.</i>
 * <i>Ukoliko takav skup znakova ne postoji naredba javlja poruku o pogrešci.</i>
 * <i>Ukoliko se drugi argument ne preda datoteka se ispisuje sa defaultnim skupom znakova.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class CatShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "cat";

	/** Konstanta koja predstavlja izvođenje uz defaultni skup znakova */
	private static final int CAT_WITH_DEFAULT_CHARSET = 1;

	/**
	 * Konstanta koja predstavlja izvođenje uz skup znakova koji nije defaultni
	 */
	private static final int CAT_WITH_CUSTOM_CHARSET = 2;

	/** Konstanta koja predstavlja poziciju na kojoj se nalazi ime datoteke */
	private static final int FILE_INDEX = 0;

	/**
	 * Konstanta koja predstavlja poziciju na kojoj se nalazi ime skupa znakova
	 */
	private static final int CHARSET_INDEX = 1;

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba koja prima jedan ili dva argumenta.",
				"Prvi argument predstavlja datoteku koja se treba ispisati.",
				"Drugi argument koji je opcionalan predstavlja skup znakova koje je potrebno koristiti za ispis.",
				"Ukoliko takav skup znakova ne postoji naredba javlja poruku o pogrešci.",
				"Ukoliko se drugi argument ne preda datoteka se ispisuje sa defaultnim skupom znakova."
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
					"Potrebno je predati minimalno jedan argument (putanju do datoteke). Za više informacija upišite 'help cat'");
		} else {
			List<String> separatedArguments = ShellCommandUtil.parseArguments(env, arguments);
			switch (separatedArguments.size()) {
			case CAT_WITH_DEFAULT_CHARSET:
				catFile(env, separatedArguments.get(FILE_INDEX), Charset.defaultCharset().name());
				break;
			case CAT_WITH_CUSTOM_CHARSET:
				catFile(env, separatedArguments.get(FILE_INDEX), separatedArguments.get(CHARSET_INDEX));
				break;
			default:
				env.writeln("Predali se netočan broj argumenata.Vaš broj argumenata: " + separatedArguments.size()
						+ " .Za više informacija upišite 'help cat'");
				break;
			}
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja služi za ispis datoteke. Metoda će za ispis koristiti
	 * primjerak sučelja {@link Environment} <b>env</b> koji joj je predan i
	 * njegovu metodu {@link Environment#writeln(String)}. Metoda također prima
	 * i primjerak razreda {@link String} <b>filePath</b> koji predstavlja
	 * apsolutnu ili relativnu putanju do datoteke koja se ispisuje. Kao dodatan
	 * argument metoda prima i naziv skup znakova <b>charsetName</b> koji se
	 * treba koristiti za ispis.
	 *
	 * @param env
	 *            primjerak sučelju {@link Environment} koji se koristi za ispis
	 * @param fileName
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do datoteke koja se ispisuje
	 * @param charsetName
	 *            naziv skup znakova koji se treba koristiti za ispis.
	 * 
	 * @see Environment#writeln(String)
	 */
	private void catFile(Environment env, String fileName, String charsetName) {
		Path filePath = ShellCommandUtil.extractSingleFile(env, fileName);
		if (filePath == null) {
			return;
		}
		try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.forName(charsetName))) {
			while (reader.ready()) {
				env.writeln(reader.readLine());
			}
		} catch (RuntimeException e) {
			env.writeln(String.format(
					"Vaša Javina platforma ne podržava skup znakova '%s'. Koji skupovi su podržani možete dobiti ako upišete 'charsets'",
					charsetName));
		} catch (IOException e) {
			env.writeln(String.format("Iz datoteke '%s' se ne može čitati!", fileName));
		}
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
