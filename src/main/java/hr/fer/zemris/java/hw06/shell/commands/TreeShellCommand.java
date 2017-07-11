package hr.fer.zemris.java.hw06.shell.commands;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
 * <i>Naredba koja prima jedan ili niti jedan argument.</i>
 * <i>Predani argument tumači se kao putanja do direktorija čiji se sadržaj želi rekurzivno ispisati.</i>
 * <i>Ukoliko se ne preda argument naredba to tumači kao korisnikovu želju da se rekurzivno ispiše trenutni direktorij.</i>
 * <i>Postupak je isti ako se kao argument preda '.'</i>
 * <i>Ukoliko predani argument nije direktorij ispisuje se prigodna poruka i ljuska nastavlja s radom.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class TreeShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "tree";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba koja prima jedan ili niti jedan argument.",
				"Predani argument tumači se kao putanja do direktorija čiji se sadržaj želi rekurzivno ispisati.",
				"Ukoliko se ne preda argument naredba to tumači kao korisnikovu želju da se rekurzivno ispiše trenutni direktorij.",
				"Postupak je isti ako se kao argument preda '.'",
				"Ukoliko predani argument nije direktorij ispisuje se prigodna poruka i ljuska nastavlja s radom." 
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
			printFileTree(filePath, env);
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja stvara primjerak razreda
	 * {@link TreeCommandFileVisitor} i predaje ga metodi
	 * {@link Files#walkFileTree(Path, java.nio.file.FileVisitor)}. Predhodno
	 * tome određuje se apstraktna reprezentacija putanje {@link Path}
	 * predstavljena primjerkom razreda {@link String} koji predstavlja
	 * apsolutnu ili relativnu putanju do direktorija čiji se sadržaj rekurzivno
	 * obilazi i ispisuje.
	 *
	 * @param filePath
	 *            predstavljena primjerkom razreda {@link String} koji
	 *            predstavlja apsolutnu ili relativnu putanju do direktorija
	 *            čiji se sadržaj rekurzivno obilazi i ispisuje.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * 
	 * @see TreeCommandFileVisitor
	 * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
	 */
	private void printFileTree(Path filePath, Environment env) {
		try {
			Files.walkFileTree(filePath, new TreeCommandFileVisitor(env));
		} catch (IOException e) {
			env.writeln(String.format("Nisam u mogućnosti rekurzivno ispisati sadržaj direktorija '%s'",
					filePath.getFileName().toString()));
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

	/**
	 * Razred koji nasljeđuje apstraktni razred {@link SimpleFileVisitor}.
	 * Primjerak ovog razreda koristi se za formatirani ispis svakog direktorija
	 * i datoteke od početnog direkotorija. Svako dijete nekog direktorija
	 * nalazi se uvučeno sa dva razmaka naspram svog roditelja. Razred nadjačava
	 * metode:
	 * <ul>
	 * <li>{@link SimpleFileVisitor#visitFile(Object, BasicFileAttributes)}</li>
	 * <li>{@link SimpleFileVisitor#preVisitDirectory(Object, BasicFileAttributes)}</li>
	 * <li>{@link SimpleFileVisitor#postVisitDirectory(Object, IOException)}</li>
	 * </ul>
	 * 
	 * @see SimpleFileVisitor
	 * 
	 * @author Davor Češljaš
	 */
	private static class TreeCommandFileVisitor extends SimpleFileVisitor<Path> {

		/** Konstanta koja predstavlja točno jedan razmak */
		private static final String WHITESPACE = " ";

		/**
		 * Konstanta koja predstavlj koliko se znakova {@link #WHITESPACE}
		 * ispisuje za pojedino dijete ovisno o članskoj varijabli
		 * {@link #level}
		 */
		private static final int MULTIPLYING_FACTOR = 2;

		/**
		 * Članska varijabla koja predstavlja razinu na kojoj se trenutno nalazi
		 * primjerak ovog razreda. Razina se određuje relativno na početni
		 * direktorij (korijen stabla)
		 */
		private int level;

		/**
		 * članska varijabla koja sadrži referencu na primjerak sučelja
		 * {@link Environment} koji se koristi za ispis
		 */
		private Environment env;

		/**
		 * Konstruktor koji inicijalizira primjerak ovog razreda. Prilikom
		 * inicijalizacije interna referenca na primjerak sučelja
		 * {@link Environment} postavlja se na <b>env</b>
		 *
		 * @param env
		 *            primjerak sučelja {@link Environment} koji se koristi za
		 *            ispis
		 */
		public TreeCommandFileVisitor(Environment env) {
			this.env = env;
			// inicijalna razina
			level = 1;
		}

		/**
		 * Pomoćna metoda koja se koristi za formatirani ispis stabla.
		 * Apstraktna reprezentacija putanje {@link Path} do trenutne
		 * datoteke/direktorija koji se ispisuje <b>dir</b> nalazi se uvučeno sa
		 * dva razmaka naspram svog roditelja.
		 *
		 * @param dir
		 *            Apstraktna reprezentacija putanje {@link Path} do trenutne
		 *            datoteke/direktorija koji se ispisuje
		 */
		private void formatOutput(Path dir) {
			String spaces = String.format("%" + (level * MULTIPLYING_FACTOR) + "s", WHITESPACE);
			env.writeln(spaces + dir.getFileName().toString());
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			formatOutput(dir);
			level++;
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			formatOutput(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			level--;
			return FileVisitResult.CONTINUE;
		}

	}
}
