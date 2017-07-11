package hr.fer.zemris.java.hw06.shell;

/**
 * Enumeracija koja oblikuje statuse izvođenja pojedinih naredbi ljuske
 * oblikovanih sučeljem {@link ShellCommand}. Mogući statusi su:
 * <ul>
 * <li>{@link #CONTINUE}</li>
 * <li>{@link #TERMINATE}</li>
 * </ul>
 * 
 * @see ShellCommand
 * 
 * @author Davor Češljaš
 */
public enum ShellStatus {

	/** Predstavlja status koji ljusci sugerira da nastavi s normalnim radom */
	CONTINUE,

	/** Predstavlja status koji ljusci sugerira da prekine s radom */
	TERMINATE
}
