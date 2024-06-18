package lpms.backend.repository;


import lpms.backend.entity.SignalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SignalDataRepository extends JpaRepository<SignalData, Long> {

    // fileName 리스트 반환 함수
    @Query("SELECT s.fileName FROM SignalData s")
    List<String> findAllFileNames();

    // fileName 으로 SignalData 찾기 함수
    SignalData findByFileName(String fileName);
}
