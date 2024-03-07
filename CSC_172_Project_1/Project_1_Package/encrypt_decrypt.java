package Project_1_Package;

import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import static Project_1_Package.Data.S;

public class encrypt_decrypt {
	public static String[] keyScheduleTransform(String Key) // method to get the transformed key, stored in an array
	{
		String C = Key.substring(0, 28);
		String D = Key.substring(28);
		String[] subKey = new String[10];
		for (int i = 0; i < 10; i++) {
			C = shiftIt(C);
			D = shiftIt(D);
			subKey[i] = C + D.substring(0, 4);
		}
		return subKey;
	}

	public static String shiftIt(String binaryInput) { // Shift the String's first character to the last one
		String Shifted = binaryInput.substring(1);
		Shifted = Shifted + binaryInput.charAt(0);
		return Shifted;
	}

	public static String xorIt(String binary1, String binary2) // XOR Gate Operation
	{
		StringBuilder XOR = new StringBuilder();
		for (int i = 0; i < binary1.length(); i++) {
			if (binary1.charAt(i) == binary2.charAt(i)) {
				XOR.append('0');
			} else
				XOR.append('1');
		}
		return XOR.toString();
	}

	public static String functionF(String rightHalf, String subKey) {
		String Operation1 = xorIt(rightHalf, subKey); // Operation1 is XOR gate
		String Operation2 = ""; // Operation 2 is the concatenation after the substitution;
		String[] substi = new String[4];
		for (int i = 0; i < 4; i++) {
			substi[i] = Operation1.substring(8 * i, 8 + 8 * i);
			substi[i] = SubstitutionS(substi[i]);
			Operation2 = Operation2 + substi[i];
		} // substitution operation
		return permuteIt(Operation2);// final permutation
	}

	public static String SubstitutionS(String binaryInput) {
		String p1 = binaryInput.substring(0, 4); // first 4 bits representing the row
		String p2 = binaryInput.substring(4); // last 4 bits representing the column
		return S[BinaryToDecimal(p1)][BinaryToDecimal(p2)];
	}

	public static int BinaryToDecimal(String p)// convert the Binary number to the decimal number
	{
		int count = 0;
		int num = 0;

		for (int i = p.length() - 1; i >= 0; i--) {
			if (p.charAt(i) == '1') {
				num += (int) Math.pow(2, count);
			}
			count++;
		}
		return num;
	}

