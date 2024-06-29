package lpms.backend.utils;

import lombok.extern.slf4j.Slf4j;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Utility class for file operations.
 */
@Slf4j
public class FileUtils {


    /**
     * Opens and reads the file at the specified path, parsing its header and channel information.
     *
     * @param filePath the path to the file
     * @return a FileInfo object containing the parsed header and channel information
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public static FileInfo fileOpen(String filePath) throws IOException, ClassNotFoundException {
        int cnt=0;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            // Read header information (64*8 bytes)
            HeaderInfo headerInfo = HeaderInfo.readHeaderInfo(dis);
             log.info("headerInfo = {}" ,headerInfo);

            List<Pair<ChInfo, List<Float>>> resultList = new ArrayList<>(headerInfo.getTotalChNumber());


            // Read channel information and float data for each channel
            for (int i = 1; i <= headerInfo.getTotalChNumber(); i++) {
                ChInfo chInfo = ChInfo.readChannelInfo(dis); // Read channel information (17*8 bytes)
                List<Float> floatList = new ArrayList<>();
                log.info("channel = {}", i);
                log.info("chInfo = {}", chInfo);

                // Read nLength float data values
                for (int j = 0; j < headerInfo.getNLength(); j++) {
                    //floatList.add(ByteUtils.readFloatLittleEndian(dis) * headerInfo.getFSensitivity());
                    floatList.add(ByteUtils.readFloatLittleEndian(dis));
                    cnt++;
                }
                for (int j=0; j<20; j++){
                    System.out.print(floatList.get(j)+" " );
                }
                System.out.println();
                floatList.remove(floatList.size()-1);
                resultList.add(new Pair<>(chInfo, floatList));
            }

            return new FileInfo(headerInfo, resultList);
        }
        catch(Exception e){
                log.error("Error processing file at read count {}: {}", cnt, e);
                cnt=0;
                throw e;
        }
    }

    /**
     * Removes the ".bin" extension from the specified file name.
     *
     * @param fileName the file name
     * @return the file name without the ".bin" extension
     */
    public static String removeBinExtension(String fileName) {
        if (fileName != null && fileName.toLowerCase().endsWith(".bin")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    public static String getTimeFromFileName(String fileName) {
        // 확장자 제거
        String nameWithoutExtension = removeBinExtension(fileName);
        // 언더스코어로 구분된 마지막 부분이 시간 정보
        String timePart = nameWithoutExtension.substring(nameWithoutExtension.lastIndexOf('_') + 1);
        return parseAndPrintTime(timePart);
    }

    public static String parseAndPrintTime(String timePart) {
        // 시간 정보 출력
        System.out.println("Raw time part: " + timePart);

        // 시간 정보 파싱 (예: YYMMDDhhmmss)
        String year = "20" + timePart.substring(0, 2);
        String month = timePart.substring(2, 4);
        String day = timePart.substring(4, 6);
        String hour = timePart.substring(6, 8);
        String minute = timePart.substring(8, 10);
        String second = timePart.substring(10, 12);

        // 파싱된 시간 정보 출력
        //System.out.println("Parsed time: " + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

}

