package response.soft.appenum;

public enum StockEnum {

    STOCK_IN(1),
    STOCK_OUT(2),
    TRANSFER(3),
    SALES(4),
    CREDIT(5);

    private int stock;
    StockEnum(int stock){
        this.stock=stock;
    }
    public int get() {
        return this.stock;
    }

    /*public enum Status {
        Active(1),
        Inactive(2),
        Deleted(3);

        private int status;

        Status(int status) {
            this.status = status;
        }

        public int get() {
            return this.status;
        }
    }*/


}
