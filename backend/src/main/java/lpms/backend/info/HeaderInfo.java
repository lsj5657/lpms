package lpms.backend.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;

import static lpms.backend.utils.ByteUtils.*;

/**
 * Class representing header information.
 */
@Getter @Setter
@ToString
@Slf4j
public class HeaderInfo {
    private String siteId;
    private String sPlant;
    private short systemId;
    private SystemType sType;
    private short eventCh;
    private short totalChNumber;
    private short eventType;
    private String eventDate;
    private short alarmResult;
    private String signalVerificationResult;
    private int samplingRate;
    private short userID;
    private short signalVerificationSelection;
    private int eventDuration;
    private String signalType;
    private float fSensitivity;
    private float fMilsV;
    private int nLength;

    public static enum SystemType {
        LPMS_BIN,
        ALMS,
        RCPVMS,
        IVMS
    }

    /**
     * Reads header information from a DataInputStream. (64 * 8 bytes)
     *
     * @param dis the DataInputStream to read from
     * @return the populated HeaderInfo object
     * @throws IOException if an I/O error occurs
     */
    public static HeaderInfo readHeaderInfo(DataInputStream dis) throws IOException {
        HeaderInfo headerInfo = new HeaderInfo();

        byte[] buffer;

        buffer = new byte[8];
        dis.readFully(buffer);
        headerInfo.siteId = new String(buffer).trim();

        if (headerInfo.siteId.toLowerCase().contains("hanbit")) {
            headerInfo.sPlant = "한빛";
        } else if (headerInfo.siteId.toLowerCase().contains("hanul")) {
            headerInfo.sPlant = "한울";
        }

        headerInfo.systemId = dis.readShort();
        headerInfo.sType = SystemType.LPMS_BIN;

        headerInfo.eventCh = readShortLittleEndian(dis);
        headerInfo.totalChNumber = readShortLittleEndian(dis);
        headerInfo.eventType = dis.readShort();

        buffer = new byte[24];
        dis.readFully(buffer);
        headerInfo.eventDate = new String(buffer).trim();

        headerInfo.alarmResult = readShortLittleEndian(dis);

        buffer = new byte[6];
        dis.readFully(buffer);
        headerInfo.signalVerificationResult =new String(buffer).trim();

        headerInfo.samplingRate = readIntLittleEndian(dis);
        headerInfo.userID = dis.readShort();
        headerInfo.signalVerificationSelection = readShortLittleEndian(dis);
        headerInfo.eventDuration = readIntLittleEndian(dis);

        int nSignalType = readShortLittleEndian(dis);

        if (headerInfo.sType == SystemType.LPMS_BIN) {
            switch (nSignalType) {
                case 0:
                    headerInfo.signalType = "BackGround Noise";
                    break;
                case 1:
                    headerInfo.signalType = "Event";
                    break;
                case 2:
                    headerInfo.signalType = "PST";
                    break;
                case 3:
                    headerInfo.signalType = "Impact Test";
                    break;
                case 4:
                    headerInfo.signalType = "Baseline Test";
                    break;
                case 5:
                    headerInfo.signalType = "RCP Trigger";
                    break;
                default:
                    headerInfo.signalType = "Unidentified";
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


        double itmp = (double) headerInfo.samplingRate * headerInfo.eventDuration;
        double tmps = itmp / 1000.0;
        headerInfo.nLength = (int) tmps;
        if (itmp < 0) {
            float tmp = (float) (headerInfo.eventDuration / 1000.0);
            headerInfo.nLength = headerInfo.samplingRate * (int) tmp;
        }

        headerInfo.nLength++;
        return headerInfo;
    }




}