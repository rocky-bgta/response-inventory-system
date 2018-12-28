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
import response.soft.model.StoreInProductModel;
import response.soft.model.view.StoreInProductsViewModel;
import response.soft.services.StoreInProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/store-in-products")
@Api(tags = "Store in Product Api List")
public class StoreInProductsController {

    @Autowired
    private StoreInProductService storeInProductService;


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.getAllStoreInProducts(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.getByStoreInProductsId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductsViewModel.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.saveStoreInProducts(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.updateStoreInProducts(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.deleteStoreInProducts(id);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }
}
