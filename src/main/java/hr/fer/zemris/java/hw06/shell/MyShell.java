package hr.fer.zemris.java.hw06.shell;

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
 * Razred koji predstavlja program koji predstavlja implementaciju ljuske.
 * Ljuska za čitanje i pisanje koristi okruženje koje je primjerak razreda
 * {@link MyShellEnvironment}. Naredbe koje su podržane oblikovane su sučeljem
 * {@link ShellCommand}. Konkretne naredbe su sljedeće:
 * <ul>
 * <li>{@link CatShellCommand}</li>
 * <li>{@link CharsetsShellCommand}</li>
 * <li>{@link CopyShellCommand}</li>
 * <li>{@link ExitShellCommand}</li>
 * <li>{@link HelpShellCommand}</li>
 * <li>{@link HexdumpShellCommand}</li>
 * <li>{@link LsShellCommand}</li>
 * <li>{@link MkdirShellCommand}</li>
 * <li>{@link SymbolShellCommand}</li>
 * <li>{@link TreeShellCommand}</li>
 * </ul>
 * 
 * Savjetuje se korisniku ove ljuske upis 'help' kako bi detaljnije proučio što
 * pojedina naredba radi ili odlazak na službene dokumentacije priložene u
 * gornjim linkovima
 * 
 * @see ShellCommand
 * @see MyShellEnvironment
 * 
 * @author Davor Češljaš
 */
public class MyShell {

	/**
	 * Metoda od koje započinje izvođenje ovog programa
	 *
	 * @param args
	 *            argumenti naredbenog redka. Unutar ovog programa se ne koriste
	 */
	public static void main(String[] args) {
		Environment env = new MyShellEnvironment();

		env.writeln("Dobrodošli u ljusku MyShell v 1.0");
		ShellStatus status = ShellStatus.CONTINUE;
		do {
			String line = readAndAppendAllLines(env);
			if (line.isEmpty()) {
				continue;
			}

			String[] splitted = extractCommandNameAndArguments(line);

			String commandName = splitted[0];
			ShellCommand command = env.commands().get(commandName);
			if (command == null) {
				env.writeln(String.format("Ne postoji naredba '%s'. Podržane naredbe možete dobiti upisom 'help'", commandName));
				continue;
			}

			String arguments = splitted[1] == null || splitted[1].isEmpty() ? null : splitted[1];
			status = command.executeCommand(env, arguments);
		} while (status != ShellStatus.TERMINATE);

		env.writeln("Zatvaram MyShell ljusku.\nDoviđenja!");
	}

	/**
	 * Pomoćna metoda koja se koristi za unos naredbi korisnika ove ljuske.
	 * Metoda će ukoliko se koriste višeredčane naredbe naredbu osloboditi od
	 * {@link Environment#getMorelinesSymbol()} i korisniku vratiti naredbu
	 * unutar jednog primjerka rzreda {@link String}
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za
	 *            čitanje i ispis
	 * @return naredba povezana u jedan primjerak razreda {@link String}.
	 *         Naredba je oslobođena {@link Environment#getMorelinesSymbol()}
	 */
	private static String readAndAppendAllLines(Environment env) {
		StringBuilder sb = new StringBuilder();
		env.write(String.format("%s ", env.getPromptSymbol()));
		String multiline = String.format("%s ", env.getMultilineSymbol());
		Character morelines = env.getMorelinesSymbol();

		String line = null;
		while ((line = env.readLine()) != null) {
			if (line.endsWith(env.getMorelinesSymbol().toString())) {
				// ovo sigurno prolazi jer je to u if uvjetu
				sb.append(line.substring(0, line.lastIndexOf(morelines)));
			} else {
				sb.append(line);
				break;
			}
			env.write(multiline);
		}
		return sb.toString();
	}

	/**
	 * Pomoćna metoda koja prima čitavu naredbu i rastavlja je na ono što ona
	 * smatra da je naziv naredbe i ono što ona smatra da su argumenti naredbe.
	 * Metoda će uvijek vratiti polje primjeraka razreda {@link String} od točno
	 * dva elementa, pri čemu je prvi sigurno naziv naredbe (takva naredba
	 * nemora nužno biti podržana). Drugi element su argumenti spojeni u jedan
	 * primjerak razreda {@link String} ili <code>null</code> ukoliko nakon
	 * imena naredbe ne postoji više znakova.
	 *
	 * @param line
	 *            korisnikova naredba oslobođena od viška razmaka prije i
	 *            poslije naredbe
	 * @return polje primjeraka razreda {@link String} od točno dva elementa,
	 *         pri čemu je prvi sigurno naziv naredbe (takva naredba nemora
	 *         nužno biti podržana). Drugi element su argumenti spojeni u jedan
	 *         primjerak razreda {@link String} ili <code>null</code> ukoliko
	 *         nakon imena naredbe ne postoji više znakova.
	 */
	private static String[] extractCommandNameAndArguments(String line) {
		int firstBlank = line.indexOf(" ");
		if (firstBlank == -1) {
			return new String[] { line, null };
		}
		String command = line.substring(0, firstBlank);
		String arguments = line.substring(firstBlank, line.length()).trim();

		return new String[] { command, arguments };
	}
}
