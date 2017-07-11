package hr.fer.zemris.java.hw06.shell.commands;

import java.nio.charset.Charset;
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
 * <i>Naredba ne prima niti jedan argument.</i>
 * <i>Naredba izlistava imena svih podržanih skupova znakova.</i>
 * <i>Koji skupovi znakova su podržani ovisi o Javinoj platformi na kojoj se ova naredba izvodi.</i>
 * <i>Svaki redak predstavlja točno jedan skup znakova. Redci se ne ponavljaju</i>
 * </pre>
 * 
 * @see ShellStatus
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public class CharsetsShellCommand implements ShellCommand{
	
	/** Konstanta koja predstavlja naziv naredbe */
	public static final String NAME = "charsets";
	
	/**
	 * Nepromijenjivi primjerak sučelja {@link List} koji predstavlja opis
	 * naredbe
	 */
	private static final List<String> DESCRIPTION;

	static {
		String[] descriptionLines = new String[] { 
				"Naredba ne prima niti jedan argument.",
				"Naredba izlistava imena svih podržanih skupova znakova.",
				"Koji skupovi znakova su podržani ovisi o Javinoj platformi na kojoj se ova naredba izvodi.",
				"Svaki redak predstavlja točno jedan skup znakova. Redci se ne ponavljaju"
			};

		DESCRIPTION = Collections.unmodifiableList(Arrays.asList(descriptionLines));
	}

	/**
	 * @return {@link ShellStatus#CONTINUE}
	 */
	@Override
	public ShellStatus executeCommand(Environment env, String arguments) {
		if(arguments == null) {
			Charset.availableCharsets().keySet().forEach(charsetName -> env.writeln("\t" + charsetName));
		} else {
			env.writeln("Naredba 'charsets' ne prima dodatne argumente.Za više informacija upišite 'help charsets'");
		}
		return ShellStatus.CONTINUE;
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
