package com.MM.voiceconsultation;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;

import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap ;
import java.util.HashSet;
import java.util.LinkedHashMap ;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static android.view.View.VISIBLE;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class .getSimpleName();
    private EditText et_input;
    private TextView tvQuestion,tvAnswer,tvBasic;
    private Button btn_startspeech, btn_startspeektext;
    private StringBuffer basicInfo = new StringBuffer();
    private StringBuffer question = new StringBuffer();
    private StringBuffer answer = new StringBuffer();
    private Set answerSet = new HashSet<String>();
    Handler handler = new Handler();
    private String res = null;
    private JSONArray json;
    private boolean flag = true;
    private Thread threadMatch = null;
    private boolean isBasicInfo = true;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String , String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        initJson();//获取后台数据库
        initView();
        initSpeech();
    }

    private void initJson() {
        if(json == null)
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String url = "http://192.168.1.114:60000";
                    Map<String,String > parms = new HashMap<String, String>();
                    res = HTTP.getContextByHttp(url, parms,"POST");
                    Log.d("result",res);


                    try{

                        json = new JSONArray(res);
                        Log.d("json",json.toString());


                        String classical_symptom = null;
                        for (int i = 0; i < json.length(); i++) {
                            classical_symptom = json.getJSONObject(i).getString("classical_symptom");
                            //System.out.println(classical_symptom);
                            for(String val : classical_symptom.split(" ")){

                                answerSet.add(val);
                            }

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        et_input = (EditText) findViewById(R.id.et_input);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        tvAnswer = (TextView) findViewById(R.id.tvAnswer);
        tvBasic = (TextView) findViewById(R.id.tvBasic);

        btn_startspeech = (Button) findViewById(R.id.btn_startspeech );
        btn_startspeektext = (Button) findViewById(R.id.btn_startspeektext );


        btn_startspeech.setOnClickListener(this);
        btn_startspeektext.setOnClickListener(this);








    }

    private void initSpeech() {
        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在 “ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility( this, SpeechConstant.APPID + "=5d0a2cb1" );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startspeech: //语音识别（把声音转文字）
                startSpeechDialog();
                break;
            case R.id. btn_startspeektext:// 语音合成（把文字转声音）
                //speekText(et_input.getText().toString());

                if(flag == true){
                    String voiceWhenAppStart = "欢迎来到医术达人,请说出您的性别和年龄";
                    speekText(voiceWhenAppStart);
                    btn_startspeektext.setText("重新问诊");

                    btn_startspeech.setVisibility(VISIBLE);

                    question = new StringBuffer();
                    answer = new StringBuffer();

                    tvAnswer.setText("");
                    tvQuestion.setText("");
                    flag = false;
                }
                else{
                    btn_startspeektext.setText("开始问诊");

                    question = new StringBuffer();
                    answer = new StringBuffer();

                    tvAnswer.setText("");
                    tvQuestion.setText("");
                    flag = true;
                }


                break;
        }

    }

    private void speekText(String str) {
        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer( this, null);
        //2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
        //设置发音人（更多在线发音人，用户可参见 附录 13.2
        mTts.setParameter(SpeechConstant. VOICE_NAME, "xq" ); // 设置发音人
        mTts.setParameter(SpeechConstant. SPEED, "50" );// 设置语速
        mTts.setParameter(SpeechConstant. VOLUME, "80" );// 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant. ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在 “./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant. TTS_AUDIO_PATH, "./sdcard/iflytek.pcm" );
        //3.开始合成
        mTts.startSpeaking( str, new MySynthesizerListener()) ;

    }

    class MySynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            showTip(" 开始播放 ");
        }

        @Override
        public void onSpeakPaused() {
            showTip(" 暂停播放 ");
        }

        @Override
        public void onSpeakResumed() {
            showTip(" 继续播放 ");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos ,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成 ");

            } else if (error != null ) {
                showTip(error.getPlainDescription( true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话 id，当业务出错时将会话 id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话 id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //     String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //     Log.d(TAG, "session id =" + sid);
            //}
        }
    }

    private void startSpeechDialog() {
        //1. 创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener()) ;
        //2. 设置accent、 language等参数
        mDialog.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mDialog.setParameter(SpeechConstant. ACCENT, "mandarin" );
        // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后 onResult回调返回将是语义理解
        // 结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener()) ;
        //4. 显示dialog，接收语音输入
        mDialog.show() ;
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param results
         * @param isLast  是否说完了
         */
        @Override
        public void onResult(RecognizerResult results, final boolean isLast) {
            String result = results.getResultString(); //为解析的
            showTip(result);
            System.out.println(" 没有解析的 :" + result);

            final String text = JsonParser.parseIatResult(result);//解析过后的
            System.out.println(" 解析后的 :" + text);

            String sn = null;
            // 读取json结果中的 sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);//没有得到一句，添加到

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            //tvOutput.setText(resultBuffer.toString());// 设置输入框的文本
            //et_input.setSelection(et_input.length()) ;//把光标定位末尾
            if (isLast) {
                String context = resultBuffer.toString();

                if (isBasicInfo) {

                    String regex = ".*(男|女).*([1-9][0-9]?|1[01][0-9]|120).*";

                    System.out.println(context);

                    String voiceToAsk = null;
                    System.out.println(Pattern.matches(regex, context));

                    if (Pattern.matches(regex, context)) {
                        basicInfo.append(context);
                        tvBasic.setText(basicInfo.toString());
                        isBasicInfo = false;

                        voiceToAsk = "请说出您哪里不舒服";
                        speekText(voiceToAsk);

                    } else {
                        voiceToAsk = "输入性别年龄有误，请重新输入";
                        speekText(voiceToAsk);
                    }


                } else {
                    question.append(context);
                    tvQuestion.setText(question.toString());


                    final String temp = context;


                    threadMatch = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                String name = null;
                                String regex = null;
                                System.out.println(answerSet);
                                for (int i = 0; i < json.length(); i++) {

                                    name = json.getJSONObject(i).getString("chinese_name");
                                    regex = ".*" + name + ".*";
                                    if (Pattern.matches(regex, temp)) {
                                        //Log.d("pattern", name);
                                        answer.append(json.getJSONObject(i) + "\n");


                                        final Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {

                                                tvAnswer.setText(answer.toString());
                                            }
                                        };
                                        handler.post(runnable);
                                    }

                                }
                                for (Object val : answerSet) {
                                    //if(val.equals("null")||val.equals("")) continue;
                                    //System.out.println(val);
                                    regex = ".*" + val + ".*";

                                    if (Pattern.matches(regex, temp)) {
                                        //Log.d("pattern", temp);
                                        answer.append(val + "\n");

                                        final Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                tvAnswer.setText(answer.toString());
                                            }
                                        };
                                        handler.post(runnable);
                                        //i = json.length();
                                        //break;
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    threadMatch.start();

                }


            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败 ");
            }

        }
    }

    /**
     * 语音识别
     */
    /*private void startSpeech() {
        //1. 创建SpeechRecognizer对象，第二个参数： 本地识别时传 InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer( this, null); //语音识别器
        //2. 设置听写参数，详见《 MSC Reference Manual》 SpeechConstant类
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        //3. 开始听写
        mIat.startListening( mRecoListener);
    }


    // 听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        // 听写结果回调接口 (返回Json 格式结果，用户可参见附录 13.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见 Demo中JsonParser 类；
        //isLast等于true 时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e (TAG, results.getResultString());
            System.out.println(results.getResultString()) ;
            showTip(results.getResultString()) ;
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true)) ;
            // 获取错误码描述
            Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
        }

        // 开始录音
        public void onBeginOfSpeech() {
            showTip(" 开始录音 ");
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
            showTip(" 声音改变了 ");
        }

        // 结束录音
        public void onEndOfSpeech() {
            showTip(" 结束录音 ");
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        }
    };*/

    private void showTip (String data) {
        Toast.makeText( this, data, Toast.LENGTH_SHORT).show() ;
    }



}
