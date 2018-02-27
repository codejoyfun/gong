package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kids.commonframe.base.util.SPUtils;
import com.runwise.supply.entity.CartCache;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.kids.commonframe.config.Constant.SP_KEY_CART;

/**
 * Created by Dong on 2017/11/27.
 */

public class CartManager {
    private static CartManager instance;
    private SharedPreferences mPrefs;
    private Context context;
    private CartManager(Context context){
        this.context = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
    public static synchronized CartManager getInstance(Context context){
        if(instance==null){
            instance = new CartManager(context);
        }
        return instance;
    }

    /**
     * 加载缓存的购物车
     * @return
     */
    public CartCache loadCart(){
        return (CartCache)SPUtils.readObject(context,SP_KEY_CART);
    }

    /**
     * 将商品列表和购物车勾选状态保存进缓存
     * @param mapCount
     * @param selected 勾选状态，只有勾选了才会去下单，否则一直保留
     */
    public void saveCart(Map<ProductBasicList.ListBean,String> mapRemarks, Map<ProductBasicList.ListBean,Double> mapCount, HashSet<Integer> selected){
        ArrayList<ProductBasicList.ListBean> list = new ArrayList<>();
        for(ProductBasicList.ListBean bean:mapCount.keySet()){
            bean.setActualQty(mapCount.get(bean));
            bean.setRemark(mapRemarks.get(bean));
            bean.setCacheSelected(selected.contains(bean.getProductID()));//在购物车中是否被选中
        }
        list.addAll(mapCount.keySet());
        CartCache cartCache = new CartCache();
        cartCache.setListBeans(list);
        SPUtils.saveObject(context,SP_KEY_CART,cartCache);
    }

    /**
     * 完全清空购物车
     */
    public void clearCart(){
        SPUtils.put(context,SP_KEY_CART,"");
    }

    /**
     * 从购物车删除特定商品
     * 清空勾选状态
     * @param productList
     */
    public void clearCart(List<ProductBasicList.ListBean> productList){
        CartCache cartCache = loadCart();//读取购物车，从购物车中删除
        if(cartCache!=null && cartCache.getListBeans()!=null){
            //short cut，如果数量一样，直接删所有
            if(productList.size()==cartCache.getListBeans().size()){
                clearCart();
                return;
            }

            HashSet<ProductBasicList.ListBean> shouldRemoved = new HashSet<>();//记录应该删除的商品
            shouldRemoved.addAll(productList);
            ListIterator<ProductBasicList.ListBean> iter = cartCache.getListBeans().listIterator();
            while(iter.hasNext()){
                if(shouldRemoved.contains(iter.next())){
                    iter.remove();
                }
            }
            SPUtils.saveObject(context,SP_KEY_CART,cartCache);
        }
    }

}
