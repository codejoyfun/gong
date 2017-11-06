package com.runwise.supply.tools;

import android.content.Context;
import android.os.Handler;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.util.net.NetWorkHelper;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnDetailResponse;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dong on 2017/10/18.
 */

public class ProductBasicHelper {
    NetWorkHelper netWorkHelper;
    Set<Integer> missingProductsID = new HashSet<>();
    Context context;

    public ProductBasicHelper(Context context,NetWorkHelper netWorkHelper){
        this.context = context.getApplicationContext();
        this.netWorkHelper = netWorkHelper;
    }

    /**
     * 检查本地数据库是否有商品信息
     * @param listToCheck
     * @return 本地数据库齐全true
     */
    public boolean check(List<? extends OrderResponse.ListBean.LinesBean> listToCheck){
        for(OrderResponse.ListBean.LinesBean linesBean:listToCheck){
            if(ProductBasicUtils.getBasicMap(context).get(linesBean.getProductID()+"")==null){
                missingProductsID.add(linesBean.getProductID());
            }
        }
        if(missingProductsID.size()==0)return true;
        return false;
    }

    /**
     * 检查本地数据库是否有商品信息
     * @param listToCheck
     * @return 本地数据库齐全true
     */
    public boolean checkTransfer(List<TransferDetailResponse.LinesBean> listToCheck){
        for(TransferDetailResponse.LinesBean linesBean:listToCheck){
            if(ProductBasicUtils.getBasicMap(context).get(linesBean.getProductID()+"")==null){
                missingProductsID.add(linesBean.getProductID());
            }
        }
        if(missingProductsID.size()==0)return true;
        return false;
    }

    /**
     *
     * @param listToCheck
     * @return
     */
    public boolean checkInventory(List<PandianResult.InventoryBean.LinesBean> listToCheck){
        for(PandianResult.InventoryBean.LinesBean linesBean:listToCheck){
            if(ProductBasicUtils.getBasicMap(context).get(linesBean.getProductID()+"")==null){
                missingProductsID.add(linesBean.getProductID());
            }
        }
        if(missingProductsID.size()==0)return true;
        return false;
    }

    public boolean checkRepertoryProducts(List<? extends RepertoryEntity.ListBean> listToCheck){
        for(RepertoryEntity.ListBean listBean:listToCheck){
            if(ProductBasicUtils.getBasicMap(context).get(listBean.getProductID()+"")==null){
                missingProductsID.add(listBean.getProductID());
            }
        }
        if(missingProductsID.size()==0)return true;
        return false;
    }

    /**
     * 向接口查询所有缺失的商品信息
     * @param where
     */
    public void requestDetail(int where){
        for(Integer id:missingProductsID){
            Object request = null;
            StringBuffer sb = new StringBuffer("/gongfu/v2/product/");
            sb.append(id).append("/");
            netWorkHelper.sendConnection(sb.toString(), request, where, true, ProductOne.class);
        }
    }

    List<ProductBasicList.ListBean> missingProducts;

    /**
     * networkHelper的callback
     * 商品信息设置进缓存
     * @param result
     * @return true：所有请求返回，可以刷新页面了，false：仍有商品请求未返回
     */
    public boolean onSuccess(BaseEntity result){
        ProductOne productOne = (ProductOne) result.getResult().getData();
        ProductBasicList.ListBean listBean = productOne.getProduct();
        //保存进缓存
        missingProductsID.remove(listBean.getProductID());
        ProductBasicUtils.getBasicMap(context).put(listBean.getProductID()+"",listBean);//更新内存缓存
        if(missingProducts==null)missingProducts = new ArrayList<>();//保存进数据库
        missingProducts.add(listBean);
        if(missingProductsID.size()==0){//所有都返回了
            ProductBasicUtils.saveProductInfoAsync(context,missingProducts);
            //刷新页面
            return true;
        }
        return false;
    }
}
