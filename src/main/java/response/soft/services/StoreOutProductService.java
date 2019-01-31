package response.soft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import response.soft.core.BaseService;
import response.soft.core.Core;
import response.soft.entities.StoreOutProduct;
import response.soft.model.StoreOutProductModel;

@Service
public class StoreOutProductService extends BaseService<StoreOutProduct> {
    private static final Logger log = LoggerFactory.getLogger(StoreInProductService.class);

    @Override
    protected void initEntityModel() {
        Core.runTimeModelType.remove();
        Core.runTimeEntityType.remove();
        Core.runTimeEntityType.set(StoreOutProduct.class);
        Core.runTimeModelType.set(StoreOutProductModel.class);
    }

   /* public List<StoreOutProductModel> saveStoreOutProduct(List<StoreInProductModel> storeInProductModelList) throws Exception{

        StoreOutProductModel storeOutProductModel,saveStoreOutProductModel;
        List<StoreOutProductModel> savedStoreOutProductModelList = new ArrayList<>();

        try {
            for(StoreInProductModel storeInProductModel: storeInProductModelList){
                storeOutProductModel = new StoreOutProductModel();
                storeOutProductModel.setStockId(storeInProductModel.getStockId());
                storeOutProductModel.setStoreId(storeInProductModel.getStoreId());
                storeOutProductModel.setStoreInProductId(storeInProductModel.getId());
                storeOutProductModel.setProductId(storeInProductModel.getProductId());
                storeOutProductModel.setDate(new Date());
                saveStoreOutProductModel = this.save(storeOutProductModel);
                savedStoreOutProductModelList.add(saveStoreOutProductModel);
            }
            Core.commitTransaction();
        } catch (Exception e) {
            Core.rollBackTransaction();
            e.printStackTrace();
            throw e;
        }

        return savedStoreOutProductModelList;
    }*/
}
