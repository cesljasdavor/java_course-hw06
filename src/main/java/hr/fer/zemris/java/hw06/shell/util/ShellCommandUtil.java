package hr.fer.zemris.java.hw06.shell.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import hr.fer.zemris.java.hw06.shell.Environment;
import hr.fer.zemris.java.hw06.shell.ShellCommand;

/**
 * Razred(biblioteka) koja nudi pet statičke metode:
 * <ul>
 * <li>{@link #extractExistingDirectory(Environment, String)}</li>
 * <li>{@link #extractSingleFile(Environment, String)}</li>
 * <li>{@link #extractSinglePath(Environment, String)}</li>
 * <li>{@link #parseArguments(Environment, String)}</li>
 * <li>{@link #getFileAttributes(Path, Environment)}</li>
 * </ul>
 * 
 * Metode služe kao pomoćne metode prilikom izvršavanja naredbi koje su
 * implementirane kao primjerci sučelje {@link ShellCommand}. Metode razred za
 * ispis koristi isključivo primjerak sučelja {@link Environment} koji se moraju
 * predati svakoj pojedinoj metodi.
 * 
 * @see ShellCommand
 * @see Environment
 * 
 * @author Davor Češljaš
 */
public class ShellCommandUtil {

	/**
	 * Metoda iz predanog primjerka razreda {@link String} <b>pathName</b> koji
	 * reprezentira relativnu ili apsolutnu putanju do direktorija pokušava
	 * parsirati apstraktnu reprezentaciju putanje {@link Path} do tog
	 * direktorija. Parsiranje će uspjeti samo ukoliko je predana putanja zaista
	 * putanja do postojećeg direktorija u datotečnom sustavu, odnosno ako
	 * {@link Files#isDirectory(Path, LinkOption...)} vrati <b>true</b>. Metoda
	 * za ispis eventualnih pogrešaka koristi isključivo primjerak sučelja
	 * {@link Environment}. Metoda za vađenje putanje koristi metodu
	 * {@link #extractSinglePath(Environment, String)}
	 * 
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * @param pathName
	 *            reprezentira relativnu ili apsolutnu putanju do direktorija
	 * @return apstraktnu reprezentaciju putanje {@link Path} do direktorija ako
	 *         direktorij postoji u datotečnom sustavu. Ukoliko direktorij ne
	 *         postoji ili predana putanja nije direktorij metoda vraća
	 *         <code>null</code>
	 */
	public static Path extractExistingDirectory(Environment env, String pathName) {
		try {
			Path filePath = null;
			if (pathName == null) {
				filePath = Paths.get(".").toRealPath(LinkOption.NOFOLLOW_LINKS);
			} else {
				filePath = extractSinglePath(env, pathName);
			}

			if (filePath != null && !Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
				env.writeln(String.format("'%s' nije direktorij. Molim Vas predajte direktorij", pathName));
			} else {
				return filePath == null ? null : filePath.toRealPath(LinkOption.NOFOLLOW_LINKS);
			}
		} catch (IOException ignorable) {
		}
		return null;
	}

	/**
	 * Metoda koja se koristi kako bi se iz primjerka razreda {@link String}
	 * <b>pathName</b> koji reprezentira relativnu ili apsolutnu putanju
	 * parsirala apstraktnu reprezentaciju putanje {@link Path}. Metoda za
	 * parsiranje koristi metodu {@link #parseArguments(Environment, String)}.
	 * Metoda ne vrši nikakvu dodatnu provjeru, osim je li unutar vraćene
	 * reference na {@link List} primjeraka razreda string točno jedan
	 * {@link String}.Metoda za ispis eventualnih pogrešaka koristi isključivo
	 * primjerak sučelja {@link Environment}.
	 * 
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * @param pathName
	 *            reprezentira relativnu ili apsolutnu putanju koja se parsira
	 *            metodom {@link #parseArguments(Environment, String)}
	 * @return apstraktnu reprezentaciju putanje {@link Path} ako je parsiranje
	 *         uspjelo ili <code>null</code> ako parsiranje nije uspjelo
	 */
	public static Path extractSinglePath(Environment env, String pathName) {
		List<String> separatedArguments = parseArguments(env, pathName);
		if (separatedArguments != null && separatedArguments.size() == 1) {
			return Paths.get(separatedArguments.get(0));
		}
		env.writeln("Predali ste krivi broj argumenata. Tražio sam 1, a Vi ste predali " + separatedArguments.size());
		return null;
	}

