package lpms.backend.info;

import lombok.Getter;
import lombok.Setter;
import lpms.backend.utils.Pair;

import java.util.List;


/**
 * Class representing file information which includes header information
 * and a list of pairs consisting of channel information and corresponding float values.
 */
@Getter @Setter
public class FileInfo {
    private HeaderInfo headerInfo;
    private List<Pair<ChInfo, List<Float>>> pairList;


    /**
     * Constructor to initialize the FileInfo object with header information and pair list.
     *
     * @param headerInfo the header information of the file
     * @param pairList the list of pairs containing channel info and corresponding float values
     */
    public FileInfo(HeaderInfo headerInfo, List<Pair<ChInfo, List<Float>>> pairList) {
        this.headerInfo = headerInfo;
        this.pairList = pairList;
    }
}
