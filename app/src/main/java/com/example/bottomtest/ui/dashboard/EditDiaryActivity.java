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
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;

/**
 * 编辑日记
 * 未完成：没修改的情况无法识别
 */
public class EditDiaryActivity extends TakePhotoActivity {

    private Button back;
    private Button done;
    private EditText title;
    private EditText massage;
    private TextView date;
    private Button pickImg;

    private Uri imageUri;
    private File file;
    public static int i;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        //隐藏原始的标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        back = findViewById(R.id.edit_back_button);
        done = findViewById(R.id.edit_edit_done);
        title = findViewById(R.id.edit_title_edit);
        massage = findViewById(R.id.edit_diary_edit);
        date = findViewById(R.id.edit_dateText);
        pickImg = findViewById(R.id.edit_diary_pickbtn);

        Intent intent = getIntent();
        final DiaryContent diaryContent= (DiaryContent)intent.getSerializableExtra("editDairy");

        title.setText(diaryContent.getTitle());

        i = diaryContent.getCount();

        String str=diaryContent.getContent();
        SpannableString spannableString = new SpannableString(str);
        for (int i = 0; i < str.length();i++) {
            String temp = "";

            if (str.charAt(i) == '[') {
                int j;
                for (j = i + 1; j < str.length(); j++) {
                    if (str.charAt(j) == ']') {
                        break;
                    }
                }
                temp = str.substring(i + 1, j);
                File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + diaryContent.getDate()+temp + ".jpg");
                Uri uri = Uri.fromFile(file);
                ImageSpan imageSpan = new ImageSpan(this, uri);
                spannableString.setSpan(imageSpan,i,j+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i = j + 1;
            }
        }
        massage.setText(spannableString);
        date.setText(diaryContent.getDate());


        verifyStoragePermissions(EditDiaryActivity.this);
        pickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(2).setWithOwnCrop(true).create();
                file = new File(Environment.getExternalStorageDirectory(), "/temp/" + diaryContent.getDate()+i + ".jpg");
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                imageUri = Uri.fromFile(file);
                getTakePhoto().onPickFromGalleryWithCrop(imageUri,cropOptions);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!title.getText().toString().isEmpty()) || (massage.getText().toString().isEmpty())){
                    AlertDialog.Builder prompt = new AlertDialog.Builder(EditDiaryActivity.this);
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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改当前date下的相关信息
                diaryContent.setContent(massage.getText().toString());
                diaryContent.setTitle(title.getText().toString());
                diaryContent.setCount(i);
                diaryContent.updateAll("date=?", diaryContent.getDate());
                //Intent intent1 = new Intent(EditDiaryActivity.this, ShowDiaryActivity.class);
                Toast.makeText(EditDiaryActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                //startActivity(intent1);
                finish();
            }
        });

    }


    @Override
    public void OnTakeSuccess(TResult result) {

        //成功取得照片
        Log.e("照片获取信息","takeSuccess: "+result.getImage().getOriginalPath());
        int start = massage.getSelectionStart();
        Editable text = massage.getText();
        text.insert(start, "[" + i + "]");
        SpannableString ss = new SpannableString(text);
        Uri img = Uri.fromFile(file);
        //SpannableString spannableString = new SpannableString("["+i+"]");
        ImageSpan imageSpan = new ImageSpan(this, img);
        ss.setSpan(imageSpan,massage.getSelectionStart()-3,massage.getSelectionStart(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //spannableString.setSpan(imageSpan, 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        i++;
        //massage.append(spannableString);

        massage.setText(ss);

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
