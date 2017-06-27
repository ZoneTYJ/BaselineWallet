package com.vfinworks.vfsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.vfinworks.vfsdk.activity.ActivityStackManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.AutonymActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.ChangePhoneActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.GestureSettingActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.NicknameActivity;
import com.vfinworks.vfsdk.activity.PeopleInfo.SafeCheckActivity;
import com.vfinworks.vfsdk.activity.assistant.SetPaymentPwdActivity;
import com.vfinworks.vfsdk.activity.core.AddWithdrawCardActivity;
import com.vfinworks.vfsdk.activity.core.BillActivity;
import com.vfinworks.vfsdk.activity.core.MyBankCardActivity;
import com.vfinworks.vfsdk.activity.core.PaymentActivity;
import com.vfinworks.vfsdk.activity.core.RechargeActivity;
import com.vfinworks.vfsdk.activity.core.TransferMethodSelectActivity;
import com.vfinworks.vfsdk.activity.core.WithdrawActivity;
import com.vfinworks.vfsdk.activity.core.channel.BaseChannel;
import com.vfinworks.vfsdk.activity.core.channel.ChannelMaps;
import com.vfinworks.vfsdk.activity.core.channel.OverageChannel;
import com.vfinworks.vfsdk.activity.login.LoadingActivity;
import com.vfinworks.vfsdk.business.DealAccept;
import com.vfinworks.vfsdk.business.GetBill;
import com.vfinworks.vfsdk.business.OrderAndPay;
import com.vfinworks.vfsdk.business.PlaceOrder;
import com.vfinworks.vfsdk.business.PlaceOrderPay;
import com.vfinworks.vfsdk.business.QueryMember;
import com.vfinworks.vfsdk.business.RealName;
import com.vfinworks.vfsdk.business.UnbindBankCard;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.SharedPreferenceUtil;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.AddWithdrawCardContext;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.context.BillContext;
import com.vfinworks.vfsdk.context.DealAcceptContext;
import com.vfinworks.vfsdk.context.PaymentContext;
import com.vfinworks.vfsdk.context.PlaceOrderContext;
import com.vfinworks.vfsdk.context.PlaceOrderPayContext;
import com.vfinworks.vfsdk.context.QueryMyBankCardContext;
import com.vfinworks.vfsdk.context.RealNameNumContext;
import com.vfinworks.vfsdk.context.RechargeContext;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.UnbindBankCardContext;
import com.vfinworks.vfsdk.context.WithdrawContext;
import com.vfinworks.vfsdk.enumtype.PaymentPwdEnum;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.enumtype.VFQueryBankTypeEnum;
import com.vfinworks.vfsdk.interfacemanager.SignInterface;
import com.vfinworks.vfsdk.interfacemanager.SignInterfaceManager;
import com.vfinworks.vfsdk.model.UserInfoModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.zxing.activity.ActivityGatheringQRCode;
import com.vfinworks.vfsdk.zxing.activity.ActivityGatheringScan;
import com.vfinworks.vfsdk.zxing.activity.ActivityGatheringScanSetAmount;
import com.vfinworks.vfsdk.zxing.activity.ActivityPaymentQRCodeGenerate;

public class SDKManager {
    private static int sdkVersion = BuildConfig.VERSION_CODE;

    public static int getSdkVersion() {
        return sdkVersion;
    }

    public static String token;
    private static SDKManager mInstance;
    //用于通知第3方的通知
    private Handler callbackHandler = null;

    public UserInfoModel getUserInfoModel() {
        return mUserInfoModel;
    }

    public void setUserInfoModel(UserInfoModel userInfoModel) {
        mUserInfoModel = userInfoModel;
    }

    private UserInfoModel mUserInfoModel;

    private SDKManager() {
    }

    @Deprecated
    public void init(Context context, String partnerId) {
        init(context, partnerId, "","");
    }

    /*
     * 初次调用需要调用初始化
     */
    public void init(Context context, String partnerId, String token,String phone) {
        this.token = token;
        Utils.getInstance().init(context.getApplicationContext());
        SharedPreferenceUtil.getInstance().init(context.getApplicationContext());
        if(!TextUtils.isEmpty(phone)){
            SharedPreferenceUtil.getInstance().setStringDataIntoSP("account",phone);
        }
        Config.PARTNER_ID = partnerId;
        ChannelMaps.getInstance().init(context.getApplicationContext());
        //		context.startActivity(new Intent(context, InitActivity.class));
    }