	public static String permuteIt(String binaryInput) // permute in the ordered way
	{
		StringBuilder permuted = new StringBuilder();
		int[] table = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13,
				30, 6, 22, 11, 4, 25 };
		for (int i = 0; i < table.length; i++) {
			permuted.append(binaryInput.charAt(table[i] - 1));
		}
		return permuted.toString();
	}

	public static String encryptBlock(String block, String inputKey) // encrypt code
	{
		String[] L = new String[11];
		String[] R = new String[11];
		L[0] = block.substring(0, 32);
		R[0] = block.substring(32);

		String[] subKey = new String[10];
		subKey = keyScheduleTransform(inputKey); // compute all transformed keys

		for (int i = 1; i <= 10; i++) {
			L[i] = R[i - 1];
			R[i] = xorIt(L[i - 1], functionF(R[i - 1], subKey[i - 1]));
		} // encryption process

		String Ciphertext = L[10] + R[10];
		return Ciphertext;
	}

	public static String decryptBlock(String block, String inputKey) // decrypt code
	{
		String[] L = new String[11];
		String[] R = new String[11];
		L[0] = block.substring(0, 32);
		R[0] = block.substring(32);

		String[] subKey = new String[10];
		subKey = keyScheduleTransform(inputKey); // compute all transformed keys

		for (int i = 1; i <= 10; i++) {
			R[i] = L[i - 1];
			L[i] = xorIt(R[i - 1], functionF(L[i - 1], subKey[10 - i]));
		} // decryption process, reverse the transformed key

		String Plaintext = L[10] + R[10];
		return Plaintext;
	}

	public static String DecToBinary(int number) // change decimal number to binary number
	{
		if (number == 0) {
			return "0";
		}
		if (number == 1) {
			return "1";
		}
		return DecToBinary(number / 2) + number % 2;
	}

	public static String encryption(String longBinaryInput, String inputKey) {
		String encryptedCode = "";
		int length = longBinaryInput.length();
		int cnt = 0; // count
		while (length >= 64) // divide the binary code to each block with 64 bits
		{
			String Block = longBinaryInput.substring(64 * cnt, 64 * (cnt + 1));
			cnt++;
			length -= 64;
			encryptedCode = encryptedCode + encryptBlock(Block, inputKey);
		}
		if (length != 0) // zero padding
		{
			String implement = longBinaryInput.substring(64 * cnt, 64 * cnt + length);
			for (int i = 1; i <= 64 - length; i++) {
				implement += "0";
			}
			encryptedCode = encryptedCode + encryptBlock(implement, inputKey);
		}
		return encryptedCode;
	}

	public static String decryption(String longBinaryInput, String inputKey) {
		String decryptedCode = "";
		int length = longBinaryInput.length();
		int cnt = 0; // count
		while (length >= 64) // divide the binary code to each block with 64 bits
		{
			String Block = longBinaryInput.substring(64 * cnt, 64 * (cnt + 1));
			cnt++;
			length -= 64;
			decryptedCode = decryptedCode + decryptBlock(Block, inputKey);
		}
		return decryptedCode;
	}

	public static void runTests() { // normal 64 bits test case
		String output = "";

		output = encryptBlock("1111111111111111111111111111111111111111111111111111111111111111",
				"11111111111111111111111111111111111111111111111111111111");
		System.out.println("Output for: encryption(all ones, all ones)");
		System.out.println(output);

		output = encryptBlock("0000000000000000000000000000000000000000000000000000000000000000",
				"11111111111111111111111111111111111111111111111111111111");
		System.out.println("Output for: encryption(all zeros, all ones)");
		System.out.println(output);

		output = encryptBlock("0000000000000000000000000000000000000000000000000000000000000000",
				"00000000000000000000000000000000000000000000000000000000");
		System.out.println("Output for: encryption(all zeros, all zeros)");
		System.out.println(output);

		output = encryptBlock("1100110010000000000001110101111100010001100101111010001001001100",
				"00000000000000000000000000000000000000000000000000000000");
		System.out.println("Output for: encryption(11001100100000000000011101"
				+ "01111100010001100101111010001001001100, all zeros)");
		System.out.println(output);

		output = decryptBlock("1111111111111111111111111111111111111111111111111111111111111111",
				"11111111111111111111111111111111111111111111111111111111");
		System.out.println("Output for: decryption(all ones, all ones)");
		System.out.println(output);

		output = decryptBlock("0000000000000000000000000000000000000000000000000000000000000000",
				"11111111111111111111111111111111111111111111111111111111");
		System.out.println("Output for: decryption(all zeros, all ones)");
		System.out.println(output);

		output = decryptBlock("0000000000000000000000000000000000000000000000000000000000000000",
				"00000000000000000000000000000000000000000000000000000000");
		System.out.println("Output for: decryption(all zeros, all zeros)");
		System.out.println(output);

		output = decryptBlock("0101011010001110111001000111100001001110010001100110000011110101",
				"11111111111111111111111111111111111111111111111111111111");
		System.out.println("Output for: decryption(0101011010001110111001000111100"
				+ "001001110010001100110000011110101, all ones)");
		System.out.println(output);

		output = decryptBlock("0011000101110111011100100101001001001101011010100110011111010111",
				"00000000000000000000000000000000000000000000000000000000");
		System.out.println("Output for: decryption(0101011010001110111001000111100"
				+ "001001110010001100110000011110101, all zeros)");
		System.out.println(output);
		System.out.println();

	}

	public static void main(String[] args) {
		runTests();
		Scanner input = new Scanner(System.in); // deal with long non-binary input
		System.out.println("Do you want to encrypt or decrypt(E/D): ");
		String status = input.next();
		System.out.println("Filename: ");
		String filename = input.next();
		String result = "";
		String info = ""; // info string to store the binary code of the file
		try {
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			if (status.equals("E")) { // encrypt long Plaintext
				int character;
				while ((character = bufferedReader.read()) != -1) // read the file character by character
				{
					info = info + DecToBinary(character); // change the character to ASCII code integer number
				}
				result = encryption(info, "10101101011101110101010101011100010110101011100010101010");
			} else if (status.equals("D")) // decrypt long Ciphertext
			{
				String line;
				while ((line = bufferedReader.readLine()) != null) // read the file's line
				{
					info = info + line;
				}
				result = decryption(info, "10101101011101110101010101011100010110101011100010101010");
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fileWriter = new FileWriter("data2.txt");
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(result);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}