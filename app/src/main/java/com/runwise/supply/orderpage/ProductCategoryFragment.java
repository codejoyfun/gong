package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.fragment.OrderProductFragment;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CategoryResponseV2;
import com.runwise.supply.orderpage.entity.ProductData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.runwise.supply.fragment.OrderProductFragment.BUNDLE_KEY_LIST;

/**
 *
 */
public class ProductCategoryFragment extends NetWorkFragment {

    public static final String INTENT_KEY_CATEGORY = "ap_category";
    private CategoryResponseV2.Category mCategory;
    @ViewInject(R.id.rv_sub_category)
    private RecyclerView mRvSubCategory;//子类别的列表view
    private SubCategoryAdapter mSubCategoryAdapter;
    private Map<String,ProductListFragmentV2> mMapProductListFragments = new HashMap<>();//子类别对应的商品列表fragment
    private String mCurrentSubCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategory = getArguments().getParcelable(INTENT_KEY_CATEGORY);
        mSubCategoryAdapter = new SubCategoryAdapter();
        mRvSubCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRvSubCategory.setAdapter(mSubCategoryAdapter);
        setupFragments();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_product_list;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        //empty
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        //empty
    }

    public void setupFragments() {
        for(String subCategory:mCategory.getCategoryII()){
            ProductListFragmentV2 fragment = newProductListFragment(subCategory);
            mMapProductListFragments.put(subCategory,fragment);
        }
        //打开第一个fragment
        switchSubCategory(mCategory.getCategoryII()[0]);
        //mLoadingLayout.onSuccess(arrayList, "这里是空哒~~", R.drawable.default_ico_none);
    }

    /**
     * @return
     */
    public ProductListFragmentV2 newProductListFragment(String subCategory) {
        ProductListFragmentV2 productListFragment = new ProductListFragmentV2();
        Bundle bundle = new Bundle();
        productListFragment.setArguments(bundle);
        return productListFragment;
    }

    /**
     * 转换子类对应的fragment
     * @param subCategory
     */
    private void switchSubCategory(String subCategory){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fl_product_list_container,mMapProductListFragments.get(subCategory),subCategory);
        ft.commitAllowingStateLoss();
        mCurrentSubCategory = subCategory;
        mSubCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 子分类列表adapter
     */
    private class SubCategoryAdapter extends RecyclerView.Adapter<ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_sub_category,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String subCategory = mCategory.getCategoryII()[position];
            holder.mmSubCategory = subCategory;
            holder.mmTvSubCategory.setText(subCategory);
            if(mCurrentSubCategory.equals(holder.mmTvSubCategory.getText())){
                holder.mmTvSubCategory.setTextColor(getResources().getColor(R.color.btn_green));
            }else{
                holder.mmTvSubCategory.setTextColor(getResources().getColor(R.color.black));
            }
        }

        @Override
        public int getItemCount() {
            return mCategory==null||mCategory.getCategoryII()==null?0:mCategory.getCategoryII().length;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        String mmSubCategory;
        TextView mmTvSubCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            mmTvSubCategory = (TextView) itemView.findViewById(R.id.rb_sub_category);
            mmTvSubCategory.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switchSubCategory(mmSubCategory);
        }
    }
}
