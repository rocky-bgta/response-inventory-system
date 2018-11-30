/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 12-Jan-18
 * Time: 4:53 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import response.soft.appenum.SqlEnum;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@Component
public abstract class Core {
    private static final Logger log = LoggerFactory.getLogger(Core.class);
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final ObjectMapper jsonMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //.setDateFormat(dateFormat);

    protected static final ModelMapper modelMapper = new ModelMapper();

    public static final ThreadLocal<Class> runTimeModelType = new ThreadLocal<>();
    public static final ThreadLocal<Class> runTimeEntityType = new ThreadLocal<>();
    //public static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<SessionFactory> sessionFactoryThreadLocal = new ThreadLocal<>();

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    public static final Validator validator = factory.getValidator();

    public CyclicBarrier barrier;

    public static BaseHistoryEntity HistoryEntity = new History();
    public static final ThreadLocal<String> messageId = new ThreadLocal<>();

  /*  public static final PublisherForRollBackAndCommit publisherForRollBackAndCommit
            = new PublisherForRollBackAndCommit();
*/
    //public static final ThreadLocal<ClientMessage> clientMessage = new ThreadLocal<>();

    public static final ThreadLocal<String> userId = new ThreadLocal<>();
    public static final ThreadLocal<Integer> pageOffset = new ThreadLocal<>();
    public static final ThreadLocal<Integer> pageSize = new ThreadLocal<>();
    public static final ThreadLocal<Long> totalRowCount = new ThreadLocal<>();



    //public static final Map<String,SecurityResMessage> securityResponseCollection;


    /*static
    {
        securityResponseCollection = Collections.synchronizedMap(new HashMap<String, SecurityResMessage>());
    }
    */


    //public static SessionFactory sessionFactoryForModule;

    //public final long allowedTime = TimeUnit.NANOSECONDS.convert(30, TimeUnit.SECONDS);

    //Wait for 30 second
    public static final Integer allowedTime = 30000;
    //public final Integer allowedTime = 10000;
    private static final Long nanoSecond = TimeUnit.NANOSECONDS.convert(allowedTime, TimeUnit.MILLISECONDS);

    /* @Autowired
        static AnnotationConfigApplicationContext applicationContext;
    */

    /*
    static
    {
         applicationContext =
                    new AnnotationConfigApplicationContext();

            applicationContext.scan("nybsys.tillboxweb.dbConfig");
            applicationContext.refresh();
    }
    */

    public static <T> T processRequestObject(RequestObject requestObject) {
      return  processRequestObject(requestObject,null);
    }

