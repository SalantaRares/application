package com.application.db2.app.service.forestryEngineer;

import com.application.db2.app.dtos.forestryEngineer.AllClientsDto;
import com.application.db2.app.models.AllClients;
import com.application.db2.app.entities.AllClientsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ForestryEngineerService {
    List<AllClientsEntity> getAllClientsEntity(String user);

    List<AllClients> getTodayExpire(String user);

    List<AllClients> getExpireByGivenDate(Long date, String user);

    String insertNewClient(AllClientsDto allClientsDto, String user);

    String giveAllQuantity(String cnp, String user);

    String givePartQuantity(String cnp, BigDecimal portieMcPredata, String user);

    String updateRemainingYears(String cnp, String aniiRestanta, String user);

    List<String> getAniiRestanta(String cnp);

    String updateClient(String nume,
                        String prenume,
                        String cnp,
                        Long dataRidicarePortie,
                        Long dataScadentaRidicarePortie,
                        BigDecimal cantitateTotala,
                        String um,
                        String imputernicit,
                        BigDecimal portieAn,
                        BigDecimal portieRamasaAniiAnteriori,
                        BigDecimal hectareDetinute,
                        BigDecimal portieRestanta,
                        String cnpImputernicit,
                        String aniiRestanta,
                        String user);

    String importClientsFromExcel(MultipartFile file, String user);
}
