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
import response.soft.model.ProductModel;
import response.soft.services.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("api/product")
@Api(tags = "Product Api List")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAll(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage;
        responseMessage = this.productService.getAllProduct(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getById(@PathVariable UUID id){
        ResponseMessage responseMessage;
        responseMessage = this.productService.getByProductId(id);
        return responseMessage;
    }

    @ApiOperation(value ="", response = Object.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{barcode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getProductByBarcode(@PathVariable String barcode){
        ResponseMessage responseMessage;
        responseMessage = this.productService.getProductByBarcode(barcode);
        return responseMessage;
    }


    @ApiOperation(value ="", response = ProductModel.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> save(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.saveProduct(requestMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ApiOperation(value ="", response = ProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage update(@RequestBody RequestMessage requestMessage) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.updateProduct(requestMessage);
        return responseMessage;
    }

    @ApiOperation(value ="", response = ProductModel.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage delete(@PathVariable UUID id) {
        ResponseMessage responseMessage;
        responseMessage = this.productService.deleteProduct(id);
        return responseMessage;
    }

/*
    @ApiOperation(value ="", response = ProductModel.class)
    @RequestMapping(value = "/image",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> image(@RequestBody String encodedImage) {
        ResponseMessage responseMessage= new ResponseMessage();
        byte[] imageByte= Base64.decodeBase64(encodedImage);
        this.productService.saveImage(imageByte);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }*/

/*
    @ApiOperation(value ="", response = ProductModel.class)
    @RequestMapping(value = "/imageGet",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> image() {
        ResponseMessage responseMessage;//= new ResponseMessage();
        //byte[] imageByte= Base64.encodeBase64(encodedImage);
        responseMessage=this.productService.getByProductId(UUID.fromString("93919509-ff30-4b5f-ba2b-9230e2737593"));
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
    */


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
