package com.runwise.supply.entity;

import com.runwise.supply.firstpage.entity.ReceiveBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 2017/10/27.
 */

public class ReceiveBeanList {
    public List<ReceiveBean> getList() {
        return list;
    }
public ReceiveBeanList(){

}
    public  ReceiveBeanList(String source) {
        try {
            JSONObject jsonObject = new JSONObject(source);
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            list = new ArrayList<>();
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                ReceiveBean receiveBean = new ReceiveBean(jsonObject1.toString());
                list.add(receiveBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (ReceiveBean receiveBean:list){
                JSONObject jsonObject1 = new JSONObject(receiveBean.toString());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("list", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setList(List<ReceiveBean> list) {
        this.list = list;
    }

    private List<ReceiveBean> list;
}