    public static synchronized SDKManager getInstance() {
        if (mInstance == null) {
            mInstance = new SDKManager();
        }
        return mInstance;
    }

    /**
     * 设置共同参数
     *
     * @param partner_id
     * @param app_id
     * @param url
     * @param staticResourceDir 一些静态资源的前缀。如快捷服务协议，其为staticResourceDir+"/pact_payment.html"
     */
    public void SetCommonParam(String partner_id, String app_id, String url, String
            staticResourceDir) {
        Config instance = Config.getInstance();
        instance.PARTNER_ID = partner_id;
        instance.APP_ID = app_id;
        instance.staticResourceDir = staticResourceDir;
        HttpRequsetUri.getInstance().updateDoUri(url);
    }

    /**
     * 清理回调
     */
    public void clear() {
        callbackHandler = null;
    }

    /**
     * 关闭sdk
     */
    public void close() {
        ActivityStackManager.getInstance().popAllActivity();
    }

    /**
     * 登录
     *
     * @param context
     * @param callbackHandler
     */
    public void Login(Context context, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        intent.setClass(context, LoadingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 创建付款码
     *
     * @param context
     * @param qrcode
     * @param number_pwd
     * @param seller
     * @param buyer
     * @param productName
     * @param amount
     * @param callbackHandler
     */
    public void PlaceOrderPay(Context context, String qrcode, String number_pwd, String seller,
							  String buyer, String productName, String amount, Handler
									  callbackHandler) {
        this.callbackHandler = callbackHandler;
        final PlaceOrderPayContext placeOrderContext = new PlaceOrderPayContext();
        placeOrderContext.setCallBackMessageId(0);
        String tradeNo = Utils.getOnlyValue();
        placeOrderContext.setRequestNo(tradeNo);
        placeOrderContext.setQrcode(qrcode);
        placeOrderContext.setNumber_pwd(number_pwd);
        placeOrderContext.setSeller(seller);
        placeOrderContext.setBuyer(buyer);
        placeOrderContext.setProductName(productName);
        placeOrderContext.setOrderAmount(amount);
        PlaceOrderPay placeOrderPay = new PlaceOrderPay(context, placeOrderContext, new
				PlaceOrderPay.PlaceOrderPayResponseHandler() {


            @Override
            public void onSuccess(Object responseBean, String responseString) {
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.OK.getCode());
                    result.setJsonData(responseString);
                    placeOrderContext.sendMessage(result);
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(statusCode);
                    result.setMessage(errorMsg);
                    placeOrderContext.sendMessage(result);
                }
            }
        });
        placeOrderPay.placeOrderPay();
    }

    /**
     * 查询会员信息
     *
     * @param context
     * @param baseContext
     * @param callbackHandler
     */
    public void QueryMember(Context context, BaseContext baseContext, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        QueryMember queryMember = new QueryMember(context, baseContext);
        queryMember.getMemberInfo();
    }

