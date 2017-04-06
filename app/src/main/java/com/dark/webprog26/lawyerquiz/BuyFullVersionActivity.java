package com.dark.webprog26.lawyerquiz;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyFullVersionActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String GAME_IS_OVER = "game_is_over";

    //Views initialization
    @BindView(R.id.etName)
    EditText mEtName;
    @BindView(R.id.etEmail)
    EditText mEtEmail;
    @BindView(R.id.etPhoneNumber)
    EditText mEtPhoneNumber;
    @BindView(R.id.etMessage)
    EditText mEtMessage;
    @BindView(R.id.btnSend)
    Button mBtnSend;
    @BindView(R.id.btnBuyFullVersion)
    Button mBtnBuyFullVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_full_version);
        ButterKnife.bind(this);
        mBtnSend.setOnClickListener(this);
        mBtnBuyFullVersion.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setting default quiz background instead of logo
        getWindow().setBackgroundDrawableResource(R.drawable.screen_bg);
    }

    @Override
    public void onClick(View v) {
        notifyGameOver();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            notifyGameOver();
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Writes GAME_OVER boolean flag to SharedPreferences (true). Reading this in onResume() method QuizActivity will end the quiz
     */
    private void notifyGameOver(){
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(GAME_IS_OVER, true).apply();
    }
}
