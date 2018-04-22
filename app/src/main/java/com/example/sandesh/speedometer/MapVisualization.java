package com.example.sandesh.speedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MapVisualization extends AppCompatActivity {
    int sideDoorwayXCoordinate = 950;
    int sideDoorwayYCoordinate = 620;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_visualization);

        ImageView responderLocationImageView = findViewById(R.id.responder_location_image_view);
        responderLocationImageView.setImageResource(R.drawable.responder_location);
        responderLocationImageView.setTranslationX(sideDoorwayXCoordinate);
        responderLocationImageView.setTranslationY(sideDoorwayYCoordinate);
        }
}
