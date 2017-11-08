package com.runwise.supply.adapter;

import android.text.TextUtils;

import com.kids.commonframe.base.BaseEntity;
import com.runwise.supply.R;
import com.runwise.supply.entity.CategoryRespone;
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

    private static List<RepertoryEntity.ListBean> listBeen;
    public static BaseEntity mockCategory(){
        BaseEntity result = new BaseEntity();
        BaseEntity.ResultBean resultBean = new BaseEntity.ResultBean();
        CategoryRespone categoryRespone = new CategoryRespone();
        List<String> cats = new ArrayList<>();
        cats.add("冷藏货");
        cats.add("干货");
        cats.add("冻货");
        categoryRespone.setCategoryList(cats);
        resultBean.setData(categoryRespone);
        result.setResult(resultBean);
        return result;
    }
    public static BaseEntity mockStockData(String category, String keyword){
        if(listBeen==null){
            listBeen = new ArrayList<>();

            listBeen.add(newListBean("300001","新鲜柠檬","冷藏货","1000克/袋",10,"袋",R.drawable.ningmeng));
            listBeen.add(newListBean("300002","莴笋","冷藏货","1000克/袋",10,"袋",R.drawable.wosuo));
            listBeen.add(newListBean("300003","新鲜油桃","冷藏货","1000克/袋",10,"袋",R.drawable.youtao));
            listBeen.add(newListBean("300006","新鲜本地番茄","冷藏货","1000克/袋",10,"袋",R.drawable.fanqie));

            listBeen.add(newListBean("300007","虾皇饺","冻货","600克/包",10,"包",R.drawable.xiahuangjiao));
            listBeen.add(newListBean("300008","香糯汤圆 黑芝麻口味","冻货","200克/包",10,"包",R.drawable.tangyuan));

            listBeen.add(newListBean("300004","无染色黑芝麻","干货","1000克/袋",10,"袋",R.drawable.zhima));
            listBeen.add(newListBean("300005","杏仁","干货","1000克/袋",10,"袋",R.drawable.xingren));
            listBeen.add(newListBean("300009","一次性包装盒","干货","1000个/袋",10,"袋",R.drawable.baozhuanghe));
        }

        List<RepertoryEntity.ListBean> resultList = new ArrayList<>();
        for(RepertoryEntity.ListBean bean:listBeen){
            if((bean.getProduct().getCategory().equals(category) && bean.getProduct().getName().contains(keyword))
                    || (TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword))
                    || (TextUtils.isEmpty(category) && bean.getProduct().getName().contains(keyword))){
                resultList.add(bean);
            }
        }
        BaseEntity result = new BaseEntity();
        BaseEntity.ResultBean resultBean = new BaseEntity.ResultBean();
        RepertoryEntity repertoryEntity = new RepertoryEntity();
        repertoryEntity.setList(resultList);
        resultBean.setData(repertoryEntity);
        result.setResult(resultBean);
        return result;
    }


}
