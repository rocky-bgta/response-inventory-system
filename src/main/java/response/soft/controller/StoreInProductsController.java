package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.BeanFactory;
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

    @Autowired
    BeanFactory beanFactory;


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

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/identification-ids/{storeId}/{barcode}/{serialNo}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> getProductListByIdentificationIds(@RequestBody RequestMessage requestMessage,
                                                                   @PathVariable UUID storeId,
                                                                   @PathVariable String barcode ,
                                                                   @PathVariable String serialNo) {

        StoreInProductService storeInProductService = beanFactory.getBean(StoreInProductService.class);
        ResponseMessage responseMessage;
        responseMessage = storeInProductService.getProductListByIdentificationIds(requestMessage,storeId,barcode,serialNo);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }

    @ApiOperation(value ="", response = UUID.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/store-id/{storeId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getStoreInProductsByStoreId(@PathVariable UUID storeId) {
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.getStoreInProductsByStoreId(storeId);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/available-products", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAllAvailableProduct(@RequestBody RequestMessage requestMessage,
                                  @RequestParam(value="storeId") String storeId,
                                  @RequestParam(value="productId", required = false) String productId,
                                  @RequestParam(value="barcode", required = false) String barcode){
        ResponseMessage responseMessage;
        responseMessage = this.storeInProductService.getSalesProductListByStoreIdOrProductIdOrBarcode(
                requestMessage,storeId,productId,barcode);
        return responseMessage;
    }
}
