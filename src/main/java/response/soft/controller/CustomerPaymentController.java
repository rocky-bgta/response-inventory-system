package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.model.CustomerPaymentModel;
import response.soft.services.CustomerPaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/customer-payment")
@Api(tags = "Customer Payment Api List")
public class CustomerPaymentController {

    @Autowired
    private CustomerPaymentService customerPaymentService;


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.customerPaymentService.getCustomerPaymentList(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.customerPaymentService.updateCustomerPayment(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value = "", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value ="/customer-id/{customerId}" ,method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerPaymentModel> test(@PathVariable String customerId){
        //ResponseMessage responseMessage;
        List<CustomerPaymentModel> list;
        list = this.customerPaymentService.dupPaymentByCustomerId(customerId);
        return list;
    }
}
