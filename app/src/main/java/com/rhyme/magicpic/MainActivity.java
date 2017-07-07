package com.rhyme.magicpic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rhyme.magicpic.adapter.MainAdapter;
import com.rhyme.magicpic.entity.Main_Pic;
import com.rhyme.magicpic.util.SQLiteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemLongClickListener {

    private GridView main_gv;
    private Spinner main_spinner;

    //相册
    private final int  RESULE_IMAGE=100;
    private final String IMAGE_TYPE="image/*";

    //拍照
    private final int RESULE_CAMERA=200;
    private String path= Environment.getExternalStorageDirectory().getPath()+"/magicPic/";
    private long time;

    private List<Main_Pic> main_pics;

    private MainAdapter mainadapter;

    private String difficute="2X2";

    private int selectPosition=0;
    private AlertDialog.Builder dialog_builder;
    private AlertDialog dialog;

    private SQLiteUtil sqLiteUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPermission();
        initDate();
    }

    private void initPermission() {
        List<String> premissions=new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            premissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
////申请权限
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//            premissions.add(Manifest.permission.READ_PHONE_STATE);
////申请权限
//        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            premissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//申请权限
        }
        if (!premissions.isEmpty()){
            String[] pms=premissions.toArray(new String[premissions.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,pms,1);
//权限请求
        }else {

        }
    }

    private void initDate() {
        main_pics=new ArrayList<>();
        Main_Pic mp=new Main_Pic();
        mp.setResource(R.mipmap.pic1);
        main_pics.add(mp);
        mp=new Main_Pic();
        mp.setResource(R.mipmap.pic2);
        main_pics.add(mp);
        mp=new Main_Pic();
        mp.setResource(R.mipmap.pic3);
        main_pics.add(mp);
        mp=new Main_Pic();
        mp.setResource(R.mipmap.pic4);
        main_pics.add(mp);

        sqLiteUtil=SQLiteUtil.InstanceDataBase(this);
        main_pics.addAll(sqLiteUtil.selectData());
        mainadapter=new MainAdapter(this,main_pics);
        main_gv.setAdapter(mainadapter);
    }

    private void initView() {
        findViewById(R.id.camera).setOnClickListener(this);
        findViewById(R.id.pictures).setOnClickListener(this);
        main_gv = (GridView) findViewById(R.id.main_gv);
        main_spinner = (Spinner) findViewById(R.id.main_spinner);
        main_spinner.setOnItemSelectedListener(this);
        main_gv.setOnItemClickListener(this);
        main_gv.setOnItemLongClickListener(this);

        dialog_builder=new AlertDialog.Builder(this);
        dialog_builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File file=new File(main_pics.get(selectPosition).getPath());
                if (file.exists()){
                    file.delete();
                }
                sqLiteUtil.delete(main_pics.get(selectPosition).getPath());
                main_pics.remove(selectPosition);
                mainadapter.notifyDataSetChanged();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sqLiteUtil.delete(main_pics.get(selectPosition).getPath());
                main_pics.remove(selectPosition);
                mainadapter.notifyDataSetChanged();
            }
        }).setMessage(R.string.delete_tip);
        dialog=dialog_builder.create();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(this,PlayActivity.class);
        intent.putExtra("info",main_pics.get(i));
        intent.putExtra("difficute",difficute);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Main_Pic main_pic=main_pics.get(i);
        if (main_pic.getPath()==null){
//            main_pics.remove(i);
//            mainadapter.notifyDataSetChanged();
        }else {
           selectPosition=i;
            if (!dialog.isShowing()){
                dialog.show();
            }
        }
        return true;
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String[] difficutes=getResources().getStringArray(R.array.difficutes);
        difficute=difficutes[i];
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pictures:
                Intent intent=new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_TYPE);
                startActivityForResult(intent,RESULE_IMAGE);
                break;
            case R.id.camera:
                File file=new File(path);
                if (!file.exists()){
                    file.mkdir();
                }
                Intent intent2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE,null);
                time=System.currentTimeMillis();
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path,time+".png")));
                startActivityForResult(intent2,RESULE_CAMERA);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULE_IMAGE&&data!=null){
            Cursor cursor=this.getContentResolver().query(data.getData(),null,null,null,null);
            if (cursor != null) {
                cursor.moveToFirst();
                String imagePath=cursor.getString(cursor.getColumnIndex("_data"));
                Main_Pic mp=new Main_Pic();
                mp.setPath(imagePath);
                if (Pic_exists(mp)){
                    Toast.makeText(this,"图片已存在",Toast.LENGTH_SHORT).show();
                }else {
                    sqLiteUtil.addData(mp);
                    main_pics.add(mp);
                }
                cursor.close();
            }else {
                Main_Pic mp=new Main_Pic();
                mp.setPath(data.getData().getPath());
                if (Pic_exists(mp)){
                    Toast.makeText(this,"图片已存在",Toast.LENGTH_SHORT).show();
                }else {
                    sqLiteUtil.addData(mp);
                    main_pics.add(mp);
                }
            }
        }else if (requestCode==RESULE_CAMERA){
            Main_Pic mp=new Main_Pic();
            if (new File(path+time+".png").exists()){
                mp.setPath(path+ time+".png");
                if (Pic_exists(mp)){
                    Toast.makeText(this,"图片已存在",Toast.LENGTH_SHORT).show();
                }else {
                    sqLiteUtil.addData(mp);
                    main_pics.add(mp);
                }
            }
        }
        mainadapter.notifyDataSetChanged();
    }

    private boolean Pic_exists(Main_Pic mp) {
        for (Main_Pic mic:main_pics){
            if (mic.getPath()!=null&&mp.getPath().equals(mic.getPath())){
                return true;
            }
        }
        return false;
    }

}
