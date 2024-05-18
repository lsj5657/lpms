package lpms.backend.info;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.DataInputStream;
import java.io.IOException;

@Getter @Setter
@ToString
public class ChInfo
{
    private short Ch_No;
    private short Ch_Type;
    private float Max_Delay_Time;
    private String Ch_Name;
    private short Channel_Bypass;
    private short Alarm_Inhibit;
    private long uiInterChannel;
    private float RMS_Over_range;
    private float RMS_Under_range;
    private int Gain_Display_Range;
    private float Fixed_Event_Setpoint;
    private float Long_Term_EA;
    private float Short_Term_EA;
    private float Floating_Offset_Value;
    private int Pre_Trigger;
    private float RMS_Rate;
    private float RMS_Range;
    private float Freq_Min;
    private float Freq_Max;
    private float First_Range;
    private float Last_Range;
    private float Delay_Time;
    private float Peak_Rate;
    private int Event_Count;
    private int Event_Time;
    private float pp;
    private float bpms;
    private float frms;
    private float ringing_Frequency;
    private float prms;
    private float pmv;
    private float drms;
    private long uiEventCountin100;

    public static ChInfo readChannelInfo(DataInputStream dis) throws IOException {
        ChInfo data = new ChInfo();

        data.Ch_No = dis.readShort();
        data.Ch_Type = dis.readShort();
        data.Max_Delay_Time = dis.readFloat();

        byte[] buffer = new byte[16];
        dis.readFully(buffer);
        data.Ch_Name = new String(buffer).trim();

        data.Channel_Bypass = dis.readShort();
        data.Alarm_Inhibit = dis.readShort();
        data.uiInterChannel = dis.readInt() & 0xFFFFFFFFL; // To get unsigned behavior

        data.RMS_Over_range = dis.readFloat();
        data.RMS_Under_range = dis.readFloat();
        data.Gain_Display_Range = dis.readInt();
        data.Fixed_Event_Setpoint = dis.readFloat();
        data.Long_Term_EA = dis.readFloat();
        data.Short_Term_EA = dis.readFloat();
        data.Floating_Offset_Value = dis.readFloat();
        data.Pre_Trigger = dis.readInt();
        data.RMS_Rate = dis.readFloat();
        data.RMS_Range = dis.readFloat();
        data.Freq_Min = dis.readFloat();
        data.Freq_Max = dis.readFloat();
        data.First_Range = dis.readFloat();
        data.Last_Range = dis.readFloat();
        data.Delay_Time = dis.readFloat();
        data.Peak_Rate = dis.readFloat();
        data.Event_Count = dis.readInt();
        data.Event_Time = dis.readInt();
        data.pp = dis.readFloat();
        data.bpms = dis.readFloat();
        data.frms = dis.readFloat();
        data.ringing_Frequency = dis.readFloat();
        data.prms = dis.readFloat();
        data.pmv = dis.readFloat();
        data.drms = dis.readFloat();
        data.uiEventCountin100 = dis.readInt() & 0xFFFFFFFFL;

        return data;
    }
}