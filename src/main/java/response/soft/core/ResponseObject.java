/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 22-Dec-17
 * Time: 11:44 AM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import org.springframework.http.HttpStatus;

public class ResponseObject {

    public String token;
    public Object data;
    public HttpStatus httpStatus;
    public String message;


    // Pagination
    //public Integer pageOffset;
    //public Integer pageSize;
    public Long totalRow;

}
