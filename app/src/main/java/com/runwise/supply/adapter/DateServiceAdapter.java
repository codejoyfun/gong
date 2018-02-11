package com.runwise.supply.adapter;

import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.view.timepacker.WheelAdapter;

/**
 * Created by mike on 2018/2/5.
 */

public class DateServiceAdapter implements WheelAdapter {

    int reserveGoodsAdvanceDate;

    public DateServiceAdapter(){

    }
    public DateServiceAdapter(int reserveGoodsAdvanceDate){
        this.reserveGoodsAdvanceDate = reserveGoodsAdvanceDate;
    }

    @Override
    public int getItemsCount() {
            return 30;
    }

    @Override
    public String getItem(int index) {
        index = reserveGoodsAdvanceDate-1+index;
        String date = TimeUtils.getABFormatDate(index).substring(5);
        String week = TimeUtils.getWeekStr(index);
        String desc = date+" "+week;
        switch (index){
            case 0:
                desc+="[今天]";
                break;
            case 1:
                desc+="[明天]";
                break;
            case 2:
                desc+="[后天]";
                break;
        }
        return desc;
    }

    public String getItemYMD(int index){
      return TimeUtils.getABFormatDate(reserveGoodsAdvanceDate-1+index);
    }

    @Override
    public int getMaximumLength() {
        return 12;
    }
}
