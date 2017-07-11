package hr.fer.zemris.java.hw06.shell.commands;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * <i>Naredba koja prima točno dva argumenta.</i>
 * <i>Prvi argument predstavlja datoteku koju kopiramo.</i>
 * <i>Drugi argument predstavlja ili direktorij u koji će datoteka biti kopirana sa istim imenom</i>
 * <i>ili punu putanju do kopirane datoteke zajedno s nazivom kopije datoteke.</i>
 * <i>Ukoliko ne postoji datoteka ili direktorij u prvom slučaju program će izbaciti odgovarajuću poruku.</i>
 * <i>Ukoliko u direktoriju već postoji datoteka sa tim imenom ljuska će pitati želite li prebrisati postojeću datoteku</i>
 * <i>Ukoliko je Vaš odgovor 'ne' kopija se neće stvoriti, a ljuska će nastaviti s radom.</i>
 * <i>Ukoliko je Vaš odgovor 'da' naredba će prebrisati sadržaj postojeće datoteke i zamijeniti ga novim.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class CopyShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "copy";

	/**
	 * Konstanta koja predstavlja broj argumenata potrebnih za normalno izvođnje
	 * ove naredbe.
	 */
	private static final int ARGUMENTS_SIZE = 2;

	/** Konstanta koja predstavlja poziciju prvog argumenta naredbe */
	private static final int FIRST_ARGUMENT_INDEX = 0;

	/** Konstanta koja predstavlja poziciju drugog argumenta naredbe */
	private static final int SECOND_ARGUMENT_INDEX = 1;

	/** Konstanta koja predstavlja potvrdan odgovor */
	private static final String YES = "da";

	/** Konstanta koja predstavlja ne potvrdan odgovor */
	private static final String NO = "ne";

	/**
	 * Konstanta koja predstavlja veličinu pomoćnog spremnika
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba koja prima točno dva argumenta.",
				"Prvi argument predstavlja datoteku koju kopiramo.",
				"Drugi argument predstavlja ili direktorij u koji će datoteka biti kopirana sa istim imenom.",
				"ili punu putanju do kopirane datoteke zajedno s nazivom kopije datoteke",
				"Ukoliko ne postoji datoteka ili direktorij u prvom slučaju program će izbaciti odgovarajuću poruku.",
				"Ukoliko u direktoriju već postoji datoteka sa tim imenom ljuska će pitati želite li prebrisati postojeću datoteku",
				"Ukoliko je Vaš odgovor 'ne' kopija se neće stvoriti, a ljuska će nastaviti s radom.",
				"Ukoliko je Vaš odgovor 'da' naredba će prebrisati sadržaj postojeće datoteke i zamijeniti ga novim."
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments == null) {
			env.writeln("Potrebno je predati točno dva argumenta. Za više informacija upišite 'help copy'");
		} else {
			List<String> separatedArguments = ShellCommandUtil.parseArguments(env, arguments);
			if (separatedArguments.size() == ARGUMENTS_SIZE) {
				attemptToCopy(env, separatedArguments.get(FIRST_ARGUMENT_INDEX),
						separatedArguments.get(SECOND_ARGUMENT_INDEX));
			} else {
				env.writeln("Predali se netočan broj argumenata.Vaš broj argumenata: " + separatedArguments.size()
						+ " .Za više informacija upišite 'help cat'");
			}
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda pokušava kopirati datoteku. Ukoliko ne postoji datoteka
	 * ili direktorij u prvom slučaju metoda će izbaciti odgovarajuću poruku.
	 * Ukoliko u direktoriju već postoji datoteka sa tim imenom metoda će
	 * pozvati pomoćnu metodu {@link #acceptOverwriteUI(Environment)}. Ovisno o
	 * korisnikovu odgovoru metoda će pozvati pomoćnu metodu
	 * {@link #copy(Environment, Path, Path)}.
	 *
	 * 
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param filePathName
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do datoteke koja se kopira
	 * @param pathName
	 *            primjerak razreda {@link String} koji predstavlja apsolutnu
	 *            ili relativnu putanju do datoteke ili direktorija u koji se
	 *            kopira sadržaj datoteke predstavljene s <b>filePathName</b>
	 */
	private void attemptToCopy(Environment env, String filePathName, String pathName) {
		Path filePath = ShellCommandUtil.extractSingleFile(env, filePathName);
		if (filePath == null) {
			return;
		}

		Path dirOrFile = ShellCommandUtil.extractSinglePath(env, pathName);
		if (dirOrFile == null) {
			return;
		}

		if (Files.isDirectory(dirOrFile, LinkOption.NOFOLLOW_LINKS)) {
			dirOrFile = Paths.get(dirOrFile.toString(), filePath.getFileName().toString());
		}

		if (Files.isRegularFile(dirOrFile, LinkOption.NOFOLLOW_LINKS)) {
			if (!acceptOverwriteUI(env)) {
				return;
			}
		}
		copy(env, filePath, dirOrFile);
	}

	/**
	 * Pomoćna metoda koja ispituje korisnika želi li prebrisati sadržaj
	 * postojeće datoteke unutar direktorija. Ukoliko je odgovor potvrdan metoda
	 * vraća <b>true</b> ukoliko odgovor nije potvrdan metoda vraća
	 * <b>false</b>. Metoda se izvodi dok god se ne preda pravovaljani odgovor.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            i unos odgovora
	 * @return Ukoliko je odgovor potvrdan <b>true</b> ukoliko odgovor nije
	 *         potvrdan <b>false</b>
	 */
	private boolean acceptOverwriteUI(Environment env) {
		while (true) {
			env.write("Želite li prebrisati datoteku na odredištu?[da/ne]: ");
			String answer = env.readLine();
			if (answer == null) {
				continue;
			}
			if (answer.equalsIgnoreCase(YES)) {
				return true;
			}
			if (answer.equalsIgnoreCase(NO)) {
				return false;
			}
		}
	}

	/**
	 * Pomoćna metoda koja vrši samo kopiranje datoteke predstavljene primjerkom
	 * razreda {@link Path} <b>filePath</b> u datoteku predstavljenu primjerkom
	 * razreda {@link Path} <b>copyPath</b>. Za ispis pogrešaka koristi se
	 * predani primjerak sučelja {@link Environment} <b>env</b>
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            i unos odgovora
	 * @param filePath
	 *            primjerak razreda {@link Path} koji predstavlja apstraktnu
	 *            reprezentaciju putanje do datoteke koja se kopira
	 * @param copyPath
	 *            primjerak razreda {@link Path} koji predstavlja apstraktnu
	 *            reprezentaciju putanje do datoteke u koju se kopira
	 */
	private void copy(Environment env, Path filePath, Path copyPath) {
		try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(filePath));
				BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(copyPath))) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			while ((length = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
				bos.flush();
			}
			env.writeln("Datoteka je uspješno kopirana u '" + copyPath.getParent().toRealPath(LinkOption.NOFOLLOW_LINKS)
					+ "'");
		} catch (IOException e) {
			env.writeln("Nemam dopuštenje operacijskog sustava za kopiranje predane datoteke!");
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
