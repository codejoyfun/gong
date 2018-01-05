package com.runwise.supply.entity;

import java.util.List;

/**
 * 检查商品有效性
 * TODO:用于自测用途，具体协议未定
 *
 * Created by Dong on 2018/1/5.
 */

public class ProductValidateResponse {
    private List<ResultItem> line;

    public List<ResultItem> getLine() {
        return line;
    }

    public void setLine(List<ResultItem> line) {
        this.line = line;
    }

    /**
     * 需要检查的商品信息
     */
    public static final class ResultItem {
        private int productId;
        private boolean isValid;

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }
    }
}
