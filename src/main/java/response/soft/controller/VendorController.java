package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.model.VendorModel;
import response.soft.services.VendorService;

import java.util.UUID;

@RestController
@RequestMapping("api/vendor")
@Api(tags = "Vendor Api List")
public class VendorController {

    @Autowired
    private VendorService vendorService;


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.vendorService.getAllVendor(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.vendorService.getByVendorId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = VendorModel.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.vendorService.saveVendor(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = VendorModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.vendorService.updateVendor(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = VendorModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.vendorService.deleteVendor(id);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }
}
