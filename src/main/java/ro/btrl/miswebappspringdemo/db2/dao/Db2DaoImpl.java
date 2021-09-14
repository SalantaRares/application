package ro.btrl.miswebappspringdemo.db2.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.btrl.miswebappspringdemo.db2.model.Db2Model;
import ro.btrl.miswebappspringdemo.utils.UtilsRepository;

import java.util.List;

@Repository
@Transactional("db2TransactionManager")
public class Db2DaoImpl implements Db2Dao {

    @Autowired
    @Qualifier("db2SessionFactory")
    SessionFactory sessionFactory;

    @Override
    public List<Db2Model> getDb2Models() {
        return UtilsRepository.getDataAsListByAliasBean(sessionFactory, "select * from bar", Db2Model.class);
    }
}
