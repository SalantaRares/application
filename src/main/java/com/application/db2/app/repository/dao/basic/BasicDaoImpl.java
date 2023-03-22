package com.application.db2.app.repository.dao.basic;

import com.application.utils.hibernate.UtilsRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional(value = "db2TransactionManager", timeout = 300)
public class BasicDaoImpl implements BasicDao {
    @Autowired
    @Qualifier("db2SessionFactory")
    SessionFactory sessionFactory;

    @Override
    public Object getObjectById(Object id, Class objectClass) {
        return UtilsRepository.getObjectById(sessionFactory, objectClass, id);
    }

    @Override
    public void deleteObject(Object obj) {
        UtilsRepository.deleteObject(sessionFactory, obj);
    }

    @Override
    public void updateObject(Object obj) {
        UtilsRepository.update(sessionFactory, obj);
    }

    @Override
    public void createObject(Object obj) {
        UtilsRepository.create(sessionFactory, obj);
    }

    @Override
    public void createObjects(List<Object> objects) {
        for (Object obj : objects) {
            UtilsRepository.create(sessionFactory, obj);
        }
    }

    @Override
    public BigDecimal getSeqValue(String sequenceStatement) {
        return new BigDecimal(UtilsRepository.getNextValue(sessionFactory, sequenceStatement).longValue());
    }
}
