package yuut.icinema.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.bean.BoxSubjectBean;
import yuut.icinema.bean.SimpleSubjectBean;

/**
 * Created by yuut on 2017/4/19.
 */

public class BoxAdapter extends BaseAdapter<BoxAdapter.ViewHolder> {
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private static final int THIRD = 3;

    private static final String GOLD = "#D9D919";
    private static final String SILVERY = "#D9D919";
    private static final String COPPER = "#B87333";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<BoxSubjectBean> mData;

    public static final String[] RANK = {"th", "1st", "2nd", "3rd"};

    public BoxAdapter(Context context, List<BoxSubjectBean> data) {
        mContext=context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateList(List<BoxSubjectBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_box_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.tv_item_box_rank)
        TextView text_rank;
        @Bind(R.id.iv_item_box_isNew)
        ImageView image_isNew;
        @Bind(R.id.iv_item_box_image)
        ImageView image_film;
        @Bind(R.id.tv_item_box_title)
        TextView text_title;
        @Bind(R.id.tv_item_box_noRating)
        TextView text_no_rating;
        @Bind(R.id.rb_item_box_rating)
        RatingBar ratingBar;
        @Bind(R.id.tv_item_box_rating)
        TextView text_rating;
        @Bind(R.id.ll_item_box_rating)
        LinearLayout layout_rating;

        BoxSubjectBean subject;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                mCallback.onItemClick(
                        mData.get(position).getSubject().getId(),
                        mData.get(position).getSubject().getImages().getLarge());

            }
        }

        public void update() {
            //获取影片信息
            subject = mData.get(getLayoutPosition());
            //将信息刷新到控件上
            int rank = subject.getRank();
            text_rank.setTextColor(getRankTextColor(rank));
            if (rank < 4) {
                text_rank.setText(RANK[rank]);
            } else {
                text_rank.setText(String.format("%d%s", rank, RANK[0]));
            }
            image_isNew.setVisibility(subject.getNewX() ? View.VISIBLE : View.GONE);
            SimpleSubjectBean simSubject = subject.getSubject();
            float rating = (float) simSubject.getRating().getAverage();
            if (rating == 0) {
                text_no_rating.setVisibility(View.VISIBLE);
                layout_rating.setVisibility(View.GONE);
            } else {
                ratingBar.setRating(rating / 2);
                text_rating.setText(String.format("%s", rating));
            }
            text_title.setText(simSubject.getTitle());
//            加载图片(Picasso)
            Picasso.with(mContext)
                    .load(simSubject.getImages().getLarge())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(image_film);
        }

        private int getRankTextColor(int rank) {
            switch (rank) {
                case FIRST:
                    return Color.parseColor(GOLD);
                case SECOND:
                    return Color.parseColor(SILVERY);
                case THIRD:
                    return Color.parseColor(COPPER);
                default:
                    return Color.GRAY;
            }
        }
    }
}



















