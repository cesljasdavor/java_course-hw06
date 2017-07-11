package hr.fer.zemris.java.hw06.shell.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
 * <i>Naredba prima točno jedan argument.</i>
 * <i>Taj argument mora biti valjana putanja do direktorija koji će se stvoriti.</i>
 * <i>Uz krajnji direktorij stvaraju se i svi ostali roditeljski direktoriji predani u putanji.</i>
 * <i>Ukoliko naredba nema dopuštenje stvoriti direktorij u određenom roditeljskom direktoriju ispisati će se odgovarajuća poruka</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class MkdirShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "mkdir";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba prima točno jedan argument.",
				"Taj argument mora biti valjana putanja do direktorija koji će se stvoriti.",
				"Uz krajnji direktorij stvaraju se i svi ostali roditeljski direktoriji predani u putanji.",
				"Ukoliko naredba nema dopuštenje stvoriti direktorij u određenom roditeljskom direktoriju ispisati će se odgovarajuća poruka" 
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments != null) {
			makeDirectory(arguments, env);
		} else {
			env.writeln("Naredba 'mkdir' mora primiti točno jedan argument. Za više informacija upišite 'help mkdir'");
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja se koristi za stvaranje direktorija u datotečnom
	 * sustavu. Predani argument <b>arguments</b> primjerak razreda
	 * {@link String} koji predstavlja apsolutnu ili relativnu putanju do
	 * direktorija koji treba stvoriti i samo ime novog direktorija. Također je
	 * moguće da neki direktoriji u samoj putanji ne postoje te će i oni usputno
	 * biti stvoreni. Direktorij se stvara pozivom
	 * {@link Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)}.
	 * Sama metoda apstraktnu reprezentaciju putanje {@link Path} stvara pomoću
	 * {@link ShellCommandUtil#extractSinglePath(Environment, String)}.
	 *
	 * @param arguments
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do direktorija koji treba stvoriti i
	 *            samo ime novog direktorija.
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * 
	 * @see Files#createDirectories(Path,
	 *      java.nio.file.attribute.FileAttribute...)
	 * @see ShellCommandUtil#extractSinglePath(Environment, String)
	 */
	private void makeDirectory(String arguments, Environment env) {
		Path path = ShellCommandUtil.extractSinglePath(env, arguments);
		if (path == null) {
			return;
		} else if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			env.writeln(String.format("Direktorij '%s' već postoji", path));
			return;
		}

		try {
			Files.createDirectories(path);
			env.writeln(String.format("Direktorij '%s' izrađen", path));
		} catch (IOException e) {
			env.writeln("Nemam dopuštenje operacijskog sustava za izradu direktorija " + path);
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
