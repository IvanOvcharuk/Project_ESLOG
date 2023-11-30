package com.example.project_eslog;

public class Utility {
    final protected static char[] s_HexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    protected static String Utility_ByteArrayToHexString(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length*2];
        int v;

        for(int j=0; j < bytes.length; j++)
        {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = s_HexArray[v>>>4];
            hexChars[j*2 + 1] = s_HexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    protected static String Utility_BuildBitString(int data, int length, int offset)
    {
        String bits = "";
        for(int i = 0; i < length; i++)
        {
            if((data>>(i+offset)&0x01) != 0)
            {
                bits = "1"+bits;
            }
            else
            {
                bits = "0"+bits;
            }
        }
        return bits;
    }

    protected static int Utility_Crc4(byte[] input)
    {
        int length = 8;
        int poly = 0x13;
        int temp = (input[0]&0xFF);
        for (int i = 1; i < input.length+1; i++)
        {
            if(i != input.length)
            {
                temp = temp << 8;
                temp |= (input[i] & 0xFF);
                length += 8;
            }
            else if (i == input.length)
            {
                temp = temp << 4;
                length += 4;
            }
            while(length > 4)
            {
                if((temp >> (length-1)) == 1){
                    temp ^= (poly << (length-5));
                }
                length--;
            }
        }
        return temp;
    }

    protected static String Utility_ConvertTemperature(short data)
    {
        char sign = '+';
        int integer = data >> 6;
        int fractional = data & 0x3F;

        if(data< 0)
        {
            sign = '-';
            integer = -integer-((fractional !=0) ? 1: 0);
            fractional = (64-fractional) & 0x3F;
        }
        String result = String.format("%c%d.%3d*C", sign, integer, (fractional*1000) >> 6);

        return result;
    }

    protected static void Utility_ReverseByteArray(byte[] array) {
        int length = array.length;
        for (int i = 0; i < length / 2; i++) {
            byte temp = array[i];
            array[i] = array[length - 1 - i];
            array[length - 1 - i] = temp;
        }
    }
}
