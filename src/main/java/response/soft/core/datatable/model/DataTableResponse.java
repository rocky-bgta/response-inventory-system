package response.soft.core.datatable.model;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class DataTableResponse {
    private List<?> data;
    private Long draw;
    private Long recordsFiltered;
    private Long recordsTotal;

    public DataTableResponse(){}


    public DataTableResponse(Page page, DataTableRequest dataTableRequest) {
        this.data = page.getContent();
        this.recordsFiltered = page.getTotalElements();
        this.recordsTotal=(long)dataTableRequest.getLength();
        this.draw = (long)dataTableRequest.getDraw();
    }


}