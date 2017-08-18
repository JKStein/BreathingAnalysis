package com.jonas.breathinganalysis;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

//import com.jonas.breathinganalysis.Deprecated.DBChartManager;
//import com.jonas.breathinganalysis.Deprecated.DBMeasurement;

public class BreathingAnalysis extends Activity{

    ArrayList<SensorDate> accelerationSensorValues, rotationSensorValues, magnetSensorValues;
    ArrayList<Sound> soundEventValues;
    ArrayList<Long> percussionEventValues;

    AccelerationRecorder accelerationRecorder;
    //AccelerationChartManager accelerationChartManager;

    RotationRecorder rotationRecorder;

    MagnetRecorder magnetRecorder;

    //DBMeasurement dbMeasurement;
    //DBChartManager dbChartManager;

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;

    private Button measurementController;

    //private MediaPlayer mediaPlayer;
    //private long metronomeStartTimestamp;
    //private long metronomeEndTimestamp;

    SoundPool soundPool;
    int tick, tock;
    Handler handler;
    Metronome metronome;
    long bestFittingStartTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        accelerationSensorValues = new ArrayList<>();
        rotationSensorValues = new ArrayList<>();
        magnetSensorValues = new ArrayList<>();
        soundEventValues = new ArrayList<>();
        percussionEventValues = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerationRecorder = new AccelerationRecorder(this);
        rotationRecorder = new RotationRecorder(this);
        magnetRecorder = new MagnetRecorder(this);
        //accelerationChartManager = new AccelerationChartManager(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        new AudioHandler(this);

        installAccelerometer();
        installGyroscope();
        installMagnetometer();

        soundPool = new SoundPool.Builder().build();
        tick = soundPool.load(this, R.raw.tick,1);
        //tick = soundPool.load(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/tick.ogg",1);
        tock = soundPool.load(this, R.raw.tock,1);
        handler = new Handler();
        metronome = new Metronome(soundPool, tick, tock, handler, this);
        bestFittingStartTimestamp = 0;


        /*File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        final File metronomeAudio = new File(path, "metronome.mp3");
        mediaPlayer = MediaPlayer.create(this, Uri.parse(metronomeAudio.getPath()));
        final BreathingAnalysis breathingAnalysis = this;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                metronomeEndTimestamp = System.currentTimeMillis() - 500;
                System.out.println("metronomeEndTimestamp: " + metronomeEndTimestamp);
                (new DataHandler(new MeasuredData(accelerationSensorValues, rotationSensorValues,  magnetSensorValues, soundEventValues, percussionEventValues))).start();
                measurementController.setText(R.string.start);
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(breathingAnalysis, Uri.parse(metronomeAudio.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

        //dbMeasurement = new DBMeasurement(this);
        //dbChartManager = new DBChartManager(this);
        //dbMeasurement.initializeDBMeasurement();
        //dbMeasurement.startRecorder();

        //audio processing
        installButton();
    }

    private void installButton() {
        measurementController = (Button) findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {
                    /*mediaPlayer.start();
                    long mediaPlayerStartTimestamp = System.currentTimeMillis();*/
                    /*while(mediaPlayer.getCurrentPosition() < 100) {
                        System.out.println("Metronome did not make sufficient progress (" + mediaPlayer.getCurrentPosition() + ") yet!");
                    }*/
                    /*metronomeStartTimestamp = mediaPlayerStartTimestamp + 700;
                    System.out.println("metronomeStartTimestamp: " + metronomeStartTimestamp);
                    idunno();*/


                    handler.post(metronome);

                    accelerationSensorValues.clear();
                    rotationSensorValues.clear();
                    magnetSensorValues.clear();
                    soundEventValues.clear();
                    measurementController.setText(R.string.stop);

                }
                else {
                    metronome.reset();
                    handler.removeCallbacks(metronome);
                    (new DataHandler(new MeasuredData(accelerationSensorValues, rotationSensorValues,  magnetSensorValues, soundEventValues, percussionEventValues, bestFittingStartTimestamp))).start();
                    measurementController.setText(R.string.start);
                }

            }
        });
    }

    /*private void idunno() {
        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);

        final Handler monitorHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                mediaPlayerMonitor();
            }

        };

        myScheduledExecutorService.scheduleWithFixedDelay(
                new Runnable(){
                    @Override
                    public void run() {
                        monitorHandler.sendMessage(monitorHandler.obtainMessage());
                    }},
                200, //initialDelay
                200, //delay
                TimeUnit.MILLISECONDS);

    }


    private void mediaPlayerMonitor(){
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                int mediaDuration = mediaPlayer.getDuration();
                int mediaPosition = mediaPlayer.getCurrentPosition();
                System.out.println("Duration: " + mediaDuration);
                System.out.println("Progress: " + mediaPosition);
            }
        }
    }*/


    void setBestFittingStartTimestamp(long bestFittingStartTimestamp) {
        System.out.println("Hello! The best fitting start time stamp would be is: " + bestFittingStartTimestamp);
        this.bestFittingStartTimestamp = bestFittingStartTimestamp;
        measurementController.performClick();
    }

    /**
     * Initializes
     */
    private void installAccelerometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(accelerationRecorder, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Accelerometer available!");
        }
    }

    private void installGyroscope() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(rotationRecorder, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No Gyroscope available!");
        }
    }

    private void installMagnetometer() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(magnetRecorder, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        else {
            Log.d(TAG, "No MagnetRecorder available!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerationRecorder, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(rotationRecorder, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magnetRecorder, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelerationRecorder);
        sensorManager.unregisterListener(rotationRecorder);
        sensorManager.unregisterListener(magnetRecorder);
    }
}