    /**
     * 充值
     *
     * @param context
     * @param rechargeContext
     * @param callbackHandler
     */
    public void Recharge(Context context, RechargeContext rechargeContext, Handler
            callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(rechargeContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", rechargeContext);
        intent.putExtras(bundle);
        intent.setClass(context, RechargeActivity.class);
        //        intent.setClass(context,RechargeActivityOld.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 查询银行卡
     *
     * @param context
     * @param token
     * @param memberId
     * @param queryType
     * @param callbackHandler
     */
    public void QueryMyBankCard(Context context, String token, String memberId,
                                VFQueryBankTypeEnum queryType, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        QueryMyBankCardContext mybankCardContext = new QueryMyBankCardContext();
        mybankCardContext.setQueryType(queryType);
        mybankCardContext.setToken(token);
        mybankCardContext.setMobile(memberId);
        setSDKtoken(mybankCardContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", mybankCardContext);
        intent.putExtras(bundle);
        intent.setClass(context, MyBankCardActivity.class);
        startActivityWithAnim(context, intent);
    }

    public void AddWithdrawBankCard(Context context, AddWithdrawCardContext baseContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", baseContext);
        intent.putExtras(bundle);
        intent.setClass(context, AddWithdrawCardActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 下单并支付
     *
     * @param activity          当前页的activity
     * @param placeOrderContext 下单的上下文
     */
    public void OrderAndPay(Context activity, PlaceOrderContext placeOrderContext, Handler
            callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(placeOrderContext);
        OrderAndPay orderAndPay = new OrderAndPay(activity, placeOrderContext);
        orderAndPay.orderAndPay();
    }

    public void UnbindBankCard(BaseActivity context, UnbindBankCardContext baseContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        UnbindBankCard unbindBankCard = new UnbindBankCard(context, baseContext);
        unbindBankCard.doUnbind();
    }

    /**
     * 提现
     *
     * @param context
     * @param withdrawContext
     * @param callbackHandler
     */
    public void Withdraw(Context context, WithdrawContext withdrawContext, Handler
            callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(withdrawContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", withdrawContext);
        intent.putExtras(bundle);
        intent.setClass(context, WithdrawActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 收银台支付
     *
     * @param context
     * @param paymentContext
     * @param callbackHandler
     */
    public void Payment(Context context, PaymentContext paymentContext, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(paymentContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", paymentContext);
        intent.putExtras(bundle);
        //        intent.setClass(context,PaymentActivityOld.class);
        intent.setClass(context, PaymentActivity.class);
        //startActivityWithAnim(context,intent);
        context.startActivity(intent);
    }

    /**
     * 下单
     *
     * @param context
     * @param placeOrderContext
     * @param callbackHandler
     */
    public void PlaceOrder(Context context, PlaceOrderContext placeOrderContext, Handler
            callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(placeOrderContext);
        PlaceOrder placeOrder = new PlaceOrder(context, placeOrderContext);
        placeOrder.doPlaceOrder();
    }

    /**
     * 获取账单列表
     *
     * @param context
     * @param billContext
     * @param callbackHandler
     */
    public void GetBillList(Context context, BillContext billContext, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(billContext);
        GetBill getBill = new GetBill(context, billContext);
        getBill.getBillList();
    }

    /**
     * 转账
     *
     * @param context
     * @param transferContext
     * @param callbackHandler
     */
    public void Transfer(Context context, TransferContext transferContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(transferContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", transferContext);
        intent.putExtras(bundle);
        intent.setClass(context, TransferMethodSelectActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 设置支付密码
     *
     * @param context
     * @param baseContext
     * @param callbackHandler
     */
    public void SetPayPassword(Context context, BaseContext baseContext, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", baseContext);
        bundle.putSerializable("type", PaymentPwdEnum.SETPASSWORD);
        intent.putExtras(bundle);
        intent.setClass(context, SetPaymentPwdActivity.class);
        startActivityWithAnim(context, intent);
    }

    //忘记支付密码
    public void ResetPayPassword(Context context, BaseContext baseContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", baseContext);
        //		bundle.putSerializable("type", PaymentPwdEnum.MODIFY_NEW_PASSWORD);
        intent.putExtras(bundle);
        intent.setClass(context, SafeCheckActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 修改支付密码
     *
     * @param context
     * @param baseContext
     * @param callbackHandler
     */
    public void ModifyPayPassword(Context context, BaseContext baseContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", baseContext);

        bundle.putSerializable("type", PaymentPwdEnum.MODIFY_VERIFY_PASSWORD);
        intent.putExtras(bundle);
        intent.setClass(context, SetPaymentPwdActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 生成二维码付款
     *
     * @param context
     * @param phone           手机号
     * @param callbackHandler
     */
    public void QRCodePayment(Context context, String phone, Handler callbackHandler,
							  SignInterface signInterface) {
        SignInterfaceManager.getInstance().setSignInterface(signInterface);
        this.callbackHandler = callbackHandler;
        Intent intent = new Intent();
        intent.putExtra("phone", phone);
        intent.setClass(context, ActivityPaymentQRCodeGenerate.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 扫二维码付款
     *
     * @param context
     * @param mobile          商家的手机号码
     * @param callbackHandler
     */
    public void QRScanPayment(Context context, String mobile, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        Intent intent = new Intent();
        intent.putExtra("mobile", mobile);
        intent.setClass(context, ActivityGatheringScan.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 扫二维码收款
     *
     * @param context
     * @param mobile          商家的手机号码
     * @param callbackHandler
     */
    public void QRScanGathering(Context context, String mobile, Handler callbackHandler,
                                SignInterface signInterface) {
        SignInterfaceManager.getInstance().setSignInterface(signInterface);
        this.callbackHandler = callbackHandler;
        Intent intent = new Intent();
        intent.putExtra("mobile", mobile);
        intent.setClass(context, ActivityGatheringScanSetAmount.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 生成二维码收款
     *
     * @param context
     * @param phone
     * @param callbackHandler
     */
    public void QRCodeGathering(Context context, String phone, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        Intent intent = new Intent();
        intent.putExtra("phone", phone);
        intent.setClass(context, ActivityGatheringQRCode.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 查询真实姓名
     *
     * @param context
     * @param baseContext
     * @param callbackHandler
     */
    public void QueryRealNameInfo(Context context, BaseContext baseContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        RealName realName = new RealName(context, baseContext);
        realName.doQuery();
    }

    /**
     * 做身份证号码实名认证
     *
     * @param context
     * @param realNameContext
     * @param callbackHandler
     */
    public void RealName(Context context, RealNameNumContext realNameContext, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(realNameContext);
        RealName realName = new RealName(context, realNameContext);
        realName.doRealName();
    }

    /**
     * 图片实名认证
     *
     * @param context
     * @param baseContext
     */
    public void PicRealName(Context context, BaseContext baseContext) {
        setSDKtoken(baseContext);
        Intent intent = new Intent(context, AutonymActivity.class);
        intent.putExtra("context", baseContext);
        startActivityWithAnim(context, intent);
    }

    /**
     * 修改手机号
     *
     * @param context
     * @param callbackHandler
     */
    public void ChangeMobile(Context context, BaseContext baseContext, Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent();
        intent.putExtra("context", baseContext);
        intent.setClass(context, ChangePhoneActivity.class);
        startActivityWithAnim(context, intent);
    }

    /**
     * 修改昵称
     *
     * @param context
     * @param nickName        旧昵称
     * @param callbackHandler
     */
    public void ModifyNickname(Context context, BaseContext baseContext, String nickName, Handler
			callbackHandler) {
        this.callbackHandler = callbackHandler;
        setSDKtoken(baseContext);
        Intent intent = new Intent(context, NicknameActivity.class);
        intent.putExtra("name", nickName);
        intent.putExtra("context", baseContext);
        startActivityWithAnim(context, intent);
    }

    /**
     * 交易列表
     *
     * @param context
     * @param billContext
     */
    public void TradeList(Context context, BillContext billContext) {
        setSDKtoken(billContext);
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", billContext);
        Intent intent = new Intent(context, BillActivity.class);
        intent.putExtras(bundle);
        startActivityWithAnim(context, intent);
    }

    public void ConfirmReceipt(Context context, final DealAcceptContext dealAcceptContext, Handler
            callbackHandler) {
        this.callbackHandler = callbackHandler;
        DealAccept dealAccept = new DealAccept(context, dealAcceptContext, new DealAccept.AcceeptResponseHandler() {
            @Override
            public void onSuccess(Object responseBean, String responseString) {
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.OK.getCode());
                    result.setJsonData(responseString);
                    dealAcceptContext.sendMessage(result);
                }
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                if (SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(statusCode);
                    result.setMessage(errorMsg);
                    dealAcceptContext.sendMessage(result);
                }
            }
        });
        dealAccept.doDealAccept();

    }

    /**
     * 手势操作，包括开启手势，修改手势，关闭手势
     *
     * @param context
     */
    public void VFGestureOperate(Context context) {
        Intent intent = new Intent(context, GestureSettingActivity.class);
        startActivityWithAnim(context, intent);
    }

    private void startActivityWithAnim(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.vf_sdk_tran_next_in, R.anim.vf_sdk_tran_next_out);
    }

    public Handler getCallbackHandler() {
        return callbackHandler;
    }

    public void setCallbackHandle(Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public void addChannel(BaseChannel baseChannel) {
        ChannelMaps.getInstance().addChannel(baseChannel.getChannelModel(), baseChannel);
    }
    public void clearChannel() {
        ChannelMaps.getInstance().clearChannel();
        ChannelMaps.getInstance().addChannel(OverageChannel.getChannel(),new OverageChannel());
    }

    private void setSDKtoken(BaseContext context) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(context.getToken())) {
            return;
        }
        token = context.getToken();
    }

}
