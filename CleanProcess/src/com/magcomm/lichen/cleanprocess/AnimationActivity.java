package com.magcomm.lichen.cleanprocess;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by weiyawei on 17-8-14 for android M
 */

public class AnimationActivity extends Activity {
    private static final String TAG = "AnimationActivity";
    private static final int MESSAGE_ROTATE_FINISHED = 0;
    private static final int MESSAGE_UPDATE_WIDTH = 1;
    private RelativeLayout mShortcut;
    private RelativeLayout mRelativeLayout;
    private ImageView backImageView;
    private ImageView roateImageView;
    private int mWidth;
    private Context mContext;
    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        mContext = getApplicationContext();
        mRelativeLayout = (RelativeLayout) findViewById(R.id.framelayout);
        mShortcut = (RelativeLayout) findViewById(R.id.shortcut);
        backImageView = (ImageView) findViewById(R.id.clean_back);
        roateImageView = (ImageView) findViewById(R.id.clean_rotate);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int hight = getWindowManager().getDefaultDisplay().getHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mShortcut
                .getLayoutParams();
        layoutParams.topMargin = hight / 3 + 150;   
        layoutParams.leftMargin = width / 4 + 30 ;
        mRelativeLayout.updateViewLayout(mShortcut, layoutParams);

    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_ROTATE_FINISHED:
                    mWidth = backImageView.getWidth();
                    roateImageView.clearAnimation();
                    roateImageView.setVisibility(View.INVISIBLE);
                    killAll(mContext);
                    finish();
                    break;
                case MESSAGE_UPDATE_WIDTH:
                    break;

                default:
                    break;
            }

        };
    };

    public void killAll(Context context){

        long beforeMem = getAvailMemory(context);//清理前的可用内存
        
        ActivityManager am = (ActivityManager)
                getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks =
                am.getRecentTasks(100, ActivityManager.RECENT_IGNORE_HOME_STACK_TASKS);
        android.util.Log.i("www", "recentTasks count == " + recentTasks.size());
        int mcount=recentTasks.size();
        for(int i = 0;i <recentTasks.size();i++) {
            int mpersistentId = recentTasks.get(i).persistentId;
            am.removeTask(mpersistentId);
            android.util.Log.i("www", "recentTasks persistentId == " + mpersistentId);
        }
        long afterMem = getAvailMemory(context);//清理后的内存占用
        
        if(afterMem < beforeMem ){
         Toast.makeText(context,getString(R.string.clear)+ " " + mcount + " " + getString(R.string.process) + " "
                + formatFileSize(beforeMem - afterMem ) + " " + getString(R.string.memory), Toast.LENGTH_LONG).show();
        }else if(afterMem == beforeMem){
            mcount = 0;
          Toast.makeText(context,getString(R.string.clear)+ " " + mcount + " " + getString(R.string.process)+ " "
                + formatFileSize(beforeMem - afterMem )+ " " + getString(R.string.memory), Toast.LENGTH_LONG).show();
        }else{      
        Toast.makeText(context, getString(R.string.clear)+ " " + mcount + " " + getString(R.string.process)+ " "
                + formatFileSize(afterMem - beforeMem )+ " " + getString(R.string.memory), Toast.LENGTH_LONG).show();
        }

    }

    /*
   * *获取可用内存大小
   */
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /*
     * *字符串转换 long-string KB/MB
     */
    private String formatFileSize(long number){
        return Formatter.formatFileSize(mContext, number);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 旋转动画开始
        roateImageView.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.rotate_anim));

        // 假设垃圾清理了两秒钟，然后开如做伸缩动画。
        mHandler.sendEmptyMessageDelayed(MESSAGE_ROTATE_FINISHED, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRelativeLayout.setVisibility(View.GONE);
        finish();
    }

}
