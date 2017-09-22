package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.mine.UserGuidActivity;

import org.greenrobot.eventbus.EventBus;


public class InfoActivity extends NetWorkActivity {
    @ViewInject(R.id.context)
    private TextView context;
    @ViewInject(R.id.checkBox)
    private CheckBox checkBox;
    @ViewInject(R.id.nextBtn)
    private Button nextBtn;
    public static Intent targerIntent;
    boolean hide;
    private final int AGGRE_ITEM = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        hide = this.getIntent().getBooleanExtra("hide",false);
        this.setTitleText(true,"用户协议");
        if(hide) {
            this.setTitleLeftIcon(true,R.drawable.back_btn);
        }
        else {
            this.setTitleLeftIcon(false,R.drawable.back_btn);
        }
        String text = "<html><body><br />欢迎您与供鲜生商城经营者（详见定义条款）共同签署本《供鲜生商城平台服务协议》（下称“本协议“）并使用供鲜生商城平台服务！<br /><br />" +
                "您在申请注册流程时应当认真阅读本协议。请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款、法律适用和争议解决条款。<br /><br />" +
                "当您按照注册页面提示填写信息、阅读本协议且完成全部注册程序后，即表示您已充分阅读、理解并接受本协议的全部内容，并与供鲜生商城达成一致，成为供鲜生商城平台“用户”。阅读本协议的过程中，如果您不同意本协议或其中任何条款约定，您应立即停止注册程序。<br /><br />" +
                "<br />" +
                "<h2>一、定义</h2>" +
                "1.1供鲜生商城平台：指供鲜生APP客户端。 <br />" +
                "1.2供鲜生商城平台服务：供鲜生商城基于互联网，以包含供鲜生APP客户端在内的各种形态（包括未来技术发展出现的新的服务形态）向您提供各项服务。<br />" +
                "1.3供鲜生商城平台规则：包括在所有供鲜生商城平台规则频道内已经发布及后续发布的全部规则、实施细则、产品说明、公告等。<br />" +
                "<br />" +
                "<h2>二、协议范围</h2>" +
                "2.1签约主体<br />" +
                "本协议由您与供鲜生商城平台经营者共同缔结，本协议对您与供鲜生商城平台经营者均具有合同效力。 <br />" +
                "2.2补充协议<br />" +
                "由于互联网高速发展，您与供鲜生商城签署的本协议列明的条款并不能完整罗列并覆盖您与供鲜生商城所有权利与义务，现有的约定也不能保证完全符合未来发展的需求。因此，供鲜生商城平台退换货规则等均为本协议的补充协议，与本协议不可分割且具有同等法律效力。如您使用供鲜生商城平台服务，视为您同意上述补充协议。<br />" +
                "<br />" +
                "<h2>三、账户注册与使用</h2>" +
                "3.1用户资格<br />" +
                "您确认，在您开始注册程序使用供鲜生商城平台服务前，您应当具备中华人民共和国法律规定的与您行为相适应的民事行为能力。<br />" +
                "3.2账户说明<br />" +
                "当您按照注册页面提示填写信息、阅读并同意本协议且完成全部注册程序后，您可获得供鲜生商城平台账户并成为供鲜生商城平台用户。您有权使用您设置或确认的供鲜生会员名、邮箱、手机号码（以下简称“账户名称“）及您设置的密码（账户名称及密码合称“账户”）登录供鲜生平台。 <br />" +
                "3.3注册信息管理<br />" +
                "3.3.1真实合法<br />" +
                "在使用供鲜生平台服务时，您应当按供鲜生平台页面的提示准确完整地提供您的信息（包括您的姓名及电子邮件地址、联系电话、联系地址等），以便供鲜生与您联系。您了解并同意，您有义务保持您提供信息的真实性及有效性。 <br />" +
                "3.3.2账户安全规范<br />" +
                "您的账户为您自行设置并由您保管，账户因您主动泄露或遭受他人攻击、诈骗等行为导致的损失及后果，均由您自行承担。除供鲜生商城存在过错外，您应对您账户项下的所有行为结果（包括但不限于在线签署各类协议、发布信息、购买商品及服务、披露信息等）负责。<br />" +
                "<br />" +
                "<h2>四、商品的购买</h2>" +
                "4.1当您在供鲜生商城平台购买商品时，请您务必仔细确认所购商品的品名、价格、数量、型号、规格、尺寸或服务的时间、内容、限制性要求等重要事项，并在下单时核实您的联系地址、电话、收货人等信息。如您填写的收货人非您本人，则该收货人的行为和意思表示产生的法律后果均由您承担。 <br />" +
                "4.2 您的购买行为应当基于真实的消费需求，不得存在对商品实施恶意购买、恶意维权等扰乱供鲜生商城平台正常交易秩序的行为。基于维护供鲜生商城平台交易秩序及交易安全的需要，供鲜生商城发现上述情形时可主动执行关闭相关交易订单等操作。<br />" +
                "4.3 <b><font color=\"#6bb400\" font-weight:\"bold\">您在供鲜生商城平台订单交易信息是以电子化形式出具，具备法律效应。</font></b><br />" +
                "<br />" +
                "<h2>五、订单的取消 </h2>" +
                "5.1 您有权在下列情况下，取消订单：订单状态“待确认”时可以取消订单。<br />" +
                "5.2 供鲜生在下列情况下，可以与用户协商取消订单： ① 网站上显示的商品信息错误或缺货的； ② 用户订单信息明显错误。<br />" +
                "<br />" +
                "<h2>六、商品信息 </h2>" +
                "6.1 供鲜生商城网站上的商品信息随时有可能发生变动，供鲜生商城将尽所有合理必要的努力，使APP内展示的商品参数、说明、价格、库存等商品信息尽可能真实准确、全面详细。但受互联网技术发展水平等客观因素的限制，APP显示的信息可能会有一定的滞后性或差错，对此情形您充分理解并予以谅解。如您发现商品信息错误或有疑问的，请在第一时间告知供鲜生商城，并终止提交该订单。 <br />" +
                "6.2供鲜生将尽最大努力确保用户所购商品价格与APP公布的价格一致，尽管如此，由于价格设置、文字描述、配图等错误及不可预见的系统故障等情形，可能出现价格误差，从而导致订单因重大误解或显失公平而无法履行的情况。若出现上述情形，建议您直接联系供鲜生商城协商解决。 <br />" +
                "6.3供鲜生商城网站上显示的每一款商品的价格都包含法律规定的税金和配送费用。<br />" +
                "<br />" +
                "<h2>七、配送规则</h2>" +
                "7.1 供鲜生商城负责将订购商品在配送时间内配送到用户指定的收货地址。 <br />" +
                "7.2 因如下情况造成订单延迟或无法配送、交货等，供鲜生商城不承担延迟配送、交货的责任： <br />" +
                "您提供的信息错误、地址不详细等原因导致的； <br />" +
                "货物送达后无人签收，导致无法配送或延迟配送的； <br />" +
                "约定特殊时间段内送货的； <br />" +
                "不可抗力因素导致的，例如：自然灾害、交通拥堵、突发战争等；<br />" +
                "7.3 订单确认后，因用户原因订单地址错误临时修改收货地址的，司机不提供改址配送服务，需重新下单修改正确收货地址。<br />" +
                "<h2>八、用户信息的保护 </h2>" +
                "8.1您在供鲜生商城进行注册、浏览、下单购物、参加活动等行为时，涉及您的真实姓名／名称、通信地址、联系电话、电子邮箱、订单详情等信息的，供鲜生商城有权从完成交易、提供配送、售后及客户服务、开展活动、完成良好的用户体验等多种角度予以收集，并将对其中涉及个人隐私信息予以严格保密。 <br />" +
                "8.2 供鲜生商城保证不对外公开或向任何第三方提供您的个人信息，但是存在下列情形之一的除外： <br />" +
                "事先获得您的明确授权； <br />" +
                "系为履行您的订单或保护您的合法权利所必须； <br />" +
                "系为履行法律义务； <br />" +
                "本注册协议或其他条款另有约定。<br />" +
                "<br />" +
                "<h2>九、用户的违约 </h2>" +
                "9.1违约认定<br />" +
                "① 使用供鲜生商城平台服务时违反有关法律法规规定的； <br />" +
                "② 违反本协议或本协议补充协议约定的。为适应电子商务发展和满足海量用户对高效优质服务的需求，您理解并同意，供鲜生可在供鲜生商城平台规则中约定违约认定的程序和标准。如：供鲜生可依据您的用户数据与海量用户数据的关系来认定您是否构成违约；您有义务对您的数据异常现象进行充分举证和合理解释，否则将被认定为违约。<br />" +
                "<br />" +
                "<h2>十、争议解决</h2>" +
                "本协议的订立、执行和解释及争议的解决均应适用中华人民共和国的法律。如就本协议内容或其执行发生任何争议，则双方应首协商解决；协商不成的，任何一方均应向广州市越秀区人民法院提起诉讼。<br />" +
                "<br />" +
                "<h2>十一、其他 </h2>" +
                "11.1本协议构成双方对本协议之约定事项及其他有关事宜的完整协议，除本协议规定的之外，未赋予本协议各方其他权利。<br />" +
                "11.2本协议中的任何条款无论因何种原因被视为完全或部分无效或不具有执行力，该条应视为可分的，不影响本协议的任何其余条款的有效性、约束力及可执行性。</body></html>";
        context.setText(Html.fromHtml(text));
        if(hide) {
            nextBtn.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        }
        nextBtn.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    nextBtn.setEnabled(true);
                }
                else {
                    nextBtn.setEnabled(false);
                }
            }
        });
    }

    @OnClick({R.id.nextBtn,R.id.left_layout})
    public void doFinish(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                Object param = null;
                sendConnection("/api/user/agree_item_time",param,AGGRE_ITEM,true,null);
                break;
            case R.id.left_layout:
                if(hide) {
                    finish();
                }
                break;

        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case AGGRE_ITEM:
//                JPushInterface.setAliasAndTags(getApplicationContext(), CommonUtils.getDeviceId(this) , null, null);
                SPUtils.setLogin(mContext,true);
//                if (targerIntent != null) {
//                    startActivity(targerIntent);
//                }
                EventBus.getDefault().post(new UserLoginEvent());
                startActivity(new Intent(mContext, UserGuidActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
    }

//    @Override
//    public void onBackPressed() {
//        if (hide) {
//            finish();
//        }
//    }
}
