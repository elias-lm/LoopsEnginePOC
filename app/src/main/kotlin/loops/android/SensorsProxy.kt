package loops.android

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import android.util.Log

class SensorsProxy(val sensorManager: SensorManager) {

    fun accelerometer(
        sensorEvent: (x: Int, y: Int, z: Int) -> Unit,
    ) {

        sensorManager.getSensorList(Sensor.TYPE_ALL).forEach {
            Log.i("Sensor", ": ${it.name}")
        }
        val magnetoSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
        sensorManager.registerListener(object : SensorEventCallback() {
            override fun onSensorChanged(event: SensorEvent) {
                super.onSensorChanged(event)
                event.values.also {
                    sensorEvent(it[0].toInt(), it[1].toInt(), it[2].toInt())
                }
            }
        }, magnetoSensor, 1000)
    }
}