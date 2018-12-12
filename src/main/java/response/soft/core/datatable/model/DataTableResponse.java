package response.soft.core.datatable.model;

import org.springframework.data.domain.Page;

import java.util.List;


public class DataTableResponse {
    public List<?> data;
    public Long draw;
    public Long recordsFiltered;
    public Long recordsTotal;

    public DataTableResponse(){}


    public DataTableResponse(Page page, DataTableRequest dataTableRequest) {
        this.data = page.getContent();
        this.recordsFiltered = page.getTotalElements();
        this.recordsTotal=(long)dataTableRequest.length;
        this.draw = (long)dataTableRequest.draw;
    }


}