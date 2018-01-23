package com.runwise.supply.firstpage.entity;

import com.runwise.supply.orderpage.entity.ImageBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/17.
 */

public class ReceiveBean {
    private String name;        //货物名称
    private int productId;   //商品id
    private double count;          //商品数量
    private double productUomQty;          //商品库存数量
    private boolean isTwoUnit;  //是否为双单位
    private String unit;        //是双单位的话，单位是什么
    private double twoUnitValue;//有双单位情况下，用户输入的值
    private String tracking;
    private ImageBean imageBean;
    List<ReceiveRequest.ProductsBean.LotBean> lot_list;
    private boolean isChange;


    public ReceiveBean(String source){
        try {
            JSONObject jsonObject = new JSONObject(source);
            name = jsonObject.optString("name");
            productId = jsonObject.optInt("productId");
            count = jsonObject.optDouble("count");
            productUomQty = jsonObject.optDouble("productUomQty");
            isTwoUnit = jsonObject.optBoolean("isTwoUnit");
            twoUnitValue = jsonObject.optDouble("twoUnitValue");
            unit = jsonObject.optString("unit");
            tracking = jsonObject.optString("tracking");
            JSONArray jsonArray = jsonObject.optJSONArray("lot_list");
            if(jsonArray !=null){
                lot_list = new ArrayList<>();
                for (int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    ReceiveRequest.ProductsBean.LotBean lotBean = new ReceiveRequest.ProductsBean.LotBean(jsonObject1.toString());
                    lot_list.add(lotBean);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("productId",productId);
            jsonObject.put("count",count);
            jsonObject.put("productUomQty",productUomQty);
            jsonObject.put("isTwoUnit",isTwoUnit);
            jsonObject.put("unit",unit);
            jsonObject.put("twoUnitValue",twoUnitValue);
            jsonObject.put("tracking",tracking);
            JSONArray jsonArray = new JSONArray();
            if (lot_list != null){
                for (ReceiveRequest.ProductsBean.LotBean lotBean:lot_list){
                    JSONObject jsonObject1 = new JSONObject(lotBean.toString());
                    jsonArray.put(jsonObject1);
                }
                jsonObject.put("lot_list",jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    private String stockType;

    private String defaultCode;

    public double getProductUomQty() {
        return productUomQty;
    }

    public void setProductUomQty(double productUomQty) {
        this.productUomQty = productUomQty;
    }



    public ReceiveBean() {
    }
    public List<ReceiveRequest.ProductsBean.LotBean> getLot_list() {
        return lot_list;
    }

    public void setLot_list(List<ReceiveRequest.ProductsBean.LotBean> lot_list) {
        this.lot_list = lot_list;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public boolean isTwoUnit() {
        return isTwoUnit;
    }

    public void setTwoUnit(boolean twoUnit) {
        isTwoUnit = twoUnit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getTwoUnitValue() {
        return twoUnitValue;
    }

    public void setTwoUnitValue(double twoUnitValue) {
        this.twoUnitValue = twoUnitValue;
    }
    public String getDefaultCode() {
        return defaultCode;
    }

    public void setDefaultCode(String defaultCode) {
        this.defaultCode = defaultCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceiveBean that = (ReceiveBean) o;

        return productId == that.productId;

    }

    @Override
    public int hashCode() {
        return productId;
    }


    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }


    public ImageBean getImageBean() {
        return imageBean;
    }

    public void setImageBean(ImageBean imageBean) {
        this.imageBean = imageBean;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }
}
