package com.example.sandesh.speedometer;

import android.util.Log;

import java.util.Random;

public class responderLocation {
    int mathBuildingSideDoorwayXCoordinate = 950;
    int mathBuildingSideDoorwayYCoordinate = 620;

    public int generateNearbyCoordinate(float currentCoordinate) {
        Random random = new Random();
        int randomNumberScale = 50;
        int upperBound = (int) currentCoordinate + randomNumberScale;
        int lowerBound = (int) currentCoordinate - randomNumberScale;
        int n = random.nextInt(upperBound - lowerBound) + lowerBound;

        Log.d("why", Float.toString(currentCoordinate));
        Log.d("why2", Integer.toString(n));


        return n;
    }
}
