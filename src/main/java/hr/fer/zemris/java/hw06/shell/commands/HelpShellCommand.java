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
 * <i>Naredba koja se koristi za ispis opisa naredbi koje su podržane u okviru ljuske na kojoj se ova naredba izvodi.</i>
 * <i>Naredba može primiti najviše jedan argument. Taj argument mora biti ime jedne od podržanih naredbi. </i>
 * <i>Ukoliko naredba ne primi argument tada ispisuje opise svih pojedinih naredbi podržanih u okviru ove ljuske.</i>
 * <i>Po završetku ispisa ljuska nastavlja s normalnim izvođenjem programa.</i>
 * </pre>
 *
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class HelpShellCommand implements ShellCommand {

	/**  Konstanta koja predstavlja naziv naredbe. */
	public static final String NAME = "help";

	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe.
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] {
				"Naredba koja se koristi za ispis opisa naredbi koje su podržane u okviru ljuske na kojoj se ova naredba izvodi.",
				"Naredba može primiti najviše jedan argument. Taj argument mora biti ime jedne od podržanih naredbi. ",
				"Ukoliko naredba ne primi argument tada ispisuje opise svih pojedinih naredbi podržanih u okviru ove ljuske.",
				"Po završetku ispisa ljuska nastavlja s normalnim izvođenjem programa." 
		};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}
	
	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if (arguments != null) {
			writeSingleCommandDescription(env, arguments.trim().toLowerCase());
		} else {
			writeAllCommandsDescription(env);
		}
		return ShellStatus.CONTINUE;
	}

	/**
	 * Pomoćna metoda koja pomoću predanog argumenta <b>env</b> ispisuje sve
	 * moguće naredbe i njihove opise podržane unutar ljuske u kojoj se ova
	 * naredba izvodi. Pomoćna metoda za svaku naredbu poziva pomoćnu metodu
	 * {@link #writeSingleCommandDescription(Environment, String)} sa imenom te
	 * naredbe.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 */
	private void writeAllCommandsDescription(Environment env) {
		for (ShellCommand command : env.commands().values()) {
			writeCommandDescription(env, command);
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za ispis imena i opisa naredbe čiji je
	 * naziv predan unutar primjerka razreda {@link String} <b>arguments</b>. Za
	 * ispis svakog redka opisa naredbe korisit se metoda
	 * {@link #writeCommandDescription(Environment, ShellCommand)}.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param arguments
	 *            naziv naredbe čiji se opis želi ispisati
	 */
	private void writeSingleCommandDescription(Environment env, String arguments) {
		ShellCommand command = env.commands().get(arguments);
		if (command == null) {
			env.writeln(String.format("Ne postoji naredba '%s'. Za više informacija upišite 'help'", arguments));
			return;
		}

		writeCommandDescription(env, command);
	}

	/**
	 * Pomoćna metoda koja ispisuje svaki redak opisa predane naredbe koja je
	 * oblikovana sučeljem {@link ShellCommand}. Za ispis se koristi metoda
	 * predanog primjerka sučelja {@link Environment}.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 * @param command
	 *            referenca na konkretnu naredbu oblikovanu sučelje
	 *            {@link ShellCommand}
	 */
	private void writeCommandDescription(Environment env, ShellCommand command) {
		env.writeln(command.getCommandName());
		command.getCommandDescription().forEach(line -> env.writeln("\t" + line));
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
