package com.application.app.repository.dao.basic;

import java.math.BigDecimal;
import java.util.List;

public interface BasicDao {

    Object getObjectById(Object id, Class classType);

    void deleteObject(Object obj);

    void updateObject(Object obj);

    void createObject(Object obj);

    void createObjects(List<Object> objects);

    BigDecimal getSeqValue(String sequenceStatement);
}
