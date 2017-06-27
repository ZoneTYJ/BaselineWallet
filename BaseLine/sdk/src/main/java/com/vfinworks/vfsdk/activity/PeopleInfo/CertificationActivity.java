package com.vfinworks.vfsdk.activity.PeopleInfo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.SDKManager;
import com.vfinworks.vfsdk.common.Config;
import com.vfinworks.vfsdk.common.HttpRequsetUri;
import com.vfinworks.vfsdk.common.L;
import com.vfinworks.vfsdk.common.StringReplaceUtil;
import com.vfinworks.vfsdk.common.TException;
import com.vfinworks.vfsdk.common.TUriParse;
import com.vfinworks.vfsdk.common.Utils;
import com.vfinworks.vfsdk.context.BaseContext;
import com.vfinworks.vfsdk.enumtype.VFCallBackEnum;
import com.vfinworks.vfsdk.http.HttpUtils;
import com.vfinworks.vfsdk.http.RequestParams;
import com.vfinworks.vfsdk.http.UploadFile;
import com.vfinworks.vfsdk.http.VFinResponseHandler;
import com.vfinworks.vfsdk.model.CertificationModel;
import com.vfinworks.vfsdk.model.VFSDKResultModel;
import com.vfinworks.vfsdk.view.LoadingDialog;
import com.vfinworks.vfsdk.view.PicDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.qqtheme.framework.picker.DatePicker;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 身份证实名认证
 * Created by xiaoshengke on 2016/8/19.
 */
