package com.runwise.supply.firstpage;

import android.text.TextUtils;

import com.runwise.supply.GlobalApplication;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;

/**
 * Created by libin on 2017/8/20.
 */

public class OrderActionUtils {
    private static String userName = GlobalApplication.getInstance().getUserName();
    //通过订单状态，获取按钮上该显示什么字
    public static String getDoBtnTextByState(OrderResponse.ListBean bean) {
        String btnText = null;
        if (bean.getState().equals(OrderState.DRAFT.getName())){
            btnText = "取消订单";
        }else if(bean.getState().equals(OrderState.SALE.getName())){
            //不做任务事情，返回null,隐藏此按钮
        }else if(bean.getState().equals(OrderState.PEISONG.getName())){
            if (bean.isIsDoubleReceive()){
                if (bean.isIsFinishTallying()){
                    //双人收货
                    btnText = "收货";
                }else{
                    //双人点货
                    btnText = "点货";
                }
            }else{
                //正常收货
                btnText = "收货";
            }

        }else if(bean.getState().equals(OrderState.DONE.getName())){
//            if (bean.getHasAttachment() != 0){
//                btnText = "查看支付凭证";
//            }else{
//                btnText = "上传支付凭证";
//            }
            btnText = "评价";
        }else if(bean.getState().equals(OrderState.RATED.getName())){
            btnText = "已评价";
        }else if(OrderState.CANCEL.getName().equals(bean.getState())){
            btnText = "删除订单";
        }
        return btnText;
    }
    //通过状态字，得到订单的下一步操作是什么
    public static OrderDoAction getDoActionByText(String doAction, OrderResponse.ListBean bean){
        OrderDoAction action = null;
        if ("取消订单".equals(doAction)){
            action = OrderDoAction.CANCLE;
        }else if("点货".equals(doAction)){
            //如果有人在点货，则弹窗提示
            if (TextUtils.isEmpty(bean.getTallyingUserName()) || userName.equals(bean.getTallyingUserName())){
                action = OrderDoAction.TALLY;
            }else{
                //有人正在点货
                action = OrderDoAction.TALLYING;
            }

        }else if("收货".equals(doAction)){
            //在这里做判断，是正常收货，还是双人收货,同时判断点货人是谁，如果是自己，则不能再收货
            if (bean.isIsDoubleReceive()){
                String userName = GlobalApplication.getInstance().getUserName();
                if (bean.getTallyingUserName().equals(userName)){
                    action = OrderDoAction.SELFTALLY;
                }else{
                    action = OrderDoAction.SETTLERECEIVE;
                }
            }else{
                action = OrderDoAction.RECEIVE;
            }

        }else if("上传支付凭证".equals(doAction)){
            action = OrderDoAction.UPLOAD;
        }else if("查看支付凭证".equals(doAction)){
            action = OrderDoAction.LOOK;
        }else if("评价".equals(doAction)){
            action = OrderDoAction.RATE;
        }else if ("完成退货".equals(OrderDoAction.FINISH_RETURN)){
            action = OrderDoAction.FINISH_RETURN;
        }else if("删除订单".equals(doAction)){
            action = OrderDoAction.DELETE;
        }
        return action;
    }
}
