package ro.btrl.miswebappspringdemo.utils.hibernate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.http.HttpStatus;
import ro.btrl.miswebappspringdemo.exceptions.CustomException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Marius Pop
 * @since 30/October/2019
 */
public class UtilsRepository {
    public static List  getDataAsListByAliasBean(SessionFactory sessionFactory, String query, Class type) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static List getDataAsListByAliasBean1Params(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }


    public static Object getObjectById(SessionFactory sessionFactory, Class objectType, Object id) {
        try {
            return sessionFactory.getCurrentSession().get(objectType, (Serializable) id);
        } catch (Exception e) {
            System.out.println("AICI E PROBLEMA");
            e.printStackTrace();
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
            if (parameterList != null && parameterList.size() > 0) {
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
                            if (param.getObjectClass() == BigDecimal.class) {

                                q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.BIG_DECIMAL);
                            }
                            if (param.getObjectClass() == Integer.class) {

                                q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.INTEGER);
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
            throwTimeoutException("DATABASE_TIMEOUT");
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            throwDatabaseException(ex.getMessage());
            return null;
        }
    }

    public static void throwTimeoutException(String message) {
        throw new CustomException(message, HttpStatus.BAD_REQUEST);
    }


    public static void executeQuery1Params(SessionFactory sessionFactory, String query, String paramName1, Object param1) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.executeUpdate();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
        }
    }

    public static void throwDatabaseException(String message) {
        throw new CustomException("Database problem: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
