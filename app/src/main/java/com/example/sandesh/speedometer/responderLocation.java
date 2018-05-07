package com.example.sandesh.speedometer;

import android.util.Log;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class responderLocation {
    int mathBuildingSideDoorwayXCoordinate = 455;
    int mathBuildingSideDoorwayYCoordinate = 800;
    int mathBuildingCenterXCoordinate = 600;
    int mathBuildingCenterYCoordinate = 550;

    int mostRecentXMeterCoordinate;
    int mostRecentYMeterCoordinate;

    public int generateNearbyCoordinate(float currentCoordinate) {
        Random random = new Random();
        int randomNumberScale = 50;
        int upperBound = (int) currentCoordinate + randomNumberScale;
        int lowerBound = (int) currentCoordinate - randomNumberScale;

        int newCoordinate = random.nextInt(upperBound - lowerBound) + lowerBound;

        //Log.d("OriginalCoordinate", Float.toString(currentCoordinate));
        //Log.d("NewCoordinate", Integer.toString(newCoordinate));

        return newCoordinate;
    }


    public int guideTowardsCoordinate(float currentCoordinate, float desiredCoordinate)
    {
        Random random = new Random();
        int bisectingCoordinate = (int) ((currentCoordinate + desiredCoordinate) / 2);
        int distanceToDesiredCoordinate = abs( (int) (currentCoordinate - desiredCoordinate));
        int upperBound = (int) max(currentCoordinate, bisectingCoordinate);
        int lowerBound = (int) min(currentCoordinate, bisectingCoordinate);

        int newCoordinate = random.nextInt(upperBound - lowerBound) + lowerBound;

        //Log.d("OriginalCoordinate", Float.toString(currentCoordinate));
        //Log.d("NewCoordinate", Integer.toString(newCoordinate));

        return newCoordinate;
    }

    public int guideTowardsCenterOfMathBuildingThenMoveAroundFromThere(float currentCoordinate, int XorYFlag) {
        int desiredCoordinate = (XorYFlag == 0) ? mathBuildingCenterXCoordinate : mathBuildingCenterYCoordinate;
        float distanceToDesiredCoordinate = abs (currentCoordinate - desiredCoordinate);

        if(distanceToDesiredCoordinate > 100) {
            return guideTowardsCoordinate(currentCoordinate, desiredCoordinate);
        }
        else {
            return generateNearbyCoordinate(currentCoordinate);
        }
    }
}
