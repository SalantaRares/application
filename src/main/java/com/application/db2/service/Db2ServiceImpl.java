package com.application.db2.service;

import com.application.db2.dao.Db2Dao;
import com.application.db2.model.Db2Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Db2ServiceImpl implements Db2Service{

    @Autowired
    Db2Dao db2Dao;

    @Override
    public List<Db2Model> getDb2Models(){
        return db2Dao.getDb2Models();
    }
}
