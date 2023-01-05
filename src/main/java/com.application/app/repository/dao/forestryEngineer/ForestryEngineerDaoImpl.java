package com.application.app.repository.dao.forestryEngineer;

import com.application.app.entities.AllClientsEntity;
import com.application.app.models.AllClients;
import com.application.app.repository.query.ForestryEngineerQueries;
import com.application.util.CustomParameter;
import com.application.util.UtilsRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Transactional(noRollbackFor=Exception.class)
public class ForestryEngineerDaoImpl implements ForestryEngineerDao {
    private final SessionFactory sessionFactory;

    public ForestryEngineerDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<AllClientsEntity> getAllClientsEntity() {
        return UtilsRepository.getDataAsListByAliasBean(sessionFactory, ForestryEngineerQueries.GET_ALL_INFO,AllClientsEntity.class);
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
