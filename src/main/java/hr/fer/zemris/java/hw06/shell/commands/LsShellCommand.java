package hr.fer.zemris.java.hw06.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
 * <i>Naredba koja izlistava sve datoteke i direktorije unutar predanog direktorija.</i>
 * <i>Ukoliko se direktorij ne preda ili se preda '.' , kao direktorij koji se izlistava uzima se trenutni direktorij</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class LsShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "ls";

	/** Konstanta koja predstavlja jedan razmak. */
	private static final String WHITESPACE = " ";

	/** Konstanta koja predstavlja da je putanja direktorij */
	private static final String DIRECTORY = "d";

	/** Konstanta koja predstavlja da je putanja čitljiva */
	private static final String READ = "r";

	/** Konstanta koja predstavlja da se u putanju može pisati */
	private static final String WRITE = "w";

	/** Konstanta koja predstavlja da je putanja izvršiva */
	private static final String EXECUTE = "x";

	/**
	 * Konstanta koja predstavlja negaciju teza {@value #EXECUTE},
	 * {@value #READ}, {@value #WRITE} ili {@value #DIRECTORY}
	 */
	private static final String NOT = "-";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] {
				"Naredba koja izlistava sve datoteke i direktorije unutar predanog direktorija.",
				"Ukoliko se direktorij ne preda ili se preda '.' , kao direktorij koji se izlistava uzima se trenutni direktorij" 
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		Path filePath = ShellCommandUtil.extractExistingDirectory(env, arguments);
		if (filePath != null) {
			listDirectory(env, filePath);
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja izlistava sadržaj direktorija predstavljenog
	 * primjerkom razreda {@link String} koji predstavlja apsolutnu ili
	 * relativnu putanju do direktorija. Za formatirani ispis svakog djeteta
	 * koristi se metoda
	 * {@link #formatOutput(Path, SimpleDateFormat, Environment)}.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param filePath
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do direktrorija čiji se sadržaj
	 *            izlistava
	 */
	private void listDirectory(Environment env, Path filePath) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Files.list(filePath).forEach(path -> formatOutput(path, sdf, env));
		} catch (IOException e) {
			env.writeln("Ne mogu izlistati direktorij " + filePath.toString());
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za formatirani ispis svakog od djeteta.
	 * Dijete je predstavljeno abstraktnom reprezentacijom putanje {@link Path}
	 * <b>path</b>. Na početku se ispisuje 'd' ako putanja predstavlja
	 * direktorij, 'r' ako je direktorij/datoteka čitljiva, 'w' ako se u
	 * datoteku/direktorij može pisati i 'x' ako se datoteka/direktorij može
	 * isvršiti na računalu. Ukoliko neka od navedenih tvrdnji nije točna
	 * ispisuje se '-' na tom mjestu. Nakon toga slijedi ispis veličine
	 * datoteke/direktorija. Potom slijedi datum stvaranja i ime
	 * datoteke/direktorija. Ispis metode ovisi o metodi
	 * {@link ShellCommandUtil#getFileAttributes(Path, Environment)}
	 *
	 * @param path
	 *            abstraktna reprezentacija putanje {@link Path} do pojedinog
	 *            djeteta
	 * @param sdf
	 *            primjerak razreda {@link SimpleDateFormat} koji predstavlja
	 *            format datuma i vremena, koji se treba ispisati datum i
	 *            vrijeme stvaranja datoteke/direktorija
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * 
	 * @see ShellCommandUtil#getFileAttributes(Path, Environment)
	 */
	private void formatOutput(Path path, SimpleDateFormat sdf, Environment env) {
		BasicFileAttributes attributes = ShellCommandUtil.getFileAttributes(path, env);
		if (attributes == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		File file = path.toFile();
		// dopuštenja
		sb.append(attributes.isDirectory() ? DIRECTORY : NOT).append(file.canRead() ? READ : NOT)
				.append(file.canWrite() ? WRITE : NOT).append(file.canExecute() ? EXECUTE : NOT).append(WHITESPACE);
		// veličina datoteke
		sb.append(String.format("%10d ", attributes.size()));
		// vrijeme stvaraja
		sb.append(sdf.format(new Date(attributes.creationTime().toMillis()))).append(WHITESPACE);
		// naziv
		sb.append(file.getName());
		env.writeln(sb.toString());
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
