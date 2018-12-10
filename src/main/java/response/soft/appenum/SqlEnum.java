package response.soft.appenum;

import java.util.HashMap;
import java.util.Map;

public class SqlEnum {

    public enum Status {
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
    }

    public enum QueryType {
        Select(1),
        Insert(2),
        Update(3),
        Delete(4),
        Join(5),
        Raw(6),
        UpdateByConditions(7),
        CountRow(8),
        GetOne(9),
        LikeOrSearch(10);

        private int queryType;

        QueryType(int queryType) {
            this.queryType = queryType;
        }

        public int get() {
            return this.queryType;
        }

        private static final Map<String, Integer> MAP = new HashMap<>();

        static {
            for (QueryType s : QueryType.values()) {
                MAP.put(s.name(), s.ordinal());
            }
        }

        public static Map<String, Integer> getMAP() {
            return MAP;
        }
    }

}
