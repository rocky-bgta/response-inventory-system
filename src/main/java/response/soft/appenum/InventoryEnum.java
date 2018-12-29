package response.soft.appenum;

import java.util.Map;
import java.util.TreeMap;

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

        private static final Map<String, Integer> MAP = new TreeMap<>();

        static {
            for (Stock kv : Stock.values()) {
                MAP.put(kv.name(), kv.get());
            }
        }
        public static Map<String, Integer> getMAP() {
            return MAP;
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

        private static final Map<String, Integer> MAP = new TreeMap<>();

        static {
            for (ProductStatus kv : ProductStatus.values()) {
                MAP.put(kv.name(), kv.get());
            }
        }
        public static Map<String, Integer> getMAP() {
            return MAP;
        }

    }

    public enum PaymentMethod {
        CASH(1),
        CREDIT(2),
        CHECK(3);
        private int enumOrdinalValue;
        PaymentMethod(int enumOrdinalValue){
            this.enumOrdinalValue=enumOrdinalValue;
        }
        public int get() {
            return this.enumOrdinalValue;
        }

        private static final Map<String, Integer> MAP = new TreeMap<>();

        static {
            for (PaymentMethod kv : PaymentMethod.values()) {
                MAP.put(kv.name(), kv.get());
            }
        }
        public static Map<String, Integer> getMAP() {
            return MAP;
        }

    }
}
