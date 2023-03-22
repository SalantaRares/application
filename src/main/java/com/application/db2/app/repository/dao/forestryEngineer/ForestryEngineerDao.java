package com.application.db2.app.repository.dao.forestryEngineer;


import com.application.db2.app.models.AllClients;
import com.application.db2.app.entities.AllClientsEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ForestryEngineerDao {

    List<AllClientsEntity> getAllClientsEntity();

    void insertObject(Object object);

    BigDecimal requestId();

    List<AllClients> getTodayExpire();

    List<AllClients> getExpireByGivenDate(Date date);

    List<AllClients> getAniiRestanta(String cnp);

    void insertClientsFromExcel(List<AllClientsEntity> list);
}
