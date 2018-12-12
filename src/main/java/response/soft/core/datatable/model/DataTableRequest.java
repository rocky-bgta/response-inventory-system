package response.soft.core.datatable.model;

import java.util.List;


public class DataTableRequest {
    public Long draw;
    public Integer length;
    public Integer start;
    public DataTableSearch search;
    public List<DataTableOrder> order;
    public List<DataTableColumns> columns;
}
