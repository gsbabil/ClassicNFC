package com.gsbabil.nfc;

import static com.gsbabil.nfc.Hex.fromHex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.gsbabil.nfc.R;

import sasc.emv.EMVApplication;
import sasc.emv.EMVCard;
import sasc.emv.EMVUtil;
import sasc.emv.SW;
import sasc.iso7816.AID;
import sasc.iso7816.BERTLV;
import sasc.terminal.CardConnection;
import sasc.terminal.CardResponse;
import sasc.terminal.Terminal;
import sasc.terminal.TerminalException;
import sasc.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

class App extends Application {
	public static String TAG = "ClassicNFC";
	public static Activity context = null;

	public static Button gpInfoButton;
	public static Button emvInfoButton;
	public static Button walletInfoButton;
	public static Button emulateButton;
	public static Button readNfcButton;
	public static Button writeNfcButton;
	public static Button infoButton;

	public static TextView infoText;

	public static Terminal terminal = null;
	public static CardConnection seConn = null;

	public static NfcAdapter mAdapter = null;
	public static PendingIntent mPendingIntent;
	public static Tag mTagFromIntent = null;
	public static MifareClassic mClassic = null;
	public static IsoDep isoDep = null;
	public static ProgressDialog mProgressDialog = null;

	public static String[][] mPreferredTechLists = new String[][] {
		new String[] { MifareClassic.class.getName() },
		new String[] { NfcA.class.getName() },
		new String[] { IsoDep.class.getName() },
		new String[] { "android.nfc.tech.IsoPcdA" }
		};

	public static Map<int[], byte[]> recentScannedBlocks = new LinkedHashMap<int[], byte[]>();
	public static Handler mHandler = null;

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd_HH-mm-ss";

