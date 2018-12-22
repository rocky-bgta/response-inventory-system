package response.soft.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import response.soft.appenum.SqlEnum;
import response.soft.constant.DbConstant;
import response.soft.core.BaseDao;
import response.soft.core.BaseHistoryEntity;
import response.soft.core.Core;
import response.soft.core.History;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Repository
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Dao<T> extends BaseDao {

    //@Autowired
    //private SessionFactory sessionFactory;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    public Dao() {
        super();
    }


    private static final Logger log = LoggerFactory.getLogger(Dao.class);


    public T save(T t, Boolean insertDataInHistory) throws Exception {
        BaseHistoryEntity historyEntity;
        Session session=null;
        try {
            session = getSession();

            session.getTransaction().begin();
            session.save(t);
            session.flush();
            session.refresh(t);

            //================ code regarding history table======================
            if (insertDataInHistory) {
                historyEntity = buildHistoryEntity(t, SqlEnum.QueryType.Insert.get());
                session.save(historyEntity);
                session.flush();
            }
            //===================================================================

            session.getTransaction().commit();
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao Save method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return t;
    }

    public T update(T t, Boolean insertDataInHistory) throws Exception {
        Object primaryKeyValue;
        Object oldEntity;
        BaseHistoryEntity historyEntity;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();


            insertDataInHistory=false;
            //================ code regarding history table======================
            if (insertDataInHistory) {
                // Find previous row's primary key before update
                primaryKeyValue = this.getPrimaryFieldValue(t);
                oldEntity = getById(primaryKeyValue);

                historyEntity = buildHistoryEntity(oldEntity, SqlEnum.QueryType.Update.get());
                session.update(historyEntity);
                session.flush();
            }
            //===================================================================


            // update row to main table
            session.update(t);
            session.flush();
            session.refresh(t);

            session.getTransaction().commit();
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao update method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return t;
    }



    public Integer deleteSoft(Object id, Boolean insertDataInHistory) throws Exception {
        BaseHistoryEntity historyEntity;
        StringBuilder queryBuilderString;
        String entityName,primaryKeyField;
        List selectedUpdateRowList;
        Query selectQuery,deleteUpdateQuery;
        Integer numberOfUpdatedRows = 0;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();

            primaryKeyField = this.getPrimaryKeyFieldName();
            Class clazz = Core.runTimeEntityType.get();
            entityName = clazz.getName();
            entityName = StringUtils.substringAfterLast(entityName, ".").trim();


            queryBuilderString = new StringBuilder();

            queryBuilderString.append("SELECT t ")
                    .append("FROM " + entityName + " t ")
                    .append("WHERE ")
                    .append("t." + primaryKeyField + " ='" + id+"'");


            //================ code regarding history table======================
            if (insertDataInHistory) {
                selectQuery = session.createQuery(queryBuilderString.toString());
                selectedUpdateRowList = selectQuery.getResultList();
                //entityName = this.getEntityNameFromHql(updateHql);
                // Insert data into history table
                historyEntity = buildHistoryEntity(selectedUpdateRowList,
                        SqlEnum.QueryType.UpdateByConditions.get(),
                        entityName);
                session.save(historyEntity);
                session.flush();
            }
            //===================================================================


            queryBuilderString.setLength(0);
            queryBuilderString.append("UPDATE ");
            queryBuilderString.append("" + entityName + " t");
            queryBuilderString.append(" SET t.status="+SqlEnum.Status.Deleted.get());
            queryBuilderString.append(" ,t.updatedDate='"+new Date()+"'");
            //queryBuilderString.append(" ,t.updatedBy="+Core.userId.get());
            queryBuilderString.append(" ,t.updatedBy='System'");
            queryBuilderString.append(" WHERE t."+primaryKeyField+"='"+id+"'");

            deleteUpdateQuery = session.createQuery(queryBuilderString.toString());
            numberOfUpdatedRows = deleteUpdateQuery.executeUpdate();
            // update row to main table
            session.flush();
            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao update method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return numberOfUpdatedRows;
    }


/*
    public T saveOrUpdate(T t) throws HibernateException, JsonProcessingException{
        BaseHistoryEntity historyEntity;
        try {
            Session session = getSession();
            session.beginTransaction();
            session.saveOrUpdate(t);
            session.flush();
            session.refresh(t);

            historyEntity = buildHistoryEntity(t,SqlEnum.QueryType.Insert.get());
            session.save(historyEntity);
            session.flush();

            session.getTransaction().commit();
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao saveOrUpdate method");
            throw ex;
        }
        return t;
    }*/

    public List<T> getAllByConditions(String hql, Map<Object, Object> keyValueParis) throws HibernateException {
        List<T> list = null;
        String key;
        Object value;
        Boolean likeQuery=false;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            likeQuery= StringUtils.contains(hql,"LIKE");
            for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                key = entry.getKey().toString();
                value = entry.getValue();
                if(likeQuery) {
                    q.setParameter(key, "'%" + value + "%'");
                }
                else{
                    q.setParameter(key,value);
                }
            }
            list = q.getResultList();

            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao getAllByConditions method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return list;
    }

    public T getById(Object id) throws HibernateException {
        return getById(id, null);
    }

    public T getById(Object id, Integer status) throws HibernateException {
        Object entity = null;
        StringBuilder query;
        String entityName;
        String primaryKeyField;
        Session session=null;
        try {
            session = getSession();
            primaryKeyField = this.getPrimaryKeyFieldName();
            query = new StringBuilder();
            Class clazz = Core.runTimeEntityType.get();
            entityName = clazz.getName();
            entityName = StringUtils.substringAfterLast(entityName, ".").trim();
            query.append("SELECT t ")
                    .append("FROM " + entityName + " t ")
                    .append("WHERE ")
                    .append("t." + primaryKeyField + " =:" + primaryKeyField);

            if (status != null)
                query.append(" AND t.status = " + Integer.parseInt(status.toString()));


            session.beginTransaction();
            Query q = session.createQuery(query.toString());
            q.setParameter(primaryKeyField, id);

            List<T> list = q.getResultList();
            if (list == null || list.isEmpty()) {
                return null;
            } else {
                entity = list.get(0);
                Core.totalRowCount.set(1L);
            }
            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao getById method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return (T) entity;
    }


    public Long setTotalActiveRecordCount(Class clazz){
        StringBuilder queryBuilderString;
        Long totalRowCont;
        String entityName;
        Session session=null;
        try {
            queryBuilderString = new StringBuilder();

            //Class clazz = Core.runTimeEntityType.get();
            entityName = clazz.getName();
            entityName = StringUtils.substringAfterLast(entityName, ".").trim();

            session = getSession();
            //session.getTransaction().begin();

            entityName = StringUtils.replace(entityName,"Model","");

            queryBuilderString.append("SELECT COUNT(*) FROM ");
            queryBuilderString.append("" + entityName + " t");
            queryBuilderString.append(" WHERE t.status=" + SqlEnum.Status.Active.get());

            Query countQuery = session.createQuery(queryBuilderString.toString());

            totalRowCont = (Long) countQuery.getSingleResult();


            Core.totalRowCount.set(totalRowCont);
            //session.getTransaction().commit();
            //session.close();

        }catch (Exception ex){
            ex.printStackTrace();
            log.error("Exception from Dao totalActiveRecord method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }

        return totalRowCont;
    }



    public List<T> getAll() throws HibernateException {
        List<T> list = null;

        Object entity = null;
        StringBuilder queryBuilderString;
        String entityName;
        Long count;
        //EntityManager entityManager;
        Session session=null;
        try {
            //entityManager = entityManagerFactory.createEntityManager();
            session = getSession();
            session.getTransaction().begin();

            queryBuilderString = new StringBuilder();
            Class clazz = Core.runTimeEntityType.get();
            entityName = clazz.getName();
            entityName = StringUtils.substringAfterLast(entityName, ".").trim();
            queryBuilderString.append("SELECT t ")
                    .append("FROM " + entityName + " t WHERE t.status=" + SqlEnum.Status.Active.get());

            if (Core.shortColumnName.get() != null && Core.shortColumnName.get() != "")
                queryBuilderString.append(" ORDER BY t." + Core.shortColumnName.get() + " " + Core.shortDirection.get().toUpperCase());

            Query q = session.createQuery(queryBuilderString.toString());

            if (Core.pageOffset.get() != 0 && Core.pageOffset.get() != null) {
                q.setFirstResult(Core.pageOffset.get());
            }

            if (Core.pageSize.get() != 0 && Core.pageSize.get() != null) {
                q.setMaxResults(Core.pageSize.get());
            }

            queryBuilderString.setLength(0);
            queryBuilderString.append("SELECT COUNT(*) FROM ");
            queryBuilderString.append("" + entityName + " t");
            queryBuilderString.append(" WHERE t.status=" + SqlEnum.Status.Active.get());

            Query countQuery = session.createQuery(queryBuilderString.toString());

            count = (Long) countQuery.getSingleResult();

          /*
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(clazz)));
            Long count = entityManager.createQuery(cq).getSingleResult();*/
            Core.totalRowCount.set(count);

            Core.recordsFilteredCount.set(count);

            list = q.getResultList();

            session.getTransaction().commit();
            session.close();
            //this.sessionFactory.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao getAll method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return list;
    }

    public Integer updateByConditions(String updateHql, String selectHql,
                                      Map<Object, Object> keyValueParisForUpdate,
                                      Map<Object, Object> keyValuePairsForWhereCondition,
                                      Boolean insertDataInHistory) throws Exception {

        Integer numberOfUpdatedRows = 0;
        String key;
        Object value;
        List selectedUpdateRowList;
        String entityName;
        BaseHistoryEntity historyEntity;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            Query updateQuery = session.createQuery(updateHql);

            //================ code regarding history table======================
            if (insertDataInHistory) {
                selectedUpdateRowList = getAllByConditions(selectHql, keyValuePairsForWhereCondition);
                entityName = this.getEntityNameFromHql(updateHql);
                // Insert data into history table
                historyEntity = buildHistoryEntity(selectedUpdateRowList,
                        SqlEnum.QueryType.UpdateByConditions.get(),
                        entityName);
                session.save(historyEntity);
                session.flush();
            }
            //===================================================================

            //Set value to be update
            for (Map.Entry<Object, Object> entry : keyValueParisForUpdate.entrySet()) {
                key = entry.getKey().toString();
                value = entry.getValue();
                updateQuery.setParameter(key, value);
            }

            //Set value for whereConditions
            for (Map.Entry<Object, Object> entry : keyValuePairsForWhereCondition.entrySet()) {
                key = entry.getKey().toString();
                value = entry.getValue();
                updateQuery.setParameter(key, value);
            }

            numberOfUpdatedRows = updateQuery.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao updateByConditions method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return numberOfUpdatedRows;
    }

    public boolean delete(T t) throws HibernateException {
        boolean isDeleted = false;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            session.delete(t);
            session.flush();
            session.getTransaction().commit();
            //session.close();
            isDeleted = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao delete method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return isDeleted;
    }

    public boolean deleteById(Object id) throws HibernateException {
        boolean isDeleted = false;
        StringBuilder query;
        String entityName;
        String primaryKeyField;
        Integer numOfDeletedRow = 0;
        Session session=null;
        try {
            session = getSession();
            primaryKeyField = this.getPrimaryKeyFieldName();
            query = new StringBuilder();
            Class clazz = Core.runTimeEntityType.get();
            entityName = clazz.getName();
            entityName = StringUtils.substringAfterLast(entityName, ".").trim();
            query.append("DELETE t ")
                    .append("FROM " + entityName + " t ")
                    .append("WHERE ")
                    .append("t." + primaryKeyField + " =:" + primaryKeyField);

            session.beginTransaction();
            Query q = session.createQuery(query.toString());
            q.setParameter(primaryKeyField, id);

            numOfDeletedRow = q.executeUpdate();
            if (numOfDeletedRow > 0)
                isDeleted = true;

            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao deleteById method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return isDeleted;
    }

    public Integer deleteByConditions(String hql, Map<Object, Object> keyValueParis) throws HibernateException {
        Integer numberOfDeletedRows = 0;
        String key;
        Object value;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            //Set value
            for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                key = entry.getKey().toString();
                value = entry.getValue();
                q.setParameter(key, value);
            }
            numberOfDeletedRows = q.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao deleteByConditions method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return numberOfDeletedRows;
    }

    public <M> List<M> executeHqlQuery(String hql, Class<M> clazz, int queryType, Boolean insertDataInHistory) throws Exception {
        List<Object[]> result=null;
        Integer numberOfUpdatedRow;
        List<M> convertedModels = new ArrayList<>();
        String selectHql;
        List selectedUpdateRowList;
        BaseHistoryEntity historyEntity;
        String entityName;
        Query selectQuery;
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);

            /*
            if (Core.pageOffset.get() != 0 && Core.pageOffset.get() != null) {
                q.setFirstResult(Core.pageOffset.get());
            }

            if (Core.pageSize.get() != 0 && Core.pageSize.get() != null) {
                q.setMaxResults(Core.pageSize.get());
            }*/

            if (SqlEnum.QueryType.Join.get() == queryType) {
                result = q.getResultList();

                if (result.size() > 0) {
                    convertedModels = this.getObjectListFromObjectArray(result, clazz);
                }


            } else if (SqlEnum.QueryType.Select.get() == queryType) {
                result = q.getResultList();
                if (result.size() > 0) {
                    convertedModels = (List<M>) result;
                }
            } else if (SqlEnum.QueryType.GetOne.get() == queryType) {
                q.setMaxResults(1);
                result = q.getResultList();
                if (result.size() > 0) {
                    convertedModels = (List<M>) result;
                }
            } else if (SqlEnum.QueryType.Update.get() == queryType) {

                //================ code regarding history table======================
                if (insertDataInHistory) {
                    selectHql = this.buildSelectHql(hql);
                    selectQuery = session.createQuery(selectHql);
                    selectedUpdateRowList = selectQuery.getResultList();
                    entityName = this.getEntityNameFromHql(hql);
                    // Insert data into history table
                    historyEntity = buildHistoryEntity(selectedUpdateRowList,
                            SqlEnum.QueryType.UpdateByConditions.get(),
                            entityName);
                    session.save(historyEntity);
                    session.flush();
                }
                //===================================================================
                numberOfUpdatedRow = q.executeUpdate();
                convertedModels = new ArrayList<>();
                convertedModels.add((M) numberOfUpdatedRow);
            }

            //========== set search count for data table =======================
            if(result!=null && (SqlEnum.QueryType.Join.get()==queryType || SqlEnum.QueryType.Select.get()==queryType))
                Core.recordsFilteredCount.set((long) result.size());

            this.setTotalActiveRecordCount(clazz);
            //========== set search count for data table =======================


            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao executeHqlQuery method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return convertedModels;
    }

    public List<Object[]> executeNativeSqlQuery(String hql, int queryType) throws HibernateException {
        List<Object[]> result = new ArrayList<>();
        Session session=null;
        try {
            session = getSession();
            session.beginTransaction();
            NativeQuery query = session.createNativeQuery(hql);
            if (SqlEnum.QueryType.Select.get() == queryType)
                if (result.size() > 0) {
                    result = query.getResultList();
                }
            if (SqlEnum.QueryType.Update.get() == queryType)
                query.executeUpdate();
            session.getTransaction().commit();
            //session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao executeNativeSqlQuery method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return result;
    }

    public Boolean isRowExist(List<Map<Object, Object>> keyValueWithTypeList, Class entityClass) throws Exception {
        Boolean isExist = false;
        String key = null;
        Object value = null;
        String entityName;
        String entityClassPath = entityClass.toString();
        String hql;
        Long count;
        Map<Object, Object> keyValueParis = keyValueWithTypeList.get(0);
        Map<Object, Object> typeMap = keyValueWithTypeList.get(1);

        entityClassPath = StringUtils.substringAfterLast(entityClassPath, "class").trim();
        entityName = StringUtils.substringAfterLast(entityClassPath, ".");
        Session session=null;
        try {
            hql = this.queryBuilder(keyValueParis, SqlEnum.QueryType.CountRow.get(), entityName);

            session = getSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            //Set value
            for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                key = entry.getKey().toString();
                value = entry.getValue();
                q.setParameter(key, value);
            }

            count = (Long) q.uniqueResult();
            session.getTransaction().commit();
            //session.close();

            if (count != null && count > 0)
                isExist = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Dao isRowExist method");
            throw ex;
        }finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        return isExist;
    }

    private Session getSession() throws HibernateException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        SessionFactory sessionFactory;
        Session session=null;
        try {
            sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            session = sessionFactory.openSession();

        /*
        if(this.sessionFactory == null)
            sessionFactory = Core.sessionFactoryThreadLocal.get();
        else {
            sessionFactory = this.sessionFactory;
            Core.sessionFactoryThreadLocal.set(sessionFactory);
        }

        */
            session = sessionFactory.openSession();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return session;
    }

    private BaseHistoryEntity buildHistoryEntity(Object t, int QueryType) throws JsonProcessingException {
        return buildHistoryEntity(t, QueryType, null);
    }

    private BaseHistoryEntity buildHistoryEntity(Object entity, int QueryType, String entityName) throws JsonProcessingException {
        String jsonString, entityClassPath;
        jsonString = Core.jsonMapper.writeValueAsString(entity);
        HistoryEntity = new History();
        HistoryEntity.setMessageId(Core.messageId.get());
        HistoryEntity.setActionType(QueryType);
        HistoryEntity.setDateTime(new Date());
        HistoryEntity.setJsonObject(jsonString);
        entityClassPath = entity.getClass().toString();


        if (entityName != null) {
            entityClassPath = DbConstant.DEFAULT_DB_ENTITY_PATH + "." + entityName;
        } else {
            entityClassPath = StringUtils.substringAfterLast(entityClassPath, "class").trim();
            entityName = StringUtils.substringAfterLast(entityClassPath, ".");
        }
        Core.HistoryEntity.setEntityClassPath(entityClassPath);
        Core.HistoryEntity.setEntityName(entityName);
        return Core.HistoryEntity;
    }
}