public class CertificationActivity extends Activity implements View.OnClickListener,
        PicDialog.ButtonsClickListener{
    private static final int RC_CAMERA_STATE = 110;
    private static final int REQUEST_TAKE_PHOTO = 1000;
    private static final int REQUEST_ALBUM_OK = 1001;
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private TextView tv_lab1;
    private TextView tv_certi_name;
    private TextView tv_lb2;
    private TextView tv_card_type;
    private TextView tv_lab3;
    private TextView tv_certi_number;
    private TextView tv_date;
    private CheckBox cb_time_live;
    private ImageView iv_certi_zheng;
    private TextView tv_tip_zheng;
    private ImageView iv_certi_fan;
    private ImageView iv_delete_zheng;
    private ImageView iv_delete_fan;
    private TextView tv_tip_fan;
    private Button btn_submit;
    private CertificationModel certificationModel;
    private String token;
    private boolean isZheng;    //是否点击正面上传图片
    private String picPath; //一次选图片操作对应图片的路径
    private PicDialog dialog;   //选图片对话框
    private String zhengPicUrl,fanPicUrl;   //正反面上传图片后得到的url地址
    private String zhengPic,fanPic;
    private String timeType;    //1代表永久，0代表选择的时间
    private Thread thread;
    private Calendar now = Calendar.getInstance();
    protected Dialog mLoadingDialog;
    private BaseContext baseContext;
    private String mCurrentPhotoPath;


    //处理上传结果
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            String response = msg.obj.toString();
            String url = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("memo")){
                    url = jsonObject.optString("memo");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(isZheng){
                zhengPicUrl = url;
            }else {
                fanPicUrl = url;
            }
            if(TextUtils.isEmpty(url)){
                showShortToast("上传失败");
            }else {
                if(isZheng) {
//                    ImageLoader.getInstance().displayImage("file:/" + picPath, iv_certi_zheng);
//                    Glide.with(CertificationActivity2.this).load(new File(picPath)).into(iv_certi_zheng);
                    showImage(iv_certi_zheng,zhengPic);
                }
                else {
//                    Glide.with(CertificationActivity2.this).load(new File(picPath)).into(iv_certi_fan);
                    showImage(iv_certi_fan,fanPic);
                }
            }
        }
    };

    private void showImage(ImageView imageView,String url) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(url, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 上传照片
     * @param filePath
     */
    private void uploadFile(final String filePath) {
        showProgress();
        StringBuilder url = new StringBuilder();
        url.append(HttpRequsetUri.getInstance().getMemberDoUri());
        url.append("?").append("service=image_upload").append("&_input_charset=utf-8")
                .append("&token=").append(token).append("&device_id=").append(Config.getInstance().getDeviceId())
                .append("&partner_id=").append(Config.getInstance().PARTNER_ID).append("&version=1.0");
        final String serviceUrl = url.toString();
        //由于是单选照片，所以只需取一个元素

        picPath = filePath;
        thread = new Thread("upload"){
            @Override
            public void run() {
                super.run();
                HashMap<String,String> params = new HashMap<>();
                HashMap<String,File> files = new HashMap<>();
                files.put("pic",new File((filePath)));
                String response = "";
                try {
                    response = UploadFile.uploadForm(params,"pic",new File(filePath),null,serviceUrl);
//                    response = UploadFile.post(serviceUrl,params,files);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(mHandler != null) {
                    Message message = mHandler.obtainMessage();
                    message.obj = response;
                    mHandler.sendMessage(message);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        L.e("activity create");
//        SDKManager.getInstance().init(this, Config.getInstance().PARTNER_ID);
        mLoadingDialog = new LoadingDialog(this);
        findViewById(R.id.layout_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title)).setText("实名认证");
        dialog = new PicDialog(this);
        dialog.setListener(this);
        certificationModel = (CertificationModel) getIntent().getSerializableExtra("data");
        token = SDKManager.token;
        bindViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("zhengPicUrl",zhengPicUrl);
        outState.putString("fanPicUrl",fanPicUrl);
        outState.putString("zhengPic",zhengPic);
        outState.putString("fanPic",fanPic);
        outState.putBoolean("isZheng",isZheng);
        if("1".equals(timeType))
            outState.putString("time",timeType);
        else
            outState.putString("time",tv_date.getText().toString());
        outState.putString("token",token);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        zhengPicUrl = savedInstanceState.getString("zhengPicUrl");
        fanPicUrl = savedInstanceState.getString("fanPicUrl");
        zhengPic = savedInstanceState.getString("zhengPic");
        fanPic = savedInstanceState.getString("fanPic");
        isZheng = savedInstanceState.getBoolean("isZheng");
        timeType = savedInstanceState.getString("time");
        token = savedInstanceState.getString("token");
        SDKManager.token = token;

        if(!TextUtils.isEmpty(zhengPic)){
            showImage(iv_certi_zheng,zhengPic);
        }
        if(!TextUtils.isEmpty(fanPicUrl)){
            showImage(iv_certi_fan,fanPic);
        }
        if(!"1".equals(timeType)){
            tv_date.setText(timeType);
        }

    }

    public void showProgress(){
        mLoadingDialog.show();
    }

    public void hideProgress(){
        mLoadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    private void bindViews() {
        tv_lab1 = (TextView) findViewById(R.id.tv_lab1);
        tv_certi_name = (TextView) findViewById(R.id.tv_certi_name);
        tv_lb2 = (TextView) findViewById(R.id.tv_lb2);
        tv_card_type = (TextView) findViewById(R.id.tv_card_type);
        tv_lab3 = (TextView) findViewById(R.id.tv_lab3);
        tv_certi_number = (TextView) findViewById(R.id.tv_certi_number);
        tv_date = (TextView) findViewById(R.id.tv_date);
        cb_time_live = (CheckBox) findViewById(R.id.cb_time_live);
        iv_certi_zheng = (ImageView) findViewById(R.id.iv_certi_zheng);
        tv_tip_zheng = (TextView) findViewById(R.id.tv_tip_zheng);
        iv_certi_fan = (ImageView) findViewById(R.id.iv_certi_fan);
        iv_delete_zheng = (ImageView) findViewById(R.id.iv_delete_zheng);
        iv_delete_fan = (ImageView) findViewById(R.id.iv_delete_fan);
        tv_tip_fan = (TextView) findViewById(R.id.tv_tip_fan);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        tv_tip_zheng.setText(Html.fromHtml("上面证件<font color='#ea8010'>正面</font>照片"));
        tv_tip_fan.setText(Html.fromHtml("上面证件<font color='#ea8010'>反面</font>照片"));
        tv_certi_name.setText(certificationModel.getReal_name());
        tv_certi_number.setText(StringReplaceUtil.getStarString(certificationModel.getCert_no(),4,certificationModel.getCert_no().length()-4));

        tv_date.setOnClickListener(this);
        iv_certi_zheng.setOnClickListener(this);
        iv_certi_fan.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        cb_time_live.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv_date.setTextColor(0xffcccccc);
                }else{
                    tv_date.setTextColor(0xff333333);
                }
            }
        });
        cb_time_live.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    timeType = "1";
                    tv_date.setText("");
                }else{
                    timeType = "0";
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == tv_date){
            Calendar now = Calendar.getInstance();
            DatePicker picker = new DatePicker(this);
            picker.setRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR)+50);
            picker.setAnimationStyle(R.style.Animation_CustomPopup);
            picker.setSelectedItem(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
            picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                @Override
                public void onDatePicked(String year, String month, String day) {
                    if(judgeBeforeNow(year,month,day)){
                        showShortToast("身份证有效期需大于今天");
                        return;
                    }
                    tv_date.setText(year + "-" + month + "-" + day);
                    cb_time_live.setChecked(false);
                }
            });
            picker.show();
        }else if(v == iv_certi_zheng){
            isZheng = true;
            dialog.show();
        }else if(v == iv_certi_fan){
            isZheng = false;
            dialog.show();
        }else if(v == btn_submit){
            updateData();
        }
    }

    /**
     * 判断选择日期是否在当前日期前
     * @param year
     * @param month
     * @param day
     * @return
     */
    private boolean judgeBeforeNow(String year, String month, String day) {
        Calendar temp = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH),0,0,0);
        temp.set(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),0,0,0);
        if(temp.compareTo(now) <= 0){
            return true;
        }
        return false;
    }

    /**
     * 显示duration为0的Toast
     */
    public void showShortToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    /**
     * 做实名认证
     */
    private void updateData() {
        if(timeType == null || "0".equals(timeType)) {
            if(TextUtils.isEmpty(tv_date.getText().toString())) {
                showShortToast("请选择证件有效期");
                return;
            }
        }
        if(TextUtils.isEmpty(zhengPicUrl)){
            showShortToast("请上传证件照正面");
            return;
        }
        if(TextUtils.isEmpty(fanPicUrl)){
            showShortToast("请上传证件照反面");
            return;
        }
        showProgress();
        RequestParams reqParam = new RequestParams();
        reqParam.putData("service", "do_certification");
        reqParam.putData("token", SDKManager.token);
        reqParam.putData("cert_type", "IC");
        reqParam.putData("cert_no", certificationModel.getCert_no());
        reqParam.putData("real_name", certificationModel.getReal_name());
        reqParam.putData("validateDate", tv_date.getText().toString());
        reqParam.putData("frontImageUrl", zhengPicUrl);
        reqParam.putData("backImageUrl", fanPicUrl);
        reqParam.putData("timeType", timeType);
        HttpUtils.getInstance(this).excuteHttpRequest(HttpRequsetUri.getInstance().getMemberDoUri(), reqParam, new VFinResponseHandler(){

            @Override
            public void onSuccess(Object responseBean, String responseString) {
                hideProgress();
                showShortToast("实名认证资料提交成功");
                if(SDKManager.getInstance().getCallbackHandler() != null) {
                    VFSDKResultModel result = new VFSDKResultModel();
                    result.setResultCode(VFCallBackEnum.OK.getCode());
                    result.setJsonData(responseString);
                    baseContext.sendMessage(result);
                    SDKManager.getInstance().setCallbackHandle(null);
                }
                Intent intent = new Intent();
                intent.putExtra("success",true);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onError(String statusCode, String errorMsg) {
                hideProgress();
                showShortToast(errorMsg);
            }

        }, this);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void capturePic() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
//            doCapture();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "获取相机权限以方便使用后续服务",
                    RC_CAMERA_STATE, Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String filePath = null;
            if(requestCode == REQUEST_TAKE_PHOTO){
                filePath = mCurrentPhotoPath;
            }
            else if(requestCode == REQUEST_ALBUM_OK){
                try {
                    filePath = TUriParse.getFilePathWithUri(data.getData(),this);
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
            saveBitmapFile(compressImage(BitmapFactory.decodeFile(filePath)));
            if(isZheng)
                uploadFile(zhengPic);
            else
                uploadFile(fanPic);
        }
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>300) {    //循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        image.recycle();
        return bitmap;
    }

    public void saveBitmapFile(Bitmap bitmap){
        String file = null;
        if(isZheng)
            zhengPic =file=getExternalCacheDir()+"/temp/" + "vf_z.jpg";
        else
            fanPic = file=getExternalCacheDir()+"/temp/"+"vf_f.jpg";
        File picFile = new File(getExternalCacheDir()+"/temp/");
        if(!picFile.exists()){
            picFile.mkdirs();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
    }


    @Override
    public void selectPic() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, REQUEST_ALBUM_OK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
