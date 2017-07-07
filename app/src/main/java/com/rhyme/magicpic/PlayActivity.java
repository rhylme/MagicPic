package com.rhyme.magicpic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rhyme.magicpic.adapter.PlayAdapter;
import com.rhyme.magicpic.entity.BitmapBean;
import com.rhyme.magicpic.entity.Main_Pic;
import com.rhyme.magicpic.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhyme on 2017/7/6.
 */

public class PlayActivity extends AppCompatActivity implements Animator.AnimatorListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "PlayActivity";
    private String difficute;
    private Main_Pic main_pic;
    private TextView play_time;
    private TextView play_step;
    private LinearLayout play_top;
    private GridView play_gv;
    private ImageView play_image;

    private PlayAdapter playadapter;
    private List<BitmapBean> bitmapBeanList;

    private Bitmap bitmap;
    private int time;
    private int step;
    private boolean isplay = true;
    private boolean isSuccess = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    initDate();
                    if (isSuccess()) {
                        isplay = false;
                        isSuccess = true;
                        bitmapBeanList.get(bitmapBeanList.size() - 1).setBitmap(BitmapUtil.lastBitmap);
                        playadapter.notifyDataSetChanged();
                        Toast.makeText(PlayActivity.this, "成功啦!你太棒了", Toast.LENGTH_SHORT).show();
                    }
                    if (alertDialog!=null&&alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    break;
                case 1:
                    play_step.setText("步数:" + step);
                    play_time.setText("时间:" + time);
                    isplay = true;
                    playadapter.notifyDataSetChanged();
                    break;
                case 2:
                    play_top.animate().translationZ(10).setDuration(1000).setListener(PlayActivity.this).start();
                    break;
                case 3:
                    Toast.makeText(PlayActivity.this, "生成拼图失败,图片过大", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (main_pic.getPath() == null) {
                        play_image.setImageResource(main_pic.getResource());//设置原图图片
                    } else {
                        play_image.setImageBitmap(BitmapUtil.caculateBitmap(PlayActivity.this, main_pic.getPath(), 320, 500));//设置原图图片
                    }
                    break;
                case 5:
                    try {
                        Palette.Swatch vi = (Palette.Swatch) msg.obj;
                        ValueAnimator valueAnimator=ValueAnimator.ofArgb(getResources().getColor(R.color.colorPrimary),vi.getRgb());
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int value= (int) valueAnimator.getAnimatedValue();
                                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(value));
                                Window window = getWindow();
                                window.setStatusBarColor(value);
                                reset.setBackground(new ColorDrawable(value));
                                play_bitmap.setBackground(new ColorDrawable(value));
                            }
                        });
                        valueAnimator.setDuration(1000);
                        valueAnimator.start();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private TextView play_bitmap;
    private TextView reset;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        difficute = getIntent().getStringExtra("difficute");
        main_pic = (Main_Pic) getIntent().getSerializableExtra("info");
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (main_pic.getPath() == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), main_pic.getResource());
                } else {
                    bitmap = BitmapFactory.decodeFile(main_pic.getPath());
                }
                handler.sendEmptyMessage(4);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        //将颜色设置给相应控件
                        Palette.Swatch vi = palette.getMutedSwatch();
                        Message msg = handler.obtainMessage();
                        msg.what = 5;
                        msg.obj = vi;
                        handler.sendMessage(msg);
                    }
                });
                bitmapBeanList = BitmapUtil.createList(difficute, bitmap);
                if (bitmapBeanList == null) {
                    handler.sendEmptyMessage(3);
                    return;
                }
                getPuzzleGenerator();
            }
        }).start();

    }


    private void initDate() {
        play_step.setText("步数:" + step);
        if (playadapter == null) {
            playadapter = new PlayAdapter(this, bitmapBeanList);
            play_gv.setAdapter(playadapter);
        } else {
            playadapter.notifyDataSetChanged();
        }

    }

    private void initView() {
        play_time = (TextView) findViewById(R.id.play_time);
        play_step = (TextView) findViewById(R.id.play_step);
        play_top = (LinearLayout) findViewById(R.id.play_top);
        play_gv = (GridView) findViewById(R.id.play_gv);
        play_gv.setNumColumns((int) Math.sqrt(BitmapUtil.Difficute(difficute)));

        play_gv.setOnItemClickListener(this);
        play_image = (ImageView) findViewById(R.id.play_image);
        play_bitmap = (TextView) findViewById(R.id.play_bitmap);
        play_bitmap.setOnClickListener(this);
        reset = (TextView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        alertDialog=new AlertDialog.Builder(this).setMessage("拼图生成中...").create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onAnimationStart(Animator animator) {
        play_time.setText("时间:" + time);
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (!isSuccess) {
            if (isplay) {
                time++;
                if (play_top.getTranslationZ() == 10) {
                    play_top.animate().translationZ(0).setDuration(1000).setListener(this).start();
                } else {
                    play_top.animate().translationZ(10).setDuration(1000).setListener(this).start();
                }
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        for (int i = 0; i < bitmapBeanList.size(); i++) {
            bitmapBeanList.get(i).getBitmap().recycle();
        }
        bitmapBeanList.clear();
        bitmapBeanList = null;
    }

    private void getPuzzleGenerator() {
        int index = 0;
        for (int i = 0; i < bitmapBeanList.size(); i++) {
            index = (int) (Math.random() * bitmapBeanList.size());
            swapItems(bitmapBeanList.get(index), BitmapUtil.blank);
        }
        List<Integer> data = new ArrayList<>();

        for (int i = 0; i < bitmapBeanList.size(); i++) {
            data.add(bitmapBeanList.get(i).getId());
        }
        if (CanPlay(data)) {
            // TODO: 可以开始玩游戏了
            handler.sendEmptyMessage(0);
            return;
        } else {
            getPuzzleGenerator();
        }
    }


    /**
     * 两项进行切换
     */
    private void swapItems(BitmapBean from, BitmapBean blank) {
        BitmapBean bitmapBean = new BitmapBean();
        //交换bitmapId
        bitmapBean.setFirstId(from.getFirstId());
        from.setFirstId(blank.getFirstId());
        blank.setFirstId(bitmapBean.getFirstId());

        //交换bitmap
        bitmapBean.setBitmap(from.getBitmap());
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(bitmapBean.getBitmap());

        BitmapUtil.blank = from;
    }

    /**
     * 是否可玩
     */
    private boolean CanPlay(List<Integer> data) {
        int blankId = BitmapUtil.blank.getId();
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            //从下往上数,空格位于奇数行
            if (((blankId - 1) / Math.sqrt(bitmapBeanList.size()) % 2 == 1)) {
                return getInversions(data) % 2 == 0;
            } else {
                //从下往上数,空格位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     */
    private int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionsCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionsCount++;
                }
            }
            inversions += inversionsCount;
            inversionsCount = 0;
        }
        return inversions;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isplay) {
            if (CanMove(i)) {
                step++;
                swapItems(bitmapBeanList.get(i), BitmapUtil.blank);
                handler.sendEmptyMessage(0);
            }
        }
    }

    /**
     * 是否可以移动  白块 int blankId=BitmapUtil.blank.getId()-1;
     * 点击的块 position
     */
    private boolean CanMove(int position) {
        int type = (int) Math.sqrt(bitmapBeanList.size());
        //获取空格item的id
        int blankId = BitmapUtil.blank.getId() - 1;
        //不同行相差为type
        if (Math.abs(blankId - position) == type) {
            return true;
        }
        //相同行相差为1
        if ((blankId / type == position / type) && Math.abs(blankId - position) == 1) {
            return true;
        }
        return false;
    }

    private boolean isSuccess() {
        for (BitmapBean bean : bitmapBeanList) {
            if (bean.getId() != 0 && bean.getId() == bean.getFirstId()) {

            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_bitmap:
                if (play_image.getAlpha() == 0.0f) {
                    play_image.animate().translationZ(20f).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
                } else {
                    play_image.animate().translationZ(0.0f).scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
                }
                break;
            case R.id.reset:
                synchronized (view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isplay) {
                                getPuzzleGenerator();
                            } else {
                                handler.sendEmptyMessage(2);
                                for (int i = 0; i < bitmapBeanList.size(); i++) {
                                    bitmapBeanList.get(i).getBitmap().recycle();
                                }
                                bitmapBeanList.clear();
                                List<BitmapBean> bitmapBeen = BitmapUtil.createList(difficute, bitmap);
                                if (bitmapBeen == null || bitmapBeen.size() == 0) {
                                    handler.sendEmptyMessage(3);
                                    return;
                                } else {
                                    bitmapBeanList.addAll(bitmapBeen);
                                }
                                getPuzzleGenerator();
                            }
                            isplay = true;
                            isSuccess = false;
                            time = 0;
                            step = 0;
                            handler.sendEmptyMessage(1);
                        }
                    }).start();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (play_image.getAlpha() == 0.0f) {
            super.onBackPressed();
        } else {
            play_image.animate().translationZ(0.0f).scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSuccess) {
            isplay = true;
            play_top.animate().translationZ(10).setDuration(1000).setListener(this).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isplay = false;
    }
}
