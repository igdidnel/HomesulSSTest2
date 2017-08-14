package com.example.administrator.homesuls;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    AudioManager soundMode;  //사운드 모드 (무음/소리 등)


    private static MediaPlayer mp;


    private SensorManager m_sensorManager;
    private Sensor m_sensor;


    SoundPool pool;
    SoundPool bgmpool;
    int sound1; //BeerFlowSound



    ImageView character;
    AnimationDrawable characterAni;

    ImageView img3;
    ImageView img2;
    ImageView img;
    AnimationDrawable ani;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//=============================================모드 사운드==========================================================================================
        soundMode = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);  // 사운드 ON OFF를 위한 현재 오디오 모드확인.



//=============================================배경 사운드==========================================================================================

        mp = MediaPlayer.create(this, R.raw.mainbgm);
        mp.setLooping(true);
        mp.start();


//=============================================사운드==========================================================================================
        pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0); // 최대 음악파일의 개수, 스트림타입, 음질 기본값0
        sound1 = pool.load(this, R.raw.beerflowbgm, 1);       // 각 재생음악을 미리준비함. this는 현재화면 제어권자
        //R.raw.cheers는 음악파일
        //1은 우선순위//1은 우선순위



//=============================================================================================================================================


        character = (ImageView) findViewById(R.id.characterimg); //character 객체에 ImageView를 xml에서 명시받음
        character.setBackgroundResource(R.drawable.ani_maincharacter); //명시받은 character의 백그라운드 리소스를 ani_maincharacter.xml로 지정
        characterAni = (AnimationDrawable) character.getBackground(); // characterAni 애니메이션에 리소스지정한 character ImageView의 백그라운드를 넣음


        m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);  //센서
        m_sensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);  //센서
        m_sensorManager.registerListener(this, m_sensor, m_sensorManager.SENSOR_DELAY_NORMAL);  //센서


        img2 = (ImageView) findViewById(R.id.img2);  //2는 건배이미지
        img3 = (ImageView) findViewById(R.id.chractertranslateimg);

        img = (ImageView) findViewById(R.id.img);
        img.setBackgroundResource(R.drawable.ani_beer);
        ani = (AnimationDrawable) img.getBackground();
        ani.setOneShot(true);

/*        img = (ImageView) findViewById(R.id.img);
        ani = (AnimationDrawable) img.getDrawable();
        ani.setOneShot(true);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher); // 뒤로가기 버튼, 내가 만든 아이콘으로 사용

        //actionBar.setHomeAsUpIndicator(R.drawable.button_back); //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요









    }  //절대영역






    //=============================================백버튼종료 로직=======================================================================================
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(this.getString(R.string.exit))
                    .setMessage(this.getString(R.string.exit_message))
                    .setPositiveButton(this.getString(R.string.yes),
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick( DialogInterface dialog, int which )
                                {
                                    moveTaskToBack(true);
                                    finish();
                                }
                            }
                    ).setNegativeButton(this.getString(R.string.no), null ).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    public void clearApplicationCache(java.io.File dir){  //종료시 모든 캐쉬 삭제
        if(dir==null) dir = getCacheDir();
        if(dir==null) return;
        java.io.File[] children = dir.listFiles();
        try{
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }




    @Override
    protected void onDestroy() {  //앱이 아얘 정지됬을때 배경음악 정지
        mp.stop();
        super.onDestroy();
        clearApplicationCache(null);
        android.os.Process.killProcess(android.os.Process.myPid() );
    }
//=============================================================================================================================================




    @Override
    protected void onUserLeaveHint() {  //홈버튼을 눌렀을때 배경음악 일시정지
        mp.pause();
        super.onUserLeaveHint();
    }

    @Override
    public void onResume(){  //다시 돌아오면 배경음악 시작
        mp.start();
        super.onResume();
    }

    @Override
    public void onBackPressed() {  //백버튼을 눌렀을때 배경음악 정지
        mp.stop();
        super.onBackPressed();
    }




    //===========================================포커스실행========================================================================================
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {


        if(hasFocus) {  //포커스가 있을때 애니메이션 / 메인BGM 시작


        characterAni.start();  //캐릭터애니 시작
        //ani.start(); //맥주애니 시작
        }

        else{ //포커스가 떠나면 애니메이션 종료
          //  characterAni.stop();
           // ani.stop();
        }
        super.onWindowFocusChanged(hasFocus);
    }

//=============================================================================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            Toast.makeText(this, "테마", Toast.LENGTH_SHORT).show();

        }


        else if (item.getItemId() == R.id.action_healthListview) {
            Toast.makeText(this, "건강백서", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(MainActivity.this, SubActivity.class);
                    startActivity(intent);

        }


        else if (item.getItemId() == R.id.action_setting) {
            Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show();
        }

        else if (item.getItemId() == R.id.miSearch){

            Intent intent = new Intent(MainActivity.this, DialogActivity.class);
            startActivity(intent);
        }

        return true;}


    /*    if or switch
        switch (item.getItemId()) {
            case R.id.action_theme:
                Toast.makeText(this, "테마", Toast.LENGTH_SHORT).show();
            case R.id.action_healthListview:
                Toast.makeText(this, "건강백서", Toast.LENGTH_SHORT).show();
            case R.id.action_setting:
                Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }*/




    /* Back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
*/







    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] == 0){   //근접했을때

            img.setVisibility(View.INVISIBLE); //맥주를 GONE상태로 만들어 없애버린다.
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)img2.getLayoutParams();
            layoutParams.width = 1000;
            img2.setLayoutParams(layoutParams);
            layoutParams.height = 1800;
            img2.setLayoutParams(layoutParams);


            Animation scaleanimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale);  //트윈애니메이션 점점커지는 맥주 애니메이션효과
           img2.startAnimation(scaleanimation);


            character.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.VISIBLE); //캐릭터translate 보이기
            Animation translateanimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate);  //트윈애니메이션 캐릭터translate 움직여 보이는 애니메이션 효과
            img3.startAnimation(translateanimation);


        }
        else{  //근접하지 않았을때


            character.setVisibility(View.VISIBLE); // 캐릭터img 보이기
            img3.setVisibility(View.INVISIBLE); //캐릭터translate 숨기기



            img.setVisibility(View.VISIBLE);
            ani.start();
            pool.play(sound1,  //준비한 soundID 맥주따르는 효과음
                    1, //왼쪽 볼륨 float 0.0(작은소리) ~ 1.0 (큰소리)
                    1, //오른쪽 볼륨 float
                    0, //우선순위 int
                    0, //반복회수 int -1:무한반복, 0:반복안함
                    1); //재생속도 float 0.5(절반속도)~2.0(2배속)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)img2.getLayoutParams();
            layoutParams.width = 0;
            img2.setLayoutParams(layoutParams);
            layoutParams.height =  0;
            img2.setLayoutParams(layoutParams);
        }

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }






}
