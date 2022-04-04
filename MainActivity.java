package com.example.mobile_monitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.view.MotionEvent;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import io.agora.rtc.video.VideoEncoderConfiguration;


public class MainActivity extends AppCompatActivity {


    private ImageButton btn_Lock_advance,btn_Lockback,btn_Lockleft,btn_Lockright;
    private TextView show_DeviceName,show_online,show_DeviceID,show_CreateTime,show_Monitor;

    private String mip = "183.230.40.39";//服务器地址
    private String apikey = "111111111=";//
    private String device_id = "111111111111";//设备id
    private int a=0,b=1,port = 6002;//

    private static final String TAG = MainActivity.class.getSimpleName();
    // 创建 SurfaceView 对象。
    private FrameLayout mLocalContainer;
    private SurfaceView mLocalView;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    // 创建一个 SurfaceView 对象。
    private RelativeLayout mRemoteContainer;
    private SurfaceView mRemoteView;

    private boolean mCallEnd;
    private boolean mMuted;
    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    // Java
    private static final int PERMISSION_REQ_ID = 22;

    // App 运行时确认麦克风和摄像头设备的使用权限。
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() !=null){ getSupportActionBar().hide(); }//去掉标题栏
        setContentView(R.layout.activity_main);
        //setTitle("智能锁V1.0");//标题

        initUI();
        // 获取权限后，初始化 RtcEngine，并加入频道。
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
        show_online = findViewById(R.id.txtNetwork);//设备联网情况
/*        show_DeviceName = findViewById(R.id.txtDeviceName);//设备名称

*//*        show_DeviceID = findViewById(R.id.txtDeviceID);//设备ID
        show_CreateTime = findViewById(R.id.txtCreateTime);//设备创建时间*//*
        show_Monitor = findViewById(R.id.txtMonitor);//监控信息*/
        btn_Lock_advance = findViewById(R.id.btnLock_advance);//前指令
        btn_Lockback = findViewById(R.id.btnLock_back);//后
        btn_Lockleft = findViewById(R.id.btnLock_left);//左
        btn_Lockright = findViewById(R.id.btnLock_right);//右

        //ButtonFunction();//按键功能

        new Handler().postDelayed(task,2000);


        btn_Lock_advance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    a=1;
                    Control(a);
                    btn_Lock_advance.setImageResource(R.drawable.ic_btn_forward_on);

                    //btn_Lock_advance.setBackgroundColor(Color.#EAEAEA);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    a=5;
                    Control(a);
                    btn_Lock_advance.setImageResource(R.drawable.ic_btn_forward_off);
                }
                return false;
            }
        });
        btn_Lockback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    a=2;
                    Control(a);
                    btn_Lockback.setImageResource(R.drawable.ic_btn_backward_on);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    a=5;
                    Control(a);
                    btn_Lockback.setImageResource(R.drawable.ic_btn_backward_off);
                }
                return false;
            }
        });
        btn_Lockleft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    a=4;
                    Control(a);
                    btn_Lockleft.setImageResource(R.drawable.ic_btn_left_on);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    a=5;
                    Control(a);
                    btn_Lockleft.setImageResource(R.drawable.ic_btn_left_off);
                }
                return false;
            }
        });
        btn_Lockright.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    a=3;
                    Control(a);
                    btn_Lockright.setImageResource(R.drawable.ic_btn_right_on);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    a=5;
                    Control(a);
                    btn_Lockright.setImageResource(R.drawable.ic_btn_right_off);
                }
                return false;
            }
        });
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this,3000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{

                        OkHttpClient client = new OkHttpClient();
                        String url1 = String.format("http://api.heclouds.com/devices/%s",device_id);
                        Request request = new Request.Builder()
                                .url(url1)
                                .addHeader("api-key", apikey)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Log.w("test", responseData);
                        //json提取数据
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                        //Log.e("e", String.valueOf(jsonObjectData));
                        final String device_title = jsonObjectData.getString("title");//提取设备名称
                        final String device_id = jsonObjectData.getString("id");//提取设备id
                        final String create_time = jsonObjectData.getString("create_time");//提取设备创建时间
                        final String device_online = jsonObjectData.getString("online");//提取设备是否在线

                        //设备监控信息
                        OkHttpClient client2 = new OkHttpClient();
                        String url2 = String.format("http://api.heclouds.com/devices/%s/datastreams/test2",device_id);
                        Request request2 = new Request.Builder()
                                .url(url2)
                                .addHeader("api-key", apikey)
                                .build();
                        Response response2 = client2.newCall(request2).execute();
                        String responseData2 = response2.body().string();
                        //json提取数据
                        JSONObject jsonObject2 = new JSONObject(responseData2);
                        JSONObject jsonObjectData2 = jsonObject2.getJSONObject("data");
                        final String current_value = jsonObjectData2.getString("current_value");//提取最新数据
                        final String update_at = jsonObjectData2.getString("update_at");//提取最新数据的时间

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //show_DeviceName.setText(device_title);
                               // show_DeviceID.setText("设备ID："+device_id);
                               // show_CreateTime.setText("创建时间："+create_time);
                                if(device_online.equals("true")) {
                                    show_online.setText("在 线");
                                    show_online.setTextColor(Color.GREEN);
                                }
                                if(device_online.equals("false")){
                                    show_online.setText("离 线");
                                    show_online.setTextColor(Color.RED);
                                }
                                switch (current_value) {
                                    case "1":
                                        show_Monitor.setText(update_at + "\n" + "手动开锁成功！");
                                        break;
                                    case "2":
                                        show_Monitor.setText(update_at + "\n" + "手动关锁成功！");
                                        break;
                                    case "3":
                                        show_Monitor.setText(update_at + "\n" + "指纹开锁成功！");
                                        break;
                                    case "4":
                                        show_Monitor.setText(update_at + "\n" + "远程关锁成功！");
                                        break;
                                    case "5":
                                        show_Monitor.setText(update_at + "\n" + "远程开锁成功！");
                                        break;
                                    case "6":
                                        show_Monitor.setText(update_at + "\n" + "指纹识别错误！");
                                        break;
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "请检查手机是否连接网络", Toast.LENGTH_SHORT).show(); //显示提示
                            }
                        });

                    }



                }
            }).start();
        }
    };


    //获取数据
    public void GetData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    String url = String.format("http://api.heclouds.com/devices/%s/datastreams/test1",device_id);
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("api-key", apikey)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.w("test", responseData);
                    //json提取数据
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Log.e("e", String.valueOf(jsonObjectData));
                    final String current_value = jsonObjectData.getString("current_value");//提取最新数据
                    final String update_at = jsonObjectData.getString("update_at");//提取最新数据的时间


                }catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //下发命令
    public void Control(final int val) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    String url = String.format("http://api.heclouds.com/cmds?device_id=%s",device_id);
                    String body = String.format("onoff:%d", val);
                    RequestBody body1=RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
                    Request request = new Request.Builder()
                            .url(url)
                            .headers(new Headers.Builder().add("api-key", apikey).build())//表头
                            .post(body1)
                            .build();
                    Call call=client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {//请求失败
                            Log.e("e", "post请求失败");
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {//请求成功
                        }
                    });
                    //平台回应
                    Response response = client.newCall(request).execute();
                    String jsonStr = response.body().string();
                    Log.e(" onenet回应数据",jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String jsonObjectRes = jsonObject.getString("errno");
                    final String jsonObjectError = jsonObject.getString("error");
                    Log.e("a", jsonObjectRes);
                    if (jsonObjectRes == "10"){

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("请求错误");
                                builder.setMessage("设备不在线");
                                //builder.setIcon(R.mipmap.ic_launcher);//图标显示
                                builder.setCancelable(true);
                                builder.show();
//                                AlertDialog dialog=builder.create();
//                                dialog.show();
                            }
                        });

                    }else if (jsonObjectRes == "0"){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "远程控制成功", Toast.LENGTH_SHORT).show(); //显示提示
                            }
                        });
                    }else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("请求错误");
                                builder.setMessage(jsonObjectError);
                                builder.setCancelable(true);
                                // builder.show();
                            }
                        });
                    }
                } catch (Exception e) {
                    // toastStr = "连接失败，请检测手机或设备的网络状态";
                    e.printStackTrace();
                }
            }
        }).start();
    }
