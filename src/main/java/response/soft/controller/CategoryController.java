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
import response.soft.model.CategoryModel;
import response.soft.services.CategoryService;

import java.util.UUID;

@RestController
@RequestMapping("api/category")
@Api(tags = "Category Api List")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*

    @ApiOperation(value ="", response = Object.class)
    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> getAllUser(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.getAllCategory(requestMessage);
        if(responseMessage.data!=null)
            return new ResponseEntity<>(responseMessage, responseMessage.httpStatus);
        else
            return new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    */


    @ApiOperation(value ="", response = Object.class)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.getAllCategory(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.getByCategoryId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.saveCategory(requestMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.updateCategory(requestMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.deleteCategory(id);
        return new ResponseEntity(responseMessage,HttpStatus.OK);
    }
}
