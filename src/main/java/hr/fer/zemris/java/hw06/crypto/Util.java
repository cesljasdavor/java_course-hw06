package hr.fer.zemris.java.hw06.crypto;

/**
 * Razred(biblioteka) koja nudi samo dvije statičke metode:
 * <ul>
 * <li>{@link #bytetohex(byte[])}</li>
 * <li>{@link #hextobyte(String)}</li>
 * </ul>
 * 
 * Metode služe za pretvorbu heksadekadskog broja u polje okteta i obrnuto. Za
 * više informacija odite na link od navedene metode
 * 
 * @author Davor Češljaš
 */
public class Util {

	/** Konstanta koja predstavlja bazu brojevnog sustava */
	private static final int BASE = 16;

	/**
	 * Konstanta koja predstavlja za koliko se mjesta pomiče prva 4 bita u
	 * byteu
	 */
	private static final int MOVE = 4;

	/**
	 * Metoda koja iz predanog parametra <b>keyText</b> pokušava parsirati polje
	 * okteta. Ukoliko je predan prazan niz znakova metoda će vratiti polje
	 * okteta veličine 0. Ukoliko se predani niz znakova ne može parsirati u
	 * heksadekadski broj baca se {@link IllegalArgumentException}. Ukoliko je
	 * parsiranje uspjelo vraća se polje okteta, gdje svaki oktet predstavlja
	 * dvije heksadekadske znamenke. Znamenke se grupiraju kao da su u
	 * Big-endian formatu
	 *
	 * @param keyText
	 *            primjerak razreda {@link String} koji se parsira u
	 *            heksadekadski broj
	 * @return polje okteta, gdje svaki oktet predstavlja dvije heksadekadske
	 *         znamenke
	 * 
	 * @throws IllegalArgumentException
	 *             ukoliko nije moguće pretvoriti predani primjerak razreda
	 *             {@link String} <b>keyText</b> u heksadekadski broj
	 */
	public static byte[] hextobyte(String keyText) {
		if (!checkInput(keyText)) {
			return new byte[0];
		}

		byte[] data = new byte[keyText.length() / 2];
		for (int i = 0; i < keyText.length(); i += 2) {
			data[i / 2] = (byte) ((Character.digit(keyText.charAt(i), BASE) << MOVE)
					+ Character.digit(keyText.charAt(i + 1), BASE));
		}

		return data;
	}

	/**
	 * Pomoćna metoda koja provjerava je li predani {@link String}
	 * <b>keyText</b> dijeljiv s dva, je li duljine veće od 0 i može li se
	 * tumačiti kao heksadekadska znamenka.
	 *
	 * @param keyText
	 *            primjerak razreda {@link String} čija se kompatibilnost
	 *            provjerava
	 * @return <b>true</b> ako je predani parametar kompatibilan, <b>false</b>
	 *         inače
	 * @throws IllegalArgumentException
	 *             ukoliko argument nije paran ili ga se ne može parsirati u
	 *             heksadekadski broj
	 */
	private static boolean checkInput(String keyText) {
		if (keyText.length() == 0) {
			return false;
		}

		checkIfInputHex(keyText);
		return true;
	}

	/**
	 * Pomoćna metoda koja provjerava je li predani argument <b>keyText</b>
	 * paran i može li se parsirati u heksadekadski broj. Ukoliko ovo nije
	 * zadovoljeno baca se {@link IllegalArgumentException}
	 *
	 * @param keyText
	 *            the key text
	 * @throws IllegalArgumentException
	 *             ukoliko argument nije paran ili ga se ne može parsirati u
	 *             heksadekadski broj
	 */
	private static void checkIfInputHex(String keyText) {
		if (keyText.length() % 2 == 1) {
			throw new IllegalArgumentException("Predani niz znakova ima neparan broj znakova!");
		}

		if (!keyText.matches("[\\p{XDigit}]+")) {
			throw new IllegalArgumentException("Predani niz znakova nije hexadecimalan broj!");
		}
	}

	/**
	 * Pomoćna metoda koja predano polje okteta pretvara u primjerak razreda
	 * {@link String} koji predstavlja heksadekadsku reprezentaciju predanog
	 * polja okteta. Metoda se oslanja na
	 * {@link String#format(String, Object...)} metodu
	 *
	 * @param bytearray
	 *            polje okteta koje se pretvara u heksadekadsku reprezentaciju
	 * @return heksadekadska reprezentacija predanog polja okteta
	 *         <b>bytearray</b>
	 */
	public static String bytetohex(byte[] bytearray) {
		StringBuilder sb = new StringBuilder();

		for (byte b : bytearray) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString().toLowerCase();
	}
}
