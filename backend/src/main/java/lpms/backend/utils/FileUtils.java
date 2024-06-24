package lpms.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.service.SignalDataService;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;




@Slf4j
public class FileUtils {

    public static FileInfo fileOpen(String filePath) throws IOException, ClassNotFoundException {
        int cnt=0;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            HeaderInfo headerInfo = HeaderInfo.readHeaderInfo(dis); // 헤더 정보 읽기 (64*8 bytes)
            // log.info("headerInfo = {}" ,headerInfo);

            List<Pair<ChInfo, List<Float>>> resultList = new ArrayList<>(headerInfo.getTotal_Ch_Number());

            for (int i = 1; i <= headerInfo.getTotal_Ch_Number(); i++) {
                ChInfo chInfo = ChInfo.readChannelInfo(dis); // 채널 정보 읽기 (17*8 bytes)
                List<Float> floatList = new ArrayList<>();
                //log.info("channel = {}", i);
               // log.info("chInfo = {}", chInfo);

                for (int j = 0; j < headerInfo.getNLength(); j++) {
                    floatList.add(ByteUtils.readFloatLittleEndian(dis) * headerInfo.getFSensitivity());
                    cnt++;
                }
                for (int j=0; j<20; j++){
                    System.out.print(floatList.get(j)+" " );
                }
                System.out.println();

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

    public static String removeBinExtension(String fileName) {
        if (fileName != null && fileName.endsWith(".bin")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

}

