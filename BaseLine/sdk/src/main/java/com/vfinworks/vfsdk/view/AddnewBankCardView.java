package com.vfinworks.vfsdk.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.activity.core.SelectBankActivity;
import com.vfinworks.vfsdk.activity.core.SelectBranchActivity;
import com.vfinworks.vfsdk.activity.core.SelectCityActivity;
import com.vfinworks.vfsdk.activity.core.SelectProvinceActivity;
import com.vfinworks.vfsdk.model.AddNewBankModel;

/**
 * Created by tangyijian on 2016/8/26.
 */
public class AddnewBankCardView extends LinearLayout implements View.OnClickListener {
    public static final int BACK_CODE = 201;
    private BaseActivity mContext;
    private RelativeLayout layout_bank;
    private RelativeLayout layout_province;
    private RelativeLayout layout_city;
    private RelativeLayout layout_subbranch;
    private TextView tv_bank;
    private TextView tv_province;
    private TextView tv_city;
    private TextView tv_subbranch;
    private String bankName;
    private String provinceName;
    private String cityName;
    private String subbranchName;
    private AddNewBankModel addNewBankModel;
    private VFCardNumberEditText et_card;


    public AddnewBankCardView(Context context) {
        super(context);
        initView(context);
    }

    public AddnewBankCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddnewBankCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setToken(String token){
        if(addNewBankModel!=null){
            addNewBankModel.setToken(token);
        }
    }

    private void initView(Context context) {
        mContext= (BaseActivity) context;
        View view=View.inflate(context,R.layout.view_addnew_bankcard,this);
        et_card = (VFCardNumberEditText)  view.findViewById(R.id.et_card);
        layout_bank = (RelativeLayout)  view.findViewById(R.id.layout_bank);
        layout_province = (RelativeLayout) view. findViewById(R.id.layout_province);
        layout_city = (RelativeLayout)  view.findViewById(R.id.layout_city);
        layout_subbranch = (RelativeLayout)  view.findViewById(R.id.layout_subbranch);
        tv_bank = (TextView) view.findViewById(R.id.tv_bank);
        tv_province = (TextView) view.findViewById(R.id.tv_province);
        tv_city = (TextView) view.findViewById(R.id.tv_city);
        tv_subbranch = (TextView) view.findViewById(R.id.tv_subbranch);
        layout_bank.setOnClickListener(this);
        layout_province.setOnClickListener(this);
        layout_city.setOnClickListener(this);
        layout_subbranch.setOnClickListener(this);
        addNewBankModel = new AddNewBankModel();
    }

    @Override
    public void onClick(View v) {
        if(v==layout_bank){
            if(addNewBankModel.getType() >= AddNewBankModel.CITY_TYPE) {
                Intent intent = new Intent(mContext,SelectBankActivity.class);
                intent.putExtra("addNewBankModel", addNewBankModel);
                mContext.startActivityForResult(intent, BACK_CODE);
            }else{
                mContext.showShortToast("请选择城市!");
            }
        }else if(v==layout_province){
            Intent intent = new Intent(mContext,SelectProvinceActivity.class);
            intent.putExtra("addNewBankModel",addNewBankModel);
            mContext.startActivityForResult(intent, BACK_CODE);
        } else if (v == layout_city){
            if(addNewBankModel.getType() >= AddNewBankModel.PROVINCE_TYPE) {
                Intent intent = new Intent(mContext,SelectCityActivity.class);
                intent.putExtra("addNewBankModel", addNewBankModel);
                mContext.startActivityForResult(intent, BACK_CODE);
            }else{
                mContext.showShortToast("请选择省市!");
            }
        }else if(v==layout_subbranch){
            if(addNewBankModel.getType() >= AddNewBankModel.BANK_TYPE) {
                Intent intent = new Intent(mContext,SelectBranchActivity.class);
                intent.putExtra("addNewBankModel",addNewBankModel);
                mContext.startActivityForResult(intent, BACK_CODE);
            }else{
                mContext.showShortToast("请选择银行!");
            }
        }
    }

    public void setActivityBack(AddNewBankModel newbankmodel){
        addNewBankModel=newbankmodel;
        if(addNewBankModel.getType()==AddNewBankModel.PROVINCE_TYPE){
            tv_province.setText(addNewBankModel.getProvinceInfoModel().getProvName());
            tv_city.setText("");
            tv_bank.setText("");
            tv_subbranch.setText("");
        }else if(addNewBankModel.getType()==AddNewBankModel.CITY_TYPE){
            tv_city.setText(addNewBankModel.getCityInfoModel().getCityName());
            tv_bank.setText("");
            tv_subbranch.setText("");
        }else if(addNewBankModel.getType()==AddNewBankModel.BANK_TYPE){
            tv_bank.setText(addNewBankModel.getBankModel().getBankName());
            tv_subbranch.setText("");
        }else if(addNewBankModel.getType()==AddNewBankModel.BRANCH_TYPE){
            tv_subbranch.setText(addNewBankModel.getBranchInfoModel().getName());
        }
    }

    public String getCardNumber() {
        return et_card.getText().toString().trim().replace(" ", "");
    }
}
