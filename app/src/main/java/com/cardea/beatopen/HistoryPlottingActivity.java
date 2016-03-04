package com.cardea.beatopen;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import static com.cardea.beatopen.plotutils.Chart.*;
import static com.cardea.beatopen.file.FileUtils.*;
import static com.cardea.beatopen.Globals.*;

public class HistoryPlottingActivity extends Activity {
	private String selectedFilePath;
	LinearLayout chartContainer;
	private Integer []data;

	private void setChartData() {
		XYSeries series = new XYSeries("BEAT Plot");
		for (int i = 0; i < data.length; i++) {
			series.add(i, data[i]);
			i++;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plot_history);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
				selectedFilePath = extras.getString("SELECTED_FILE_PATH");
		} else {
			selectedFilePath = (String) savedInstanceState
					.getSerializable("SELECTED_FILE_PATH");
		}
		if (selectedFilePath.contains("ECG"))
			samplingRateForSavedData = ECG_SAMPLING_RATE;
		if (selectedFilePath.contains("EMG"))
			samplingRateForSavedData = EMG_SAMPLING_RATE;
		if (selectedFilePath.contains("PPG"))
			samplingRateForSavedData = PPG_SAMPLING_RATE;
		if (selectedFilePath.contains("EOG"))
			samplingRateForSavedData = EOG_SAMPLING_RATE;
	}

	@Override
	protected void onStart() {
		super.onStart();
		data = loadDataFromFile(selectedFilePath);
		setChartData();// read from file and load data

		chartContainer = (LinearLayout) findViewById(R.id.chartContainer);
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		drawGraph(samplingRateForSavedData, data, data.length - 1, dataSet, multiRenderer);
		GraphicalView chart = ChartFactory.getLineChartView(getBaseContext(),
				dataSet, multiRenderer);
		chartContainer.addView(chart);
		chart.repaint();
	}
}
