package com.example.bottomtest.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bottomtest.R;
import com.example.bottomtest.ui.User;
import com.example.bottomtest.ui.home.util.HttpUtil;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 写日记
 */

public class WriteDairyActivity extends TakePhotoActivity {

    private TextView curDate;
    private EditText titleEdit;
    private EditText diaryEdit;
    private Button done;
    private Button back;
    private Button pickImg;

    private Uri imageUri;
    private File file;
    public static int i=0;

    private DiaryContent diaryContent;



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_dairy);

        //隐藏原始的标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        curDate = findViewById(R.id.dateText);
        titleEdit = findViewById(R.id.title_edit);
        diaryEdit = findViewById(R.id.diary_edit);
        done = findViewById(R.id.edit_done);
        back = findViewById(R.id.back_button);
        pickImg = findViewById(R.id.add_diary_pickbtn);

        diaryContent = new DiaryContent();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        Date date = new Date(System.currentTimeMillis());
        final String dateFormat = formatter.format(date);
        curDate.setText(dateFormat.substring(0,11));
        diaryContent.date = dateFormat;

        verifyStoragePermissions(WriteDairyActivity.this);
        pickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(2).setWithOwnCrop(true).create();
                file = new File(Environment.getExternalStorageDirectory(), "/temp/" + dateFormat+i + ".jpg");
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                imageUri = Uri.fromFile(file);
                getTakePhoto().onPickFromGalleryWithCrop(imageUri,cropOptions);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleEdit.getText().toString().isEmpty()){
                    //内容为空
                    Toast.makeText(WriteDairyActivity.this, "请添加标题", Toast.LENGTH_SHORT).show();
                }else if(diaryEdit.getText().toString().isEmpty()){
                    Toast.makeText(WriteDairyActivity.this, "没有填写日记内容", Toast.LENGTH_SHORT).show();
                }else {
                    diaryContent.setTitle(titleEdit.getText().toString());
                    diaryContent.setContent(diaryEdit.getText().toString());
                    diaryContent.setCount(i);
                    diaryContent.save();
                    try {
                        uptoServer();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    Intent intent = new Intent(WriteDairyActivity.this, ShowDiaryActivity.class);
//                    startActivity(intent);
                    //finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!titleEdit.getText().toString().isEmpty()) || (diaryEdit.getText().toString().isEmpty())){
                    AlertDialog.Builder prompt = new AlertDialog.Builder(WriteDairyActivity.this);
                    prompt.setTitle("提示");
                    prompt.setMessage("已编辑文字但是未保存，是否继续退出？");
                    prompt.setCancelable(false);
                    prompt.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    prompt.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    prompt.show();
                }
            }
        });


    }

    public void uptoServer() throws UnsupportedEncodingException {

        String userid = load();
        //因为可能有[]这样的特殊字符，是tomcat设置的特殊字符，使用URL上传的时候需要进行编码
        String content = java.net.URLEncoder.encode(diaryContent.getContent(), "utf-8");
        String address = "http://47.113.95.141:8080/oneday/diary/add?insertdate=" + diaryContent.getDate() + "&userid=" + userid +
                "&imgcount=" + diaryContent.getCount() + "&title=" + diaryContent.getTitle() + "&content=" + content;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WriteDairyActivity.this, "上传失败，可以在个人中心手动上传", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finish();
            }
        });
    }

    public String load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("userid");
            reader = new BufferedReader(new InputStreamReader(in));
            content.append(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (content.toString().isEmpty()) {
            List<User> all = DataSupport.findAll(User.class);
            return all.get(0).getUserId();
        } else {
            return content.toString();
        }
    }


    @Override
    public void OnTakeSuccess(TResult result) {

        //成功取得照片
        Log.e("照片获取信息","takeSuccess: "+result.getImage().getOriginalPath());
        int start = diaryEdit.getSelectionStart();
        Editable text = diaryEdit.getText();
        text.insert(start, "[" + i + "]");
        SpannableString ss = new SpannableString(text);
        Uri img = Uri.fromFile(file);
        ImageSpan imageSpan = new ImageSpan(this, img);
        ss.setSpan(imageSpan,diaryEdit.getSelectionStart()-3,diaryEdit.getSelectionStart(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        i++;
        diaryEdit.setText(ss);
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
