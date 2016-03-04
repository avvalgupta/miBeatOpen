package com.cardea.beatopen;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cardea.beatopen.file.FileChooser;

import java.util.ArrayList;

import static com.cardea.beatopen.Globals.*;


public class HomeActivity extends Activity implements View.OnClickListener {
    // Debugging
    private static final String TAG = "HomeActivity";
    private static final boolean D = true;

    // Bluetooth Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_FILE_CHOOSER = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // UI elements
    private Button getDataButton, ecg, emg, ppg, eog;
    private TextView statusTextView;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the bluetooth service
    public static BluetoothSetupService btService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.d(TAG, "+++ ON CREATE +++");

        setContentView(R.layout.activity_bluetooth_setup);
        statusTextView = (TextView)findViewById(R.id.textViewStatus);

        getDataButton = (Button) findViewById(R.id.button_send);
        getDataButton.setOnClickListener(this);

        ecg = (Button) findViewById(R.id.btn_ecg);
        ecg.setOnClickListener(this);

        emg = (Button) findViewById(R.id.btn_emg);
        emg.setOnClickListener(this);

        ppg = (Button) findViewById(R.id.btn_ppg);
        ppg.setOnClickListener(this);

        eog = (Button) findViewById(R.id.btn_eog);
        eog.setOnClickListener(this);

        // Initialize the BluetoothSetupService to perform bluetooth connections
        btService = new BluetoothSetupService(mHandler);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        if (btService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (btService.getState() == STATE_NONE) {
                // Start the Bluetooth service
                btService.start();
            }
        }
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth service
        if (btService != null) btService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void setStatus(int resId) {
        statusTextView.setText(resId);
    }

    private void setStatus(CharSequence subTitle) {
        statusTextView.setText(subTitle);
    }

    // The Handler that gets information back from the BluetoothSetupService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_FILE_CHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    String fileSelected = data.getStringExtra("fileSelected");
                    Toast.makeText(this, fileSelected, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, HistoryPlottingActivity.class);
                    i.putExtra("SELECTED_FILE_PATH", fileSelected);
                    startActivity(i);
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bluetooth_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent;
        switch (item.getItemId()) {
            case R.id.beat_history:
                Intent intent = new Intent(this, FileChooser.class);
                ArrayList<String> extensions = new ArrayList<>();
                extensions.add(".txt");
                intent.putStringArrayListExtra("filterFileExtension", extensions);
                startActivityForResult(intent, REQUEST_FILE_CHOOSER);
                return true;
            case R.id.bt_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v==getDataButton) {
            if (btService.getState() != STATE_CONNECTED) {
                Toast.makeText(getBaseContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            } else if (selectedSignalType.isEmpty()) {
                Toast.makeText(getBaseContext(), R.string.signal_not_selected, Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(getBaseContext(), PlottingActivity.class);
                startActivityForResult(i, 0);
            }
        }
        if (v==ecg) {
            selectedSignalType = REQUEST_ECG;
            samplingRateForDataAcquire = ECG_SAMPLING_RATE;
            getDataButton.setText("Get ECG data");
        }
        if (v==emg) {
            selectedSignalType = REQUEST_EMG;
            samplingRateForDataAcquire = EMG_SAMPLING_RATE;
            getDataButton.setText("Get EMG data");
        }
        if (v==ppg) {
            selectedSignalType = REQUEST_PPG;
            samplingRateForDataAcquire = PPG_SAMPLING_RATE;
            getDataButton.setText("Get PPG data");
        }
        if (v==eog) {
            selectedSignalType = REQUEST_EOG;
            samplingRateForDataAcquire = EOG_SAMPLING_RATE;
            getDataButton.setText("Get EOG data");
        }
    }
}
