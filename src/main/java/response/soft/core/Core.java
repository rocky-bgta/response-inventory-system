/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 12-Jan-18
 * Time: 4:53 PM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.SessionFactory;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import response.soft.appenum.SqlEnum;
import response.soft.core.datatable.model.DataTableResponse;
import response.soft.utils.AppUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


@Component
public abstract class Core {
    private static final Logger log = LoggerFactory.getLogger(Core.class);
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected static final ObjectMapper jsonMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS,true);
    //.setDateFormat(dateFormat);

    protected static final ModelMapper modelMapper = new ModelMapper();
    private static GsonBuilder builder = new GsonBuilder();
    public static final Gson gson = builder.create();

    public static final ThreadLocal<Class> runTimeModelType = new ThreadLocal<>();
    public static final ThreadLocal<Class> runTimeEntityType = new ThreadLocal<>();
    //public static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<SessionFactory> sessionFactoryThreadLocal = new ThreadLocal<>();

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    public static final Validator validator = factory.getValidator();

    //public CyclicBarrier barrier;

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
    public static final ThreadLocal<Long> recordsFilteredCount = new ThreadLocal<>();

    public static final ThreadLocal<Long> dataTableDraw = new ThreadLocal<>();
    public static final ThreadLocal<String> shortDirection = new ThreadLocal<>();
    public static final ThreadLocal<String> shortColumnName = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> isDataTablePagination = new ThreadLocal<>();


    //==================== update validation lib ===============================
    private final static Javers compareObject = JaversBuilder.javers().build();
    //private final static int baseEntityPropertyCount=3;
    //==================== update validation lib ===============================

    public static int comparePropertyValueDifference(Object newObject, Object oldObject){
        //List<Change> changeList;
        int changeCount,result;
        int baseEntityPropertyCount=5;
        Diff diff= Core.compareObject.compare(newObject,oldObject);
        changeCount = diff.getChanges().size();

       /*
        changeList = diff.getChanges();
        for(Change change: changeList){
            System.out.printf(change.toString());
        }

        */

        result = Math.abs(baseEntityPropertyCount-changeCount);
        return result;
    }


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


    public static <M> M getTrimmedModel(M m) throws Exception {
        Class clazz = m.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String fieldName;
        Type type;
        Object fieldValue;
        M model;
        String jsonObject;
        Pattern pattern;

        String temJson = "";
        String buildJson = "";

        try {
            //jsonObject = Core.gson.toJson(m);
            jsonObject = Core.jsonMapper.writeValueAsString(m);
            jsonObject = org.apache.commons.lang3.StringUtils.replace(jsonObject, "{", "");
            jsonObject = org.apache.commons.lang3.StringUtils.replace(jsonObject, "}", "");
            jsonObject = org.apache.commons.lang3.StringUtils.replace(jsonObject,"null","\"\"");
            jsonObject = jsonObject.substring(1, jsonObject.length() - 1);

            pattern = Pattern.compile("\",\"");
            String[] token = pattern.split(jsonObject);
            int i=0;
            for(String item: token){

                //String propertyName = org.apache.commons.lang3.StringUtils.substringBefore(item,":");
                //propertyName = org.apache.commons.lang3.StringUtils.remove(propertyName,"\"");
                String propertyValue = org.apache.commons.lang3.StringUtils.substringAfter(item,":").trim();

                propertyValue=org.apache.commons.lang3.StringUtils.remove(propertyValue,"\"");
                if(!org.apache.commons.lang3.StringUtils.isEmpty(propertyValue)){
                    propertyValue = "\"" + propertyValue + "\"";
                    fieldName = fields[i].getName();
                    type = fields[i].getType();
                    fieldValue = AppUtils.castValue(org.apache.commons.lang3.StringUtils.remove(type.toString(),"class ").trim(), propertyValue);
                    temJson += "\"" + fieldName+ "\":"+fieldValue +",";
                }
                //if(org.apache.commons.lang3.StringUtils.equalsIgnoreCase(fields[i].getName(),propertyName))
                    i++;
            }
                temJson = temJson.substring(0,temJson.length()-1);
                buildJson += "{"+temJson+"}";
                model = (M)clazz.newInstance();
                model = (M) Core.gson.fromJson(buildJson, model.getClass());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getTrimmedModel -> got exception",e.getCause());
            throw e;
        }
        return model;
    }







    public static <T> T processRequestMessage(RequestMessage requestMessage) throws Exception {
        return processRequestMessage(requestMessage, null);
    }

    public static <T> T processRequestMessage(RequestMessage requestMessage, Class clazz) throws Exception {
        Object convertedObject = null;
        Object trimmedObject = null;
        Object requestData = null;
        Integer shortColumnIndex;
        String shortColumnName;
        String shortDirection;

        try {
            if (requestMessage.data != null && !ObjectUtils.isEmpty(requestMessage.data)) {
                requestData = requestMessage.data;
            }
            if (requestMessage.dataTableRequest != null && requestMessage.dataTableRequest.length!=null && requestMessage.dataTableRequest.length!=0) {
                Core.isDataTablePagination.set(true);
                Core.pageOffset.set(requestMessage.dataTableRequest.start);
                Core.pageSize.set(requestMessage.dataTableRequest.length);
                Core.dataTableDraw.set(requestMessage.dataTableRequest.draw);


                shortDirection = requestMessage.dataTableRequest.order.get(0).dir;
                shortColumnIndex = requestMessage.dataTableRequest.order.get(0).column;
                shortColumnName = requestMessage.dataTableRequest.columns.get(shortColumnIndex).data;

                if (shortDirection.equals("asc") || shortDirection.equals("desc"))
                    Core.shortDirection.set(shortDirection);

                if (!shortColumnName.equals("string"))
                    Core.shortColumnName.set(shortColumnName);

            } else {
                Core.isDataTablePagination.set(false);
                Core.pageOffset.set(requestMessage.pageOffset);
                Core.pageSize.set(requestMessage.pageSize);
            }

            if (clazz != null) {
                convertedObject = Core.jsonMapper.convertValue(requestData, clazz);
                trimmedObject = Core.getTrimmedModel(convertedObject);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return (T) trimmedObject;
    }

    public ResponseMessage buildFailedResponseMessage() {
        return this.buildFailedResponseMessage(null);
    }
    public ResponseMessage buildFailedResponseMessage(String message) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.data = null;
        responseMessage.totalRow = 0l;
        responseMessage.token = null;
        responseMessage.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if(message!=null)
            responseMessage.message = message;
        else
            responseMessage.message = "Internal server error!!!";
        return responseMessage;
    }

    public ResponseMessage buildResponseMessage() {
        return buildResponseMessage(null);
    }

    public ResponseMessage buildResponseMessage(Object data) {
        DataTableResponse dataTableResponse;
        ResponseMessage responseMessage = new ResponseMessage();

        if(data!=null) {
            responseMessage.data = data;

            if (Core.totalRowCount.get() != null)
                responseMessage.totalRow = Core.totalRowCount.get();
            else {
                if (!ObjectUtils.isEmpty(data))
                    responseMessage.totalRow = 1L;
            }

            responseMessage.token = "token" + UUID.randomUUID();
            responseMessage.httpStatus = HttpStatus.FOUND.value();
            responseMessage.message = "Successful";


            if (Core.isDataTablePagination.get() != null) {
                dataTableResponse = new DataTableResponse();
                responseMessage.dataTableResponse = dataTableResponse;
                //responseMessage.dataTableResponse.setData((List) data);
                responseMessage.dataTableResponse.recordsTotal=(Core.totalRowCount.get());
                responseMessage.dataTableResponse.recordsFiltered=(Core.recordsFilteredCount.get());
                responseMessage.dataTableResponse.draw=(Core.dataTableDraw.get());
            }
        }else {
            responseMessage.token = "token" + UUID.randomUUID();
            responseMessage.httpStatus = HttpStatus.CONFLICT.value();
            responseMessage.message = "Failed";

        }

        return responseMessage;
    }

  /*
    public static <T> T getResponseObject(ResponseMessage requestMessage, Class clazz) {
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
    /*public static <T extends BaseModel> T processRequestMessage(RequestMessage requestMessage) {
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
    public static ResponseMessage buildDefaultResponseMessage() {
        ResponseMessage responseMessage = new ResponseMessage();
        return responseMessage;
    }

    */
    /*

    public static RequestMessage getDefaultWorkerRequestMessage() {
        RequestMessage requestMessage = new RequestMessage();
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
                        keyValue.put(field.getName().toString() + queryType, AppUtils.castValue(type, field.get(obj)));
                    else
                        keyValue.put(field.getName().toString(), AppUtils.castValue(type, field.get(obj)));
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

            if (SqlEnum.QueryType.Select.get() == queryType || SqlEnum.QueryType.LikeOrSearch.get()==queryType) {
                query.append("SELECT t ")
                        .append("FROM " + entityName + " t ")
                        .append(" WHERE ");

                // Select query with and condition
                if(SqlEnum.QueryType.Select.get() == queryType)
                    query = this.criteriaBuilder(keyValueParisForWhereCondition, query, SqlEnum.QueryType.Select.get());
                // Select query with or and like condition
                if(SqlEnum.QueryType.LikeOrSearch.get() == queryType)
                    query = this.criteriaBuilder(keyValueParisForWhereCondition, query, SqlEnum.QueryType.LikeOrSearch.get());
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

            if(SqlEnum.QueryType.LikeOrSearch.get()==queryType){
                for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                    key = entry.getKey().toString();
                    criteria.add("lower(t." + key + ") LIKE :" + key + queryType);
                }
            }else {

                for (Map.Entry<Object, Object> entry : keyValueParis.entrySet()) {
                    key = entry.getKey().toString();
                    criteria.add("t." + key + " = :" + key + queryType);
                }
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
                    if(queryType==SqlEnum.QueryType.LikeOrSearch.get())
                        query.append(" OR ");
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

    public static <M> List<M> convertResponseToList(ResponseMessage responseMessage, M model) throws Exception {
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
