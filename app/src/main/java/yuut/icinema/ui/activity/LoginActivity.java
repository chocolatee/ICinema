package yuut.icinema.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yuut.icinema.R;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.UserBean;
import yuut.icinema.support.Util.PhoneFormatCheckUtils;

/**
 * Created by yuut on 2017/4/26.
 */

public class LoginActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_username)
    EditText usernameView;
    @Bind(R.id.et_password)
    EditText passwordView;
    @Bind(R.id.bt_go)
    Button loginView;
    @Bind(R.id.cv)
    CardView cv;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        initToolBar(toolbar, "Login", R.mipmap.icon_arrow_back);
    }

    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                            fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
//                Intent i2 = new Intent(this, LoginSuccessActivity.class);
//                startActivity(i2, oc2.toBundle());
                String username = usernameView.getText().toString();
                String password = passwordView.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(this, "请填E-mail", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                long queryNum = MyApplication.liteOrm.queryCount(new QueryBuilder<UserBean>(UserBean.class)
                        .whereEquals("user_name", username));
                if (queryNum <= 0) {
                    Toast.makeText(this, "用户不存在请注册", Toast.LENGTH_SHORT).show();
                    return;
                }
                queryNum = MyApplication.liteOrm.queryCount(new QueryBuilder<UserBean>(UserBean.class)
                        .whereEquals("user_name", username)
                        .whereAppendAnd()
                        .whereEquals("password", password));
                if (queryNum <= 0) {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                //成功通过验证
                UserBean userBean = new UserBean();
                userBean.setUser_name(username);
                userBean.setPassword(password);

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
