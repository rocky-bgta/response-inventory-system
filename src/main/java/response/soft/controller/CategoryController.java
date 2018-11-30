package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.soft.core.RequestObject;
import response.soft.core.ResponseObject;
import response.soft.entities.Author;
import response.soft.model.CategoryModel;
import response.soft.services.CategoryService;

@RestController
@RequestMapping("api/category/")
@Api(tags = "Category Api List")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value ="", response = Author.class)
    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> getAllUser(@RequestBody RequestObject requestMessage){
        ResponseObject responseObject;
        responseObject = this.categoryService.getAllCategory(requestMessage);
        if(responseObject.data!=null)
            return new ResponseEntity<>(responseObject,responseObject.httpStatus);
        else
            return new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(value = "save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> save(@RequestBody RequestObject requestMessage) {
        ResponseObject responseObject;
        responseObject = this.categoryService.save(requestMessage);
        if(responseObject.data!=null)
           return new ResponseEntity<>(responseObject,responseObject.httpStatus);
        else
          return new ResponseEntity<>(null,null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
