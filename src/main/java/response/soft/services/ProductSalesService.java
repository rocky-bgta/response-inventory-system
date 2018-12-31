package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.ProductSales;
import response.soft.entities.StoreOutProduct;
import response.soft.model.ProductSalesModel;
import response.soft.model.StoreOutProductModel;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSalesService extends BaseService<ProductSales> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(ProductSales.class);
        Core.runTimeModelType.set(ProductSalesModel.class);
    }

    public void saveProductSales(List<StoreOutProductModel> storeOutProductModelList){
        ProductSalesModel productSalesModel, savedProductSalesModel;
        List<ProductSalesModel> savedProductSalesModelList = new ArrayList<>();

        for(StoreOutProductModel storeOutProductModel: storeOutProductModelList){
            productSalesModel = new ProductSalesModel();
            productSalesModel.setStoreOutId(storeOutProductModel.getId());
            productSalesModel.setProductId(storeOutProductModel.getProductId());

        }

    }
}
