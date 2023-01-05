package com.application.app.repository.dao.forestryEngineer;


import com.application.app.entities.AllClientsEntity;
import com.application.app.models.AllClients;

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
