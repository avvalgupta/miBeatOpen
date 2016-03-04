package com.cardea.beatopen.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import static com.cardea.beatopen.Globals.*;

public class FileUtils {
    public static String directoryPath;
    private static String TAG = "Beat Open File Utils";

    public static Integer[] loadDataFromFile(String selectedFilePath) {
        BufferedReader in;
        int y;
        String line;
        ArrayList <Integer> dataList = new ArrayList<>();
        Integer [] dataArray;
        try {
            in = new BufferedReader(new FileReader(selectedFilePath));
            while ((line = in.readLine()) != null) {
                y = Integer.parseInt(line);
                dataList.add(y);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Store in data array
        dataArray = new Integer[dataList.size()];
        return dataList.toArray(dataArray);
    }

    // write beat data of recording duration in file
    public static void writeFile(Activity activity, final Context context, String prefix) {

        File txtFile = createFile(prefix);
        if (txtFile != null) {
            Log.d(TAG, "$$$$ TXT FILE CREATED $$$$");
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(txtFile));

                //write the remaining data in txt file
                for (int i = 0; i < writeIndex; i++) {
                    buf.write("" + dataBuffer[i]);
                    buf.newLine();
                }
                buf.close();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Data saved in TXT file", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            } catch (IOException ignored) {
            }
        }
    }

    private static String getTimeString() {
        String timestamp;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        StringBuffer temp;
        temp = new StringBuffer();
        temp.append(calendar.get(Calendar.DAY_OF_MONTH));
        temp.append('-');
        temp.append(calendar.get(Calendar.MONTH) + 1);
        temp.append('-');
        temp.append(calendar.get(Calendar.YEAR));
        temp.append(' ');
        temp.append(calendar.get(Calendar.HOUR_OF_DAY));
        temp.append('.');
        temp.append(calendar.get(Calendar.MINUTE));
        temp.append('.');
        temp.append(calendar.get(Calendar.SECOND));
        timestamp = temp.toString();

        return timestamp;

    }

    static File createFile(String prefix) {
        File storageDir = new File(Environment.getExternalStorageDirectory(), DIRECTORY);

        if (!storageDir.mkdirs()) {
            if (!storageDir.exists()) {
                Log.e(TAG, "failed to create directoryPath");
                return null;
            }
        }

        directoryPath = storageDir.getAbsolutePath();
        String ext = ".txt";

        String file_name = "/" + prefix + " " + getTimeString() + ext;
        File datafile = new File(storageDir + file_name);
        try {
            datafile.createNewFile();
            return datafile;
        } catch (IOException e) {
            Log.e("IOException", "exception in createNewFile() method");
            return null;
        }
    }
}
