package lpms.backend.info;

import lombok.Getter;
import lombok.Setter;
import lpms.backend.utils.Pair;

import java.util.List;


@Getter @Setter
public class FileInfo {
    private HeaderInfo headerInfo;
    private List<Pair<ChInfo, List<Float>>> pairList;

    public FileInfo() {

    }

    public FileInfo(HeaderInfo headerInfo, List<Pair<ChInfo, List<Float>>> pairList) {
        this.headerInfo = headerInfo;
        this.pairList = pairList;
    }
}
