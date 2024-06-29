package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.dto.ResultDTO;
import lpms.backend.dto.SignalDataDTO;
import lpms.backend.service.SignalProcessingService;
import lpms.backend.utils.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class apiController {
    private final SignalProcessingService signalProcessingService;

    /**
     * Endpoint to request the time graph for a given file.
     * If no channel is selected, the event channel is used; otherwise, the selected channel is used.
     *
     * @param selectedFileName the name of the file to process
     * @param selectedCh the selected channel, can be null
     * @return a list of DataPoints representing the time graph
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @GetMapping("/api/time/{selectedFileName}")
    public List<DataPoint> timeGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        log.info("Time Graph for fileName={}", selectedFileName);
        return signalProcessingService.processTimeGraph(selectedFileName, selectedCh);
    }

    /**
     * Endpoint to request the frequency graph for a given file.
     * If no channel is selected, the event channel is used; otherwise, the selected channel is used.
     *
     * @param selectedFileName the name of the file to process
     * @param selectedCh the selected channel, can be null
     * @return a list of DataPoints representing the frequency graph
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @GetMapping("/api/freq/{selectedFileName}")
    public List<DataPoint> freqGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        log.info("Frequency Graph for fileName={}", selectedFileName);
        return signalProcessingService.processFrequencyGraph(selectedFileName, selectedCh);
    }

    /**
     * Endpoint to generate the STFT image and request AI results for a given file.
     * STFT is performed on the event channel if no channel is selected, otherwise on the selected channel.
     * AI is performed on the event channel.
     *
     * @param selectedFileName the name of the file to process
     * @param selectedCh the selected channel, can be null
     * @return a Result object containing the STFT and AI results
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @GetMapping("/api/result/{selectedFileName}")
    public ResultDTO getResult(@PathVariable String selectedFileName,
                               @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        log.info("STFT and AI for fileName ={}", selectedFileName);
        log.info("selectedCh = {}", selectedCh);
        return signalProcessingService.processResultV2(selectedFileName, selectedCh);
    }

    /**
     * Endpoint to load files stored in the database.
     *
     * @return a list of SignalDataDTO objects representing the loaded files
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @GetMapping("/api/files")
    public List<SignalDataDTO> loadFiles() throws IOException, ClassNotFoundException {
        log.info("Loading all files");
        return signalProcessingService.loadAllFiles();
    }
}

