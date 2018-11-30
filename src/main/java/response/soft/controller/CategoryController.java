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

@RestController
@RequestMapping("api/category/")
@Api(tags = "Category Api List")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

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

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(value = "save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.categoryService.save(requestMessage);
        if(responseMessage.data!=null)
           return new ResponseEntity<>(responseMessage, responseMessage.httpStatus);
        else
          return new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
