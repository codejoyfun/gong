package com.runwise.supply.orderpage;

import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.List;

/**
 * 智能下单和常购清单返回的数据
 *
 * Created by Dong on 2017/11/24.
 */

public class PresetProductData {
    private List<PresetListBean> list;

    public List<PresetListBean> getList() {
        return list;
    }

    public void setList(List<PresetListBean> list) {
        this.list = list;
    }

    /**
     * 预设数量的接口返回的商品（智能下单和常购清单）
     *
     * 商品数据通过一个另外的product字段返回，这里做的兼容处理
     * 需要保证json框架是通过setter方法设置变量的
     */
    public static final class PresetListBean extends ProductData.ListBean{
        ProductBasicList.ListBean product;

        public ProductBasicList.ListBean getProduct() {
            return product;
        }

        public void setProduct(ProductBasicList.ListBean product) {
            if(product!=null){
                setName(product.getName());
                setCategory(product.getCategory());
                setDefaultCode(product.getDefaultCode());
                setIsTwoUnit(product.isTwoUnit());
                setProductUom(product.getProductUom());
                setTracking(product.getTracking());
                setUom(product.getUom());
                setSettleUomId(product.getSettleUomId());
                setUnit(product.getUnit());
                setPrice(product.getPrice());
                setImage(product.getImage());
            }
            this.product = product;
        }
    }
}
