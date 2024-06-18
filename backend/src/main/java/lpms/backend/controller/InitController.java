package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.service.SignalDataService;
import lpms.backend.utils.FileUtils;
import lpms.backend.utils.Pair;
import lpms.backend.utils.ProjectConstans;
import lpms.enums.EventType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static lpms.backend.utils.ProjectConstans.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InitController {
    private final SignalDataService service;
    private static final String folderPath = Paths.get(USER_DIR,"lpms_binary_data").toString();

    @GetMapping("/init")
    public String init() throws IOException, ClassNotFoundException {
        loadBinaryFile();
        return "init";
    }

    private void loadBinaryFile() throws IOException, ClassNotFoundException {
        log.info("file Opened!");
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        if(files!=null){
            for (File file: files){
                // 확장자가 .bin인 파일 찾기
                if (file.isFile() && file.getName().endsWith(".bin")) {

                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();


                    if(service.findByFileName(fileName) != null) {
                        log.info("skip");
                        continue;
                    }

                    FileInfo fileInfo = FileUtils.fileOpen(filePath);

                    log.info("filePath={}", filePath);
                    log.info("fileName={}", fileName);



                    HeaderInfo headerInfo = fileInfo.getHeaderInfo();
                    System.out.println("headerInfo = " + headerInfo);
                    List<Pair<ChInfo, List<Float>>> pairList = fileInfo.getPairList();
                    for (Pair<ChInfo, List<Float>> pair : pairList) {
                        ChInfo chInfo = pair.getKey();
                        List<Float> floatList = pair.getValue();
                        System.out.print(chInfo.getCh_No()+ ": ");
                        for (int i=0; i<20; i++) {
                            System.out.print(floatList.get(i)+" ");
                        }
                        System.out.println();
                    }
                    System.out.println();



                    SignalData signalData = new SignalData();
                    signalData.setFilePath(filePath);
                    signalData.setFileName(fileName);
                    //headerInfo.EventType 데이터에 따라 String 값
                    EventType eventType = EventType.fromValue(headerInfo.getEventType());
                    signalData.setType(eventType);


                    if(headerInfo.getAlarm_Result()== 1)signalData.setStatus(true);
                    else signalData.setStatus(false);


                    service.save(signalData);
                }
            }
        }

    }
}
