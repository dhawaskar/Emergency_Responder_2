package com.example.sandesh.speedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MapVisualization extends AppCompatActivity {
    responderLocation responderLocationController = new responderLocation();
    ImageView responderLocationImageView;
    private Button traceResponderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_visualization);
        responderLocationImageView = findViewById(R.id.responder_location_image_view);

        responderLocationImageView.setImageResource(R.drawable.responder_location);
        responderLocationImageView.setTranslationX(responderLocationController.mathBuildingSideDoorwayXCoordinate);
        responderLocationImageView.setTranslationY(responderLocationController.mathBuildingSideDoorwayYCoordinate);

        traceResponderButton = (Button) findViewById(R.id.beginButton);
        traceResponderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResponderImageViewLocation();
            }
        });
    }

    protected void updateResponderImageViewLocation() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                responderLocationImageView.setTranslationX(
                        responderLocationController.guideTowardsCenterOfMathBuildingThenMoveAroundFromThere(
                        responderLocationImageView.getX(), 0));

                responderLocationImageView.setTranslationY(
                        responderLocationController.guideTowardsCenterOfMathBuildingThenMoveAroundFromThere(
                        responderLocationImageView.getY(), 1));
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second
    }
}
