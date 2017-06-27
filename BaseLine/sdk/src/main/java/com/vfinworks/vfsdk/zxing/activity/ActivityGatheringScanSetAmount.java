package com.vfinworks.vfsdk.zxing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.TradeResultActivity;
import com.vfinworks.vfsdk.authenticator.AuthenMain;
import com.vfinworks.vfsdk.business.PlaceOrderPay;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.PlaceOrderPayContext;
import com.vfinworks.vfsdk.view.TipsDialog;

import org.json.JSONException;

import java.math.BigDecimal;

import static com.vfinworks.vfsdk.R.id.et_subject;

/**
 * Created by tangyijian  收款扫设定金额
 * 扫一扫收款
 */
public class ActivityGatheringScanSetAmount extends BaseActivity implements OnClickListener {
    private EditText etAmount;
    private Button btnSubmit;
    /**
     * 二维码扫描intent code
     */
    public final static int SCANNIN_GATHERING_CODE = 100;
    private int placeOrdercallbackMessageID = 1;
    private String mobile; //商家号码

    private String mResult;
    private AuthenMain mAuthenMain;
    private String mStrAmount;
    private EditText etSubject;
    private PlaceOrderPayContext mPlaceOrderPayContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gathering_scan_set_amount, FLAG_TITLE_LINEARLAYOUT);
        mobile = getIntent().getStringExtra("mobile");
        mAuthenMain = new AuthenMain(this);
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("收款");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
    }

    @Override
    public void initWidget() {
        etAmount = (EditText) this.findViewById(R.id.et_amount);
        etSubject = (EditText) this.findViewById(et_subject);
        btnSubmit = (Button) this.findViewById(R.id.btn_confirm);
        btnSubmit.setOnClickListener(this);
        initEditTextAmount();
    }

    private void initEditTextAmount() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSubmit.setEnabled(etAmount.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String charText = s.toString();
                // 第一位不允许输入空格
                if (charText != null && charText.startsWith(" ")
                        || charText.startsWith(".")) {
                    s.delete(0, 1);
                    return;
                }
                // 最后一位不允许输入空格 并且限制长度为12位
                if((charText != null && charText.endsWith(" ")) ||(charText.length()>12)) {
                    s.delete(charText.length() - 1, charText.length());
                    return;
                }
                // 如果输入的是数字,只允许输入小数点后两位
                int posDot = charText.indexOf(".");
                if (posDot <= 0)
                    return;
                if (charText.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
            submitClick();
        }
    }

    private void submitClick() {
        //检查金额输入
        mStrAmount = etAmount.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mStrAmount);
        BigDecimal zero = new BigDecimal(0.00);
        if (amount.compareTo(zero) <= 0) {
            showShortToast("请输入正确的收款金额!");
            return;
        }
        startCapture();
    }

    private void startCapture() {
        Intent intent = new Intent();
        intent.setClass(this, MipcaActivityCapture.class);
        intent.putExtra("title","收款");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivityForResult(intent, SCANNIN_GATHERING_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GATHERING_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mResult = bundle.getString("result");
                    scanResultPro(mResult);
                }
                break;
        }
    }

    private void scanResultPro(String result) {
        try {
            scanResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
            showDiatips("该二维码/条形码不是正规的付款码");
        }
    }

    private void showDiatips(String str) {
        TipsDialog tipsDialog = new TipsDialog(this);
        tipsDialog.setOnCompleteListener(new TipsDialog.OnCompleteListener() {
            @Override
            public void onCancel() {
                startCapture();
            }
        });
        tipsDialog.setContent(str).show();
    }


    /**
     * 获取信息,下订单
     *
     * @param result
     * @return
     * @throws JSONException
     */
    private void scanResult(String result) throws JSONException {
//        JSONObject object = new JSONObject(result);
//        String code=object.optString("code");
        String code=result;
        if (TextUtils.isEmpty(code) || !TextUtils.isDigitsOnly(code)) {
            showDiatips("该二维码/条形码不是正规的付款码");
            return;
        }
        String[] strs=mAuthenMain.decodeQRCode(code);
        String buyerPhone=strs[0];
        String number_pwd=strs[1];
        String productName= etSubject.getText().toString().trim();

        mPlaceOrderPayContext = new PlaceOrderPayContext();
        mPlaceOrderPayContext.setRequestNo(Utils.getOnlyValue());
        mPlaceOrderPayContext.setQrcode(code);
        mPlaceOrderPayContext.setNumber_pwd(number_pwd);
        mPlaceOrderPayContext.setSeller(mobile);
        mPlaceOrderPayContext.setBuyer(buyerPhone);
        mPlaceOrderPayContext.setProductName(productName);
        mPlaceOrderPayContext.setOrderAmount(mStrAmount);
        PlaceOrderPay placeOrderPay=new PlaceOrderPay(this, mPlaceOrderPayContext, new PlaceOrderPay.PlaceOrderPayResponseHandler() {
            @Override
            public void onSuccess(Object responseBean, String responseString) {
                startSuccessAct();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                showShortToast(errorMsg);
            }
        });
        placeOrderPay.placeOrderPay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backOnClick() {
        this.finishAll();
    }

    public void startSuccessAct() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", mPlaceOrderPayContext);
        bundle.putSerializable("statue", TradeResultActivity.SUCCESS);
        intent.putExtras(bundle);
        intent.setClass(this, TradeResultActivity.class);
        startActivity(intent);
        finishAll();
    }

}
