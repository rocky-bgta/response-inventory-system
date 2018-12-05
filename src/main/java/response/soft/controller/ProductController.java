package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.model.CategoryModel;
import response.soft.services.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/product")
@Api(tags = "Category Api List")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value ="", response = Object.class)
    @RequestMapping(value = "/getAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.productService.getAllProduct(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.productService.getByProductId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.saveProduct(requestMessage);
        return new ResponseEntity<>(responseMessage, responseMessage.httpStatus);
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.updateProduct(requestMessage);
        return new ResponseEntity<>(responseMessage, responseMessage.httpStatus);
    }

    @ApiOperation(value ="", response = CategoryModel.class)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.deleteProduct(id);
        return new ResponseEntity<>(responseMessage, responseMessage.httpStatus);
    }

   /* *//*
     * MultipartFile Upload
     *//*
    @PostMapping("/api/file/upload")
    public String uploadMultipartFile(@RequestParam("file") MultipartFile file) {
        try {
            // save file to PostgreSQL
            FileModel filemode = new FileModel(file.getOriginalFilename(), file.getContentType(), file.getBytes());
            fileRepository.save(filemode);
            return "File uploaded successfully! -> filename = " + file.getOriginalFilename();
        } catch (	Exception e) {
            return "FAIL! Maybe You had uploaded the file before or the file's size > 500KB";
        }
    }*/
}
