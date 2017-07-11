package hr.fer.zemris.java.hw06.crypto;

import static org.junit.Assert.*;
import static hr.fer.zemris.java.hw06.crypto.Util.*;

import org.junit.Test;

public class UtilTest {

	@Test
	public void pretvorbaUPoljeOktetaStringPrazan() {
		assertEquals(hextobyte("").length, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void pretvorbaUPoljeOktetaJednaZnamenka() {
		hextobyte("9");
	}

	@Test(expected = IllegalArgumentException.class)
	public void pretvorbaUPoljeOktetaNijeHex() {
		hextobyte("k232i31čj1ž3");
	}

	@Test
	public void pretvorbaUPoljeOktetaJedanOktekt() {
		assertArrayEquals(hextobyte("5a"), new byte[] { 90 });
	}

	@Test
	public void pretvorbaUPoljeOktetaDvaOktekt() {
		assertArrayEquals(hextobyte("5A73"), new byte[] { 90, 115 });
	}

	@Test
	public void pretvorbaUPoljeOktetaIzZadatka() {
		assertArrayEquals(hextobyte("01aE22"), new byte[] { 1, -82, 34 });
	}

	@Test
	public void pretvorbaUHexadecimalniBrojPoljePrazno() {
		assertEquals(bytetohex(new byte[0]), "");
	}

	@Test
	public void pretvorbaUHexadecimalniBrojJednaZnamenkaURasponu() {
		assertEquals(bytetohex(new byte[] { 10 }), "0a");
	}

	@Test
	public void pretvorbaUHexadecimalniBrojJednaZnamenkaVanRasponaPoz() {
		assertEquals(bytetohex(new byte[] { 127 }), "7f");
	}

	@Test
	public void pretvorbaUHexadecimalniBrojJednaZnamenkaVanRasponaNeg() {
		assertEquals(bytetohex(new byte[] { -128 }), "80");
	}

	@Test
	public void pretvorbaUHexadecimalniBrojIzZadatka() {
		assertEquals(bytetohex(new byte[] { 1, -82, 34 }), "01ae22");
	}

	@Test
	public void pretvorbaUHexadecimalniBrojViseZnamenka() {
		assertEquals(bytetohex(new byte[] { 12, 35, -25, -69, 86, 19 }), "0c23e7bb5613");
	}

	@Test
	public void izStringaUBytePaUString() throws Exception {
		String str = "01AE22";
		byte[] bytes = hextobyte(str);

		assertEquals(bytetohex(bytes), str.toLowerCase());
	}

	@Test
	public void izStringaUBytePaUString2() throws Exception {
		String str = "0d3d4424461e22a458c6c716395f07dd9cea2180a996e78349985eda78e8b800";
		byte[] bytes = hextobyte(str);

		assertEquals(bytetohex(bytes), str);
	}

	@Test
	public void izPoljaOktetaUStringPaUPoljeOkteta() throws Exception {
		byte[] bytes = new byte[] { 20, 100, -35, -58, -99, 28, -128, 127, 0, 36, 86, 94, 42, 53, -53, 86, 69, 56, 22,
				75, 61, 120, 111, 21, 66, 68 };
		String str = bytetohex(bytes);
		assertArrayEquals(hextobyte(str), bytes);
	}

}