	/**
	 * Metoda koja se koristi kako bi se iz primjerka razreda {@link String}
	 * <b>pathName</b> koji reprezentira relativnu ili apsolutnu putanju do
	 * datoteke parsirala apstraktnu reprezentaciju putanje {@link Path}. Metoda
	 * kako bi prikupila apstraktnu reprezentaciju putanje {@link Path} koristi
	 * metodu {@link #extractSinglePath(Environment, String)}. Dodatno metoda
	 * provjerava je li apstraktna putanja doista putanja do neke datoteke
	 * pomoću {@link Files#isRegularFile(Path, LinkOption...)}.Metoda za ispis
	 * eventualnih pogrešaka koristi isključivo primjerak sučelja
	 * {@link Environment}.
	 * 
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * @param pathName
	 *            reprezentira relativnu ili apsolutnu putanju do datoteke koja
	 *            se parsira pomoću metode
	 *            {@link #extractSinglePath(Environment, String)}
	 * @return apstraktnu reprezentaciju putanje {@link Path} ako je parsiranje
	 *         uspjelo ili <code>null</code> ako parsiranje nije uspjelo
	 */
	public static Path extractSingleFile(Environment env, String fileName) {
		Path filePath = Paths.get(fileName);
		if (!Files.isRegularFile(filePath, LinkOption.NOFOLLOW_LINKS)) {
			env.writeln(String.format(
					"Predana datoteka '%s' ne postoji ili nije datoteka. Molim Vas predajte valjanu datoteku",
					fileName));
			return null;
		}

		return filePath;
	}

	/**
	 * Metoda vrši parsiranje predanog argumenta <b>arguments</b> koristeći
	 * pritom primjerak razreda {@link ArgumentParser}. Metoda vraća rezultat
	 * {@link ArgumentParser#getSeparatedArguments()} ili <code>null</code>
	 * ukoliko parsiranje nije uspjelo. Metoda za ispis eventualnih pogrešaka
	 * koristi isključivo primjerak sučelja {@link Environment}.
	 *
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * @param arguments
	 *            primjerak razreda {@link String} koji se parsira.
	 * @return rezultat {@link ArgumentParser#getSeparatedArguments()} ili
	 *         <code>null</code> ukoliko parsiranje nije uspjelo.
	 */
	public static List<String> parseArguments(Environment env, String arguments) {
		try {
			ArgumentParser parser = new ArgumentParser(arguments);
			return parser.getSeparatedArguments();
		} catch (ArgumentLexerException e) {
			env.writeln(e.getMessage());
			return null;
		}
	}

	/**
	 * Metoda koja se koristi kako bi se iz apstraktne reprezentacije putanje
	 * {@link Path} <b>path</b> pribavili atributi te datoteke/direktorija
	 * ukoliko ista postoji u datotečnom sustavu. Atributi se oblikuju sučeljem
	 * {@link BasicFileAttributes}. Metoda za ispis eventualnih pogrešaka
	 * koristi isključivo primjerak sučelja {@link Environment}.
	 *
	 * @param path
	 *            tapstraktne reprezentacije putanje {@link Path} do
	 *            datoteke/direktorija čiji se atributi pribavljaju
	 * @param env
	 *            primjerak sučelja {@link Environment} koji se koristi za ispis
	 *            eventualnih pogrešaka
	 * @return primjerak sučelja {@link BasicFileAttributes} koji oblikuje
	 *         atribute koje sadrži datoteka/direktorij predstavljen sa
	 *         <b>path</b>. Ako datoteka/direktorij ne postoji na disku metoda
	 *         vraća <code>null</code>
	 */
	public static BasicFileAttributes getFileAttributes(Path path, Environment env) {
		try {
			BasicFileAttributeView faView = Files.getFileAttributeView(path, BasicFileAttributeView.class,
					LinkOption.NOFOLLOW_LINKS);
			return faView.readAttributes();
		} catch (IOException e) {
			env.writeln(String.format("Ne mogu pristupiti atributima %s '%s'",
					Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) ? "direktorija" : "datoteke",
					path.getFileName().toString()));
		}
		return null;
	}
}
