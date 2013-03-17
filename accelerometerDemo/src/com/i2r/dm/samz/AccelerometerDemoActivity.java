package com.i2r.dm.samz;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ParserError", "ParserError", "ParserError" })
public class AccelerometerDemoActivity extends Activity implements
SensorEventListener {

	public static final String DEBUG_TAG = "Accelerometer Log";
	private static final int NORMAL_DELAY = 3;
	private static final int UI_DELAY = 2;
	private static final int GAME_DELAY = 1;
	private static final int FASTEST_DELAY = 0;
	private boolean mInitialized;
	private int sessionNum = 0;
	private int rate = 3;//3
	private long startSenseTime;
	private int senseDuration;
	AccelerometerDataSource accDataSource;
	
	ImageButton walkingBtn;
	ImageButton runningBtn;
	ImageButton standingBtn;
	ImageButton sittingBtn;
	ImageButton layingBtn;

	List<AccData> dataList = new ArrayList<AccData>();
	List<Integer> sessionList = new ArrayList<Integer>();
	List<int[]> featureList1 = new ArrayList<int[]>();
	List<int[]> featureList2 = new ArrayList<int[]>();
	List<int[]> featureList3 = new ArrayList<int[]>();
	List<int[]> featureList4 = new ArrayList<int[]>();
	List<int[]> featureList5 = new ArrayList<int[]>();
	
	boolean test=false;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean sensing = false;
	PowerManager pm;
	PowerManager.WakeLock wl;

	// private final float NOISE = (float) 2.0;

	OnlineBayes ob=new OnlineBayes(2);
	double[]p={0,0};

	AccData data;
	TextView t4,t5,t6,t1,t2,t3;
	private int windowSize=50;
	EditText windowSizeEdit;
	Thread thread;
	int activity=0;
	
	Visualization visuals; 
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		mInitialized = false;  

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// mSensorManager.registerListener(AccelerometerDemoActivity.this,
		// mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		// mSensorManager.connectSimulator();

		sensing=false;
		
		initLayout();


		initSession();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

	}

	private void initSession() {
		AccelerometerDataSource ds = new AccelerometerDataSource(this);
		try {
			ds.open();
			sessionList = ds.getAllSessions();
			ds.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in session retrieval", e);
		}
		try {
			sessionNum = sessionList.get(sessionList.size() - 1);
		} catch (Exception e) {
			sessionNum = 0;
			Log.e(DEBUG_TAG, "there were no previous session num");
		}
	}


	@TargetApi(11)
	private void initLayout() {
		// clearDatabase();
		
		// Visualization Layout
		LinearLayout plotlayout = (LinearLayout) findViewById(R.id.mControls);
		visuals = new Visualization (this);
		
		// Initiate Screen		
	    Bitmap result = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(result);
	    visuals.onDraw(canvas);
	    visuals.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	    plotlayout.addView(visuals);

	    
		final EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
		windowSizeEdit.setText("10");

		final EditText duration = (EditText) findViewById(R.id.editText_senseDuration);
		duration.setText("10");

		/* Walking Button */
		walkingBtn =  (ImageButton) findViewById(R.id.WalkingButton);
	    executeTrainButton(walkingBtn);
	    
		/* Running Button */
		 runningBtn =  (ImageButton) findViewById(R.id.RunningButton);
	    executeTrainButton(runningBtn);
	    
	    /* Standing Button */
		 standingBtn =  (ImageButton) findViewById(R.id.StandingButton);
		 executeTrainButton(standingBtn);

		 /* Sitting Button */
		 sittingBtn =  (ImageButton) findViewById(R.id.SittingButton);
		 executeTrainButton(sittingBtn);

		 /* Laying Button */
		 layingBtn =  (ImageButton) findViewById(R.id.LayingButton);
		 executeTrainButton(layingBtn);

		 /* Sensor Button */
		 final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);
		 sensorBtn.setActivated(false);
		 sensorBtn.setOnClickListener(new View.OnClickListener() {

			 public void onClick(View v) {
				 /*final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
				wl.acquire();
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				wl.release();*/
				 if(!walkingBtn.isActivated()&&!runningBtn.isActivated()&&!standingBtn.isActivated()&&!sittingBtn.isActivated()&&!layingBtn.isActivated()){						
					 if(!test){
						 sensorBtn.setActivated(true);

						 test = true;
						 sensing = true;

						 EditText duration = (EditText) findViewById(R.id.editText_senseDuration);
						 senseDuration = Integer.parseInt(duration.getText()
								 .toString());
						 dataList.clear();
						 startSenseTime = System.currentTimeMillis();

						 EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
						 windowSize = Integer.parseInt(windowSizeEdit.getText().toString());

						 registerListener();

					 }else{
						 sensorBtn.setActivated(false);
						 test=false;
						 sensing=false;
						 unregisterListener();
					 }
				 }	
			 }
		 });

		 /* Spinner */
		 /*Spinner rateSpinner = (Spinner) findViewById(R.id.spinner_rate);
		String[] rates = new String[] { "Normal: 180", "UI: 60", "Game: 20",
				"Fastest: 10" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, rates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rateSpinner.setAdapter(adapter);
		rateSpinner.setSelection(1);
		rateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
						switch (position) {
						case 0:
							rate = NORMAL_DELAY;
							break;
						case 1:
							rate = UI_DELAY;
							break;
						case 2:
							rate = GAME_DELAY;
							break;
						case 3:
							rate = FASTEST_DELAY;
							break;
						}

					}

					public void onNothingSelected(AdapterView<?> adapterView) {
						return;
					}
				});*/

		/* Clear Database Button */
		Button clearDatabase = (Button) findViewById(R.id.clearDbBtn);
		clearDatabase.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				
				
				new AlertDialog.Builder(AccelerometerDemoActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Are you sure?")
				.setMessage("All Data in Database Will be Lost")
				.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {
						clearDatabase();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new AlertDialog.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {

					}
				}).show();
				
			}
		});

		/* Extract Features Button */
		Button extract = (Button) findViewById(R.id.extractFeaturesBtn);
		extract.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				dataList.clear();
				featureList1.clear();
				featureList2.clear();
				featureList3.clear();
				featureList4.clear();
				featureList5.clear();
				ob.resetAllDistributions();
				ob.resetFeatures();
				ob.updateAllVisualizationParameters();
				
				visuals.clearScreen();
				
				
				/*	EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
				int windowSize = Integer.parseInt(windowSizeEdit.getText()
						.toString());
				Intent sessionView = new Intent(AccelerometerDemoActivity.this,
						SessionActivity.class);
				sessionView.putExtra("windowSize", windowSize);
				startActivity(sessionView);*/
			}
		});

		/* Test Button */
		Button testBtn = (Button) findViewById(R.id.TestBtn);
		testBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(AccelerometerDemoActivity.this,TestActivity.class));
			}
		});
	}

	@TargetApi(11)
	public void executeTrainButton(final ImageButton btn){
		btn.setActivated(false);
		btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				/*final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
				wl.acquire();
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				android.os.SystemClock.sleep(1000);
				tg.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY_ONE_SHOT);
				wl.release();*/
				if(!test){
					if(!sensing){

						btn.setActivated(true);

						if(walkingBtn.isActivated()){
							activity=0;
						}
						if(runningBtn.isActivated()){
							activity=1;		
						}
						if(standingBtn.isActivated()){
							activity=2;
						}
						if(sittingBtn.isActivated()){
							activity=3;
						}
						if(layingBtn.isActivated()){
							activity=4;
						}
						sensing = true;

						EditText duration = (EditText) findViewById(R.id.editText_senseDuration);
						senseDuration = Integer.parseInt(duration.getText()
								.toString());
						dataList.clear();
						startSenseTime = System.currentTimeMillis();

						EditText windowSizeEdit = (EditText) findViewById(R.id.editText_windowSize);
						windowSize = Integer.parseInt(windowSizeEdit.getText().toString());

						registerListener();
					} else {
						if (btn.isActivated()) {
							btn.setActivated(false);
							sensing = false;
							unregisterListener();
						}
					}
				}
			}
		});
	}

	
	private void clearDatabase() {
		AccelerometerDataSource ds = new AccelerometerDataSource(
				AccelerometerDemoActivity.this);
		try {
			ds.open();
			int rowDeleted = ds.deleteAllData();
			Toast.makeText(AccelerometerDemoActivity.this,
					rowDeleted + " rows deleted", Toast.LENGTH_SHORT).show();
			ds.close();
		} catch (SQLException e) {
			Log.e(DEBUG_TAG, "SQL Exception in clear database", e);
			Toast.makeText(AccelerometerDemoActivity.this,
					"could not delete rows", Toast.LENGTH_SHORT).show();
		}
		sessionNum = 0;
	}

	private void unregisterListener() {
		wl.release();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);

		mSensorManager.unregisterListener(AccelerometerDemoActivity.this);
		// mSensorManager.disconnectSimulator();
		sensorBtn.setText(getString(R.string.start));

	}

	private void registerListener() {

		createNewSession();
		wl.acquire();
		final Button sensorBtn = (Button) findViewById(R.id.sensorBtn);

		if (mSensorManager.registerListener(AccelerometerDemoActivity.this,
				mAccelerometer, 10)) {
			sensorBtn.setText(getString(R.string.stop));
		}

	}

	private void createNewSession() {
		sessionNum++;
	}

	protected void onResume() {
		super.onResume();
		//Log.i(DEBUG_TAG, "Resume");
		if (sensing)
			mSensorManager.registerListener(this, mAccelerometer, rate);
	}

	protected void onPause() {
		super.onPause();
		//Log.i(DEBUG_TAG, "Pause");
		if (sensing)
			mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Toast.makeText(this,"Sensor Accuracy",Toast.LENGTH_SHORT).show();
	}

	@SuppressLint({ "ParserError", "ParserError", "ParserError", "ParserError", "ParserError", "ParserError", "ParserError", "ParserError" })
	public void onSensorChanged(SensorEvent event) {
		// Log.i(DEBUG_TAG, "Sensing...");

		 t1 = (TextView) findViewById(R.id.x_axis);
		 t2 = (TextView) findViewById(R.id.y_axis);
		 t3 = (TextView) findViewById(R.id.z_axis);
		TextView counter = (TextView) findViewById(R.id.textView_counter);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

			/*tvX.setText(Float.toString(x));
			tvY.setText(Float.toString(y));
			tvZ.setText(Float.toString(z));*/
			data = new AccData(sessionNum, System.currentTimeMillis(),
					x, y, z);
			//data.setRate(rate);

			dataList.add(data);
			counter.setText(Integer.toString(dataList.size()));
			

			/*************USE CLASSIFIER HERE*************/

			Learn();
			
			/*if(ob.classes.isEmpty()==false){
				//Log.i("Dan classifier",Double.toString(ob.classes.get(0).mean[0]));
				/*tvX.setText(String.format("%4.4f",ob.classes.get(0).mean[0]));
				tvY.setText(String.format("%4.4f",ob.classes.get(0).mean[1]));
				tvZ.setText(String.format("%4.4f",ob.classes.get(0).rotation));
			}*/
			
			/*
			major.setText(String.format("%4.2f",data.getRSS()));
			minor.setText(String.format("%4.2f",ob.maxdifference));
			rotation.setText(String.format("%4.2f",ob.energy));
			*/
			/*************USE CLASSIFIER HERE*************/


			/*if (System.currentTimeMillis() > startSenseTime + senseDuration
					* 1000) {
				unregisterListener();
				sensing = false;
				//insertToDatabase(dataList);
				dataList.clear();
				ob.resetFeatures();
			}*/
		
	}

	private int toScreenX(double x){
		
		return (int) (700*x+20);
	}
	
	private int toScreenY(double y){
		return (int)(-550*y+480);
	}
	
	
	private void Learn() {
		
		ob.updateFeatures(data.getRSS());

		if(windowSize==dataList.size()){

			p[0]=ob.energy/(windowSize*50);
			p[1]=ob.maxdifference/20;
			
			int[] p1={toScreenX(p[0]),toScreenY(p[1])};

			if(activity==0)
				featureList1.add(p1);
			if(activity==1)
				featureList2.add(p1);
			if(activity==2)
				featureList3.add(p1);
			if(activity==3)
				featureList4.add(p1);
			if(activity==4)
				featureList5.add(p1);
			
			if(!test){
				int id;
				id=ob.Train(p,activity);
				 
				 try{
						visuals.BDistance.set(0,String.format("%4.4f",ob.classes.get(0).Bdistance(ob.classes.get(activity))));
					 //visuals.BDistance.set(0,Integer.toString(activity));
					}catch(Exception ioobe){
						visuals.BDistance.set(0,"0");
					}
					try{
						visuals.BDistance.set(1,String.format("%4.4f",ob.classes.get(1).Bdistance(ob.classes.get(activity))));
						//visuals.BDistance.set(1,Integer.toString(id));
					}catch(Exception ioobe){
						visuals.BDistance.set(1,"0");
					}
					try{
						visuals.BDistance.set(2,String.format("%4.4f",ob.classes.get(2).Bdistance(ob.classes.get(activity))));
						
					}catch(Exception ioobe){
						visuals.BDistance.set(2,"0");
					}
					try{
						visuals.BDistance.set(3,String.format("%4.4f",ob.classes.get(3).Bdistance(ob.classes.get(activity))));
					}catch(Exception ioobe){
						visuals.BDistance.set(3,"0");
					}
					try{
						visuals.BDistance.set(4,String.format("%4.4f",ob.classes.get(4).Bdistance(ob.classes.get(activity))));
					}catch(Exception ioobe){
					    visuals.BDistance.set(4,"0");
					}
					
					int x= toScreenX(ob.classes.get(id).mean[0]);
					int y= toScreenY(ob.classes.get(id).mean[1]);
					int min= (int) (ob.classes.get(id).minor*10000*5);
					int maj= (int) (ob.classes.get(id).major*700*5);
					int rot= (int) (ob.classes.get(id).rotation);
					
					//id and activity are almost the same thing... see Train in OnlineBayes class
					visuals.redraw(x,y,min,maj,rot,activity);
					
					visuals.drawData(featureList1,featureList2,featureList3,featureList4,featureList5);
				
			}else{
				int[] id_act;
				id_act=ob.Adapt(p);
				if(id_act[0]>=0){

					int x= toScreenX(ob.classes.get(id_act[0]).mean[0]);
					int y= toScreenY(ob.classes.get(id_act[0]).mean[1]);
					int min= (int) (ob.classes.get(id_act[0]).minor*10000*5);
					int maj= (int) (ob.classes.get(id_act[0]).major*700*5);
					int rot= (int) (ob.classes.get(id_act[0]).rotation);

					//id and activity are almost the same thing... see Train in OnlineBayes class
					visuals.redraw(x,y,min,maj,rot,id_act[1]);
					 try{
							visuals.BDistance.set(0,String.format("%4.4f",ob.classes.get(0).Bdistance(ob.classes.get(id_act[0]))));
						 //visuals.BDistance.set(0,Integer.toString(activity));
						}catch(Exception ioobe){
							visuals.BDistance.set(0,"0");
						}
						try{
							visuals.BDistance.set(1,String.format("%4.4f",ob.classes.get(1).Bdistance(ob.classes.get(id_act[0]))));
							//visuals.BDistance.set(1,Integer.toString(id));
						}catch(Exception ioobe){
							visuals.BDistance.set(1,"0");
						}
						try{
							visuals.BDistance.set(2,String.format("%4.4f",ob.classes.get(2).Bdistance(ob.classes.get(id_act[0]))));
							
						}catch(Exception ioobe){
							visuals.BDistance.set(2,"0");
						}
						try{
							visuals.BDistance.set(3,String.format("%4.4f",ob.classes.get(3).Bdistance(ob.classes.get(id_act[0]))));
						}catch(Exception ioobe){
							visuals.BDistance.set(3,"0");
						}
						try{
							visuals.BDistance.set(4,String.format("%4.4f",ob.classes.get(4).Bdistance(ob.classes.get(id_act[0]))));
						}catch(Exception ioobe){
						    visuals.BDistance.set(4,"0");
						}
				}				
			}
			
			dataList.clear();
			ob.resetFeatures();
		}
	}

	private void insertToDatabase(final List<AccData> dataList) {

		accDataSource = new AccelerometerDataSource(
				AccelerometerDemoActivity.this);
		Log.i(DEBUG_TAG, "inserting to database");
		try {
			accDataSource.open();
			ListIterator<AccData> iterator = dataList.listIterator();
			while (iterator.hasNext()) {
				AccData data = iterator.next();
				accDataSource.insertData(data);
			}
			accDataSource.close();

		} catch (Exception e) {
			Log.e(DEBUG_TAG, "SQL Exception in inserting to database", e);
		}
		Log.i(DEBUG_TAG, "data list inserted to database");

	}

}