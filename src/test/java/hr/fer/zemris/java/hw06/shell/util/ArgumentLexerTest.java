package hr.fer.zemris.java.hw06.shell.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArgumentLexerTest {

	@Test(expected = ArgumentLexerException.class)
	public void predanNull() {
		new ArgumentLexer(null);
	}

	@Test(expected = ArgumentLexerException.class)
	public void predanPrazanString() {
		new ArgumentLexer("").nextToken();
	}

	@Test(expected = ArgumentLexerException.class)
	public void predanSamoRazmaci() {
		new ArgumentLexer("    \t \r    ").nextToken();
	}

	@Test
	public void stringBezNavodnika() {
		String arguments = "test";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), arguments);
	}

	@Test
	public void stringSaNavodnicima() {
		String arguments = "\"test\"";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), "test");
	}

	@Test
	public void dvaStringaBezNavodnika() {
		String arguments = "test test2";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), "test");
		assertEquals(lexer.nextToken(), "test2");
	}

	@Test
	public void dvaStringaSaNavodnicima() {
		String arguments = "\"test\" \"test2\"";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), "test");
		assertEquals(lexer.nextToken(), "test2");
	}

	@Test
	public void dvaStringaSaNavodnicimaIEscapeanjem() {
		String arguments = "\"C:\\User\\test\\2\" \"test2\\\"\"";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), "C:\\User\\test\\2");
		assertEquals(lexer.nextToken(), "test2\"");
	}

	@Test
	public void kombinacijaSaIBezNavodnika() {
		String arguments = "\"C:\\User\\test\\2\" /home/davor/FER";
		ArgumentLexer lexer = new ArgumentLexer(arguments);
		assertEquals(lexer.nextToken(), "C:\\User\\test\\2");
		assertEquals(lexer.nextToken(), "/home/davor/FER");
	}

	@Test
	public void realanSlucajCopy() {
		String arguments1 = "\"/home/davor/Radna Površina/moj.txt\" \".\"";
		String arguments2 = "\"/home/davor/Radna Površina/moj.txt\" .";

		ArgumentLexer lexer1 = new ArgumentLexer(arguments1);
		ArgumentLexer lexer2 = new ArgumentLexer(arguments2);

		assertEquals(lexer1.nextToken(), "/home/davor/Radna Površina/moj.txt");
		assertEquals(lexer1.nextToken(), ".");
		assertEquals(lexer2.nextToken(), "/home/davor/Radna Površina/moj.txt");
		assertEquals(lexer2.nextToken(), ".");
	}

	@Test
	public void realanSlucajCat() {
		String arguments1 = "\"C:\\Users\\Davor\\Desktop\\moj.txt\"";
		String arguments2 = "C:\\Users\\network\\moj.txt UTF-8";

		ArgumentLexer lexer1 = new ArgumentLexer(arguments1);
		ArgumentLexer lexer2 = new ArgumentLexer(arguments2);

		assertEquals(lexer1.nextToken(), "C:\\Users\\Davor\\Desktop\\moj.txt");
		assertEquals(lexer2.nextToken(), "C:\\Users\\network\\moj.txt");
		assertEquals(lexer2.nextToken(), "UTF-8");
	}

	@Test
	public void realanSlucajMkdirLsiTree() {
		String arguments1 = "\"C:\\Users\\Davor\\Desktop\"";
		String arguments2 = "/home/davor/FER";

		ArgumentLexer lexer1 = new ArgumentLexer(arguments1);
		ArgumentLexer lexer2 = new ArgumentLexer(arguments2);
		assertEquals(lexer1.nextToken(), "C:\\Users\\Davor\\Desktop");
		assertEquals(lexer2.nextToken(), "/home/davor/FER");
	}
}
