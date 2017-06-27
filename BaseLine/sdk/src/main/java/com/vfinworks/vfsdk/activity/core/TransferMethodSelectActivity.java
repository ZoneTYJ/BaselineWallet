package com.vfinworks.vfsdk.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.activity.BaseActivity;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.context.TransferContext;
import com.vfinworks.vfsdk.context.TransferToCardContext;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.VFinResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 转账
 */
public class TransferMethodSelectActivity extends BaseActivity implements OnClickListener {
    private BaseActivity mContext;
    private RelativeLayout layoutTransferAccount;
    private RelativeLayout layoutTransferCard;

    private TransferContext transferContext;
    private ListView lv_history;
    private HistoryAdapter mAdapter;
    private List<HistoryInfos> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        transferContext = (TransferContext) this.getIntent().getExtras().getSerializable("context");
        setContentView(R.layout.transfer_method_select_activity, FLAG_TITLE_LINEARLAYOUT);
        super.onCreate(savedInstanceState);
        this.getTitlebarView().setTitle("转账");
        this.getTitlebarView().initLeft(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backOnClick();
            }
        });
        getTransferInfos();
    }

    @Override
    public void initWidget() {
        layoutTransferAccount = (RelativeLayout) findViewById(R.id.layout_transfer_account);
        layoutTransferAccount.setOnClickListener(this);
        layoutTransferCard = (RelativeLayout) findViewById(R.id.layout_transfer_card);
        layoutTransferCard.setOnClickListener(this);
        lv_history = (ListView) findViewById(R.id.lv_history);
        mList = new ArrayList<>();
        mAdapter = new HistoryAdapter();
        lv_history.setAdapter(mAdapter);

        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryInfos bean = mList.get(position);
                if (bean.getType() == HistoryInfos.TOCARD_TYPE) {
                    transferContext.setMethod("card");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("context", transferContext);
                    bundle.putSerializable("TransferToCardContext", bean.getTransferToCardContext());
                    intent.putExtras(bundle);
                    intent.setClass(mContext, TransferToCardInputInfoActivity.class);
                    mContext.startActivity(intent);
                } else if (bean.getType() == HistoryInfos.ACCOUNT_TYPE) {
                    transferContext.setMethod("account");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("context", transferContext);
                    bundle.putString("account_number", bean.getDetailValue());
                    bundle.putString("nickname", bean.getName());
                    bundle.putString("realName", bean.getRealname());
                    intent.putExtras(bundle);
                    intent.setClass(mContext, TransferAccountInputInfoActivity.class);
                    mContext.startActivity(intent);

                }
            }
        });
    }


    private void getTransferInfos() {
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "query_transfer_contact");
        reqParam.putData("token", SDKManager.token);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(),
                reqParam, new VFinResponseHandler() {

                    @Override
                    public void onSuccess(Object responseBean, String responseString) {
                        hideProgress();
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            if (!jsonObject.isNull("contactAccountInfo")) {
                                JSONArray jary = jsonObject.getJSONArray("contactAccountInfo");
                                for (int i = 0; i < jary.length(); i++) {
                                    HistoryInfos historyInfos = new HistoryInfos();
                                    historyInfos.setType(HistoryInfos.ACCOUNT_TYPE);
                                    JSONObject keySets = (JSONObject) jary.get(i);
                                    for (Iterator it = keySets.keys(); it.hasNext(); ) {
                                        String id = (String) it.next();
                                        historyInfos.setDetailValue(id);
                                        String strs[] = keySets.getString(id).split("\\,");
                                        historyInfos.setName(strs[0]);
                                        historyInfos.setRealname(strs[1]);
                                        historyInfos.setDetailValue(id);
                                    }
                                    mList.add(historyInfos);
                                }
                            }
                            if (!jsonObject.isNull("contactCardInfo")) {
                                JSONArray jary = jsonObject.getJSONArray("contactCardInfo");
                                for (int i = 0; i < jary.length(); i++) {
                                    HistoryInfos historyInfos = new HistoryInfos();
                                    historyInfos.setType(HistoryInfos.TOCARD_TYPE);
                                    JSONObject keySets = (JSONObject) jary.get(i);
                                    for (Iterator it = keySets.keys(); it.hasNext(); ) {
                                        String id = (String) it.next();
                                        JSONObject transferObject = keySets.getJSONObject(id);
                                        Gson gson = new Gson();
                                        TransferToCardContext transferToCardContext = gson
                                                .fromJson(transferObject.toString(),
                                                        TransferToCardContext.class);
                                        historyInfos.setName(transferToCardContext
                                                .getAccount_name());
                                        String no = transferToCardContext.getBank_account_no();
                                        String value = transferToCardContext.getBank_name() + '('
                                                + no.substring(no.length() - 4) + ')';
                                        historyInfos.setTransferToCardContext
                                                (transferToCardContext);
                                        historyInfos.setDetailValue(value);
                                    }
                                    mList.add(historyInfos);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String statusCode, String errorMsg) {
                        hideProgress();
                        showShortToast(errorMsg);
                    }

                }, this);
    }


    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.layout_transfer_account) {
            transferAccountClick();
        } else if (arg0.getId() == R.id.layout_transfer_card) {
            transferCardClick();
        }
    }

    private void transferAccountClick() {
        transferContext.setMethod("account");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", transferContext);
        intent.putExtras(bundle);
        intent.setClass(this, TransferAccountInputAccountActivity.class);
        this.startActivity(intent);
    }

    private void transferCardClick() {
        transferContext.setMethod("card");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", transferContext);
        intent.putExtras(bundle);
        intent.setClass(this, TransferToCardActivity.class);
        this.startActivity(intent);
    }

    private void backOnClick() {
        this.finishAll();
    }

    class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoryInfos bean = mList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.list_transfer_history_item, null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (bean.getType() == HistoryInfos.ACCOUNT_TYPE) {
                viewHolder.iv_icon.setImageResource(R.drawable.vf_qb_icon);

            } else if (bean.getType() == HistoryInfos.TOCARD_TYPE) {
                viewHolder.iv_icon.setImageResource(R.drawable.vf_card_icon02);
            }
            viewHolder.tv_name.setText(bean.getName());
            viewHolder.tv_value.setText(bean.getDetailValue());
            return convertView;
        }
    }

    class ViewHolder {
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_value;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backOnClick();
        }
        return super.onKeyDown(keyCode, event);
    }


    class HistoryInfos {
        public static final int ACCOUNT_TYPE = 0;
        public static final int TOCARD_TYPE = 1;
        private int mType;
        private String name;
        private String realname;
        private String detailValue;

        private TransferToCardContext transferToCardContext;

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            mType = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetailValue() {
            return detailValue;
        }

        public void setDetailValue(String detailValue) {
            this.detailValue = detailValue;
        }

        public TransferToCardContext getTransferToCardContext() {
            return transferToCardContext;
        }

        public void setTransferToCardContext(TransferToCardContext transferToCardContext) {
            this.transferToCardContext = transferToCardContext;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }
    }
}
