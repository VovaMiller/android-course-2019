package com.vovamiller_97.pioneer;

import android.content.Context;
import android.view.OrientationEventListener;

public abstract class OrientationDetector {

    private OrientationEventListener mOrientationEventListener;
    private int rightAngle;

    public OrientationDetector(Context context) {
        rightAngle = 0;
        mOrientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int sensorAngle) {
                onOrientationAngleChanged(sensorAngle);
            }
        };
    }

    public void enable() {
        mOrientationEventListener.enable();
    }

    public void disable() {
        mOrientationEventListener.disable();
    }

    private void onOrientationAngleChanged(int sensorAngle) {
        int angle = fromSensor(sensorAngle);
        angle = angle + 360 * Math.round((rightAngle - angle) / 360f);
        if (Math.abs(rightAngle - angle) >= 60) {
            rightAngle = 90 * Math.round(angle / 90f);
            onOrientationChanged(rightAngle);
        }
    }

    abstract public int fromSensor(int sensorAngle);

    abstract public void onOrientationChanged(int rightAngle);

}
