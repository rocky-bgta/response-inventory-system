package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    public ResponseObject getAllUser(@RequestBody RequestObject requestMessage){
        ResponseObject responseMessage = null;
        responseMessage = this.categoryService.getAllCategory(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(value = "save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseObject save(@RequestBody RequestObject requestMessage) {
        ResponseObject responseMessage;
        responseMessage = this.categoryService.save(requestMessage);
        return responseMessage;
    }
}
