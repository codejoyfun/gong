package com.runwise.supply.entity;

import com.runwise.supply.firstpage.entity.OrderResponse;

import java.util.List;

/**
 * Created by libin on 2017/8/20.
 */

public class OrderDetailResponse{


    /**
     * order : {"isPaid":false,"stateTracker":["2017-08-18 11:14 订单已提交"],"settleAmountTotal":0,"waybill":null,"hasAttachment":0,"deliveryType":"vendor_delivery","isFinishTallying":false,"isThirdPartLog":false,"amountTotal":287.64,"createUserName":"小卢","hasReturn":0,"endUnloadDatetime":"","publicAmountTotal":287.64,"isDoubleReceive":true,"deliveredQty":0,"appraisalUserName":"","thirdPartLog":null,"estimatedDate":"2017-08-19","isTwoUnit":false,"orderID":364,"confirmationDate":"","loadingTime":"","name":"SO17081800013","estimatedTime":"2017-08-19 00:00:00","amount":6,"createDate":"2017-08-18 11:14:15","lines":[{"productUom":"件","unloadAmount":0,"priceUnit":0.95,"discount":0,"returnAmount":0,"deliveredQty":0,"priceSubtotal":2.85,"productID":21,"tallyingAmount":0,"saleOrderProductID":499,"lotIDs":[],"stockType":"other","settleAmount":0,"lotList":[],"productUomQty":3},{"productUom":"瓶","unloadAmount":0,"priceUnit":81,"discount":0,"returnAmount":0,"deliveredQty":0,"priceSubtotal":243,"productID":30,"tallyingAmount":0,"saleOrderProductID":500,"lotIDs":[],"stockType":"ganhuo","settleAmount":0,"lotList":[],"productUomQty":3}],"startUnloadDatetime":"","returnOrders":[],"state":"draft","orderSettleName":"单次结算","isToday":false,"tallyingUserName":"","receiveUserName":"","doneDatetime":"","store":{"mobile":"18819260991","partner":"小卢","partnerID":360,"name":"【老班长】华农店","address":"华南农业大学花山区07号"}}
     */

    private OrderResponse.ListBean order;

    public OrderResponse.ListBean getOrder() {
        return order;
    }

    public void setOrder(OrderResponse.ListBean order) {
        this.order = order;
    }
}
