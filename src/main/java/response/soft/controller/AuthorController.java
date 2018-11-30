package response.soft.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import response.soft.core.RequestMessage;
import response.soft.core.ResponseMessage;
import response.soft.entities.Author;

@RestController
@RequestMapping("api/author/")
@Api(tags = "Author Api List")
public class AuthorController {

    @ApiOperation(value ="", response = Author.class)
    @RequestMapping(value = "getAll", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage getAllUser(@RequestBody RequestMessage requestMessage){
        ResponseMessage responseMessage = null;
        try {

            //responseMessage = this.mqttSubscribePublished.getResponseMessage("api/user/getAll", requestMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
