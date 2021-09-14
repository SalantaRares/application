package ro.btrl.miswebappspringdemo.db1.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.btrl.miswebappspringdemo.db1.model.Db1Model;
import ro.btrl.miswebappspringdemo.utils.UtilsRepository;

import java.util.List;

@Repository
@Transactional
public class Db1DaoImpl implements Db1Dao {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public List<Db1Model> getDb1Models() {
        return UtilsRepository.getDataAsListByAliasBean(sessionFactory, "select * from abc", Db1Model.class);
    }
}
