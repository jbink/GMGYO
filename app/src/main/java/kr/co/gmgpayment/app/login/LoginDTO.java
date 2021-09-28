package kr.co.gmgpayment.app.login;

public class LoginDTO {

    /**
     * RESULT : SUCCESS
     * DATA : {"no":"36","store_id":"GP17302252"}
     */

    private String RESULT;
    private DATABean DATA;

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public DATABean getDATA() {
        return DATA;
    }

    public void setDATA(DATABean DATA) {
        this.DATA = DATA;
    }

    public static class DATABean {
        /**
         * no : 36
         * store_id : GP17302252
         */

        private String no;
        private String store_id;

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }
    }
}
