package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.dto.SignalDataDTO;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.models.DataPoint;
import lpms.backend.service.SignalDataService;
import lpms.backend.utils.ByteUtils;
import lpms.backend.utils.FileUtils;
import lpms.backend.utils.Pair;
import lpms.backend.utils.ProjectConstans;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static lpms.backend.utils.ProjectConstans.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class apiController {
    private final SignalDataService service;




    @GetMapping("/api/time/{selectedFileName}")
    public List<DataPoint> timeGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
       // log.info("selectedIdx: " + selectedIdx);

        SignalData signalData = service.findByFileName(selectedFileName);
        String filePath = signalData.getFilePath();
        FileInfo fileInfo = FileUtils.fileOpen(filePath);
        HeaderInfo headerInfo = fileInfo.getHeaderInfo();


        int ch_id;
        if (selectedCh==null) ch_id = headerInfo.getEvent_Ch();
        else ch_id= selectedCh;


        log.info("ch_id={} ", ch_id);

        Pair<ChInfo, List<Float>> pairList = fileInfo.getPairList().get(ch_id);
        List<Float> ListByCh= pairList.getValue();
        double eps = 0.1 / 20000;

        List<DataPoint> dataPointList = new ArrayList<>(20000);

        for (int i=0; i<20000; i++){
            dataPointList.add(new DataPoint((i+1)*eps,ListByCh.get(i)));
        }

        return dataPointList;
    }

    @GetMapping("/api/freq/{selectedFileName}")
    public List<DataPoint> freqGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        // log.info("selectedIdx: " + selectedIdx);
        log.info("selectedFileName = {} ", selectedFileName);
        SignalData signalData = service.findByFileName(selectedFileName);
        String filePath = signalData.getFilePath();
        FileInfo fileInfo = FileUtils.fileOpen(filePath);

        HeaderInfo headerInfo = fileInfo.getHeaderInfo();
        int ch_id;
        if (selectedCh==null) ch_id = headerInfo.getEvent_Ch();
        else ch_id= selectedCh;

        log.info("ch_id={} ", ch_id);

        Pair<ChInfo, List<Float>> pairList = fileInfo.getPairList().get(ch_id);
        List<Float> ListByCh= pairList.getValue();

        stft(ListByCh);


        //String binFilePath = "D:\\lpms_merge\\sample.bin";
        String binFilePath = Paths.get(USER_DIR,"sample.bin").toString();
        DataInputStream dis = new DataInputStream(new FileInputStream(binFilePath));
        List<DataPoint> dataPointList = new ArrayList<>();
        for (int i=0; i<2500; i++){
            float x=(i+1)*10;
            double y= ByteUtils.readDoubleLittleEndian(dis);
            dataPointList.add(new DataPoint(x,y));
        }



        log.info("size= {}", dataPointList.size());
        for (DataPoint dataPoint : dataPointList) {
            //System.out.println("x y: "+ dataPoint.getX() + " " + dataPoint.getY());
        }

        return dataPointList;
    }

    @GetMapping("/api/files")
    public List<SignalDataDTO> loadFiles() throws IOException, ClassNotFoundException {
        log.info("loadfiles");
        List<SignalData> signalDataList = service.findAll();
        List<SignalDataDTO> signalDataDTOList = new ArrayList<>();
        for (SignalData signalData : signalDataList) {
            String filePath = signalData.getFilePath();
            FileInfo fileInfo = FileUtils.fileOpen(filePath);
            HeaderInfo headerInfo = fileInfo.getHeaderInfo();

            signalDataDTOList.add(new SignalDataDTO(signalData,headerInfo));
        }

        for (SignalDataDTO signalDataDTO : signalDataDTOList) {
            log.info("signalDataDTO = " + signalDataDTO);
        }

        return signalDataDTOList;
    }

    private static void stft(List<Float> ListByCh) {
        log.info("stft start");
        String pythonPath = Paths.get(USER_DIR,"python.exe").toString();
        String pythonScriptPath = Paths.get(USER_DIR, "temp.py").toString();
        String weightPath =Paths.get(USER_DIR,"3_0.002536800100707111.pth").toString();

        try{

            // 외부 프로세스 실행
            ProcessBuilder pb = new ProcessBuilder(pythonPath, pythonScriptPath);
            Process process = pb.start();

            log.info("path ={} ", pythonScriptPath);


            // Python 스크립트로 데이터 전달
            OutputStream outputStream = process.getOutputStream();
            StringBuilder sb = new StringBuilder();

            sb.append(weightPath).append("\n");
            //2만개
            for (Float value : ListByCh) {
                sb.append(value.toString()).append(",");
            }


            // 마지막 콤마 제거
            sb.deleteCharAt(sb.length() - 1);
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // Python 스크립트 실행 결과 확인
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cnt=0;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                cnt++;
            }

            log.info("cnt={}", cnt);

            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                // 오류 처리
                System.out.println("errorLine = " + errorLine);
            }

            // 외부 프로세스가 종료되길 대기
            int exitCode = process.waitFor();
            System.out.println("exitCode = " + exitCode);

            // 프로세스 종료
            process.destroy();

            int exitValue = process.exitValue();
            System.out.println("exitValue = " + exitValue);

            log.info("stft finished");

        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }





}

