package com.gsbabil.nfc;

public class Hex {

    private Hex() {
    }

    public static String toHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }
    
    public static String printableHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        int lineLength = 8;
        int byteCount = 0;
        for (byte b : bytes) {
            buff.append(String.format("%02X ", b));
            byteCount++;
            if (byteCount % lineLength == 0) {
            	buff.append("\n");
            }
        }

        return buff.toString();
    }

    public static byte[] fromHex(String digits) {
        digits = digits.replace(" ", "");
        final int bytes = digits.length() / 2;
        if (2 * bytes != digits.length()) {
            throw new IllegalArgumentException(
                    "Hex string must have an even number of digits");
        }

        byte[] result = new byte[bytes];
        for (int i = 0; i < digits.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(digits.substring(i, i + 2),
                    16);
        }
        return result;
    }
    
    public static String Byte2String(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length; ++ i) {
            sb.append((char) data[i]);
        }
        return sb.toString();
    }
    
    
	public static String Byte2Hex(byte[] input) {
		return Byte2Hex(input, " ");
	}

	public static String Byte2Hex(byte[] input, String space) {
		StringBuilder result = new StringBuilder();
		
		for (Byte inputbyte : input) {
			result.append(String.format("%02X" + space, inputbyte));
		}
		return result.toString();
	}
}
