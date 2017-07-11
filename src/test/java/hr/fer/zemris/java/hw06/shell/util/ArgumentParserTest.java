package hr.fer.zemris.java.hw06.shell.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Sam parser ne izvodi ništa pametno osim poziva {@link ArgumentLexer} tako da
 * ovdje testiram samo što se dogodi kada se dogode neke od grešaka
 * 
 * @author Davor Češljaš
 *
 */
public class ArgumentParserTest {


	@Test(expected = ArgumentLexerException.class)
	public void predanNull() {
		new ArgumentParser(null);
	}

	@Test
	public void predanPrazanString() {
		ArgumentParser parser = new ArgumentParser("");
		List<String> separatedArguments = parser.getSeparatedArguments();
		assertEquals(separatedArguments.size(), 0);
	}

	@Test
	public void predanSamoRazmaci() {
		ArgumentParser parser = new ArgumentParser("    \t \r    ");
		List<String> separatedArguments = parser.getSeparatedArguments();
		assertEquals(separatedArguments.size(), 0);
	}
	
	@Test
	public void kombinacijaSaIBezNavodnika() {
		String arguments = "/home/davor/FER \"C:\\User\\test\\2\" ";
		ArgumentParser parser = new ArgumentParser(arguments);
		List<String> separatedArguments = parser.getSeparatedArguments();
		assertEquals(separatedArguments.size(), 2);
	}

}
