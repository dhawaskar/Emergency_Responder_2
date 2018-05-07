package com.example.sandesh.speedometer;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.Manifest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamException;

import static android.text.TextUtils.split;

public class MapVisualization extends AppCompatActivity {
    responderLocation responderLocationController = new responderLocation();
    ImageView responderLocationImageView;
    ImageView mathBuildingMapImageView;
    private ImageView responderLineImageView;
    private Button traceResponderButton;
    Bitmap responderLineBitmap;
    Canvas responderLineCanvas;
    Paint paint;
    int numberOfLinesRead = 0;

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        Log.d("Checkexternel storage","read only?");
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            Log.d("Read only?","No both read and write");
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        Log.d("isextenelstorage avail","available?");
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            Log.d("Avaiable?","yes");
            return true;
        }
        return false;
    }


    /*
    data comes in as decimeters, divide by 10 to get meters
     */
    private void parseLineFromCoordinateFile(String line) {
        String [] dataFromLine = line.split("\\s+");
        float NewXMeterCoordinate = Float.parseFloat(dataFromLine[1]);
        float NewYMeterCoordinate = Float.parseFloat(dataFromLine[2]);

        double pixelsPerMeter = 4.4951;

        if (responderLocationController.mostRecentXMeterCoordinate == -2000)
        {
            responderLocationController.mostRecentXMeterCoordinate = (int) NewXMeterCoordinate;
        }
        if (responderLocationController.mostRecentYMeterCoordinate == -2000)
        {
            responderLocationController.mostRecentYMeterCoordinate = (int) NewYMeterCoordinate;
        }

        float initialXCoordinate, initialYCoordinate, finalXCoordinate, finalYCoordinate;

        initialXCoordinate = responderLocationImageView.getTranslationX();
        initialYCoordinate = responderLocationImageView.getTranslationY();

        float initialXMeterCoordinate, initialYMeterCoordinate, changeInXMeters, changeInYMeters;

        initialXMeterCoordinate = responderLocationController.mostRecentXMeterCoordinate;
        initialYMeterCoordinate = responderLocationController.mostRecentYMeterCoordinate;

        changeInXMeters = (NewXMeterCoordinate - initialXMeterCoordinate) / 10;
        changeInYMeters = (NewYMeterCoordinate - initialYMeterCoordinate) / 10;

        finalXCoordinate = (float) ((changeInXMeters * pixelsPerMeter) + initialXCoordinate);
        finalYCoordinate = (float) ((changeInYMeters * pixelsPerMeter) + initialYCoordinate);

        responderLineCanvas.drawLine((initialXCoordinate + 10),
                initialYCoordinate + 5,
                finalXCoordinate + 10,
                finalYCoordinate + 5,
                paint
        );

        responderLocationImageView.setTranslationX(finalXCoordinate);
        responderLocationImageView.setTranslationY(finalYCoordinate);
        responderLocationController.mostRecentXMeterCoordinate = (int) (NewXMeterCoordinate);
        responderLocationController.mostRecentYMeterCoordinate = (int) (NewYMeterCoordinate);

    }

    public void ftp_operations() {
        Thread t = new Thread() {
            public void run() {
                FTPClient mFtpClient = new FTPClient();
                mFtpClient.setConnectTimeout(10 * 1000);

                boolean status;
                try {
                    mFtpClient = new FTPClient();
                    mFtpClient.setConnectTimeout(10 * 1000);
                    mFtpClient.connect(InetAddress.getByName("18.218.55.254"));
                    status = mFtpClient.login("sandesh", "sandesh");
                    Log.e("isFTPConnected", String.valueOf(status));

                    if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                        mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                        mFtpClient.enterLocalPassiveMode();
                        FTPFile[] mFileArray = mFtpClient.listFiles();

                        Log.d("connect","Files are"+ Arrays.toString(mFileArray));
                        Log.e("Size", String.valueOf(mFileArray.length));
                        //for AWS server we need passive mode
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String filename = "reading2.txt";
                File file = new File(getFilesDir(), filename);
                Log.d("download", "File path:" + file.getAbsolutePath());
                OutputStream outputStream = null;

                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(file));
                    try {
                        mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        mFtpClient.enterLocalPassiveMode();
                        mFtpClient.setAutodetectUTF8(true);
                        Boolean download_status = mFtpClient.retrieveFile("reading2.txt", outputStream);
                        Log.d("download:",""+mFtpClient.getReplyString());
                        Log.d("Download ststus",""+download_status);
                    }
                    catch (FTPConnectionClosedException fe){
                        fe.getCause();
                    }
                    catch (CopyStreamException ce){
                        ce.getCause();
                    }
                    catch (IOException ie){
                        ie.getCause();
                    }
                    outputStream.flush();
                    outputStream.close();
                    Log.d("file size", "size of file\n" + file.length() + "\nFile path" + file.getAbsolutePath());
                    Log.d("FTP","Lets read the last line of the file");

                    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                        Log.d("File", "Sorry cannot read the file");
                    }
                    else {
                        String line;
                        if (numberOfLinesRead == 0) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                                while ((line = reader.readLine()) != null) {
                                    parseLineFromCoordinateFile(line);
                                    numberOfLinesRead++;
                                }
                                reader.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Log.d("Lines", "already read");
                                BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                                for (int i = 1; i <= numberOfLinesRead; i++) {
                                    reader.readLine();
                                }
                                if ((line = reader.readLine()) != null) {
                                    parseLineFromCoordinateFile(line);
                                    numberOfLinesRead++;
                                }
                                reader.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        t.start();
        try {
            t.join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_visualization);

        numberOfLinesRead = 0;

        if (ActivityCompat.checkSelfPermission(MapVisualization.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(MapVisualization.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MapVisualization.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                }
                else {
                    ActivityCompat.requestPermissions(MapVisualization.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 777);
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(MapVisualization.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(MapVisualization.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MapVisualization.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
                else {
                    ActivityCompat.requestPermissions(MapVisualization.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 777);
                }
            }
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mathBuildingMapImageView = findViewById(R.id.mapImage);
        responderLineImageView = findViewById(R.id.responder_line_image_view);
        traceResponderButton = findViewById(R.id.beginButton);

        responderLocationImageView = findViewById(R.id.responder_location_image_view);
        responderLocationImageView.setImageResource(R.drawable.responder_location);
        responderLocationImageView.setTranslationX(responderLocationController.mathBuildingSideDoorwayXCoordinate);
        responderLocationImageView.setTranslationY(responderLocationController.mathBuildingSideDoorwayYCoordinate);

        responderLocationController.mostRecentXMeterCoordinate = -2000;
        responderLocationController.mostRecentYMeterCoordinate = -2000;

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
                Log.d("FILE", "OPENING");
                ftp_operations();
                Log.d("FILE", "CLOSING");
            }
        }, 0, 1000);
    }
}
