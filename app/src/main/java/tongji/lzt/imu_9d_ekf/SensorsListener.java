package tongji.lzt.imu_9d_ekf;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorsListener implements SensorEventListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static boolean isIinit=true;
    private float[] accValue = new float[3];//用来保存加速度传感器的值
    private float[] magValue = new float[3];//用来保存地磁传感器的值
    private float[] gyrValue = new float[3];//用来保存陀螺仪传感器的值
    private float[] values = new float[3];//用来保存最终的结果
    private float[] degrees = new float[3];

    public SensorsListener() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // x y z
            accValue = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // x y z
            magValue = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // x y z
            gyrValue = event.values;
        }

        if(isIinit && accValue!=null){
            initEKF(accValue);
            isIinit=false;
        }
        if (!isIinit && accValue!=null && gyrValue!=null && magValue!=null){
            runEKF(accValue,magValue,gyrValue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] getOritation(float[] gravity, float[] geomagnetic) {
        float[] r = new float[9];//
        float[] res = new float[3];
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
        SensorManager.getOrientation(r, res);
        return res;
    }

    public float getDegreesZ() {
        return degrees[2];
    }

    public native void initEKF(float[] accValue);
    public native void runEKF(float[] accValue, float[] magValue,float[] gyrValue);
}
