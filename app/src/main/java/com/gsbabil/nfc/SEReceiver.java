package com.gsbabil.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

public class SEReceiver extends BroadcastReceiver {

    private static final String TAG = App.TAG;

    public static final String ACTION_AID_SELECTED = "com.android.nfc_extras.action.AID_SELECTED";
    public static final String EXTRA_AID = "com.android.nfc_extras.extra.AID";

    public static final String ACTION_APDU_RECEIVED = "com.android.nfc_extras.action.APDU_RECEIVED";
    public static final String EXTRA_APDU_BYTES = "com.android.nfc_extras.extra.APDU_BYTES";

    public static final String ACTION_EMV_CARD_REMOVAL = "com.android.nfc_extras.action.EMV_CARD_REMOVAL";

    public static final String ACTION_MIFARE_ACCESS_DETECTED = "com.android.nfc_extras.action.MIFARE_ACCESS_DETECTED";
    public static final String EXTRA_MIFARE_BLOCK = "com.android.nfc_extras.extra.MIFARE_BLOCK";
    
    public static final String RF_FIELD_OFF_DETECTED = "com.android.nfc_extras.action.RF_FIELD_OFF_DETECTED";
    public static final String RF_FIELD_ON_DETECTED = "com.android.nfc_extras.action.RF_FIELD_ON_DETECTED";


    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        Log.d(App.TAG, "New Intent Received: " + action);
        if (ACTION_AID_SELECTED.equals(action)) {
            byte[] aid = intent.getByteArrayExtra(EXTRA_AID);
            Log.d(App.TAG, "AID: " + Hex.toHex(aid));
        } else if (ACTION_APDU_RECEIVED.equals(action)) {
            byte[] apdu = intent.getByteArrayExtra(EXTRA_APDU_BYTES);
            Log.d(App.TAG, "APDU: " + Hex.toHex(apdu));
        } else if (ACTION_MIFARE_ACCESS_DETECTED.equals(action)) {
            byte[] block = intent.getByteArrayExtra(EXTRA_MIFARE_BLOCK);
            Log.d(App.TAG, "Mifare block: " + Hex.toHex(block));
        } else if  (RF_FIELD_ON_DETECTED.equals(action)) {
        	Log.d(App.TAG, "RF_FILED_ON_DETECTED");
        	App.utils.toggleButtons(true);
        }  else if  (RF_FIELD_OFF_DETECTED.equals(action)) {
        	Log.d(App.TAG, "RF_FILED_OFF_DETECTED");
        	App.utils.toggleButtons(false);
        }
    }
}
