package com.jonas.breathinganalysis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.content.ContentValues.TAG;

public class BreathingAnalysis extends Activity implements OnMetronomeDoneListener, OnVolumeDetectedListener, AdapterView.OnItemSelectedListener, OnSavingDoneListener {

    private static final int SENSOR_IDS[] = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE};
    private static final String SENSOR_NAMES[] = {"Accelerometer", "Gyroscope"};
    private static final String SENSOR_ENTRIES[][] =
            {{"accelerometer-x-axis", "accelerometer-y-axis", "accelerometer-z-axis"},
             {"gyroscope-x-axis", "gyroscope-y-axis", "gyroscope-z-axis"}};
    static final String[] EXERCISE_IDS = {"long-tone-piano", "long-tone-forte",
            "scale-slurred", "scale-tongued", "octave-jump-piano", "octave-jump-forte"};

    private static final int SAMPLERATE = 22050;
    private static final int BUFFER = 1024;
    private static final int OVERLAP = 0;
    private static final double SENSITIVITY = 65;
    private static final double THRESHOLD = PercussionOnsetDetector.DEFAULT_THRESHOLD * 1.5;
    private static final int DEFAULT_TUNING = 442;

    private Button measurementController;
    private Spinner spinner;
    private String exerciseId;
    private EditText nameInput, instrumentInput;
    private int tuning;
    private ArrayList<Feature> features;

    private Metronome metronome;

    private ArrayList<Recorder> recorders;
    private ArrayList<AudioProcessor> audioProcessors;

    private Thread dispatcher;
    AudioDispatcher audioDispatcher;

    private SilenceDetector silenceDetector;
    private PitchRecorder pitchRecorder;
    private VolumeRecorder volumeRecorder;
    private PercussionRecorder percussionRecorder;

    /**
     * Called when the activity is starting.
     * Requests required permissions, initializes the recorders and the interactive UI elements.
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestPermissions();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tuning = DEFAULT_TUNING;

        recorders = new ArrayList<>();
        features = new ArrayList<>();

        initializeSensorRecorders();

        metronome = new Metronome();

        initializeFragments();
        installButton();
        installSpinner();

        nameInput = findViewById(R.id.nameInput);
        instrumentInput = findViewById(R.id.instrumentInput);
    }

    /**
     * Requests the permissions to record audio and to write to external storage.
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);
    }

    /**
     * Instantiates the Button and sets the on-click-events.
     * Starts the recording when start is pressed and interrupts it, when stop is pressed.
     * Disables changes in the recording settings while recording.
     */
    private void installButton() {
        measurementController = findViewById(R.id.button_send);
        measurementController.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(measurementController.getText().equals("Start")) {
                    spinner.setEnabled(false);
                    nameInput.setEnabled(false);
                    instrumentInput.setEnabled(false);

                    Recorder.startRecording();

                    metronome.begin();
                    measurementController.setText(R.string.stop);
                }
                else {
                    spinner.setEnabled(true);
                    nameInput.setEnabled(true);
                    instrumentInput.setEnabled(true);

                    Recorder.stopRecording();
                    metronome.reset();
                    metronome.interrupt();
                    measurementController.setText(R.string.start);
                }

            }
        });
    }

    /**
     * Initializes the Spinner for selecting which exercise is getting recorded.
     */
    private void installSpinner() {
        spinner = findViewById(R.id.exercise_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Initializes the motion sensor recorders as well as the audio recorders.
     * Adds them all to the recorders ArrayList.
     */
    private void initializeSensorRecorders() {
        SensorRecorder[] sensorRecorders = new SensorRecorder[SENSOR_IDS.length];

        for(int i = 0; i < sensorRecorders.length; i++) {
            sensorRecorders[i] = SensorRecorder.newInstance(SENSOR_NAMES[i], SENSOR_ENTRIES[i]);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor[] sensors = new Sensor[sensorRecorders.length];

        for(int i = 0; i < sensorRecorders.length; i++) {
            if (sensorManager.getDefaultSensor(SENSOR_IDS[i]) != null) {
                sensors[i] = sensorManager.getDefaultSensor(SENSOR_IDS[i]);
                sensorManager.registerListener(sensorRecorders[i], sensors[i], SensorManager.SENSOR_DELAY_FASTEST);
            }
            else {
                Log.d(TAG, "No " + SENSOR_NAMES[i] + " available!");
            }
        }

        pitchRecorder = new PitchRecorder();
        silenceDetector = new SilenceDetector(THRESHOLD,false);
        volumeRecorder = new VolumeRecorder();
        percussionRecorder = new PercussionRecorder();

        Collections.addAll(recorders, sensorRecorders);
        recorders.add(pitchRecorder);
        recorders.add(volumeRecorder);
        recorders.add(percussionRecorder);
    }

    /**
     * Initializes the start of the audio recording.
     */
    private void startAudioRecording() {
        audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLERATE, BUFFER, OVERLAP);

        this.audioProcessors = new ArrayList<>();

        audioProcessors.add(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLERATE, BUFFER, pitchRecorder));
        audioProcessors.add(silenceDetector);
        audioProcessors.add(volumeRecorder);
        audioProcessors.add(new PercussionOnsetDetector(SAMPLERATE, BUFFER, percussionRecorder, SENSITIVITY,THRESHOLD));

        for (AudioProcessor audioProcessor : audioProcessors) {
            audioDispatcher.addAudioProcessor(audioProcessor);
        }

        this.dispatcher = new Thread(audioDispatcher);
        this.dispatcher.start();
    }

    /**
     * Initializes all fragments.
     */
    private void initializeFragments() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        for (Recorder recorder : recorders) {
            fragmentTransaction.add(R.id.generalized_sensor_fragment, recorder, recorder.getSensorName());
        }
        fragmentTransaction.add(metronome, "Metronome");

        fragmentTransaction.commit();
    }


    /**
     * Callback for the result from requesting permissions.
     * Start recording if all permissions are granted. Request permissions again if not.
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(String permission: permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Log.e("denied", permission);
                requestPermissions();
            }
            else {
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    Log.e("allowed", permission);
                    if(permission.equals("android.permission.RECORD_AUDIO")) {
                        startAudioRecording();
                    }
                }
                else {
                    Log.e("set to never ask again", permission);
                    showGrantingPermissionsRequirementAlertDialog();
                }
            }
        }
    }


    /**
     * Initiates the data handling with primed values.
     * @param bestFittingStartTimestamp A timestamp in milliseconds suggesting the official
     *                                  timestamp of the first beat.
     * @param overallDuration The destined time in milliseconds from the first to the end of
     */
    @Override
    public void onMetronomeDone(long bestFittingStartTimestamp, long overallDuration) {
        Recorder.stopRecording();

        ArrayList<MeasurementSeries> allRecordedSensorData = new ArrayList<>();

        for (Recorder recorder : recorders) {
            allRecordedSensorData.add(new MeasurementSeries(recorder.getSensorData(), recorder.getEntryNames()));
        }
        allRecordedSensorData.add(new MeasurementSeries(metronome.getSensorData(), new String[]{"beat-played"}));

        features.clear();

        features.add(new Feature("instrument", "{'" + instrumentInput.getText().toString() + "'}", "'" + instrumentInput.getText().toString() + "'"));
        features.add(new Feature("player-name", "{'" + nameInput.getText().toString() + "'}", "'" + nameInput.getText().toString() + "'"));
        features.add(new Feature("exercise-name", Arrays.toString(EXERCISE_IDS).replace('[', '{').replace(']', '}'), exerciseId));
        features.add(new Feature("overall-duration", Long.toString(overallDuration)));
        features.add(new Feature("duration-of-one-beat", Long.toString(metronome.getDurationOfOneBeat())));
        features.add(new Feature("beats-per-bar", Integer.toString(metronome.getBeatsPerBar())));
        features.add(new Feature("bpm", Integer.toString(metronome.getBpm())));
        features.add(new Feature("tuning", Integer.toString(this.tuning)));

        File csvFile = DataLogger.getFilePath(nameInput.getText().toString(), instrumentInput.getText().toString(), exerciseId, ".csv");
        File arffFile = DataLogger.getFilePath(nameInput.getText().toString(), instrumentInput.getText().toString(), exerciseId, ".arff");

        new DataHandler(new MeasuredData(allRecordedSensorData, bestFittingStartTimestamp), features, csvFile, arffFile, this);

        measurementController.performClick();
        measurementController.setEnabled(false);
    }

    /**
     * Gets called by the VolumeRecorder and passes the current spl forward to it.
     * @return The current spl of the SilenceDetector.
     */
    @Override
    public double getSPL() {
        return silenceDetector.currentSPL();
    }

    /**
     * Is invoked when an item in the Spinner gets selected.
     * @param parent The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param pos The position of the view in the adapter.
     * @param id The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        this.exerciseId = EXERCISE_IDS[pos];
    }

    /**
     * Is invoked when the selection disappears from this view. Nothing needs to be done.
     * @param adapterView The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Gets called when The DataHandler finished processing the data.
     * Re-enables the interacting UI elements.
     * @param savingFailed Is true, if the DataHandler was unsuccessful in saving the data.
     */
    @Override
    public void savingDone(final boolean savingFailed) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measurementController.setEnabled(true);
                spinner.setEnabled(true);
                nameInput.setEnabled(true);
                instrumentInput.setEnabled(true);
                if(savingFailed) {
                    showSavingErrorAlertDialog();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Saving successful!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        for (Recorder recorder : recorders) {
            recorder.clearSensorData();
        }
    }

    /**
     * Displays an Alert Dialog illustrating the requirement of granting all permissions.
     */
    private void showGrantingPermissionsRequirementAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permission(s) missing");

        builder.setMessage("All permissions have to be granted to use this app! + " +
                "\nGrant all permissions via:" +
                "\n'Settings -> Apps -> BreathingAnalysis -> Permissions'")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Displays an Alert Dialog illustrating the fail of storing the data.
     */
    private void showSavingErrorAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Saving error");

        builder.setMessage("The recorded data did not get saved!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("Clicked ok");
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Gets called when another activity comes into the foreground.
     * Stops audio
     */
    @Override
    public void onPause() {
        if(dispatcher != null) {
            audioDispatcher.stop();
            for (AudioProcessor audioProcessor : audioProcessors) {
                audioDispatcher.removeAudioProcessor(audioProcessor);
            }
            audioProcessors.clear();
        }
        super.onPause();
    }

    /**
     * Gets called when the user navigates to the activity.
     * (Re)starts the audio recording.
     */
    @Override
    public void onRestart() {
        super.onRestart();
        startAudioRecording();
    }
}