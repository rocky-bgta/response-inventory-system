package response.soft.core.datatable.model;

import lombok.Data;

import java.util.List;

@Data
public class DataTableRequest {
    private Long draw;
    private Integer length;
    private Integer start;
    private DataTableSearch search;
    private List<DataTableOrder> order;
    private List<DataTableColumns> columns;
}