	public static byte[][] CUSTOM_MIFARE_KEY = {
		{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00 }, /* Blank key */
		{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF },
		{ (byte) 0x16, (byte) 0x0A, (byte) 0x91, (byte) 0xD2,
			(byte) 0x9A, (byte) 0x9C },
		{ (byte) 0xb7, (byte) 0xbf, (byte) 0x0c, (byte) 0x13,
			(byte) 0x06, (byte) 0x6e },
		{ (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			(byte) 0xA4, (byte) 0xA5 },
		{ (byte) 0xB0, (byte) 0xB1, (byte) 0xB2, (byte) 0xB3,
			(byte) 0xB4, (byte) 0xB5 },
		{ (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xF0 },
		{ (byte) 0x48, (byte) 0x45, (byte) 0x58, (byte) 0x41,
			(byte) 0x43, (byte) 0x54 },
		{ (byte) 0xA0, (byte) 0xB1, (byte) 0xC2, (byte) 0xD3,
			(byte) 0xE4, (byte) 0xF5 },
		{ (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD,
			(byte) 0xEE, (byte) 0xFF },
		{ (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
			(byte) 0x89, (byte) 0x01 },
		{ (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
			(byte) 0x89, (byte) 0xAB },
		{ (byte) 0xD3, (byte) 0xF7, (byte) 0xD3, (byte) 0xF7,
			(byte) 0xD3, (byte) 0xF7 },
		{ (byte) 0x4d, (byte) 0x3a, (byte) 0x99, (byte) 0xc3,
			(byte) 0x51, (byte) 0xdd},
		{(byte) 0x1a, (byte) 0x98, (byte) 0x2c, (byte) 0x7e,
			(byte) 0x45, (byte) 0x9a},
		{(byte) 0x71, (byte) 0x4c, (byte) 0x5c, (byte) 0x88,
			(byte) 0x6e, (byte) 0x97},
		{(byte) 0x58, (byte) 0x7e, (byte) 0xe5, (byte) 0xf9,
			(byte) 0x35, (byte) 0x0f},
		{(byte) 0xa0, (byte) 0x47, (byte) 0x8c, (byte) 0xc3,
			(byte) 0x90, (byte) 0x91},
	    {(byte) 0x53, (byte) 0x3c, (byte) 0xb6, (byte) 0xc7,
			(byte) 0x23, (byte) 0xf6},
	    {(byte) 0x8f, (byte) 0xd0, (byte) 0xa4, (byte) 0xf2,
			(byte) 0x56, (byte) 0xe9},
		{(byte) 0x8a, (byte) 0x19, (byte) 0xd4, (byte) 0x0c,
				(byte) 0xf2, (byte) 0xb5}, // 8a19d40cf2b5
	};

	public static final byte[] EMPTY_ARRAY = {};
	public static final String DUMP_FILE_EXTENSION = ".txt";
	public static String SDCARD_DIR = null;
	public static Utilities utils = null;
}

public class MainActivity extends Activity {
	@Override
	public void onResume() {
		super.onResume();
		App.mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
				this, this.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		try {
			App.mAdapter.enableForegroundDispatch(App.context, App.mPendingIntent,
				null, App.mPreferredTechLists);
		} catch (Exception e) {
			Log.d(App.TAG, e.getCause().getMessage().toString());
		}
	}

	@Override
	public void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);

		if (intent.getAction()
				.equals("com.android.nfc_extras.action.RF_FIELD_ON_DETECTED")) {
			App.utils.toggleButtons(true);
		}

		if (intent.getAction()
				.equals("com.android.nfc_extras.action.RF_FIELD_OFF_DETECTED")) {
			App.utils.toggleButtons(false);
		}

		App.mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
				this, this.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			setIntent(intent);
			App.mTagFromIntent = intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			String techList = "";
			for (String tech: App.mTagFromIntent.getTechList()) {
				techList = techList + "\n - " + tech;
			}

			Log.d(App.TAG, "NFC Tech(s): " + techList);
			Toast.makeText(App.context,
					"NFC Technology detected: " + techList,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		App.mAdapter = NfcAdapter.getDefaultAdapter(App.context);
		if (App.mAdapter != null) {
			App.mAdapter.disableForegroundDispatch(App.context);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.optionMenuExit) {
        	closeSeSilently();
			Vibrator vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			vib.vibrate(64);
			Toast.makeText(getApplicationContext(), "Goodbye!",
					Toast.LENGTH_SHORT).show();
			App.context.finish();
			return true;

        } else if (item.getItemId() == R.id.optionMenuClearScreen) {
        	App.infoText.setText("");
        	return true;

        } else if (item.getItemId() == R.id.optionMenuEditSavedTag) {
            File mPath = new File(Environment.getExternalStorageDirectory() + "/"
            		+ App.SDCARD_DIR);

            FileSelectionDialog fileSelectionDialog = new FileSelectionDialog(App.context, mPath);
            fileSelectionDialog.setFileEndsWith(".mfd");
            fileSelectionDialog.addFileListener(new FileSelectionDialog.FileSelectedListener() {
                public void fileSelected(File file) {
                    Log.d(App.TAG, "Selected file to edit" + file.toString());

                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    Uri uri = Uri.parse(file.toURI().toString());
                    intent.setDataAndType(uri, "text/plain");
                    startActivity(intent);
                }
            });

            fileSelectionDialog.showDialog();
        	return true;

        } else if (item.getItemId() == R.id.optionMenuLoadSavedTag) {
            File mPath = new File(Environment.getExternalStorageDirectory() + "/"
            		+ App.SDCARD_DIR);

            FileSelectionDialog fileSelectionDialog = new FileSelectionDialog(App.context, mPath);
            fileSelectionDialog.setFileEndsWith(".mfd");
            fileSelectionDialog.addFileListener(new FileSelectionDialog.FileSelectedListener() {
                public void fileSelected(File file) {
                    Log.d(App.TAG, "Selected file to load" + file.toString());
                    readBlocksFromFile(file.toString());
                }
            });

            fileSelectionDialog.showDialog();

            //fileDialog.addDirectoryListener(new FileSelectionDialog.DirectorySelectedListener() {
            //  public void directorySelected(File directory) {
            //      Log.d(getClass().getName(), "selected dir " + directory.toString());
            //  }
            //});
            //fileDialog.setSelectDirectoryOption(false);


        	return true;
        }
        return false;
    }

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		App.context = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setProgressBarIndeterminateVisibility(false);

		App.gpInfoButton = (Button) findViewById(R.id.gp_info_button);
		App.emvInfoButton = (Button) findViewById(R.id.emv_info_button);
		App.walletInfoButton = (Button) findViewById(R.id.wallet_info_button);
		App.emulateButton = (Button) findViewById(R.id.emulate_button);
		App.readNfcButton = (Button) findViewById(R.id.read_nfc_button);
		App.writeNfcButton = (Button) findViewById(R.id.write_nfc_button);

		App.infoText = (TextView) findViewById(R.id.info_text);
		App.infoButton = (Button) findViewById(R.id.info_button);

		App.utils = new Utilities();
		App.SDCARD_DIR = App.context.getString(R.string.app_name)
				.replace(" ", "");

		setIntent(getIntent());

		App.mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            String tmp[] = (String [])msg.obj;

	            if (tmp[0].equals("infoButton")) {
	            	App.infoButton.setText(tmp[1]);
	            } else if (tmp[0].equals("infoText")) {
	            	infoTextUpdateFromHandler(tmp[1]);
	            }
	        }
	   };
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		closeSeSilently();
	}

	private void closeSeSilently() {
		if (App.seConn != null) {
			try {
				App.seConn.disconnect(false);
			} catch (TerminalException e) {
				Log.w(App.TAG, "Error closing SE: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		App.mAdapter = NfcAdapter.getDefaultAdapter(App.context);
		App.mPendingIntent = PendingIntent.getActivity(App.context, 0,
				new Intent(App.context, getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	@Override
	public void onStop() {
		super.onStop();

		closeSeSilently();
		App.seConn = null;
	}

	@SuppressWarnings("resource")
	public void copyFile(File src, File dst) throws IOException {
	    FileChannel inChannel = new FileInputStream(src).getChannel();
	    FileChannel outChannel = new FileOutputStream(dst).getChannel();
	    try {
	        inChannel.transferTo(0, inChannel.size(), outChannel);
	    } finally {
	        if (inChannel != null)
	            inChannel.close();
	        if (outChannel != null)
	            outChannel.close();
	    }
	}

	public boolean saveInExternalStorage(String fileName) {
		String dirName = App.SDCARD_DIR;
		boolean success = true;

		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
				File dstDir = new File(Environment.getExternalStorageDirectory(), dirName);
				if (dstDir.exists() == false && dstDir.isDirectory() == false) {
					dstDir.mkdirs();
				}

				File srcFile = new File(App.context.getFilesDir() + "/" + fileName);
				File dstFile = new File(dstDir.getAbsolutePath() + "/" + fileName);
				try {
					copyFile(srcFile, dstFile);
				} catch (IOException e) {
					success = false;
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	public boolean isTechPresent(String[] techList, String req) {
		boolean techDetected = false;

		for (String tech: techList) {
			if (tech.equals(req)) {
				techDetected = true;
				break;
			}
		}

		return techDetected;
	}

	private void readIsoDep(final Intent intent) {
		new AsyncTask<Void, Void, Object[]>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);

				App.mAdapter = NfcAdapter.getDefaultAdapter(App.context);
				App.mPendingIntent = PendingIntent.getActivity(App.context, 0,
						new Intent(App.context, getClass())
								.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				Log.d(App.TAG, "intent.getAction() -> " + intent.getAction());
				if (NfcAdapter.ACTION_TECH_DISCOVERED
						.equals(intent.getAction())) {
					App.mTagFromIntent = intent
							.getParcelableExtra(NfcAdapter.EXTRA_TAG);

					if (isTechPresent(App.mTagFromIntent.getTechList(),
							IsoDep.class.getName())) {
						App.isoDep = IsoDep.get(App.mTagFromIntent);
						String data = readCreditCard();
						infoTextUpdate("\n\n" + data + "\n\n");
					}
				} else {
					infoTextUpdate("[●] ReadTag :: IsoDep not found. \n");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object[] data) {
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);

				App.mAdapter = null;
				App.mPendingIntent = null;
			}
		}.execute();
	}

	public String readCreditCard() {
		int cardNumOffset = 29;
		int cardExpOffset = 73;

		String cardNum = "";
		String cardExp = "";
		String expYear = "";
		String expMon = "";

		String response = "";

		byte[] recv = {};

		try {
			App.isoDep.connect();
			recv = App.utils
					.isoDepTransceive("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
			recv = App.utils
					.isoDepTransceive("00 A4 04 00 07 A0 00 00 00 04 10 10 00");
			recv = App.utils.isoDepTransceive("80 A8 00 00 02 83 00 00");
			recv = App.utils.isoDepTransceive("00 B2 01 0C 00");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String str = Hex.Byte2String(recv);

		/* Babil: parse card no. by looking for 'VLB' */
		try {
			cardNum = str.substring(cardNumOffset, cardNumOffset + 16);
		} catch (Exception e) {
			cardNum = "(not found)";
		}

		/* Babil: parse expiry by splitting on '^' */
		try {
			cardExp = str.substring(cardExpOffset, cardExpOffset + 4);
		} catch (Exception e) {
			cardExp = "(not found)";
		}

		if (cardNum.length() == 16) {
			response = "Card number: " + cardNum + "\n";
		}

		if (cardExp.length() == 4) {
			expYear = "20" + cardExp.substring(0, 2);
			expMon = cardExp.substring(2, 4);

			response = response + "Expiry: " + expMon + "/" + expYear;
		}

		return response;
	}

	/* Babil: from MFOC source. I using a different approach. Not using this
	 * function at the moment.
	 */
	public boolean isTrailerBlock(int block) {
		if (block < 128) {
			if ((block + 1) % 4 == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if((block + 1) % 16 == 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void getGpInfo() {
		new AsyncTask<Void, Void, Object[]>() {

			Exception error;

			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				App.terminal = new SETerminal(getApplication());
				if (App.terminal != null) {
					try {
						App.seConn = App.terminal.connect();
					} catch (TerminalException e) {
						String message = "Failed to open SE: " + e.getMessage();
						Log.w(App.TAG, message, e);
						Toast.makeText(App.context, message, Toast.LENGTH_LONG)
								.show();
						App.seConn = null;
						finish();
					}
				} else {
					infoTextUpdate("[●] Can not open SE terminal.");
					return null;
				}

				try {
					CardResponse response = transmit(GPCommands.EMPTY_SELECT,
							"EMPTY_SELECT");
					if (response.getSW() != SW.SUCCESS.getSW()) {
						// do something
						return null;
					}

					SecurityDomainFCI sdFci = SecurityDomainFCI.parse(response
							.getData());
					Log.d(App.TAG, "SD FCI: " + sdFci.toString());

					response = transmit(GPCommands.GET_ISSUER_ID_COMMAND,
							"GET_ISSUER_ID_COMMAND");

					response = transmit(fromHex("00CA004500"),
							"GET_CARD_IMAGE_NUMBER");

					response = transmit(GPCommands.GET_CARD_DATA,
							"GET_CARD_DATA");

					List<KeyInfo> keys = null;
					response = transmit(
							GPCommands.GET_KEY_INFORMATION_TEMPLATE_COMMAND,
							"GET_KEY_INFORMATION_TEMPLATE_COMMAND");
					if (response.getSW() == SW.SUCCESS.getSW()) {
						keys = KeyInfo.parse(response.getData());
						for (KeyInfo key : keys) {
							Log.d(App.TAG, "Key: " + key);
						}
					}

					response = transmit(
							GPCommands.GET_KEY_VERSION_SEQUENCE_COUNTER_COMMAND,
							"GET_KEY_VERSION_SEQUENCE_COUNTER_COMMAND");

					response = transmit(GPCommands.GET_CPLC_COMMAND,
							"GET_CPLC_COMMAND");
					CPLC cplc = null;
					if (response.getSW() == SW.SUCCESS.getSW()) {
						cplc = CPLC.parse(response.getData());
					}
					Log.d(App.TAG, "CPLC: " + cplc);

					return new Object[] { sdFci, keys, cplc };
				} catch (Exception e) {
					Log.e(App.TAG, "Error:" + e.getMessage(), e);
					return null;
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Object[] data) {
				closeSeSilently();
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);

				if (data == null && error != null) {
					Toast.makeText(MainActivity.this,
							"Error: " + error.getMessage(), Toast.LENGTH_LONG)
							.show();

					return;
				}

				if (data == null) {
					App.infoText
							.setText("Error selecting CardManager. Does the SE work?");
					return;
				}

				App.infoText.setText(getGpInfoDisplayString(
						(SecurityDomainFCI) data[0], (List<KeyInfo>) data[1],
						(CPLC) data[2]));
			}
		}.execute();
	}

	public void getWalletInfo() {
		new AsyncTask<Void, Void, EMVCard>() {

			Exception error;

			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);
			}

			@Override
			protected EMVCard doInBackground(Void... arg0) {
				App.terminal = new SETerminal(getApplication());
				if (App.terminal != null) {
					try {
						App.seConn = App.terminal.connect();
					} catch (TerminalException e) {
						String message = "Failed to open SE: " + e.getMessage();
						Log.w(App.TAG, message, e);
						Toast.makeText(App.context, message, Toast.LENGTH_LONG)
								.show();
						App.seConn = null;
						finish();
					}
				} else {
					infoTextUpdate("[●] Can not open SE terminal.");
					return null;
				}

				try {
					SEEMVSession emvSession = SEEMVSession.startSession(
							getApplication(), App.seConn);
					EMVCard emvCard = emvSession.initCard();
					Log.d(App.TAG, "card: " + emvCard);
					if (emvCard != null) {
						Collection<EMVApplication> apps = emvCard
								.getApplications();
						for (EMVApplication app : apps) {
							Log.d(App.TAG, "EMV app: " + app);
							// always fails with 0x6999
							// emvSession.selectApplication(app);
						}
					}

					return emvCard;
				} catch (Exception e) {
					Log.e(App.TAG, "Error: " + e.getMessage(), e);
					error = e;
					return null;
				}
			}

			@Override
			protected void onPostExecute(EMVCard emvCard) {
				closeSeSilently();
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);

				if (emvCard == null && error != null) {
					Toast.makeText(MainActivity.this,
							"Error: " + error.getMessage(), Toast.LENGTH_LONG)
							.show();

					return;
				}
				App.infoText.setText(getEmvCardDisplayString(emvCard));
			}
		}.execute();
	}

	public void getEmvInfo() {
		new AsyncTask<Void, Void, Object[]>() {

			Exception error;

			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				App.terminal = new SETerminal(getApplication());
				if (App.terminal != null) {
					try {
						App.seConn = App.terminal.connect();
					} catch (TerminalException e) {
						String message = "Failed to open SE: " + e.getMessage();
						Log.w(App.TAG, message, e);
						Toast.makeText(App.context, message, Toast.LENGTH_LONG)
								.show();
						App.seConn = null;
						finish();
					}
				} else {
					infoTextUpdate("[+] Can not open SE terminal.");
					return null;
				}

				try {
					CardResponse response = transmit(
							WalletControllerCommands.SELECT_WALLET_CONTROLLER_COMMAND,
							"SELECT_WALLET_CONTROLLER_COMMAND");

					WalletControllerFCI wcFci = null;
					if (response.getSW() == SW.SUCCESS.getSW()) {
						wcFci = WalletControllerFCI.parse(response.getData());
						Log.d(App.TAG, "Wallet controller: " + wcFci.toString());
					} else {
						Log.d(App.TAG, "Wallet controller applet not found");
					}

					AID mmAid = null;
					response = transmit(
							MifareManagerCommands.SELECT_MIFARE_MANAGER,
							"SELECT_MIFARE_MANAGER_COMMAND");
					if (response.getSW() == SW.SUCCESS.getSW()) {
						mmAid = MifareManagerCommands.MIFARE_MANAGER_AID;
					} else {
						Log.d(App.TAG, "Mifare manager applet not found. SW: "
								+ Integer.toHexString(response.getSW()));
					}

					return new Object[] { wcFci, mmAid };
				} catch (Exception e) {
					Log.e(App.TAG, "Error: " + e.getMessage(), e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(Object[] data) {
				closeSeSilently();
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);

				if (data == null && error != null) {
					Toast.makeText(MainActivity.this,
							"Error: " + error.getMessage(), Toast.LENGTH_LONG)
							.show();

					return;
				}

				App.infoText.setText(getWalletDisplayString(
						(WalletControllerFCI) data[0], (AID) data[1]));
			}
		}.execute();
	}

	private void writeMifareClassic(final Intent intent) {
		new AsyncTask<Void, Void, Object[]>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);

				App.mAdapter = NfcAdapter.getDefaultAdapter(App.context);
				App.mPendingIntent = PendingIntent.getActivity(App.context, 0,
						new Intent(App.context, getClass())
								.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				Log.d(App.TAG, "intent.getAction() -> " + intent.getAction());
				if (NfcAdapter.ACTION_TECH_DISCOVERED
						.equals(intent.getAction())) {

					App.mTagFromIntent = intent
							.getParcelableExtra(NfcAdapter.EXTRA_TAG);


					App.mClassic = MifareClassic.get(App.mTagFromIntent);
					writeMifareSectors();
				} else {
					infoTextUpdate("[●] Tag to write not found. \n");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object[] data) {
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);
				App.mAdapter = null;
				App.mPendingIntent = null;
			}

		}.execute();
	}

	private void readMifareClassic(final Intent intent) {
		new AsyncTask<Void, Void, Object[]>() {
			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);

				App.mAdapter = NfcAdapter.getDefaultAdapter(App.context);
				App.mPendingIntent = PendingIntent.getActivity(App.context, 0,
						new Intent(App.context, getClass())
								.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				Log.d(App.TAG, "intent.getAction() -> " + intent.getAction());
				if (NfcAdapter.ACTION_TECH_DISCOVERED
						.equals(intent.getAction())) {
					App.mTagFromIntent = intent
							.getParcelableExtra(NfcAdapter.EXTRA_TAG);

					if (isTechPresent(App.mTagFromIntent.getTechList(),
							MifareClassic.class.getName())) {
						App.mClassic = MifareClassic.get(App.mTagFromIntent);
						readMifareClassicSectors();
					}
				} else {
					infoTextUpdate("[●] ReadTag :: Mifare Classic not found. \n");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object[] data) {
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);

				App.mAdapter = null;
				App.mPendingIntent = null;
			}
		}.execute();
	}

	public void readBlocksFromFile(String fileName) {

		/* Babil: format of data to read.
		 *
		 * 	sector:0 block:0
		 *	CD 70 CE 52 21 08 04 00
		 *	62 63 64 65 66 67 68 69
		 *	sector:0 block:1
		 *	80 0F 00 00 00 00 00 00
		 *	00 00 00 00 00 00 00 00
		 *
		 */

        String line = "";
        App.recentScannedBlocks.clear();

        try {
        	FileReader fr = new FileReader(fileName);
        	BufferedReader br = new BufferedReader(fr);
        	int readBlockCount = 0;
        	int readErrorCount = 0;
        	String readErrorBlocks = "";

            while ((line = br.readLine()) != null) {
                String tokens[] = line.trim().split(" +");
                int sectorIndex = -1;
                int blockIndex = -1;

                try {
	                if (tokens.length > 0) {
	                	if (tokens[0].contains("sector:")) {
	                		sectorIndex = Integer.valueOf(tokens[0].split(":")[1]);
	                		blockIndex = Integer.valueOf(tokens[1].split(":")[1]);
	                	}

	                	if (sectorIndex >= 0 && blockIndex >= 0) {
	                		int bs[] = {blockIndex, sectorIndex};

	                		String blockData = br.readLine().trim();
	                		blockData = blockData + " " + br.readLine().trim();
	                		byte data[] = Hex.fromHex(blockData);

	                		App.recentScannedBlocks.put(bs, data);

	                		infoTextUpdate("\nLoading → sector: "
	                				+ sectorIndex + " block: " + blockIndex + "\n");
	                		infoTextUpdate(Hex.printableHex(data));
	                		readBlockCount = readBlockCount + 1;
	                	}
	                }
                } catch (Exception e) {
                	readErrorCount = readErrorCount + 1;
					if (readErrorBlocks.length() == 0) {
						readErrorBlocks = (Integer.valueOf(sectorIndex)).toString();
					} else {
						readErrorBlocks = readErrorBlocks + " " + sectorIndex;
					}
                	e.printStackTrace();
                }
            }

			infoTextUpdate("\n\nLoading Finished.\n");
			infoTextUpdate("- Successfully loaded " + readBlockCount + " block(s). \n");
			if (readErrorCount > 0) {
				infoTextUpdate("- Error loading " + readErrorCount + " block(s). [" + readErrorBlocks + "]");
			}
			infoTextUpdate("\n\n");
			infoButtonUpdate(readBlockCount + " block(s) loaded in memory.\n Write it to a tag?");

            br.close();
            fr.close();

        } catch (Exception e){
        	e.printStackTrace();
        }
	}

	public void performIsoPcdAEmulation() {
		// TO-DO - fill this function
	}

	/*
	 * Babil:
	 * http://stackoverflow.com/questions/12955919/enabling-cardemulation-
	 * on-android-ics-with-nfc-extras
	 *
	 * Context mContext = App.context; NfcAdapterExtras nfcAdapterExtras =
	 * NfcAdapterExtras.get(NfcAdapter.getDefaultAdapter(mContext));
	 * NfcExecutionEnvironment mEe =
	 * nfcAdapterExtras.getEmbeddedExecutionEnvironment();
	 * nfcAdapterExtras.setCardEmulationRoute(new
	 * CardEmulationRoute(CardEmulationRoute.ROUTE_ON_WHEN_SCREEN_ON, mEe));
	 */

	public void enableSEemulation() {
		new AsyncTask<Void, Void, Object[]>() {

			@Override
			protected void onPreExecute() {
				setProgressBarIndeterminateVisibility(true);
				App.utils.toggleButtons(false);
			}

			@Override
			protected Object[] doInBackground(Void... arg0) {
				/* Babil: Java reflection hell */
				Class<?> nfcExtrasClazz;
				Object nfcAdapterExtras;
				Object mExecEnv;

				App.terminal = new SETerminal(getApplication());
				if (App.terminal != null) {
					try {
						App.seConn = App.terminal.connect();
					} catch (TerminalException e) {
						String message = "Failed to open SE: " + e.getMessage();
						Log.w(App.TAG, message, e);
						Toast.makeText(App.context, message, Toast.LENGTH_LONG)
								.show();
						App.seConn = null;
						finish();
					}
				} else {
					infoTextUpdate("[●] Can not open SE terminal.");
				}

				try {
					nfcExtrasClazz = Class
							.forName("com.android.nfc_extras.NfcAdapterExtras");

					Method getMethod = nfcExtrasClazz.getMethod("get",
							Class.forName("android.nfc.NfcAdapter"));

					nfcAdapterExtras = getMethod.invoke(nfcExtrasClazz,
							((SETerminal) App.terminal).getDefaultAdapter());

					// public NfcExecutionEnvironment
					// getEmbeddedExecutionEnvironment()
					Method getEEMethod = nfcAdapterExtras.getClass().getMethod(
							"getEmbeddedExecutionEnvironment",
							(Class<?>[]) null);
					mExecEnv = getEEMethod.invoke(nfcAdapterExtras,
							(Object[]) null);

					Class<?> clsCardEmuRoute = (Class<?>) nfcExtrasClazz
							.getDeclaredClasses()[0];

					Constructor<?> c = clsCardEmuRoute
							.getConstructor(
									Integer.TYPE,
									Class.forName("com.android.nfc_extras.NfcExecutionEnvironment"));

					// int routeOnValue = 1; /* disable emulation */
					int routeOnValue = 2; /* enable emulation */

					Object cardEmuRoute = c.newInstance(routeOnValue, mExecEnv);

					Method setCardEmulateRouteMethod = nfcAdapterExtras
							.getClass().getMethod("setCardEmulationRoute",
									clsCardEmuRoute);

					setCardEmulateRouteMethod.invoke(nfcAdapterExtras,
							cardEmuRoute);


					infoTextUpdate("[●] Emulation Mode enabled.\n");

					// Method getCardEmulateRouteMethod =
					// nfcAdapterExtras.getClass().getDeclaredMethod(
					// "getCardEmulationRoute", (Class<?>[]) null);
					// Object tt =
					// getCardEmulateRouteMethod.invoke(nfcAdapterExtras);


	                App.terminal = new SETerminal(getApplication());
	                if (App.terminal != null) {
	                    try {
	                    	if (App.seConn.getConnectionInfo().length() <=0) {
	                    		App.seConn = App.terminal.connect();
	                    	}
	                    } catch (TerminalException e) {
	                        String message = "Failed to open SE: " + e.getMessage();
	                        Log.w(App.TAG, message, e);
	                        Toast.makeText(App.context, message, Toast.LENGTH_LONG)
	                                .show();
	                        App.seConn = null;
	                        finish();
	                    }
	                } else {
	                    infoTextUpdate("[●] Can not open SE terminal.");
	                    return null;
	                }

//	    			CardResponse response = transmit(MifareManagerCommands.SELECT_MIFARE_MANAGER,
//                    		"SELECT_MIFARE");
//                  response = transmit(MifareManagerCommands.PUT_MIFARE_KEYS_FIRST_K,
//                    		"PUT_MIFARE_KEYS");

//	                App.seConn.disconnect(true);


				} catch (Exception e) {
					Log.w(App.TAG, e.getMessage().toString());
				}

				return null;
			}

			@Override
			protected void onPostExecute(Object[] data) {
				closeSeSilently();
				setProgressBarIndeterminateVisibility(false);
				App.utils.toggleButtons(true);
			}
		}.execute();
	}

	public void onClick(View v) {
		try {
			if (v.getId() == R.id.gp_info_button) {
				getGpInfo();
			} else if (v.getId() == R.id.emv_info_button) {
				getWalletInfo();
			} else if (v.getId() == R.id.wallet_info_button) {
				getEmvInfo();
			} else if (v.getId() == R.id.emulate_button) {
				enableSEemulation();
			} else if (v.getId() == R.id.read_nfc_button) {
				setIntent(getIntent());
				readMifareClassic(getIntent());
				readIsoDep(getIntent());
			} else if (v.getId() == R.id.write_nfc_button) {
				setIntent(getIntent());
				writeMifareClassic(getIntent());
			} else if (v.getId() == R.id.info_button) {
				dumpRecentScannedBlocks();
			}
		} catch (Exception e) {
			Log.e(App.TAG, "Error: " + e.getMessage());
			Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	private CardResponse transmit(byte[] command, String description)
			throws TerminalException {
		Log.d(App.TAG, description);
		CardResponse response = App.seConn.transmit(command);
		EMVUtil.printResponse(response, true);

		return response;
	}

	private CharSequence getGpInfoDisplayString(SecurityDomainFCI sdFci,
			List<KeyInfo> keys, CPLC cplc) {
		StringBuilder buff = new StringBuilder();
		buff.append(sdFci.toString());
		buff.append("\n\n");
		if (keys != null) {
			buff.append("Security domain keys:\n");
			for (KeyInfo key : keys) {
				buff.append(Util.getSpaces(2) + key.toString() + "\n");
			}
		}
		buff.append("\n");

		buff.append("Card Production Life Cyle Data");
		buff.append("\n");
		buff.append(cplc.toString());

		return buff.toString();
	}


	private String getWalletDisplayString(WalletControllerFCI wcFci, AID mmAid) {
		StringBuilder buff = new StringBuilder();
		buff.append("Wallet applets: ");
		buff.append("\n\n");
		buff.append(wcFci == null ? "Wallet controller: not installed" : wcFci
				.toString());
		buff.append("\n\n");
		buff.append("MIFARE manager applet");
		buff.append("\n");
		buff.append(mmAid == null ? "Mifare manager: not installed" : mmAid
				.toString());

		return buff.toString();
	}

	private String getEmvCardDisplayString(EMVCard card) {
		StringBuilder buff = new StringBuilder();
		int indent = 2;

		if (card.getApplications().isEmpty()) {
			buff.append("Google Wallet not installed or locked. Install and unlock Wallet and try again.");
			buff.append("\n");
			// PPSE in fact
			if (card.getPSE() != null) {
				buff.append("\n");
				buff.append("PPSE: ");
				buff.append("\n");
				buff.append(Util.getSpaces(indent) + card.getPSE().toString());

				return buff.toString();
			}
		}

		buff.append((Util.getSpaces(indent) + "EMV applications on SE"));
		buff.append("\n\n");
		buff.append(Util.getSpaces(indent + 2 * indent) + "Applications ("
				+ card.getApplications().size() + " found):");
		buff.append("\n");
		for (EMVApplication app : card.getApplications()) {
			buff.append(Util.getSpaces(indent + 3 * indent) + app.toString());
		}

		if (card.getMasterFile() != null) {
			buff.append(Util.getSpaces(indent + indent) + "MF: "
					+ card.getMasterFile());
		}

		buff.append("Extra info (if any)");
		buff.append(Util.getSpaces(indent) + "ATR: " + App.seConn.getATR());
//		buff.append(Util.getSpaces(indent + indent) + "Interface Type: "
//		 + card.getType());
		buff.append("\n");

		if (!card.getUnhandledRecords().isEmpty()) {
			buff.append(Util.getSpaces(indent + indent)
					+ "UNHANDLED GLOBAL RECORDS ("
					+ card.getUnhandledRecords().size() + " found):");

			for (BERTLV tlv : card.getUnhandledRecords()) {
				buff.append(Util.getSpaces(indent + 2 * indent) + tlv.getTag()
						+ " " + tlv);
			}
		}
		buff.append("\n");

		return buff.toString();
	}

	public void infoButtonUpdate(String text) {
		Message msg = new Message();
		if (text.length() == 0) {
			text = "";
		}

		String tmp[] = {"infoButton", text};
		msg.obj = tmp;
		App.mHandler.sendMessage(msg);
	}

	public void infoTextUpdate(String text) {
		Message msg = new Message();
		if (text.length() == 0) {
			text = "";
		}

		String tmp[] = {"infoText", text};
		msg.obj = tmp;
		App.mHandler.sendMessage(msg);
	}

	public void infoTextUpdateFromHandler(String msg) {
		final TextView tv = (TextView) App.context.findViewById(R.id.info_text);
		final ScrollView sv = (ScrollView) App.context.findViewById(R.id.scrollView);
		tv.append(msg);

		try {
			int lineTop = 0;
			try {
				lineTop = tv.getLayout().getLineTop(tv.getLineCount());
			} catch (Throwable e) {
				lineTop = 0;
			}

			final int scrollAmount = lineTop - tv.getHeight();

			if (scrollAmount > 0) {
				tv.scrollTo(0, scrollAmount);
			} else {
				tv.scrollTo(0, 0);
			}

			sv.post(new Runnable() {
			    @Override
			    public void run() {
			           sv.fullScroll(View.FOCUS_DOWN);
			    }
			});

		} catch (Throwable e) {
			Log.i(App.TAG, e.getMessage().toString());
		}
	}

	private byte[] adjustKeysAndPermBits(byte[] keyA, byte[] keyB) {
		if (keyA.length < 6) {

			byte[] keyA2 = {
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			};
			keyA = keyA2;
		}

		if (keyB.length < 6) {
			byte[] keyB2 = {
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			};
			keyB = keyB2;
		}

		byte[] trailer_block = {
			/* Key-A */
			(byte) keyA[0], (byte) keyA[1], (byte) keyA[2],
			(byte) keyA[3], (byte) keyA[4], (byte) keyA[5],

			/* Permission bits */
			(byte) 0xFF, (byte) 0x07, (byte) 0x80, (byte) 0x69,

			/* Key-B */
			(byte) keyB[0], (byte) keyB[1], (byte) keyB[2],
			(byte) keyB[3], (byte) keyB[4], (byte) keyB[5],
			};

		return trailer_block;
	}

	/* Babil: sector index is needed to authenticate */
	private boolean writeMifareBlock(int bs[], byte[] data) {
		boolean result = false;
		int blockIndex = bs[0];
		int sectorIndex = bs[1];

		try {
			if (App.mClassic.isConnected()) {
				App.mClassic.close();
			} else {
				App.mClassic.connect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		/* Babil: check if this is a trailer block */
		int blockCountInSector = App.mClassic.getBlockCountInSector(sectorIndex);
		if ((blockIndex+1) % blockCountInSector == 0) {
			Log.d(App.TAG, "Tailer block - forcing permission bits. "
					+ sectorIndex + " " + blockIndex);
//			0, 1, 2, 3, 4, 5 --> keyA
			data[6] = (byte) 0xff;
			data[7] = (byte) 0x07;
			data[8] = (byte) 0x80;
			data[9] = (byte) 0x69;
//			10, 11, 12, 13, 14, 15 --> keyB
			Log.d(App.TAG, Hex.printableHex(data));
		}

		if (sectorIndex >=0 && blockIndex >= 0 && data.length > 0) {
			for (int i = 0; i < App.CUSTOM_MIFARE_KEY.length; i++) {
				byte[] key = App.CUSTOM_MIFARE_KEY[i];
				try {
					if (App.mClassic.authenticateSectorWithKeyB(sectorIndex, key)) {
					App.mClassic.writeBlock(blockIndex, data);
						result = true;
						break;
					} else if (App.mClassic.authenticateSectorWithKeyA(sectorIndex, key)) {
						App.mClassic.writeBlock(blockIndex, data);
						result = true;
						break;
					}
				}
				catch (final TagLostException e) {
					e.printStackTrace();
					result = false;
				}
				catch (IOException e) {
					e.printStackTrace();
					result = false;
				}
			}
		}

		try {
			if (App.mClassic.isConnected()) {
				App.mClassic.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private void dumpRecentScannedBlocks() {
		Set<int[]> blocks = App.recentScannedBlocks.keySet();

		infoTextUpdate("\n");

		try {
			for (int[] bs : blocks) {
				infoTextUpdate("\nBuffer → Sector: " + bs[1] + " Block: "
						+ bs[0] + "\n");
				infoTextUpdate(Hex
						.printableHex(App.recentScannedBlocks.get(bs)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		infoTextUpdate("\n\nRead Buffer Dumped.\n");
		infoTextUpdate("- " + App.recentScannedBlocks.keySet().size() + " block(s) found in buffer");
	}

	private void writeMifareSectors() {
		Set<int[]> blocks = App.recentScannedBlocks.keySet();

		infoTextUpdate("\n");

		try {
			for (int[] bs : blocks) {
				infoTextUpdate("\nWriting → Sector: " + bs[1] + " Block: "
						+ bs[0] + "\n");
				infoTextUpdate(Hex.printableHex(App.recentScannedBlocks
						.get(bs)));

					boolean result = writeMifareBlock(bs,
							App.recentScannedBlocks.get(bs));
					if (result == true) {
						infoTextUpdate("Success! \n");
					} else {
						infoTextUpdate("Failed! \n");
					}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		infoTextUpdate("\n\nWriting Finished.\n\n");
	}

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT_NOW,
				Locale.getDefault());
		return sdf.format(cal.getTime());
	}

	@SuppressLint({ "WorldWriteableFiles" })
	private void readMifareClassicSectors() {
		StringBuilder sb = new StringBuilder();
		int blockReadSuccess = 0;
		int authFailedCount = 0;
		String authFailedSectors = "";
		App.recentScannedBlocks.clear();

		String fileName = "";
		FileOutputStream fileOut = null;
		OutputStreamWriter osw = null;

		try {
			if (!App.mClassic.isConnected()) {
				App.mClassic.connect();
			}

			int totalSectorCount = App.mClassic.getSectorCount();
			int blockCount = 0;

			fileName = now() + ".mfd";
			fileOut = App.context.openFileOutput(
					fileName, Context.MODE_WORLD_WRITEABLE);
			osw = new OutputStreamWriter(fileOut);

			/* Babil: iterate over the sectors */
			for (int sectorIndex = 0; sectorIndex < totalSectorCount; sectorIndex++) {

				/* Babil: brute-force with default keys */
				boolean keyA_found = false;
				boolean keyB_found = false;
				byte[] keyA = {};
				byte[] keyB = {};

				for (int i = 0; i < App.CUSTOM_MIFARE_KEY.length; i++) {
					byte[] key = App.CUSTOM_MIFARE_KEY[i];
					keyA = App.EMPTY_ARRAY;

					if (!App.mClassic.isConnected()) {
						App.mClassic.connect();
					}

					if (App.mClassic.authenticateSectorWithKeyA(sectorIndex, key)) {
						keyA_found = true;
						keyA = key;
						Log.d(App.TAG, "Sector: " + sectorIndex + " keyA found : " + Hex.printableHex(keyA));
						break;
					}
				}

				for (int i = 0; i < App.CUSTOM_MIFARE_KEY.length; i++) {
					byte[] key = App.CUSTOM_MIFARE_KEY[i];
					keyB = App.EMPTY_ARRAY;

					if (!App.mClassic.isConnected()) {
						App.mClassic.connect();
					}

					if (App.mClassic.authenticateSectorWithKeyB(sectorIndex, key)) {
						keyB_found = true;
						keyB = key;
						Log.d(App.TAG, "Sector: " + sectorIndex + " keyB found : " + Hex.printableHex(keyB));
						break;
					}
				}

				if (keyA_found && keyB_found) {
					/* Babil: grab data from all blocks in this sector */
					int lastBlockInSector = blockCount
							+ App.mClassic.getBlockCountInSector(sectorIndex);

					for (int blockIndex = blockCount; blockIndex < lastBlockInSector; blockIndex++) {
						String block_title = "\nReading → Sector: " + sectorIndex + " Block: " + blockIndex + " \n";

						Log.d(App.TAG, block_title);
						sb.append(block_title);
						String pretty_data = new String();

						/* Babil: at this point we should have both key A and B.
						 * So, let the snooping party begin!
						 */
						try {

							if (!App.mClassic.isConnected()) {
								App.mClassic.connect();
							}

							if(!App.mClassic.authenticateSectorWithKeyA(sectorIndex, keyA)) {
								App.mClassic.authenticateSectorWithKeyB(sectorIndex, keyB);
							}
							byte[] data = App.mClassic.readBlock(blockIndex);

							/* Babil: after reading block-0, we will have the
							 * UID of the tag. So, lets prepend the UID to the
							 * file-name.
							 */
							if (blockIndex == 0) {
								String uid = Hex.toHex(Arrays.copyOfRange(data, 0, 4));
								fileName = uid + "_" + fileName;

								/* Babil: close file handles */
								osw.flush();
								osw.close();
								fileOut.close();

								/* Babil: move/rename the file */
								File file = new File(fileName);
								file.renameTo(new File(fileName));

								/* Babil: now reopen the new file as if
								 * nothing happened */
								fileOut = App.context.openFileOutput(
										fileName, (Context.MODE_WORLD_READABLE
												| Context.MODE_WORLD_WRITEABLE));
								osw = new OutputStreamWriter(fileOut);
							}

							/* Babil: sanity check for trailer blocks */
							int blockCountInSector = App.mClassic
									.getBlockCountInSector(sectorIndex);
							if ((blockIndex + 1) % blockCountInSector == 0) {
								Log.d(App.TAG,
										"Tailer block (sector:"
												+ sectorIndex
												+ " block:"
												+ blockIndex
												+ ")- adjusting keys and permission bits.");

								data = adjustKeysAndPermBits(keyA, keyB);
								Log.d(App.TAG, Hex.printableHex(data));
							}

							pretty_data = Hex.printableHex(data);
							int[] bs = {blockIndex, sectorIndex};
							App.recentScannedBlocks.put(bs, data);

						} catch (TagLostException e) {
							pretty_data = "Tag lost! \n";
							e.printStackTrace();
						} catch (Exception e) {
							pretty_data = "Read Failed! \n";
							e.printStackTrace();
						}
						blockCount = blockCount + 1;
						blockReadSuccess = blockReadSuccess + 1;
						sb.append(pretty_data);
						osw.append("sector:" + sectorIndex + " block:" + blockIndex + "\n");
						osw.append(pretty_data);
						infoTextUpdate(sb.toString());
						sb = new StringBuilder();
					}
				} else {
					/* Babil: Authentication failed. So we have to skip the
					 * whole sector.
					 */
					String block_title = "\nReading → Sector: " + sectorIndex + " Block: "
					 + blockCount + "-"
							+ (blockCount
									+ App.mClassic.getBlockCountInSector(sectorIndex)
									- 1)
							+ " \n";
					Log.d(App.TAG, block_title);
					sb.append(block_title);
					osw.append(block_title);
					blockCount = blockCount + App.mClassic.getBlockCountInSector(sectorIndex);
					sb.append("Authentication failed. \n");

					authFailedCount = authFailedCount + 1;
					if (authFailedSectors.length() == 0) {
						authFailedSectors = (Integer.valueOf(sectorIndex)).toString();
					} else {
						authFailedSectors = authFailedSectors + " " + sectorIndex;
					}
				}
			}

		}
		catch (final TagLostException tag) {
			tag.printStackTrace();
			sb.append("Tag Lost.");
		}
		catch (final IOException e) {
			e.printStackTrace();
			sb.append("IOEception");
		}

		finally {
			try {
				osw.flush();
				osw.close();
				fileOut.close();

				if ( App.mClassic.isConnected()) {
					App.mClassic.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			infoTextUpdate("\n\nReading Finished.\n");
			infoTextUpdate("- Successfully read " + blockReadSuccess + " block(s). \n");
			if (blockReadSuccess < App.mClassic.getBlockCount()) {
				infoTextUpdate("- Auth. failed on " + authFailedCount
						+ " sector(s) ["
						+ authFailedSectors
						+ "]");
			}
			infoTextUpdate("\n\n");
			infoButtonUpdate(blockReadSuccess + " block(s) loaded in memory.\n Write it to a tag?");

			if (saveInExternalStorage(fileName) == true) {
				 new File(fileName).delete();
				 Log.d(App.TAG, "Internal tag data deleted.");
			}
		}
	}
}
