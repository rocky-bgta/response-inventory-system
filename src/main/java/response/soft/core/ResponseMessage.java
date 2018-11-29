/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 22-Dec-17
 * Time: 11:44 AM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

public class ResponseMessage {

    public String token;
    public Object data;
    public Integer code;
    public String message;


    // Pagination
    public Integer pageOffset;
    public Integer pageSize;
    public Integer totalRow;

}
