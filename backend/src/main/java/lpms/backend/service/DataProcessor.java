package lpms.backend.service;

import lombok.extern.slf4j.Slf4j;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.utils.ByteUtils;
import lpms.backend.utils.DataPoint;
import lpms.backend.utils.Pair;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static lpms.backend.utils.ProjectConstans.ROOT_DIR;

@Component
@Slf4j
public class DataProcessor {

    /**
     * Converts a list of float values to a list of DataPoints for time graph.
     *
     * @param floatList the list of float values
     * @return a list of DataPoints
     */
    public List<DataPoint> getTimeDataPoint(List<Float> floatList) {
        double eps = 0.1 / 20000;
        List<DataPoint> dataPointList = new ArrayList<>(20000);
        for (int i = 0; i < 20000; i++) {
            dataPointList.add(new DataPoint((i + 1) * eps, floatList.get(i)));
        }
        return dataPointList;
    }

    /**
     * Reads frequency data points from a file for the frequency graph.
     *
     * @param selectedFileName the name of the file
     * @return a list of DataPoints representing the frequency graph
     * @throws IOException if an I/O error occurs
     */
    public List<DataPoint> getFrequencyDataPoint(String selectedFileName) throws IOException {
        String binFilePath = Paths.get(ROOT_DIR, selectedFileName).toString();
        DataInputStream dis = new DataInputStream(new FileInputStream(binFilePath));
        List<DataPoint> dataPointList = new ArrayList<>();
        for (int i = 0; i < 2500; i++) {
            float x = (i + 1) * 10;
            double y = ByteUtils.readDoubleLittleEndian(dis);
            dataPointList.add(new DataPoint(x, y));
        }
        return dataPointList;
    }

    /**
     * Retrieves the list of float values for the specified channel from the file information.
     *
     * @param fileInfo the file information
     * @param chId the channel ID
     * @return the list of float values for the specified channel
     */
    public List<Float> getListByCh(FileInfo fileInfo, int chId) {
        Pair<ChInfo, List<Float>> pairList = fileInfo.getPairList().get(chId);
        log.info("chId ={}" , chId);
        return pairList.getValue();
    }

    /**
     * Gets the channel ID based on the selected channel or event channel from the file information.
     *
     * @param selectedCh the selected channel, can be null
     * @param fileInfo the file information
     * @return the channel ID
     */
    public int getChannelId(Integer selectedCh, FileInfo fileInfo) {
        return (selectedCh == null) ? fileInfo.getHeaderInfo().getEventCh() : selectedCh;
    }
}