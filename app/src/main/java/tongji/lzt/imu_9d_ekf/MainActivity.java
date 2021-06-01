package tongji.lzt.imu_9d_ekf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tongji.lzt.imu_9d_ekf.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String TAG ="mainActivity";

    //sensor
    //sensor object
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private List<String> sensorNameList;
    private Sensor accSensor;
    private Sensor magSensor;
    private Sensor gyrSensor;
    private SensorEventListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initSensor();
    }

    private void initSensor() {

        // 实例化传感器管理者
        sensorListener =new SensorsListener();
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor= sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyrSensor = sensorManager.getDefaultSensor(Sensor. TYPE_GYROSCOPE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregist listener
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //regist listener
        sensorManager.registerListener(sensorListener,accSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, magSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, gyrSensor, SensorManager.SENSOR_DELAY_UI);
        new Thread(){
            @Override
            public void run() {
                super.run();
                //定时器
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                };
                timer.schedule(task,1000,20);//3秒后每隔一秒刷新一次
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}