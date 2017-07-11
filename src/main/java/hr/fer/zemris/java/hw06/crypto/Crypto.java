package hr.fer.zemris.java.hw06.crypto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Razred predstavlja program koji preko naredbenog redka prima argumente i
 * izvodi jednu od tri akcije:
 * <ol>
 * <li>Ukoliko se kao prvi argument (<b>args[0]</b>) unese {@value #CHECK_SHA}
 * program će iz drugog predanog argumenta (koji predstavlja putanju do
 * datoteke) izračunati zaštitnu sumu(sažetak). Ako drugi argument ne postoji
 * program će izbaciti prigodnu poruku. Od korisnika programa traži se da kroz
 * konzolu unese očekivanu zaštitnu sumu radi uporedbe generirane i predane
 * sume. Za stvaranje zaštitne sume koristi se {@value #DIGEST_ALGORITHM}</li>
 * <li>Ukoliko se kao prvi argument (<b>args[0]</b>) unese {@value #ENCRYPT}
 * program će pokušati enkriptirati datoteku predstavljenu putanjom unutar
 * drugog argumenta. Ukoliko enkripcija uspije enkriptirana datoteka biti će
 * spremljena kako je to navedeno trećim argumentom naredbenog redka. Od
 * korisnika se prije enkriptiranja zahtjeva ključ i inicijalizacijski vektor
 * enkripcije.Ukoliko enkripcija ne uspije program će izbaciti prigodnu poruku.
 * Enkripcija se vrši transformacijom {@value #CRYPT_TRANSFORMATION}.</li>
 * <li>Ukoliko se kao prvi argument (<b>args[0]</b>) unese {@value #DECRYPT}
 * program će pokušati dekriptirati datoteku predstavljenu putanjom unutar
 * drugog argumenta. Ukoliko dekripcija uspije dekriptirana datoteka biti će
 * spremljena kako je to navedeno trećim argumentom naredbenog redka. Od
 * korisnika se prije dekriptiranja zahtjeva ključ i inicijalizacijski vektor
 * dekripcije.Ukoliko dekripcija ne uspije program će izbaciti prigodnu poruku.
 * Dekripcija se vrši transformacijom {@value #CRYPT_TRANSFORMATION}.</li>
 * </ol>
 * 
 * Ukoliko se ne unese neka od traženih operacija program će ispisati da niste
 * unijeli valjanu operaciju.
 * 
 * @see Cipher
 * @see MessageDigest
 * 
 * @author Davor Češljaš
 */
public class Crypto {

	/**
	 * Konstanta koja predstavlja količinu argumenata potrebnu za izvršavanje
	 * operacije izračuna zaštitne sume
	 */
	private static final int DIGEST_ARGS_SIZE = 2;

	/**
	 * Konstanta koja predstavlja količinu argumenata potrebnu za izvršavanje
	 * operacije kriptiranje (dakle ili dekripcije ili enkripcije)
	 */
	private static final int CRYPT_ARGS_SIZE = 3;

	/**
	 * Konstanta koja predstavlja poziciju unutar predanih argumenata naredbenog
	 * redka koja predstavlja operaciju
	 */
	private static final int OPERATION_INDEX = 0;

	/**
	 * Konstanta koja predstavlja poziciju unutar predanih argumenata naredbenog
	 * redka koja predstavlja putanju do datoteke nad kojom vršimo operaciju
	 */
	private static final int FROM_FILE_INDEX = 1;

	/**
	 * Konstanta koja predstavlja poziciju unutar predanih argumenata naredbenog
	 * redka koja će biti putanja do kriptirane datoteke
	 */
	private static final int TO_FILE_INDEX = 2;

	/**
	 * Konstanta koja predstavlja opciju dekriptiranja algoritmom
	 * {@link #CRYPT_ALGORITHM}
	 */
	private static final String DECRYPT = "decrypt";

	/**
	 * Konstanta koja predstavlja opciju enkriptiranja algoritmom
	 * {@link #CRYPT_ALGORITHM}
	 */
	private static final String ENCRYPT = "encrypt";

	/**
	 * Konstanta koja predstavlja opciju izračuna zaštitne sume algoritmom
	 * {@value #DIGEST_ALGORITHM}
	 */
	private static final String CHECK_SHA = "checksha";

	/**
	 * Predstavlja znak koji će se pojaviti korisniku kada se od njega zahtjeva
	 * unos. Prije ovog znaka doći poruka koja pobliže opisuje što je potrebno
	 * unijeti
	 */
	private static final String PROMPT_SYMBOL = "> ";

	/** Konstanta koja predstavlja veličinu pomoćnog polja okteta */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Konstanta koja predstavlja naziv algoritma koji se koristi za kriptiranje
	 * datoteka (i dekripciju i enkripciju).
	 */
	private static final String CRYPT_ALGORITHM = "AES";

	/**
	 * Konstanta koja predstavlja punu transformaciju koja se koristi za
	 * prilikom kriptiranja datoteka (i dekripciju i enkripciju).
	 */
	private static final String CRYPT_TRANSFORMATION = "AES/CBC/PKCS5Padding";

	/**
	 * Konstanta koja predstavlja naziv algoritma koji se koristi za izračun
	 * zaštitne sume datoteka (i dekripciju i enkripciju).
	 */
	private static final String DIGEST_ALGORITHM = "SHA-256";

	/**
	 * Metoda od koje započinje izvođenje programa
	 *
	 * @param args
	 *            <ul>
	 *            <li>Ako je <code>args[0]=={@value #CHECK_SHA}</code> tada je
	 *            <code>args[1]</code> naziv datoteke čija se zaštitna suma
	 *            računa</li>
	 *            <li>Ako je
	 *            <code>args[0]=={@value #ENCRYPT} ili args[0]=={@value #DECRYPT}</code>
	 *            tada je <code>args[1]</code> naziv datoteke koja se kriptira,
	 *            a <code>args[2]</code> naziv buduće kriptirane datoteke</li>
	 *            </ul>
	 */
	public static void main(String[] args) {
		if (args.length < DIGEST_ARGS_SIZE) {
			throw new IllegalArgumentException("Predali ste pre mali broj argumenata!");
		}

		String operation = args[OPERATION_INDEX];

		if (operation.equalsIgnoreCase(CHECK_SHA) && args.length == DIGEST_ARGS_SIZE) {
			digestFile(args[FROM_FILE_INDEX]);
			return;
		}

		boolean encrypt = false;
		if (((encrypt = operation.equalsIgnoreCase(ENCRYPT)) || operation.equalsIgnoreCase(DECRYPT))
				&& args.length == CRYPT_ARGS_SIZE) {
			cryptFile(encrypt, args[FROM_FILE_INDEX], args[TO_FILE_INDEX]);
		} else {
			System.out.println("Niste unijeli valjanu operaciju");
		}
	}

	/**
	 * Pomoćna metoda koja se koristi za kriptiranje datoteke algoritmom
	 * {@link #CRYPT_ALGORITHM}. Metoda otvara dva toka okteta iz predanih
	 * <b>fromFileName</b> i <b>toFileName</b>. Te podatke iz ulaznog toka
	 * (fromFileName) kriptira i sprema u podatke izlaznog toka (toFileName)
	 *
	 * @param encrypt
	 *            <b>true</b> ukoliko se zadana datoteka treba enkriptirati, a
	 *            <b>false</b> ukoliko se datoteka treba dekriptirati
	 * @param fromFileName
	 *            primjerak razreda {@link String} koji predstavlja putanju do
	 *            datoteke nad kojom se vrši kriptiranje
	 * @param toFileName
	 *            primjerak razreda {@link String} koji predstavlja putanju gdje
	 *            će se spremiti kriptirana datoteka (zajedno sa nazivom
	 *            datoteke)
	 */
	private static void cryptFile(boolean encrypt, String fromFileName, String toFileName) {
		Path fromFilePath = Paths.get(fromFileName);
		Path toFilePath = Paths.get(toFileName);

		try (InputStream is = new BufferedInputStream(Files.newInputStream(fromFilePath));
				OutputStream os = new BufferedOutputStream(Files.newOutputStream(toFilePath))) {
			Cipher cipher = initCipher(encrypt);

			byte[] bytes = new byte[BUFFER_SIZE];
			int len;
			while ((len = is.read(bytes)) != -1) {
				byte[] output = cipher.update(bytes, 0, len);
				if (output != null) {
					os.write(output);
					os.flush();
				}
			}
			os.write(cipher.doFinal());

			System.out.printf("%s završena. Generirana datoteka %s temeljem datoteke %s.",
					encrypt ? "Enkripcija" : "Dekripcija", toFileName, fromFilePath);
		} catch (IOException e) {
			System.out.printf("Ne mogu otvoriti tokove podataka nad datotekama: %n\t%s%n\t%s",
					fromFilePath.toAbsolutePath(), toFilePath.toAbsolutePath());
		} catch (Exception e) {
			// Problemi sa kriptiranjem
			System.out.printf("Nisam uspio %s predanu datoteku '%s'", encrypt ? "kriptirati" : "dekritpirati",
					fromFileName);
		}
	}

	/**
	 * Metoda koja inicijalizira primjerak razreda {@link Cipher} sa svim
	 * argumentima njemu potrebnim (vidjeti
	 * {@link Cipher#init(int, java.security.Key, AlgorithmParameterSpec)}).
	 * Metoda pozivatelju vraća upravo referencu na inicijalizirani primjerak
	 * razreda {@link Cipher}
	 * 
	 *
	 * @param encrypt
	 *            <b>true</b> ukoliko se zadana datoteka treba enkriptirati, a
	 *            <b>false</b> ukoliko se datoteka treba dekriptirati
	 * @return inicijalizirani primjerak razreda {@link Cipher}
	 * @throws NoSuchAlgorithmException
	 *             ukoliko ne postoji algoritam {@link #CRYPT_ALGORITHM}
	 * @throws NoSuchPaddingException
	 *             ukoliko ne postoji zadani algoritam punjenja (vidjeti
	 *             {@link #CRYPT_TRANSFORMATION})
	 * @throws InvalidKeyException
	 *             ukoliko je ključ nije dobro zadan
	 * @throws InvalidAlgorithmParameterException
	 *             ukoliko inicijalizacijski vektor nije dobro zadan
	 * 
	 * @see Cipher#init(int, java.security.Key, AlgorithmParameterSpec)
	 * @see IvParameterSpec
	 * @see SecretKeySpec
	 */
	private static Cipher initCipher(boolean encrypt) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		CipherParams info = cipherUI();

		// inicijalizacija primjerka razreda Cipher
		SecretKeySpec keySpec = new SecretKeySpec(Util.hextobyte(info.key), CRYPT_ALGORITHM);
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(Util.hextobyte(info.initVector));
		Cipher cipher = Cipher.getInstance(CRYPT_TRANSFORMATION);
		cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, paramSpec);
		return cipher;
	}

	/**
	 * Pomoćna metoda koja predstavlja razrgovor s korisnikom preko konzole. Ova
	 * pomoćna metoda traži od korisnika da unese ključ kriptiranja i
	 * inicijalizacijski vektor kao niz od 32 heksadekadske znamenke
	 * 
	 * @return primjerak razreda {@link CipherParams} koji sadrži ključ i
	 *         inicijalizacijski vektors
	 * 
	 * @see CipherParams
	 */
	private static CipherParams cipherUI() {
		Scanner sc = new Scanner(System.in);

		System.out.println(
				"Molim Vas unesite lozinku (ključ) kao heksadecimalno enkodirani tekst (16 byteova, tj. 32 heksadecimalne znamenke): ");
		System.out.print(PROMPT_SYMBOL);
		String keyText = sc.next();

		System.out.println(
				"Molim Vas unesite inicijalizacijski vektor kao heksadecimalno enkodirani tekst (32 heksadecimalne znamenke):");
		System.out.print(PROMPT_SYMBOL);
		String ivText = sc.next();

		sc.close();
		return new CipherParams(keyText, ivText);
	}

	/**
	 * Pomoćna metoda koja poziva metodu {@link #calculateDigest(InputStream)}
	 * kako bi izračunala zaštitnu sumu datoteke zadane sa
	 * putanjom<b>fileName</b>. Metoda će pozvati i metodu
	 * {@link #digestUI(String, String)} kako bi korisniku predstavila je li
	 * upisana vrijednost zaštitne sume jedanka izračunatoj
	 *
	 * @param fileName
	 *            putanja do datoteke čija se zaštitna suma računa
	 */
	private static void digestFile(String fileName) {
		Path filePath = Paths.get(fileName);

		try (InputStream is = new BufferedInputStream(Files.newInputStream(filePath))) {
			String calculatedDigest = calculateDigest(is);
			digestUI(calculatedDigest, fileName);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Ne postoji algoritam SHA-256!");
		} catch (IOException e) {
			System.out.println("Datoteka s putanjom '" + filePath.toAbsolutePath() + "' se ne može učitati!");
		}
	}

	/**
	 * Pomoćna metoda koja od korisnika zahtjeva unos očekivane zaštitne sume te
	 * ovisno o toj sumi ustanovljuje je li očekivana suma jednaka ili različita
	 * predanoj sumi. Očekivana suma bi trebala imati točno 64 heksadekadske
	 * znamenke kako je propisano algoritmom {@value #DIGEST_ALGORITHM}.
	 *
	 * @param calculatedDigest
	 *            izračunata zaštitna suma s kojom se očekivana suma uspoređuje
	 * @param fileName
	 *            putanja do datoteke čija se zaštitna suma računa
	 */
	private static void digestUI(String calculatedDigest, String fileName) {
		// dohvati md od korisnika
		System.out.printf("Molim Vas unesite očekivanu sha-256 zaštitinu sumu za %s:%n", fileName);
		System.out.print(PROMPT_SYMBOL);

		Scanner sc = new Scanner(System.in);
		String inputDigest = sc.next();
		sc.close();

		// usporedi i ispiši poruku
		boolean matches = calculatedDigest.equalsIgnoreCase(inputDigest);
		System.out.println(
				String.format("Izračun zaštitne sume završen. Zaštitna suma od %s %s s očekivanom zaštitnom sumom.",
						fileName, matches ? "poklapa se" : "ne poklapa se")
						+ (matches ? "" : String.format("Zaštitna suma je: %s", calculatedDigest)));
	}

	/**
	 * Pomoćna metoda koja izračunava zaštitnu sumu iz predanog toka okteta
	 * {@link InputStream}. Kao algoritam izračuna zaštitne sume koristi se
	 * {@value #DIGEST_ALGORITHM}.
	 *
	 * @param is
	 *            ulazni tok okteta iz kojeg se računa zaštitna suma.
	 * @return primjerak razreda {@link String} koji predstavlja izračunatu
	 *         zaštitnu sumu
	 * @throws NoSuchAlgorithmException
	 *             ukoliko algoritam {@value #DIGEST_ALGORITHM} ne postoji
	 * @throws IOException
	 *             ukoliko nije moguće čitati iz predanog ulaznog toka okteta
	 *             <b>is</b>
	 * @see MessageDigest
	 */
	private static String calculateDigest(InputStream is) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);

		byte[] bytes = new byte[BUFFER_SIZE];
		int len;
		while ((len = is.read(bytes)) != -1) {
			md.update(bytes, 0, len);
		}

		return Util.bytetohex(md.digest());
	}

	/**
	 * Predstavlja privatnu strukturu podataka koju koriste metode
	 * {@link Crypto#cryptFile(boolean, String, String)} i
	 * {@link Crypto#cipherUI()} za razmjenu parametara. Konkretno ti parametri
	 * su ključ kriptiranja i inicijalizacijski vektor korišten prilikom
	 * kriptiranja.
	 * 
	 * @see Crypto
	 * @see SecretKeySpec
	 * @see IvParameterSpec
	 * 
	 * @author Davor Češljaš
	 * 
	 */
	private static class CipherParams {

		/** Članska varijabla koja predstavlja ključ kriptiranja */
		private String key;

		/**
		 * Članska varijabla koja predstavlja inicijalizacijski vektor korišten
		 * prilikom kriptiranja.
		 */
		private String initVector;

		/**
		 * Konstruktor koji se koristi za incijalizaciju primjerka ovog razreda.
		 * Ovaj konstruktor bez dodatne provjere postavlja vrijednosti članskih
		 * varijabli na <b>key</b> , odnosno <b>initVector</b>
		 *
		 * @param key
		 *            predstavlja ključ kriptiranja
		 * @param initVector
		 *            predstavlja inicijalizacijski vektor korišten prilikom
		 *            kriptiranja.
		 */
		public CipherParams(String key, String initVector) {
			this.key = key;
			this.initVector = initVector;
		}
	}
}
