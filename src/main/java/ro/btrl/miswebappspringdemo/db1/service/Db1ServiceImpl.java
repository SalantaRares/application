package ro.btrl.miswebappspringdemo.db1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.btrl.miswebappspringdemo.db1.dao.Db1Dao;
import ro.btrl.miswebappspringdemo.db1.model.Db1Model;

import java.util.List;

@Service
public class Db1ServiceImpl implements Db1Service {

    @Autowired
    Db1Dao db1Dao;

    @Override
    public List<Db1Model> getDb1Models(){
        return db1Dao.getDb1Models();
    }
}
