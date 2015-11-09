package com.gsbabil.nfc;

import java.io.IOException;
import android.util.Log;

public class Utilities {
	public void toggleButtons(boolean enable) {
		// App.gpInfoButton.setEnabled(enable);
		// App.emvInfoButton.setEnabled(enable);
		// App.walletInfoButton.setEnabled(enable);

    /* Babil:: Emulation code is currently incomplete. See README.md for
     * more details.
     */
		// App.emulateButton.setEnabled(enable);

		App.readNfcButton.setEnabled(enable);
		// App.writeNfcButton.setEnabled(enable);
	}

	protected byte[] isoDepTransceive(String hexStr) throws IOException {
		String[] hexbytes = hexStr.split("\\s");
		byte[] bytes = new byte[hexbytes.length];
		for (int i = 0; i < hexbytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
		}
		Log.d(App.TAG, "Send: " + Hex.Byte2Hex(bytes));
		byte[] recv = App.isoDep.transceive(bytes);
		Log.d(App.TAG, "Received: " + Hex.Byte2Hex(recv));
		return recv;
	}
}
