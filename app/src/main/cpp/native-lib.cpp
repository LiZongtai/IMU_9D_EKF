#include <jni.h>
#include <string>
#include "Eigen/Eigen/Core"
#include "EKF/ESKF.cpp"
#include <android/log.h>

using namespace IMU_EKF;
char *TAG = "EKF";

// magnetometer calibration
Eigen::Matrix<float, 3, 3> W; // soft-iron
Eigen::Matrix<float, 3, 1> V; // hard-iron
float incl = 0.0773704f; // 磁倾角 inclination
float B = 0.00005f; // 地磁场强度 geomagnetic field strength
float dt = 0.1f;
IMU_EKF::ESKF<float> filter;

extern "C"
JNIEXPORT void JNICALL
Java_tongji_lzt_imu_19d_1ekf_SensorsListener_initEKF(JNIEnv *env, jobject thiz,
                                                     jfloatArray acc_value) {
    jfloat *accValue = (jfloat *) env->GetFloatArrayElements(acc_value, 0);
    filter.initWithAcc(accValue[0], accValue[1], accValue[2]);
    W <<1.04447897427, 1.8970667048e-19, -0.00309824736558,
        1.8970667048e-19, 1.0, -1.32142928588e-20,
        -0.00309824736558, -1.32142928588e-20, 1.00021581291;
    V<<-0.0339891339972, 3.95076761901e-19, -0.0064523062561;
}

extern "C"
JNIEXPORT void JNICALL
Java_tongji_lzt_imu_19d_1ekf_SensorsListener_runEKF(JNIEnv *env, jobject thiz,
                                                    jfloatArray acc_value, jfloatArray mag_value,
                                                    jfloatArray gyr_value) {

    jfloat *accValue = (jfloat *) env->GetFloatArrayElements(acc_value, 0);
    jfloat *magValue = (jfloat *) env->GetFloatArrayElements(mag_value, 0);
    jfloat *gyrValue = (jfloat *) env->GetFloatArrayElements(gyr_value, 0);
    filter.predict(dt);
    filter.correctGyr(gyrValue[0], gyrValue[1], gyrValue[2]);
    filter.correctAcc(accValue[0], accValue[1], accValue[2]);
    filter.correctMag(magValue[0], magValue[1], magValue[2], incl, B, W, V);
    filter.reset();
    // get attitude as roll, pitch, yaw
    float roll, pitch, yaw;
    filter.getAttitude(roll, pitch, yaw);
    // or quaternion
    IMU_EKF::Quaternion<float> q = filter.getAttitude();
    Eigen::Matrix<float, 3, 3> rotM=q.toRotationMatrix();
    float* rotArray = rotM.data();
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "%f %f %f %f %f %f %f %f %f",
                        rotArray[0],rotArray[1],rotArray[2],
                        rotArray[3],rotArray[4],rotArray[5],
                        rotArray[6],rotArray[7],rotArray[8]);
}