    public static <T> T processRequestObject(RequestObject requestObject, Class clazz) {
        Object convertedObject=null;
        Object requestData=null;
        try {
            if(requestObject.data!=null) {
                requestData = requestObject.data;
            }

            Core.pageOffset.set(requestObject.pageOffset);
            Core.pageSize.set(requestObject.pageSize);

            if(clazz!=null)
                convertedObject = Core.jsonMapper.convertValue(requestData, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return (T) convertedObject;
    }

    public ResponseObject buildResponseObject(Object data){
        ResponseObject responseObject = new ResponseObject();
        responseObject.data = data;
        responseObject.totalRow = Core.totalRowCount.get();
        responseObject.token="token1122555";
        responseObject.httpStatus=HttpStatus.FOUND;
        responseObject.message="Successful";
        return responseObject;
    }

  /*
    public static <T> T getResponseObject(ResponseObject requestMessage, Class clazz) {
        Object convertedObject;
        Object responseObject=null;
        try {
            if(requestMessage.data!=null)
                responseObject = requestMessage.data;

            convertedObject = Core.jsonMapper.convertValue(responseObject, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return (T) convertedObject;
    }
*/
    /*public static <T extends BaseModel> T processRequestObject(RequestObject requestMessage) {
        Object convertedObject = null;
        try {
            Object requestObj = requestMessage.requestObj;
            Class clazz = Core.runTimeModelType.get();
            //clazz.newInstance();

            convertedObject = jsonMapper.convertValue(requestObj, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return (T) convertedObject;
    }*/

    /*
    public static ResponseObject buildDefaultResponseMessage() {
        ResponseObject responseMessage = new ResponseObject();
        return responseMessage;
    }

    */
    /*

    public static RequestObject getDefaultWorkerRequestMessage() {
        RequestObject requestMessage = new RequestObject();
        requestMessage.brokerMessage = new BrokerMessage();
        requestMessage.brokerMessage.requestFrom =
                SqlEnum.BrokerRequestType.WORKER.get();
        requestMessage.brokerMessage.messageId = AppUtils.getUUID();
        return requestMessage;
    }
    */

    public static Map getKeyValuePairFromObject(Object obj) {
        return getKeyValuePairFromObject(obj, null);
    }

    public static Map getKeyValuePairFromObject(Object obj, Integer queryType) {
        Map<Object, Object> keyValue = new HashMap<>();
        ReflectionUtils.doWithFields(obj.getClass(), field -> {
            try {
                String type = field.getType().toString();
                type = org.apache.commons.lang3.StringUtils.substringAfterLast(type, "class").trim();

                //Object something = "1";
                //Object result= AppUtils.castValue(type.trim(),something);


                boolean condition1 = true, condition2, condition3, condition4, condition5;
                //System.out.println("Field name: " + field.getName());
                field.setAccessible(true);
                //System.out.println("Field value: "+ field.get(obj));
                condition2 = !ObjectUtils.isEmpty(field.get(obj));
                condition3 = !StringUtils.isEmpty(field.get(obj));
                //if (condition3)
                //    condition1 = !field.get(obj).toString().equals("0");
                // condition4 = !StringUtils.startsWithIgnoreCase(field.getName().toString(), "created");
                // condition5 = !StringUtils.startsWithIgnoreCase(field.getName().toString(), "updated");
                if (condition3 && condition2) {
                    if (queryType != null)
                        keyValue.put(field.getName().toString() + queryType, response.soft.Utils.AppUtils.castValue(type, field.get(obj)));
                    else
                        keyValue.put(field.getName().toString(), response.soft.Utils.AppUtils.castValue(type, field.get(obj)));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }

        });
        return keyValue;
    }


    /**
     * BUILD SELECT, UPDATE OR DELETE QUERY WITH WHERE CONDITION
     *
     * @param whereCondition *
     * @param queryType      *
     * @return build query string *
     * @throws Exception if there is any error in building query
     */
    public String queryBuilder(Object whereCondition, int queryType) throws Exception {
        String buildQuery;
        buildQuery = this.queryBuilder(whereCondition, queryType, null);
        return buildQuery;
    }


    /**
     * BUILD SELECT, UPDATE OR DELETE QUERY WITH WHERE CONDITION
     *
     * @param whereCondition *
     * @param queryType      *
     * @param entityName     *
     * @return build query string *
     * @throws Exception if there are any error in building query
     */
    public String queryBuilder(Object whereCondition, int queryType, String entityName) throws Exception {
        return queryBuilder(whereCondition, queryType, null, entityName);
    }

    public String queryBuilder(Object whereCondition, int queryType, Object updateObject) throws Exception {
        return queryBuilder(whereCondition, queryType, updateObject, null);
    }

    public String queryBuilder(Object whereCondition, int queryType, Object updateObject, String entityName) throws Exception {
        Map<Object, Object> keyValueParisForWhereCondition;
        Map<Object, Object> keyValuePairsForUpdate;
        Map<Object, Object> keyValuePairsWhereConditionForDelete;
        StringBuilder query;
        try {
            Class clazz = Core.runTimeEntityType.get();
            if (entityName == null) {
                entityName = clazz.getName();
                //entityName = org.apache.commons.lang3.StringUtils.substringAfterLast(entityName, ".").trim();
                keyValueParisForWhereCondition = Core.getKeyValuePairFromObject(whereCondition);
            } else {
                keyValueParisForWhereCondition = (Map<Object, Object>) whereCondition;
            }


            query = new StringBuilder();

            if (SqlEnum.QueryType.Select.get() == queryType) {
                query.append("SELECT t ")
                        .append("FROM " + entityName + " t ")
                        .append(" WHERE ");
                query = this.criteriaBuilder(keyValueParisForWhereCondition, query, SqlEnum.QueryType.Select.get());
            }

            if (SqlEnum.QueryType.Delete.get() == queryType) {
                query.append("DELETE FROM ");
                query.append("" + entityName + " t ");
                keyValuePairsWhereConditionForDelete = Core.getKeyValuePairFromObject(whereCondition);
                query.append(" WHERE ");
                query = this.criteriaBuilder(keyValuePairsWhereConditionForDelete, query, SqlEnum.QueryType.Select.get());
            }

            if (SqlEnum.QueryType.Update.get() == queryType) {
                query.append("UPDATE ");
                query.append("" + entityName + " t ");
                query.append("set ");
                keyValuePairsForUpdate = Core.getKeyValuePairFromObject(updateObject);
                query = this.criteriaBuilder(keyValuePairsForUpdate, query, SqlEnum.QueryType.Update.get());
                query.append(" WHERE ");
                query = this.criteriaBuilder(keyValueParisForWhereCondition, query, SqlEnum.QueryType.Select.get());

            }

            if (SqlEnum.QueryType.CountRow.get() == queryType) {
                query.append("SELECT COUNT(*) FROM ");
                query.append("" + entityName + " t");
                query.append(" WHERE ");
                query = this.criteriaBuilder(keyValueParisForWhereCondition, query, SqlEnum.QueryType.Select.get());

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return query.toString();
    }

    private StringBuilder criteriaBuilder(Map<Object, Object> keyValueParis, StringBuilder query, int queryType) throws Exception {
        String key;
        try {
            List<String> criteria = new ArrayList<String>();

            for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                key = entry.getKey().toString();
                criteria.add("t." + key + " = :" + key + queryType);
            }

            if (criteria.size() == 0) {
                throw new RuntimeException("no criteria");
            }
            for (int i = 0; i < criteria.size(); i++) {
                if (i > 0) {
                    if (queryType == SqlEnum.QueryType.Select.get())
                        query.append(" AND ");
                    if (queryType == SqlEnum.QueryType.Update.get())
                        query.append(" , ");
                }
                query.append(criteria.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return query;
    }

    public static boolean isResponseWithInAllowedTime(long startTime) {
        return isResponseWithInAllowedTime(startTime, Core.allowedTime);
    }

    public static boolean isResponseWithInAllowedTime(long startTime, long allowedTime) {
        //Long nanoSecond = TimeUnit.NANOSECONDS.convert(allowedTime,TimeUnit.MILLISECONDS);
        if ((System.nanoTime() - startTime) > Core.nanoSecond)
            return false;
        else
            return true;
    }



    /*public void rollBack() {
        String messageId = Core.messageId.get();
        Core.publisherForRollBackAndCommit.publishedMessageForRollBack(messageId);
    }

    public void commit() {
        String messageId = Core.messageId.get();
        Core.publisherForRollBackAndCommit.publishedMessageForCommit(messageId);
    }*/

    public static <M> List<M> convertResponseToList(ResponseObject responseMessage, M model) throws Exception {
        List<M> finalList = new ArrayList<>();
        List tempList;
        try {
            if (responseMessage.data != null) {
                tempList = (List) responseMessage.data;
                if (tempList.size() > 0) {
                    for (Object object : tempList) {
                        object = Core.modelMapper.map(object, model.getClass());
                        finalList.add((M) object);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Exception from Core convertResponseToList method");
            throw ex;
        }
        return finalList;
    }

}
