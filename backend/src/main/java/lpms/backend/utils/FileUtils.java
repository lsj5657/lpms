package lpms.backend.utils;

import lombok.RequiredArgsConstructor;
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




public class FileUtils {

    public static FileInfo fileOpen(String filePath) throws IOException, ClassNotFoundException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            HeaderInfo headerInfo = HeaderInfo.readHeaderInfo(dis); // 헤더 정보 읽기 (64*8 bytes)
            //System.out.println("headerInfo = " + headerInfo); // 헤더 정보 출력

            List<Pair<ChInfo, List<Float>>> resultList = new ArrayList<>(headerInfo.getTotal_Ch_Number());

            for (int i = 1; i <= headerInfo.getTotal_Ch_Number(); i++) {
                ChInfo chInfo = ChInfo.readChannelInfo(dis); // 채널 정보 읽기 (17*8 bytes)
                List<Float> floatList = new ArrayList<>();

                for (int j = 0; j < headerInfo.getNLength(); j++) {
                    floatList.add(ByteUtils.readFloatLittleEndian(dis) * headerInfo.getFSensitivity());
                }

                resultList.add(new Pair<>(chInfo, floatList));
            }

            return new FileInfo(headerInfo, resultList);
        }
    }

}

