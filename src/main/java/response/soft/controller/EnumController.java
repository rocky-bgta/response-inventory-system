package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import response.soft.Utils.AppUtils;
import response.soft.appenum.InventoryEnum;
import response.soft.core.KeyValueModel;
import response.soft.core.ResponseMessage;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/enum")
@Api(tags = "Inventory Enum List")
public class EnumController {


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/payment-method", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getPaymentMethods(){
        ResponseMessage responseMessage;
        Map<String,Integer> salesMethods;
        List<KeyValueModel> keyValueModelList;
        responseMessage = new ResponseMessage();
        try {

            salesMethods = InventoryEnum.PaymentMethod.getMAP();
            if(salesMethods!=null){
                keyValueModelList = AppUtils.getKeyValueFromMap(salesMethods);
                responseMessage.data =keyValueModelList;
                responseMessage.httpStatus = HttpStatus.FOUND.value();
                responseMessage.totalRow= (long) keyValueModelList.size();
                responseMessage.message = "Get all Sales method successfully";
            }else {
                responseMessage.httpStatus=HttpStatus.NOT_FOUND.value();
                responseMessage.message = "No sales method found!!!";
            }

        } catch (Exception ex) {
            responseMessage.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
            ex.printStackTrace();
            return responseMessage;

        }
        return responseMessage;
    }
}
