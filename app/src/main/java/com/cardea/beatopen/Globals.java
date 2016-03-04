package com.cardea.beatopen;

public class Globals {
    // Variables for signaling Beat hardware
    public static final String START = "A";
    public static final String STOP = "S";
    public static final String FILTER_ON = "F";
    public static final String FILTER_OFF = "X";

    // Data acquire signal codes
    public static final String REQUEST_ECG = "ECG";
    public static final String REQUEST_EMG = "EMG";
    public static final String REQUEST_PPG = "PPG";
    public static final String REQUEST_EOG = "EOG";
    public static String selectedSignalType = "";

    // Message types sent from the BluetoothSetupService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 2;
    public static final int MESSAGE_TOAST = 3;

    // Key names received from the BluetoothSetupService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    // Constants required for file handling
    public static final String DIRECTORY = "miBeatOpen Files";

    // Data I/O related variables
    public static final int MAXBUFFER = 10000;
    public static final int ECG_SAMPLING_RATE = 250;
    public static final int EMG_SAMPLING_RATE = 500;
    public static final int PPG_SAMPLING_RATE = 250;
    public static final int EOG_SAMPLING_RATE = 250;
    public static Integer[] dataBuffer = new Integer[MAXBUFFER];
    public static int writeIndex, samplingRateForDataAcquire, samplingRateForSavedData;
}
