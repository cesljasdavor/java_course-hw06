package hr.fer.zemris.java.hw06.shell.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hr.fer.zemris.java.hw06.shell.Environment;
import hr.fer.zemris.java.hw06.shell.ShellCommand;
import hr.fer.zemris.java.hw06.shell.ShellStatus;

/**
 * Razred koji predstavlja naredbu {@value #NAME}. Razred implementira sučelje
 * {@link ShellCommand} i nudi implementaciju svih njegovih metoda.Opis naredbe
 * dan je u nastavku:
 * 
 * <pre>
 * <i>Naredba koja se koristi kako bi se izašlo iz ljuske.</i>
 * <i>Naredba ne prima dodatne argumente, te ukoliko se isti upišu ljuska neće biti terminirana!</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class ExitShellCommand implements ShellCommand {

	/** Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "exit";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba koja se koristi kako bi se izašlo iz programa MyShell.",
				"Naredba ne prima dodatne argumente, te ukoliko se isti upišu ljuska neće biti terminirana! "
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#TERMINATE} ako se ne preda argument,
	 *         {@link ShellStatus#CONTINUE} inače
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments == null) {
			return ShellStatus.TERMINATE;
		} else {
			env.writeln("Naredba 'exit' ne prima dodatne argumente. Za više informacija upišite 'help exit'");
			return ShellStatus.CONTINUE;
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
