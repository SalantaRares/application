package ro.btrl.miswebappspringdemo.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.http.HttpStatus;
import ro.btrl.miswebappspringdemo.exceptions.CustomException;
import ro.btrl.miswebappspringdemo.utils.hibernate.CustomParameter;
import ro.btrl.miswebappspringdemo.utils.hibernate.CustomTransformers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Marius Pop
 * @since 30/October/2019
 */
public class UtilsRepository {


    public static List getDataAsList(SessionFactory sessionFactory, String query) {
        try {
            Query query2 = sessionFactory.getCurrentSession().createNativeQuery(query);
            return query2.list();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException("");
            return null;
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }


    public static List getDataAsListByAliasBean(SessionFactory sessionFactory, String query, Class type) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException("");
            return null;
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }


    public static Object getObjectById(SessionFactory sessionFactory, Class objectType, Object id) {
        try {
            return sessionFactory.getCurrentSession().get(objectType, (Serializable) id);
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static BigDecimal getNextValue(SessionFactory sessionFactory, String query) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query q = session.createNativeQuery(query);
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static void create(SessionFactory sessionFactory, Object object) {
        try {
            sessionFactory.getCurrentSession().persist(object);
        } catch (Exception e) {
            e.printStackTrace();
            throwDatabaseException(e.getMessage());
        }
    }

    public static void createOrUpdate(SessionFactory sessionFactory, Object object) {
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(object);
        } catch (Exception e) {
            e.printStackTrace();
            throwDatabaseException(e.getMessage());
        }
    }

    public static void deleteObject(SessionFactory sessionFactory, Object object) {
        try {
            sessionFactory.getCurrentSession().delete(object);
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
        }
    }

    public static void update(SessionFactory sessionFactory, Object object) {
        try {
            sessionFactory.getCurrentSession().merge(object);
        } catch (Exception e) {
            e.printStackTrace();
            throwDatabaseException(e.getMessage());
        }
    }


    public static List getDataAsListByAliasBeanWithParams(SessionFactory sessionFactory, String query, Class type,
                                                          List<CustomParameter> parameterList) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            if (parameterList != null & parameterList.size() > 0) {
                for (CustomParameter param : parameterList) {
                    if (param.getValue() != null) {
                        q.setParameter(param.getName(), param.getValue());
                    } else {
                        if (param.getObjectClass() != null) {
                            if (param.getObjectClass() == Date.class) {
                                q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.DATE);
                            }
                            if (param.getObjectClass() == String.class) {
                                q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.STRING);
                            }
                        } else {
                            q.setParameter(param.getName(), param.getValue());
                        }
                    }
                }
            }
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException("");
            return null;
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static void executeWithParameters(SessionFactory sessionFactory, String statement, List<CustomParameter> parameterList) {
        Query q = sessionFactory.getCurrentSession().createNativeQuery(statement);
        if (parameterList != null & parameterList.size() > 0) {
            for (CustomParameter param : parameterList) {
                if (param.getValue() != null) {
                    q.setParameter(param.getName(), param.getValue());
                } else {
                    if (param.getObjectClass() != null) {
                        if (param.getObjectClass() == Date.class) {
                            q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.DATE);
                        }
                        if (param.getObjectClass() == String.class) {
                            q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.STRING);
                        }
                    } else {
                        q.setParameter(param.getName(), param.getValue());
                    }
                }
            }
        }
        try {
            q.executeUpdate();
//        } catch (javax.persistence.QueryTimeoutException e) {   // PROCEDURILE SE EXECUTA INTR-UN INTERVAL MARE DE TIMP SI TREBUIE ELIMINATA CONDITIA DE TIMEOUT
//            throwTimeoutException(Messages.TO_MUCH_DATA_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            throwDatabaseException(e.getMessage());
        }
    }


    public static void throwDatabaseException(String message) {
        throw new CustomException("Database problem: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void throwTimeoutException(String message) {
        throw new CustomException(message, HttpStatus.BAD_REQUEST);
    }


}
