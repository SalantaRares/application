package com.application.utils.hibernate;

import com.application.exceptions.CustomException;
import com.application.utils.Messages;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.http.HttpStatus;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class UtilsRepository {
    private static int QUERY_TIMEOUT = 300;

    public static Object getObjectById(SessionFactory sessionFactory, Class objectType, Object id) {
        try {
            return sessionFactory.getCurrentSession().get(objectType, (Serializable) id);
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

    public static Object getObjectByAliasBeanWithParams(SessionFactory sessionFactory, String query, Class type,
                                                        List<CustomParameter> parameterList) throws CustomException {
        try {
            Query q = createQueryWithparams(sessionFactory, query, parameterList).setTimeout(QUERY_TIMEOUT);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).getSingleResult();
        } catch (javax.persistence.QueryTimeoutException e) {
            e.printStackTrace();
            throw e;
        } catch (NullPointerException | NoResultException e) {
            return null;
        } catch (Exception e) {
            if (e.getMessage().contains("transaction timeout expired")) {
                throwTimeoutException(com.application.utils.Messages.TO_MUCH_DATA_TIMEOUT);
            } else {
                throwDatabaseException(e.getMessage());
            }
            return null;
        }
    }

    public static void executeWithParameters(SessionFactory sessionFactory, String statement, List<CustomParameter> parameterList) {
        Query q = createQueryWithparams(sessionFactory, statement, parameterList).setTimeout(QUERY_TIMEOUT);
        try {
            q.executeUpdate();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException(com.application.utils.Messages.TO_MUCH_DATA_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("transaction timeout expired")) {
                throwTimeoutException(com.application.utils.Messages.TO_MUCH_DATA_TIMEOUT);
            } else {
                throwDatabaseException(e.getMessage());
            }
        }
    }


    public static List getDataAsListByAliasBeanWithParams(SessionFactory sessionFactory, String query, Class type,
                                                          List<CustomParameter> parameterList) {
        try {
            Query q = createQueryWithparams(sessionFactory, query, parameterList).setTimeout(QUERY_TIMEOUT);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException(com.application.utils.Messages.TO_MUCH_DATA_TIMEOUT);
        } catch (Exception e) {
            if (e.getMessage().contains("transaction timeout expired")) {
                throwTimeoutException(Messages.TO_MUCH_DATA_TIMEOUT);
            } else {
                throwDatabaseException(e.getMessage());
            }
        }
        return null;
    }

    private static Query createQueryWithparams(SessionFactory sessionFactory, String query,
                                               List<CustomParameter> parameterList) {
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
                        if (param.getObjectClass() == BigDecimal.class) {
                            q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.BIG_DECIMAL);
                        }
                        if (param.getObjectClass() == Integer.class) {
                            q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.INTEGER);
                        }
                        if (param.getObjectClass() == Long.class) {
                            q.setParameter(param.getName(), param.getValue(), StandardBasicTypes.LONG);
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
        return q;
    }


    public static void throwDatabaseException(String message) {
        throw new CustomException("Database problem: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void throwTimeoutException(String message) {
        throw new CustomException(message, HttpStatus.BAD_REQUEST);
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

}
