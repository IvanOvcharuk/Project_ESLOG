package com.example.project_eslog;

import android.nfc.TagLostException;
import android.nfc.tech.MifareUltralight;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NfcComm {
    protected static void NfcComm_ReadNDEF() {
        int blocksToRead = 0;
        int rest = 0;
        int ndefLength = 0;
        int offset = 0;
        byte[] ndefMessage;
        int typeLength = 0;
        int payloadLength = 0;
        byte[] payloadType;
        byte[] ndefPayload;
        byte[] language = new byte[2];
        byte[] temperature_raw;

        if (MainActivity2.s_MyTag != null) {
            MifareUltralight uTag = MifareUltralight.get(MainActivity2.s_MyTag);
            try {
                uTag.connect();
                if (uTag.isConnected()) {
                    byte[] data = uTag.readPages(4);
                    if (data != null) {
                        if (data[0] == (byte) 0x03) {
                            ndefLength = data[1] & 0xFF;
                            if (ndefLength != 0) {
                                ndefMessage = new byte[ndefLength];
                                System.arraycopy(data, 2, ndefMessage, 0, 14);
                                blocksToRead = ((ndefLength - 14 + 16 - 1) / 16);
                                rest = (ndefLength - 14) % 16;
                                for (int i = 0; i < blocksToRead; i++) {
                                    data = uTag.readPages(8 + 4 * i);
                                    if (i < blocksToRead - 1) {
                                        System.arraycopy(data, 0, ndefMessage, 14 + i * 16, 16);
                                    } else if (i == blocksToRead - 1) {
                                        if (rest == 0)
                                            System.arraycopy(data, 0, ndefMessage, 14 + i * 16, 16);
                                        else
                                            System.arraycopy(data, 0, ndefMessage, 14 + i * 16, rest);
                                    }
                                }
                                typeLength = ndefMessage[1] & 0xFF;

                                payloadLength = ndefMessage[2] & 0xFF;

                                payloadType = new byte[typeLength];
                                System.arraycopy(ndefMessage, 3 + offset, payloadType, 0, typeLength);
                                offset += typeLength;

                                ndefPayload = new byte[payloadLength];
                                System.arraycopy(ndefMessage, 3 + offset, ndefPayload, 0, payloadLength);
                                System.arraycopy(ndefPayload, 1, language, 0, 2);


                                byte[] id_raw = MainActivity2.s_MyTag.getId();
                                String id = Utility.Utility_ByteArrayToHexString(id_raw);
                                MainActivity2.idView.setText("Id: " + id);

                                //  Display Temperature
                                Date dateBeforeJava8 = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                MainActivity2.currenttime.setText("Current time: "+simpleDateFormat.format(dateBeforeJava8));
                                temperature_raw = new byte[7];
                                Utility.Utility_ReverseByteArray(ndefPayload);
                                System.arraycopy(ndefPayload, 18, temperature_raw, 0, 7);
                                Utility.Utility_ReverseByteArray(temperature_raw);
                                String temperature = new String(temperature_raw, StandardCharsets.UTF_8);
                                MainActivity2.tempView.setText(temperature + "℃");
                                MainActivity2.showText("NDEF read");
                            }
                        } else {MainActivity2.showText("Couldn't read data");
                        }
                    } else {MainActivity2.showText("Data not read");
                        }
                    uTag.close();
                } else {MainActivity2.showText("Tag not connected");
                    }
            } catch (IOException e) {
                if (e instanceof TagLostException) MainActivity2.showText("Tag lost!");
                else MainActivity2.showText("Error!");
            }
        } else {MainActivity2.showText("Tag not detected");
            }
    }

    protected static String NfcComm_ReadNDEF_2() {
       return "Scan again";
    }
}






