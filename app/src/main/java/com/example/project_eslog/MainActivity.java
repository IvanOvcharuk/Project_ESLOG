package com.example.project_eslog;

import static android.support.v4.app.RemoteActionCompatParcelizer.write;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NdefFormatable;

import android.widget.TextView;
import android.nfc.Tag;
import android.widget.Toast;
import android.content.Context;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static NfcAdapter s_NfcAdapter;
    private static Intent s_Intent;
    private static PendingIntent s_PendingIntent;
    private static IntentFilter s_IntentFilter;
    private static final String[][] s_TechList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName(),
                    NdefFormatable.class.getName()
            }
    };

    public static Tag s_MyTag;
    static TextView s_NfcContent;

    static public byte[] s_OpCode;
    static boolean s_TestingOne = false;
    static Toast s_Toast;
    static Context s_Context;



    private Button startButton;

    ProgressBar simpleProgressBar;

    ImageView icon;
    NavigationView nav_view;
    int count=0;
    Timer timer;

    public static final String Error_Detected = "No NFC Tag Detected";
    public static final String Write_Success = "Text Written Successfully!";
    public static final String Write_Error = "Text No Written";
    NfcAdapter nfcAdapter;
    PendingIntent pedingIntent;
    IntentFilter writingTagFilters;
    boolean writeMode;
    Tag myTag;
    Context context;

    TextView edit_message;
    TextView textView2;

    Button button;

    private int CurrentProgress=0;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initiate progress bar and start button
        //final ProgressBar simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);

        //simpleProgressBar=findViewById(R.id.simpleProgressBar);
        //timer=new Timer();
        //TimerTask timerTask=new TimerTask() {
           // @Override
            //public void run() {
               // count+=5;
               // simpleProgressBar.setProgress(count);
               // if(count==100){
                //    timer.cancel();
                 //   openActivity2();
                //}
           // }
       // };
        //timer.schedule(timerTask, 0,100);

        s_Intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        s_PendingIntent = PendingIntent.getActivity(this, 0, s_Intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        s_IntentFilter = new IntentFilter();
        s_IntentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        s_IntentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        s_IntentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);

        s_Context = this;

        s_Toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        //  OTHERS
        try {
            detectTag(getIntent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        s_OpCode = new byte[] {(byte) 0, (byte) 0};
    }

    // FUNCTIONS
    protected static void byteArrayDisplay(byte[] inArray)
    {
        int rest2 = inArray.length%4;
        int i;

        for (i = 0; i < inArray.length / 4; i++)
        {
            byte[] temp = new byte[4];
            System.arraycopy(inArray, i * 4, temp, 0, 4);
            s_NfcContent.append("[" + i + "] " + Utility.Utility_ByteArrayToHexString(temp) + "\n");
        }
        if(rest2 != 0)
        {
            byte[] temp = new byte[rest2];
            System.arraycopy(inArray, i * 4, temp, 0, rest2);
            s_NfcContent.append("[" + i + "] " + Utility.Utility_ByteArrayToHexString(temp) + "\n");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        s_NfcAdapter = NfcAdapter.getDefaultAdapter(this);
        s_NfcAdapter.enableForegroundDispatch(this, s_PendingIntent, new IntentFilter[]{s_IntentFilter}, s_TechList);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this);
        s_NfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            detectTag(intent);
        } catch (IOException e) {
            showException(e);
        }
    }



    private void detectTag(Intent intent) throws IOException  {
        simpleProgressBar=findViewById(R.id.simpleProgressBar);
        s_MyTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        CountDownTimer countDownTimer = new CountDownTimer(11*150,150) {
            @Override
            public void onTick(long millisUntilFinished) {

                CurrentProgress = CurrentProgress+10;
                simpleProgressBar.setProgress(CurrentProgress);
                simpleProgressBar.setMax(100);

            }

            @Override
            public void onFinish() {
                openActivity2();
            }
        };


        if(s_MyTag != null) {
            textView2.setText("Tag detected!");
            countDownTimer.start();
        }
        else{
            textView2 = (TextView) findViewById(R.id.textView2);
            textView2.setText("Scan NFC tag to start");
        }
    }

    private void showException(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    protected static void showText(String text) {
        s_Toast.cancel();
        s_Toast.makeText(s_Context, text, Toast.LENGTH_SHORT).show();
    }


    public void openActivity2(){
        String nfcData = NfcComm.NfcComm_ReadNDEF_2();
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("Przerzucanie", nfcData);
        startActivity(intent);
    }




}


