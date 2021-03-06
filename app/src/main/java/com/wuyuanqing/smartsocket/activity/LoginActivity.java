package com.wuyuanqing.smartsocket.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.wuyuanqing.smartsocket.R;
import com.wuyuanqing.smartsocket.constant.SmartSocketUrl;
import com.wuyuanqing.smartsocket.model.ResLogin;
import com.wuyuanqing.smartsocket.util.JsonStringUtil;
import com.wuyuanqing.smartsocket.util.OkHttpClientManager;

/**
 * Created by wyq on 2015/11/23.
 */
public class LoginActivity extends BaseActivity {

    private EditText userEdit, passwordEdit;
    private Button  loginBt, backBt;
    private TextView registerText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        userEdit = (EditText) findViewById(R.id.activity_login_edit_login_user);
        passwordEdit = (EditText) findViewById(R.id.activity_login_edit_login_password);
        registerText = (TextView) findViewById(R.id.activity_login_register);
        loginBt = (Button) findViewById(R.id.activity_login_bt_login);
        backBt = (Button) findViewById(R.id.activity_login_bt_login_back);

        registerText.setMovementMethod(LinkMovementMethod.getInstance());
//        registerText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!userEdit.getText().toString().equals("") || !passwordEdit.getText().toString().equals("")) {
//                    OkHttpClientManager.postAsyn(SmartSocketUrl.registerUrl, new OkHttpClientManager.ResultCallback<String>() {
//                        @Override
//                        public void onError(Request request, Exception e) {
//                            e.printStackTrace();
//                            l("出现错误");
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            ResLogin resLogin = new Gson().fromJson(response, ResLogin.class);
//                            if (resLogin.getErrorCode() == 0) {//注册成功
//                                t("注册成功");
//                                ISLOGIN = true;
//                                rememberUserName(resLogin);//记住用户名与密码
//                                Intent intent = new Intent();
//                                intent.setClass(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            } else if (resLogin.getErrorCode() == 1) {//注册失败
//                                t("注册失败");
//                            } else if (resLogin.getErrorCode() == 2) {//用户已经存在
//                                t("用户已经存在");
//                            }
//                        }
//                    }, new OkHttpClientManager.Param("register", JsonStringUtil.loginStr(userEdit.getText().toString(), passwordEdit.getText().toString())));
//
//                } else {
//                    t("账号与密码不能为空");
//                }
//
//            }
//        });

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userEdit.getText().toString().equals("") || !passwordEdit.getText().toString().equals("")) {
                   final ProgressDialog dialog= new ProgressDialog(LoginActivity.this);
                    dialog.show();
                    l(JsonStringUtil.loginStr(userEdit.getText().toString(), passwordEdit.getText().toString()));
                    OkHttpClientManager.postAsyn(SmartSocketUrl.loginUrl, new OkHttpClientManager.ResultCallback<String>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            e.printStackTrace();
                            l("出现错误");
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response) {
                            if (response == null) {
                                l("服务器返回值为空");
                            } else {
                                l(response);
                                ResLogin resLogin = new Gson().fromJson(response, ResLogin.class);
                                if (resLogin.getErrorCode() == 0) {//登录成功
                                    t("登录成功");
                                    dialog.dismiss();
                                    ISLOGIN = true;
                                    rememberUserName(resLogin);//记住用户名与密码
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (resLogin.getErrorCode() == 1) {//密码错误
                                    t("请检查密码");
                                } else if (resLogin.getErrorCode() == 2) {//没有用户
                                    t("请检查用户名");
                                }
                            }
                        }
                    }, new OkHttpClientManager.Param("login", JsonStringUtil.loginStr(userEdit.getText().toString(), passwordEdit.getText().toString())));

                } else {
                    t("账号与密码不能为空");
                }

            }
        });

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    private void rememberUserName(ResLogin resLogin) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", resLogin.getUser().getUserName());
        editor.putString("password", resLogin.getUser().getPassWord());
        editor.commit();
    }
}
