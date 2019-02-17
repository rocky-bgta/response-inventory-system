package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.model.StoreInProductModel;
import response.soft.model.view.StoreInProductsViewModel;
import response.soft.services.StockService;

import java.util.UUID;

@RestController
@RequestMapping("api/stock")
@Api(tags = "Stock Api List")
public class StockController {

    private final StockService stockService;

    //private final BeanFactory beanFactory;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
        //this.beanFactory = beanFactory;
    }


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage,
                                  @RequestParam(value="storeId", required = false) String storeId,
                                  @RequestParam(value="categoryId", required = false) String categoryId,
                                  @RequestParam(value="productId", required = false) String productId){
        ResponseMessage responseMessage;
        responseMessage = this.stockService.getAllStock(requestMessage,storeId,categoryId,productId);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.stockService.getByStockId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductsViewModel.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.stockService.saveStock(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.stockService.updateStock(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = StoreInProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.stockService.deleteStock(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/product-details-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getStockProductDetails(@RequestBody RequestMessage requestMessage,
                                                  @RequestParam(value="storeId") String storeId,
                                                  @RequestParam(value="productId", required = false) String productId,
                                                  @RequestParam(value="categoryId", required = false) String categoryId){
        ResponseMessage responseMessage;
        responseMessage = this.stockService.getStockProductDetailsListByStoreIdAndProductIdAndCategoryId(
                requestMessage,storeId,productId,categoryId);
        return responseMessage;
    }

}
