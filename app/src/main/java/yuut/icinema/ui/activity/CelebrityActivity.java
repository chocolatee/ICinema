package yuut.icinema.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.adapter.SimpleFilmAdapter;
import yuut.icinema.app.MyApplication;
import yuut.icinema.bean.CelebrityBean;
import yuut.icinema.bean.SimpleCardBean;
import yuut.icinema.bean.WorksEntity;
import yuut.icinema.support.Constant;
import yuut.icinema.support.Util.StringUtil;

/**
 * Created by yuut on 2017/4/20.
 */

public class CelebrityActivity extends BaseActivity implements SimpleFilmAdapter.OnItemClickListener {

    private static final String VOLLEY_TAG = "CelActivity";
    private static final String KEY_CEL_ID = "cel_id";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_cel_name)
    TextView mName;
    @Bind(R.id.tv_cel_name_en)
    TextView mNameEn;
    @Bind(R.id.tv_cel_gender)
    TextView mGender;
    @Bind(R.id.tv_cel_bron_place)
    TextView mBronPlace;
    @Bind(R.id.tv_cel_ake)
    TextView mAke;
    @Bind(R.id.tv_cel_ake_en)
    TextView mAkeEn;
    @Bind(R.id.iv_cel_image)
    ImageView mImage;
    @Bind(R.id.tv_cel_works)
    TextView mWorks;
    @Bind(R.id.rv_cel_works)
    RecyclerView mWorksView;
    @Bind(R.id.ll_cel_layout)
    LinearLayout mCelLayout;

    private CelebrityBean mCelebrity;
    private List<SimpleCardBean> mWorksData = new ArrayList<>();

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MyApplication.getLoaderOptions();

    public static void toActivity(Context context, String id) {
        Intent intent = new Intent(context, CelebrityActivity.class);
        intent.putExtra(KEY_CEL_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebrity);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mWorksView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initData() {
        String mId = getIntent().getStringExtra(KEY_CEL_ID);
        String url = Constant.API + Constant.CELEBRITY + mId;
        volleyGetCelebrity(url);
    }

    protected void onStop() {
        super.onStop();
        MyApplication.removeRequest(VOLLEY_TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cel_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通过volley得到mCelebrity
     */
    private void volleyGetCelebrity(String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCelebrity = new GsonBuilder().create().fromJson(response,
                        Constant.cleType);
                setViewAfterGetData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CelebrityActivity.this, error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.addRequest(request, VOLLEY_TAG);
    }

    /**
     * 得到mCelebrity实例后设置界面
     */
    private void setViewAfterGetData() {
        if (mCelebrity == null) return;
        initToolBar(toolbar, mCelebrity.getName(),R.mipmap.icon_arrow_back);
        imageLoader.displayImage(mCelebrity.getAvatars().getMedium(), mImage, options);
        mName.setText(mCelebrity.getName());
        mNameEn.setText(mCelebrity.getName_en());
        String gender = "性别: ";
        mGender.setText(String.format("%s%s", gender, mCelebrity.getGender()));
        String bronPlace = "出生地: ";
        mBronPlace.setText(String.format("%s%s", bronPlace, mCelebrity.getBorn_place()));

        if (mCelebrity.getAka().size() > 0) {
            mAke.setText(StringUtil.getSpannableString("更多中文名: ", Color.BLACK));
            mAke.append(StringUtil.getListString(mCelebrity.getAka(), '/'));
        } else {
            mAke.setVisibility(View.GONE);
        }

        if (mCelebrity.getAka_en().size() > 0) {
            mAkeEn.setText(StringUtil.getSpannableString("更多英文名", Color.BLACK));
            mAkeEn.append(StringUtil.getListString(mCelebrity.getAka_en(), '/'));
        } else {
            mAkeEn.setVisibility(View.GONE);
        }

        mWorks.setText(String.format("%s的影视作品",
                mCelebrity.getName()));

        for (WorksEntity work : mCelebrity.getWorks()) {
            SimpleCardBean data = new SimpleCardBean(
                    work.getSubject().getId(),
                    work.getSubject().getTitle(),
                    work.getSubject().getImages().getLarge(),
                    true);
            mWorksData.add(data);
        }
        SimpleFilmAdapter mWorksAdapter =
                new SimpleFilmAdapter(CelebrityActivity.this);
        mWorksAdapter.setOnItemClickListener(this);
        mWorksView.setAdapter(mWorksAdapter);
        mWorksAdapter.update(mWorksData);

        mCelLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemClick(String id, String imageUrl, boolean isCom) {
        SubjectActivity.toActivity(this, id, imageUrl);
    }
}