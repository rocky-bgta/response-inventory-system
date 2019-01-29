package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.services.InvoiceHistoryService;

import java.util.UUID;

@RestController
@RequestMapping("api/invoice-history")
@Api(tags = "Invoice History Api List")
public class InvoiceHistoryController {

    @Autowired
    private InvoiceHistoryService invoiceHistoryService;


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage,
                                  @RequestParam(value="customerId", required = false) String customerId){
        ResponseMessage responseMessage;
        responseMessage = this.invoiceHistoryService.getInvoiceDetailsList(requestMessage,customerId);
        return responseMessage;
    }

    /*@ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.brandService.getByBrandId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = BrandModel.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.brandService.saveBrand(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = BrandModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.brandService.updateBrand(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = BrandModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.brandService.deleteBrand(id);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }*/
}
