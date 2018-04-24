package com.example.sandesh.speedometer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Paint;


import java.util.Timer;
import java.util.TimerTask;

public class MapVisualization extends AppCompatActivity {
    responderLocation responderLocationController = new responderLocation();
    ImageView responderLocationImageView;
    ImageView mathBuildingMapImageView;
    private ImageView responderLineImageView;
    private Button traceResponderButton;
    Bitmap responderLineBitmap;
    Canvas responderLineCanvas;
    int distanceFromResponderLineCanvasEdge = 50;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_visualization);
        mathBuildingMapImageView = findViewById(R.id.mapImage);
        responderLineImageView = findViewById(R.id.responder_line_image_view);
        traceResponderButton = findViewById(R.id.beginButton);

        responderLocationImageView = findViewById(R.id.responder_location_image_view);
        responderLocationImageView.setImageResource(R.drawable.responder_location);
        responderLocationImageView.setTranslationX(responderLocationController.mathBuildingSideDoorwayXCoordinate);
        responderLocationImageView.setTranslationY(responderLocationController.mathBuildingSideDoorwayYCoordinate);

        responderLineBitmap = Bitmap.createBitmap(1080, 1584, Bitmap.Config.ARGB_8888);
        responderLineCanvas = new Canvas(responderLineBitmap);
        responderLineCanvas.drawColor(Color.TRANSPARENT);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);

        responderLineImageView.setImageBitmap(responderLineBitmap);

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
                float initialX, initialY, finalX, finalY;
                int pixelCorrection = 10;

                initialX = responderLocationImageView.getTranslationX();
                initialY = responderLocationImageView.getTranslationY();

                finalX = responderLocationController.guideTowardsCenterOfMathBuildingThenMoveAroundFromThere(
                        initialX, 0);
                finalY = responderLocationController.guideTowardsCenterOfMathBuildingThenMoveAroundFromThere(
                        initialY, 1);

                responderLineCanvas.drawLine((initialX + pixelCorrection),
                        initialY + pixelCorrection,
                        finalX + pixelCorrection,
                        finalY + pixelCorrection,
                        paint
                );
                responderLocationImageView.setTranslationX(finalX);
                responderLocationImageView.setTranslationY(finalY);
            }
        }, 0, 1000);
    }
}
