package com.example.pain;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    Button colorButton1;
    Button colorButton2;
    Button colorButton3;
    Button colorButton4;
    Button colorButton5;
    Button btnLoadImg;
    Button colorButton7;
    Button btnSaveImg;
    Button colorButton9;

    ImageButton eraserButton;
    ImageButton brushButton;
    ImageButton textButton;
    ImageButton removeButton;
    ImageButton figureCircleButton;
    ImageButton figureSquareButton;
    ImageButton figureRectButton;
    DrawView drawView;

    private static final int PICK_IMAGE = 1;
    Uri imageUri;

    private static final int PERMISSION_CODE = 100;

    View.OnClickListener instrumentsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instrumentsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.brushButton:
                        drawView.setInstrument(DrawView.Instruments.BRUSH);
                        break;
                    case R.id.eraserButton:
                        drawView.setInstrument(DrawView.Instruments.ERASER);
                        break;
                    case R.id.textButton:
                        drawView.setInstrument(DrawView.Instruments.TEXT);
                        break;
                    case R.id.removeButton:
                        drawView.clearCanvas();
                        break;
                    case R.id.figureCircleButton:
                        drawView.setInstrument(DrawView.Instruments.CIRCLE);
                        break;
                    case R.id.figureRectButton:
                        drawView.setInstrument(DrawView.Instruments.RECTANGLE);
                        break;
                    case R.id.figureSquareButton:
                        drawView.setInstrument(DrawView.Instruments.SQUARE);
                        break;
                }
            }
        };
        initializeButtons();
        drawView = findViewById(R.id.view);
        drawView.inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v){
        ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
        drawView.setBrushColor(buttonColor.getColor());
    }

    private void initializeButtons(){
        colorButton1 = findViewById(R.id.colorButton1);
        colorButton2 = findViewById(R.id.colorButton2);
        colorButton3 = findViewById(R.id.colorButton3);
        colorButton4 = findViewById(R.id.colorButton4);
        colorButton5 = findViewById(R.id.colorButton5);
        btnLoadImg = findViewById(R.id.btnLoadImg);
        colorButton7 = findViewById(R.id.colorButton7);
        btnSaveImg = findViewById(R.id.btnSaveImg);
        colorButton9 = findViewById(R.id.colorButton9);
        eraserButton = findViewById(R.id.eraserButton);
        brushButton = findViewById(R.id.brushButton);
        textButton = findViewById(R.id.textButton);
        removeButton = findViewById(R.id.removeButton);
        figureCircleButton = findViewById(R.id.figureCircleButton);
        figureRectButton = findViewById(R.id.figureRectButton);
        figureSquareButton = findViewById(R.id.figureSquareButton);

        colorButton1.setBackgroundColor(Color.GREEN);
        colorButton2.setBackgroundColor(Color.RED);
        colorButton3.setBackgroundColor(Color.BLACK);
        colorButton4.setBackgroundColor(Color.BLUE);
        colorButton5.setBackgroundColor(Color.YELLOW);
        colorButton7.setBackgroundColor(Color.DKGRAY);
        colorButton9.setBackgroundColor(Color.LTGRAY);
        colorButton1.setOnClickListener(this);
        colorButton2.setOnClickListener(this);
        colorButton3.setOnClickListener(this);
        colorButton4.setOnClickListener(this);
        colorButton5.setOnClickListener(this);
        colorButton7.setOnClickListener(this);
        colorButton9.setOnClickListener(this);
        eraserButton.setOnClickListener(instrumentsListener);
        brushButton.setOnClickListener(instrumentsListener);
        textButton.setOnClickListener(instrumentsListener);
        removeButton.setOnClickListener(instrumentsListener);
        figureCircleButton.setOnClickListener(instrumentsListener);
        figureRectButton.setOnClickListener(instrumentsListener);
        figureSquareButton.setOnClickListener(instrumentsListener);

        btnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        btnSaveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap screenBitmap = drawView.display.copy(drawView.display.getConfig(), true);

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            String path = Environment.getExternalStorageDirectory().toString();
                            File file = new File(path, String.valueOf(System.currentTimeMillis())+".jpg");
                            if (!file.exists()) {
                                Log.d("path", file.toString());
                                try {
                                    FileOutputStream fos = new FileOutputStream(file);
                                    screenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    Toast.makeText(getApplicationContext(),"Successfully saved",Toast.LENGTH_SHORT).show();
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            requestPermissions(); // Code for permission
                        }
                    } else {
                        String path = Environment.getExternalStorageDirectory().toString();
                        File file = new File(path, String.valueOf(System.currentTimeMillis())+".jpg");
                        if (!file.exists()) {
                            Log.i("path", file.toString());
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                screenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                Toast.makeText(getApplicationContext(),"Successfully saved",Toast.LENGTH_SHORT).show();
                                fos.flush();
                                fos.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "Permissions received");
            } else {
                Log.i("ERRRROR", "Owh!");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                drawView.display = bitmap.copy(bitmap.getConfig(), true);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        drawView.handleKeys(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }
}







class DrawView extends View implements View.OnTouchListener{
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    private int currentBrushColor = Color.BLACK;
    private Instruments currentInstrument = Instruments.BRUSH;
    public Bitmap display;
    private boolean isBitmapInitialized = false;
    private float textX = 0;
    private float textY = 0;
    public InputMethodManager inputMethodManager;
    private String textBuffer = "";

    enum Instruments {
        BRUSH,
        ERASER,
        TEXT,
        CIRCLE,
        RECTANGLE,
        SQUARE

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkDisplay();
        Paint paint = new Paint();
        canvas.drawBitmap(display, 0, 0, paint);
        invalidate();
    }

    @Override
    public boolean performClick () {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        performClick();
        Paint paint = new Paint();
        Canvas canvas = new Canvas(display);
        switch (currentInstrument){
            case BRUSH:
                paint.setColor(currentBrushColor);
                canvas.drawCircle(event.getX(), event.getY(), 20, paint);
                break;
            case ERASER:
                paint.setColor(Color.WHITE);
                canvas.drawCircle(event.getX(), event.getY(), 20, paint);
                break;
            case CIRCLE:
                paint.setColor(currentBrushColor);
                canvas.drawCircle(  event.getX(), event.getY(), 100, paint  );
                break;
            case RECTANGLE:
                paint.setColor(currentBrushColor);
                canvas.drawRect(  event.getX(), event.getY(), event.getX()+160,  event.getY() + 75, paint  );
                break;
            case SQUARE:
                paint.setColor(currentBrushColor);
                canvas.drawRect(  event.getX(), event.getY(), event.getX()+150,  event.getY() + 150, paint  );
                break;
            case TEXT:
                paint.setTextSize(100);
                this.requestFocus();
                this.setFocusableInTouchMode(true);
                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
                textX = event.getX();
                textY = event.getY();
                textBuffer = "";
                break;
        }
        return true;
    }

    private void checkDisplay(){
        if(!isBitmapInitialized){
            isBitmapInitialized = true;
            display = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            display.eraseColor(Color.WHITE);
        }
    }

    public void setBrushColor(int color){
        currentBrushColor = color;
    }

    public void setInstrument(Instruments instrument){
        currentInstrument = instrument;
    }

    public void drawText(String text, int color){
        Paint paint = new Paint();
        Canvas canvas = new Canvas(display);
        paint.setTextSize(100);
        paint.setColor(color);
        canvas.drawText(text, textX, textY, paint);
    }

    public void handleKeys(int keyCode, KeyEvent event){
        drawText(textBuffer, Color.BLACK);
        if(keyCode == 67){
            if(!textBuffer.equals("")){
                textBuffer = textBuffer.substring(0, textBuffer.length() - 1);
            }
        }else{
            textBuffer += (char) event.getUnicodeChar();
        }
        drawText(textBuffer, currentBrushColor);
    }

    public void clearCanvas(){
        display.eraseColor(Color.WHITE);
    }

}