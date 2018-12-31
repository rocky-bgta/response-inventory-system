/**
 * Created By: Md. Nazmus Salahin
 * Created Date: 22-Dec-17
 * Time: 11:56 AM
 * Modified By:
 * Modified date:
 * (C) CopyRight Nybsys ltd.
 */
package response.soft.core;

import response.soft.core.datatable.model.DataTableRequest;

public class RequestMessage {
    public String token;
    public Object data;
    public Integer pageOffset;
    public Integer pageSize;

    public DataTableRequest dataTableRequest;

}
