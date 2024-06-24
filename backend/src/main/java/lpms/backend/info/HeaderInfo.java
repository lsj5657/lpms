package lpms.backend.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static lpms.backend.utils.ByteUtils.*;

@Getter @Setter
@ToString
public class HeaderInfo {
    public String SiteID;
    public String sPlant;
    public short System_ID;
    public SystemType sType;
    public short Event_Ch;
    public short Total_Ch_Number;
    public short EventType;
    public String Event_Date;
    public short Alarm_Result;
    public String Signal_Verification_Result;
    public int Sampling_Rate;
    public short User_ID;
    public short Signal_Verificaion_Selection;
    public int Event_Duration;
    public String Signal_Type;
    public float fSensitivity;
    public float fMilsV;
    public int nLength;

    public static enum SystemType {
        LPMS_BIN,
        ALMS,
        RCPVMS,
        IVMS
    }

    public static HeaderInfo readHeaderInfo(DataInputStream dis) throws IOException {
        HeaderInfo headerInfo = new HeaderInfo();

        byte[] buffer;

        buffer = new byte[8];
        dis.readFully(buffer);
        headerInfo.SiteID = new String(buffer).trim();

        if (headerInfo.SiteID.toLowerCase().contains("hanbit")) {
            headerInfo.sPlant = "한빛";
        } else if (headerInfo.SiteID.toLowerCase().contains("hanul")) {
            headerInfo.sPlant = "한울";
        }

        headerInfo.System_ID = dis.readShort();
        headerInfo.sType = SystemType.LPMS_BIN;

        headerInfo.Event_Ch = readShortLittleEndian(dis);
        headerInfo.Total_Ch_Number = readShortLittleEndian(dis);
        headerInfo.EventType = dis.readShort();

        buffer = new byte[24];
        dis.readFully(buffer);
        headerInfo.Event_Date = new String(buffer).trim();

        headerInfo.Alarm_Result = readShortLittleEndian(dis);

        buffer = new byte[6];
        dis.readFully(buffer);
        headerInfo.Signal_Verification_Result=new String(buffer).trim();

        headerInfo.Sampling_Rate = readIntLittleEndian(dis);
        headerInfo.User_ID = dis.readShort();
        headerInfo.Signal_Verificaion_Selection = readShortLittleEndian(dis);
        headerInfo.Event_Duration = readIntLittleEndian(dis);

        int nSignalType = readShortLittleEndian(dis);

        if (headerInfo.sType == SystemType.LPMS_BIN) {
            switch (nSignalType) {
                case 0:
                    headerInfo.Signal_Type = "BackGround Noise";
                    break;
                case 1:
                    headerInfo.Signal_Type = "Event";
                    break;
                case 2:
                    headerInfo.Signal_Type = "PST";
                    break;
                case 3:
                    headerInfo.Signal_Type = "Impact Test";
                    break;
                case 4:
                    headerInfo.Signal_Type = "Baseline Test";
                    break;
                case 5:
                    headerInfo.Signal_Type = "RCP Trigger";
                    break;
                default:
                    headerInfo.Signal_Type = "Unidentified";
                    break;
            }
        } else {
            System.out.println("RG NIMS LPMS_BIN 데이터 파일형식이 아닙니다.");
            return null;
        }

        dis.readShort();
        headerInfo.fSensitivity = readFloatLittleEndian(dis);
        headerInfo.fMilsV = dis.readFloat();


        dis.skipBytes(8 * (64 - 9));


        double itmp = (double) headerInfo.Sampling_Rate * headerInfo.Event_Duration;
        double tmps = itmp / 1000.0;
        headerInfo.nLength = (int) tmps;
        if (itmp < 0) {
            float tmp = (float) (headerInfo.Event_Duration / 1000.0);
            headerInfo.nLength = headerInfo.Sampling_Rate * (int) tmp;
        }


        return headerInfo;
    }




}