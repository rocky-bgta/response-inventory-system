package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.services.ProductSalesService;

import java.util.UUID;

@RestController
@RequestMapping("api/product-sales")
@Api(tags = "Product Sales Api List")
public class SalesReportController {

    private final ProductSalesService productSalesService;

    @Autowired
    public SalesReportController(ProductSalesService productSalesService) {
        this.productSalesService = productSalesService;
    }


    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/report", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage,
                                  @RequestParam(value="storeId", required = false) UUID storeId,
                                  @RequestParam(value="productId", required = false) UUID productId){
        ResponseMessage responseMessage;
        responseMessage = this.productSalesService.getProductSalesReport(requestMessage);
        return responseMessage;
    }

   /*

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
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.stockService.deleteStock(id);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }
*/
}
