package com.runwise.supply.adapter;

import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 2017/10/31.
 */

public class FictitiousStock {


    public static RepertoryEntity getRepertoryEntity() {
        RepertoryEntity repertoryEntity = new RepertoryEntity();
        List<RepertoryEntity.ListBean> listBeen = new ArrayList<>();

        listBeen.add(newListBean("300001","新鲜柠檬","冷藏货","1000克/袋",10,"袋",R.drawable.ningmeng));
        listBeen.add(newListBean("300002","莴笋","冷藏货","1000克/袋",10,"袋",R.drawable.wosuo));
        listBeen.add(newListBean("300003","新鲜油桃","冷藏货","1000克/袋",10,"袋",R.drawable.youtao));
        listBeen.add(newListBean("300006","新鲜本地番茄","冷藏货","1000克/袋",10,"袋",R.drawable.fanqie));

        listBeen.add(newListBean("300007","虾皇饺","冻货","600克/包",10,"包",R.drawable.xiahuangjiao));
        listBeen.add(newListBean("300008","香糯汤圆 黑芝麻口味","冻货","200克/包",10,"包",R.drawable.tangyuan));

        listBeen.add(newListBean("300004","无染色黑芝麻","干货","1000克/袋",10,"袋",R.drawable.zhima));
        listBeen.add(newListBean("300005","杏仁","干货","1000克/袋",10,"袋",R.drawable.xingren));
        listBeen.add(newListBean("300009","一次性包装盒","干货","1000个/袋",10,"袋",R.drawable.baozhuanghe));


        repertoryEntity.setList(listBeen);
        return repertoryEntity;
    }

    private static RepertoryEntity.ListBean newListBean(String defaultCode,String name,String category,String unit,int qty,String uom,int imageId){
        RepertoryEntity.ListBean listBean = new RepertoryEntity.ListBean();
        ProductBasicList.ListBean product = new ProductBasicList.ListBean();
        product.setDefaultCode(defaultCode);
        product.setName(name);
        product.setCategory(category);
        product.setUnit(unit);
        listBean.setQty(qty);
        listBean.setUom(uom);
        listBean.setImageId(imageId);
        listBean.setProduct(product);
        return  listBean;
    }

}
