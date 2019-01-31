package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import response.soft.core.ResponseMessage;
import response.soft.services.CustomerDuePaymentHistoryService;

@RestController
@RequestMapping("api/customer-due-payment-history")
@Api(tags = "Customer Due Payment History Api List")
public class CustomerDuePaymentHistoryController {

    private final CustomerDuePaymentHistoryService customerDuePaymentHistoryService;

    @Autowired
    public CustomerDuePaymentHistoryController(CustomerDuePaymentHistoryService customerDuePaymentHistoryService) {
        this.customerDuePaymentHistoryService = customerDuePaymentHistoryService;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/invoice-no/{invoiceNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getDuePaymentHistoryByInvoiceNo(@PathVariable String invoiceNo){
        ResponseMessage responseMessage;
        responseMessage = this.customerDuePaymentHistoryService.getCustomerPaymentHistoryByInvoiceNo(invoiceNo);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/previous-due/customer-id/{customerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getPreviousDueByCustomerId(@PathVariable String customerId){
        ResponseMessage responseMessage;
        responseMessage = this.customerDuePaymentHistoryService.getPreviousDueByCustomerId(customerId);
        return responseMessage;
    }

}
