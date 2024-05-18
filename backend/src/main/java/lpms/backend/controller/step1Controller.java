package lpms.backend.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.BinaryData;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.service.SignalDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class step1Controller {
    private final SignalDataService service;


    @GetMapping("/step1")
    public String step1(@RequestParam("selectedIdx") int selectedIdx, Model model) throws IOException, ClassNotFoundException {
        log.info("selectedIdx: " + selectedIdx);
        List<List<Float>> floatList = fileOpen(Long.valueOf(selectedIdx));

        int ch_id=1;
        List<Float> ListByCh= floatList.get(ch_id);
        model.addAttribute("data",ListByCh);
        model.addAttribute("selectedIdx", selectedIdx);
        return "step1";
    }
    

    public List<List<Float>> fileOpen(Long id) throws IOException, ClassNotFoundException {
        SignalData signalData = service.findById(id);
        BinaryData binaryData = signalData.getBinaryData();
        String filePath = binaryData.getBinaryPath();

        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));

        HeaderInfo headerInfo = HeaderInfo.readHeaderInfo(dis);// 헤더 정보 읽기 (64*8 bytes)

        //헤더 정보 출력
        System.out.println("headerInfo = " + headerInfo);

        //바이너리 파일을 변환한 데이터를 저장할 List
        List<List<Float>> floatList = new ArrayList<>(headerInfo.getTotal_Ch_Number() + 1);

        //채널 정보를 저장하는 List
        List<ChInfo> chInfoList = new ArrayList<>(headerInfo.getTotal_Ch_Number() + 1);
        for (int i = 0; i <= headerInfo.getTotal_Ch_Number(); i++) {
            floatList.add(new ArrayList<>());
        }

        chInfoList.add(null);
        for (int i = 1; i <= headerInfo.getTotal_Ch_Number(); i++) {
            chInfoList.add(ChInfo.readChannelInfo(dis)); //채널 정보 읽기 (17*8 bytes)

            //20000개의 4bytes float 데이터 읽기
            for (int j = 0; j < headerInfo.getNLength(); j++) {
                floatList.get(i).add(readFloatLittleEndian(dis) * headerInfo.getFSensitivity());
            }
        }


        printFloatList(floatList,chInfoList);
        log.info("size={}", floatList.get(1).size() * 18);

        return floatList;
    }

    private void printFloatList(List<List<Float>> floatList, List<ChInfo> chInfoList) {
        for (int i=1; i<=18; i++){
            int ch_id= 100+i;
            System.out.print("v" + ch_id+": ");
            //System.out.println("chInfoList = " + chInfoList.get(i));
            //csv파일과 비교하기 위해 현재 일부 값만을 출력
            for (int j=0; j<20; j++){
                System.out.print(floatList.get(i).get(j)+" ");
            }
            System.out.println();
        }
    }

    public static float readFloatLittleEndian(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[4];
        dis.readFully(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getFloat();
    }

}

