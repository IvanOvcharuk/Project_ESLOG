package com.example.project_eslog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
import android.widget.Toolbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.material.navigation.NavigationView;

public class MainActivity2 extends AppCompatActivity {

    ImageView icon;
    static TextView idView;
    ImageView iconend;
    Toolbar toolbar;

    static TextView tempView;

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
    static TextView testView;

    static TextView temp2View;

    static TextView currenttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        testView = (TextView)findViewById(R.id.testView);
        String nfcData = getIntent().getStringExtra("Przerzucanie");
        testView.setText(nfcData);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setVisibility(View.GONE);

        icon = findViewById(R.id.icon);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView.setVisibility(View.VISIBLE);
            }
        });
        iconend = findViewById(R.id.iconend);
        iconend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView.setVisibility(View.GONE);
            }
        });
        





        //  VARIABLES
        tempView = (TextView) findViewById(R.id.tempView);
        idView = (TextView) findViewById(R.id.idView);

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
        s_MyTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(s_MyTag != null) {
            Toast.makeText(this, "Tag detected!", Toast.LENGTH_SHORT).show();
            temp2View = (TextView)findViewById(R.id.temp2View);
            currenttime = (TextView)findViewById(R.id.currenttime);
            testView.setText(" ");
            temp2View.setText("Temperature: ");
            NfcComm.NfcComm_ReadNDEF();
        }
    }

    private void showException(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    protected static void showText(String text) {
        s_Toast.cancel();
        s_Toast.makeText(s_Context, text, Toast.LENGTH_SHORT).show();
    }
}
