package com.application.db2.app.repository.dao.forestryEngineer;

import com.application.db2.app.models.AllClients;
import com.application.db2.app.repository.query.forestryEngineer.ForestryEngineerQueries;
import com.application.utils.UtilsRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.application.db2.app.entities.AllClientsEntity;
import com.application.utils.hibernate.CustomParameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional(value = "db2TransactionManager", timeout = 300)
public class ForestryEngineerDaoImpl implements ForestryEngineerDao {
    @Autowired
    @Qualifier("db2SessionFactory")
    SessionFactory sessionFactory;

    @Override
    public List<AllClientsEntity> getAllClientsEntity() {
        return UtilsRepository.getDataAsListByAliasBean(sessionFactory, ForestryEngineerQueries.GET_ALL_INFO, AllClientsEntity.class);
    }

    @Override
    public void insertObject(Object object) {
        try {
            UtilsRepository.create(sessionFactory, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal requestId() {
        return UtilsRepository.getNextValue(sessionFactory, ForestryEngineerQueries.SEQ_LOGS_UTILIZATORI_RARES);
    }

    @Override
    public List<AllClients> getTodayExpire() {
        return UtilsRepository.getDataAsListByAliasBean(sessionFactory, ForestryEngineerQueries.GET_EXPIRY_TODAY_DATE, AllClients.class);
    }

    @Override
    public List<AllClients> getExpireByGivenDate(Date date) {
        List<CustomParameter> list = new ArrayList<>();
        list.add(new CustomParameter(ForestryEngineerQueries.P_DATE, date, Date.class));
        return UtilsRepository.getDataAsListByAliasBeanWithParams(sessionFactory, ForestryEngineerQueries.GET_EXPIRY_BY_GIVEN_DATE, AllClients.class, list);
    }

    @Override
    public List<AllClients> getAniiRestanta(String cnp) {
        List<CustomParameter> list = new ArrayList<>();
        list.add(new CustomParameter(ForestryEngineerQueries.P_CNP, cnp, String.class));
        return UtilsRepository.getDataAsListByAliasBeanWithParams(sessionFactory, ForestryEngineerQueries.GET_ANII_RESTANTA, AllClients.class, list);
    }


    @Override
    public void insertClientsFromExcel(List<AllClientsEntity> list) {
        try {
            for (AllClientsEntity imp : list) {
                UtilsRepository.create(sessionFactory, imp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
