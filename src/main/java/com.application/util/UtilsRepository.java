package com.application.util;

import com.application.error.BadRequestAlertException;
import com.application.mappers.CustomTransformers;
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
    public static List getDataAsListByAliasBean(SessionFactory sessionFactory, String query, Class type) {
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

    public static List getDataAsList1Params(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1) {
        try {
            Query query2 = sessionFactory.getCurrentSession().createNativeQuery(query);
            query2.setParameter(paramName1, param1);
            return query2.list();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static Object getDataAsListByAliasBean1ParamsSingleResult(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).getSingleResult();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static Object getDataAsListByAliasBean2ParamssingleResilt(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1, String paramName2, Object param2) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.setParameter(paramName2, param2);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }

    public static List getDataAsListByAliasBean2Params(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1, String paramName2, Object param2) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.setParameter(paramName2, param2);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            return null;
        }
    }
    public static List getDataAsListByAliasBeanWithParams(SessionFactory sessionFactory, String query, Class type,
                                                          List<CustomParameter> parameterList) {
        try {
            Query q = createQueryWithparams(sessionFactory, query, parameterList).setTimeout(QUERY_TIMEOUT);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (javax.persistence.QueryTimeoutException e) {
            throwTimeoutException(Messages.TO_MUCH_DATA_TIMEOUT);
        } catch (Exception e) {
            if (e.getMessage().contains("transaction timeout expired")) {
                throwTimeoutException(Messages.TO_MUCH_DATA_TIMEOUT);
            } else {
                throwDatabaseException(e.getMessage());
            }
        }
        return null;
    }

    public static void throwTimeoutException(String message) {
        throw new CustomException(message, HttpStatus.BAD_REQUEST);
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


    public static List getDataAsListByAliasBean3Params(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1, String paramName2, Object param2, String paramName3, Object param3) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.setParameter(paramName2, param2);
            q.setParameter(paramName3, param3);
            ResultTransformer aliasToBean = CustomTransformers.aliasToBean(type);
            return q.setResultTransformer(aliasToBean).list();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List getDataAsListByAliasBean4Params(SessionFactory sessionFactory, String query, Class type, String paramName1, Object param1, String paramName2, Object param2, String paramName3, Object param3, String paramName4, Object param4) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.setParameter(paramName2, param2);
            q.setParameter(paramName3, param3);
            q.setParameter(paramName4, param4);
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
//            System.out.println(e);
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

    public static void executeQuery3Params(SessionFactory sessionFactory, String query, String paramName1, Object param1, String paramName2, Object param2, String paramName3, Object param3) {
        try {
            Query q = sessionFactory.getCurrentSession().createNativeQuery(query);
            q.setParameter(paramName1, param1);
            q.setParameter(paramName2, param2);
            q.setParameter(paramName3, param3);
            q.executeUpdate();
        } catch (Exception e) {
            throwDatabaseException(e.getMessage());
        }
    }

    public static void throwDatabaseException(String message) {
        throw new BadRequestAlertException("Database problem: " + message,"", HttpStatus.INTERNAL_SERVER_ERROR.toString());
    }
}
