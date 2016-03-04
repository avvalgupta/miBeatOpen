package com.cardea.beatopen;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import static com.cardea.beatopen.Globals.*;
import static com.cardea.beatopen.file.FileUtils.*;
import static com.cardea.beatopen.plotutils.Chart.*;


public class PlottingActivity extends Activity {

    Button save, restart;
    ProgressBar progressBar;

    //for plotting
    public static GraphicalView chart;
    LinearLayout chartContainer;
    private static XYMultipleSeriesDataset dataSet;
    private static XYMultipleSeriesRenderer multiRenderer;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        waitingview();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // send specified byte 5 times on 100ms intervals
    private void sendSignals(final String what) {
        new CountDownTimer(600, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (what.equals(START))
                    HomeActivity.btService.write(START.getBytes());
                if (what.equals(STOP))
                    HomeActivity.btService.write(STOP.getBytes());
                if (what.equals(FILTER_ON))
                    HomeActivity.btService.write(FILTER_ON
                            .getBytes());
                if (what.equals(FILTER_OFF))
                    HomeActivity.btService.write(FILTER_OFF
                            .getBytes());
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    /*Wait for 10 sec*/
    private void waitingview() {
        writeIndex = 0;
        sendSignals(START);
        setContentView(R.layout.recording);
        final TextView time_remain = (TextView) findViewById(R.id.textTime);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration(10000); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        animation.start();

        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                time_remain.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                progressBar.clearAnimation();
                sendSignals(STOP);
                setContentView(R.layout.activity_plotting);
                save = (Button) findViewById(R.id.btnSave);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        writeFile(activity, getBaseContext(), "BEAT " + selectedSignalType);
                        finish();
                    }
                });
                restart = (Button) findViewById(R.id.btnRestart);
                restart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        waitingview();
                    }
                });
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                chartContainer = (LinearLayout) findViewById(R.id.chartContainer);
                dataSet = new XYMultipleSeriesDataset();
                multiRenderer = new XYMultipleSeriesRenderer();
                drawGraph(samplingRateForDataAcquire, dataBuffer, writeIndex, dataSet, multiRenderer);
                chart = ChartFactory.getLineChartView(getBaseContext(), dataSet, multiRenderer);
                chartContainer.addView(chart);
                chart.repaint();
            }
        }.start();
    }
}
