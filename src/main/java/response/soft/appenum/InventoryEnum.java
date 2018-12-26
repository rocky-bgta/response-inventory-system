package response.soft.appenum;

public class InventoryEnum {

    public enum Stock {
        STOCK_IN(1),
        STOCK_OUT(2);
        private int enumOrdinalValue;
        Stock(int enumOrdinalValue){
            this.enumOrdinalValue=enumOrdinalValue;
        }
        public int get() {
            return this.enumOrdinalValue;
        }
    }

    public enum ProductStatus {
        AVAILABLE(1),
        SOLD(2),
        TRANSFER(3);
        private int enumOrdinalValue;
        ProductStatus(int enumOrdinalValue){
            this.enumOrdinalValue=enumOrdinalValue;
        }
        public int get() {
            return this.enumOrdinalValue;
        }
    }
}
