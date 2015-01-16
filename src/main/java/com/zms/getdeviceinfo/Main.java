package com.zms.getdeviceinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;


public class Main extends ActionBarActivity {
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setText(getBuildInfo(0) + getDisplayInfo(0) + getTelephonyState(0) + getTimeZoneInfo()
                + getHardwareInfo(0) + getSensorInfo());
    }

    /**
     * @param type 0-All,1-Model,2-VersionRelease,Other-Error.
     * @return String
     */
    private String getBuildInfo(int type) {
        String model = Build.MODEL; // 型号
        String versionRelease = Build.VERSION.RELEASE;
        String versionSDK = Build.VERSION.SDK;
        String versionIncremental = Build.VERSION.INCREMENTAL;
        String brand = Build.BRAND;
        String id = Build.ID;
        String manufacturer = Build.MANUFACTURER;
        String hardware = Build.HARDWARE;
        String product = Build.PRODUCT;
        String cpuABI = Build.CPU_ABI;
        String cpuABI2 = Build.CPU_ABI2;
        String fingerPrint = Build.FINGERPRINT;
        String board = Build.BOARD;
        String serial = Build.SERIAL;
        String user = Build.USER;
        if (type == 0) {
            return "Model:" + model + "\nVersionRelease:" + versionRelease + "\nVersionSDK:" +
                    versionSDK + "\nVersionIncremental:" + versionIncremental + "\nBrand:" + brand
                    + "\nID:" + id + "\nManufacturer:" + manufacturer + "\nHardware:" + hardware
                    + "\nProduct:" + product + "\nCpuABI:" + cpuABI + "\nCpuABI2:" + cpuABI2 +
                    "\nFingerPrint:" + fingerPrint + "\nBoard:" + board + "\nSerial:" + serial +
                    "\nUser:" + user + "\n";
        } else if (type == 1) {
            return "Model" + model + "\n";
        } else if (type == 2) {
            return "VersionRelease:" + versionRelease + "\n";
        } else {
            return "[getBuildInfo Err]";
        }
    }

    private String getHardwareInfo(int type) {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();

        String strCPU = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfStringCPU;
        try {
            FileReader fileReader = new FileReader("/proc/cpuinfo");
            BufferedReader localBufferedReader = new BufferedReader(fileReader, 8192);
            strCPU = localBufferedReader.readLine();
            arrayOfStringCPU = strCPU.split("\\s+");
            for (int i = 2; i < arrayOfStringCPU.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfStringCPU[i] + " ";
            }
            strCPU = localBufferedReader.readLine();
            arrayOfStringCPU = strCPU.split("\\s+");
            cpuInfo[1] += arrayOfStringCPU[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // adb shell;cat /proc/meminfo
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(
                Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long mTotalMem = memoryInfo.totalMem;//0;
        long mAvailMem = memoryInfo.availMem;
        /*
        String strMemory;
        String[] arrayOfStringMemory;
        try {
            FileReader localFileReader = new FileReader("/proc/meminfo");
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            strMemory = localBufferedReader.readLine();
            arrayOfStringMemory = strMemory.split("\\s+");
            mTotalMem = Integer.valueOf(arrayOfStringMemory[1]).intValue() * 1024;
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        String totalMemory = Formatter.formatFileSize(this, mTotalMem);
        String availMemory = Formatter.formatFileSize(this, mAvailMem);

        if (type == 0) {
            return "MacAddress:" + macAddress + "\nCpuModel:" + cpuInfo[0] + "\nCpuClock:" +
                    cpuInfo[1] + "\nTotalMemory:" + totalMemory + "\nAvailableMemory:" + availMemory
                    + "\n";
        } else {
            return "";
        }
    }


    /**
     * @param type 0-All,1-DeviceId,2-SubscriberId,3-PhoneNumber,Other-Error.
     * @return String
     */
    private String getTelephonyState(int type) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        String subscriberId = telephonyManager.getSubscriberId();
        String phoneNumber = telephonyManager.getLine1Number();

        if (type == 0) {
            return "DeviceId:" + deviceId + "\nSubscriberId:" + subscriberId + "\nPhoneNumber:" + phoneNumber + "\n";
        } else if (type == 1) {
            return "DeviceId:" + deviceId + "\n";
        } else if (type == 2) {
            return "SubscriberId" + subscriberId + "\n";
        } else if (type == 3) {
            return "PhoneNumber:" + phoneNumber + "\n";
        } else {
            return "[getTelephonyState Err]";
        }
    }

    /**
     * @param type 0-All,1-ScreenHeight,2-ScreenWidth,Other-Error.
     * @return String
     */
    private String getDisplayInfo(int type) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        if (type == 0) {
            return "ScreenHeight:" + screenHeight + "\nScreenWidth:" + screenWidth + "\n";
        } else if (type == 1) {
            return "ScreenHeight:" + screenHeight + "\n";
        } else if (type == 2) {
            return "ScreenWidth:" + screenWidth + "\n";
        } else {
            return "[getTelephonyState Err]";
        }
    }

    private String getTimeZoneInfo() {
        TimeZone timeZone = TimeZone.getDefault();
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return "TimeZone:" + timeZone.toString() + "\n" + "Language:" + language + "\n";
    }

    private String getSensorInfo() {
        String checkResult = "Sensor:\n";
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // ACCELEROMETER
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            checkResult = checkResult + "加速传感器: YES\n";
        } else {
            checkResult = checkResult + "加速传感器: NO\n";
        }

        // AMBIENT_TEMPERATURE
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            checkResult = checkResult + "温度计: YES\n";
        } else {
            checkResult = checkResult + "温度计: NO\n";
        }

        // GAME_ROTATION_VECTOR
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null) {
            checkResult = checkResult + "游戏旋转矢量传感器: YES\n";
        } else {
            checkResult = checkResult + "游戏旋转矢量传感器: NO\n";
        }

        // GEOMAGNETIC_ROTATION_VECTOR
        if (mSensorManager
                .getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null) {
            checkResult = checkResult + "地磁旋转矢量传感器: YES\n";
        } else {
            checkResult = checkResult + "地磁旋转矢量传感器: NO\n";
        }

        // GRAVITY
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            checkResult = checkResult + "重力传感器: YES\n";
        } else {
            checkResult = checkResult + "重力传感器: NO\n";
        }

        // GYROSCOPE
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            checkResult = checkResult + "陀螺仪: YES\n";
        } else {
            checkResult = checkResult + "陀螺仪: NO\n";
        }

        // GYROSCOPE_UNCALIBRATED
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null) {
            checkResult = checkResult + "未校准陀螺仪: YES\n";
        } else {
            checkResult = checkResult + "未校准陀螺仪: NO\n";
        }

        // LIGHT
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            checkResult = checkResult + "光线传感器: YES\n";
        } else {
            checkResult = checkResult + "光线传感器: NO\n";
        }

        // LINEAR_ACCELERATION
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            checkResult = checkResult + "加速度传感器: YES\n";
        } else {
            checkResult = checkResult + "加速度传感器: NO\n";
        }

        // MAGNETIC_FIELD
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            checkResult = checkResult + "磁场传感器: YES\n";
        } else {
            checkResult = checkResult + "磁场传感器: NO\n";
        }

        // MAGNETIC_FIELD_UNCALIBRATED
        if (mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) != null) {
            checkResult = checkResult + "未校准磁场传感器: YES\n";
        } else {
            checkResult = checkResult + "未校准磁场传感器: NO\n";
        }

        // ORIENTATION
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            checkResult = checkResult + "方向传感器: YES\n";
        } else {
            checkResult = checkResult + "方向传感器: NO\n";
        }

        // PRESSURE
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            checkResult = checkResult + "压力传感器: YES\n";
        } else {
            checkResult = checkResult + "压力传感器: NO\n";
        }

        // PROXIMITY
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            checkResult = checkResult + "距离传感器: YES\n";
        } else {
            checkResult = checkResult + "距离传感器: NO\n";
        }

        // RELATIVE_HUMIDITY
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            checkResult = checkResult + "相对湿度传感器: YES\n";
        } else {
            checkResult = checkResult + "相对湿度传感器: NO\n";
        }

        // ROTATION_VECTOR
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            checkResult = checkResult + "旋转矢量传感器: YES\n";
        } else {
            checkResult = checkResult + "旋转矢量传感器: NO\n";
        }

        // SIGNIFICANT_MOTION
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null) {
            checkResult = checkResult + "显著运动传感器: YES\n";
        } else {
            checkResult = checkResult + "显著运动传感器: NO\n";
        }

        // STEP_COUNTER
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            checkResult = checkResult + "计步传感器: YES\n";
        } else {
            checkResult = checkResult + "计步传感器: NO\n";
        }

        // STEP_DETECTOR
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            checkResult = checkResult + "步伐探测器: YES\n";
        } else {
            checkResult = checkResult + "步伐探测器: NO\n";
        }

        // TEMPERATURE
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE) != null) {
            checkResult = checkResult + "温度传感器: YES\n";
        } else {
            checkResult = checkResult + "温度传感器: NO\n";
        }
        return checkResult;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
