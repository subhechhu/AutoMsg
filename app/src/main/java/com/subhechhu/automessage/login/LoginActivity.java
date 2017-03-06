package com.subhechhu.automessage.login;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.subhechhu.automessage.R;
import com.subhechhu.automessage.message.MainListActivity;

public class LoginActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    EditText emailET;
    ProgressBar progressBar_login;
    Button registerBtn;
    TextInputLayout emailTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        progressBar_login = (ProgressBar) findViewById(R.id.progressBar_login);
        progressBar_login.setVisibility(View.INVISIBLE);
        registerBtn = (Button) findViewById(R.id.button_register);

        emailET = (EditText) findViewById(R.id.editText_email);
        emailET.addTextChangedListener(new SigninTextWatcher(emailET));

        emailTIL = (TextInputLayout) findViewById(R.id.textInputLayout);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            emailET.getBackground().mutate().setColorFilter(ContextCompat.getColor(LoginActivity.this, R.color.registerColor), PorterDuff.Mode.SRC_ATOP);
        } else {
            emailET.getBackground().mutate().setColorFilter(getResources().getColor(R.color.registerColor), PorterDuff.Mode.SRC_ATOP);
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateUser()) {
                    progressBar_login.setVisibility(View.VISIBLE);
                    //TODO call API
                    Log.e(TAG, "registerBtn()");
                    RequestLogin();
                }
            }
        });
    }

    private void RequestLogin() {
        progressBar_login.setVisibility(View.INVISIBLE);
        startActivity(new Intent(LoginActivity.this, MainListActivity.class));
        finish();
    }

    private boolean ValidateUser() {
        String email = emailET.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            emailTIL.setError(getString(R.string.error_invalid_email));
            requestFocus(emailET);
            return false;
        } else {
            emailTIL.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class SigninTextWatcher implements TextWatcher {
        private EditText emailET;

        public SigninTextWatcher(EditText emailET) {
            this.emailET = emailET;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ValidateUser();
        }
    }
}