/*    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(task);
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitBy2Click();
        }
        return false;
    }

    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit = null;
        if(!isExit){
            isExit = true;
            Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        }else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }


    //shengwang
    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);



        // Sample logs are optional.

    }
    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupLocalVideo();
        joinChannel();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }
    // Java
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // 注册 onJoinChannelSuccess 回调。
        // 本地用户成功加入频道时，会触发该回调。
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora","Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        // 注册 onUserJoined 回调。
        // 远端用户成功加入频道时，会触发该回调。
        // 可以在该回调中调用 setupRemoteVideo 方法设置远端视图。
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora","Remote user joined, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }

        private void onRemoteUserLeft(int uid) {
            if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
                removeFromParent(mRemoteVideo);
                // Destroys remote view
                mRemoteVideo = null;
            }
        }
        @Override
        // 注册 onUserOffline 回调。
        // 远端用户离开频道或掉线时，会触发该回调。
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora","User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft(uid);
                }
            });
        }
    };

    // 初始化 RtcEngine 对象。
    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }
    // Java
    private void setupLocalVideo() {

        // 启用视频模块。
        mRtcEngine.enableVideo();



        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // 设置本地视图。
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
    }
    // Java
    private void joinChannel() {
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        // 调用 joinChannel 方法 加入频道。
        mRtcEngine.joinChannel(token, "demoChannel1", "Extra Optional Data", 0);
    }
    // Java


    private void setupRemoteVideo(int uid) {



        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // 设置远端视图。
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

    }
    // Java停止发送音频流
    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }
    // Java
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }
    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }

        showButtons(!mCallEnd);
    }
    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }
    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
    }
    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    // Java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
        handler.removeCallbacks(task);
    }

    private void leaveChannel() {
        // 离开当前频道。
        mRtcEngine.leaveChannel();
    }

}
