package yuut.icinema.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuut.icinema.R;
import yuut.icinema.bean.SimpleSubjectBean;
import yuut.icinema.support.Util.CelebrityUtil;
import yuut.icinema.support.Util.StringUtil;

/**
 * Created by yuut on 2017/4/22.
 */

public class SearchAdapter extends BaseAdapter<SearchAdapter.ViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SimpleSubjectBean> mData;

    public SearchAdapter(Context context, List<SimpleSubjectBean> data) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_layout, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_search_images)
        ImageView image_film;
        @Bind(R.id.rb_item_search_rating)
        RatingBar rating_bar;
        @Bind(R.id.tv_item_search_rating)
        TextView text_rating;
        @Bind(R.id.tv_item_search_collect_count)
        TextView text_collect_count;
        @Bind(R.id.tv_item_search_title)
        TextView text_title;
        @Bind(R.id.tv_item_search_original_title)
        TextView text_original_title;
        @Bind(R.id.tv_item_search_genres)
        TextView text_genres;
        @Bind(R.id.tv_item_search_director)
        TextView text_director;
        @Bind(R.id.tv_item_search_cast)
        TextView text_cast;

        SimpleSubjectBean subj;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }
        public void update() {
            subj = mData.get(getLayoutPosition());
            rating_bar.setRating(((float) subj.getRating().getAverage()) / 2);
            text_rating.setText(String.format("%s", subj.getRating().getAverage()));
            text_collect_count.setText("(");
            text_collect_count.append(String.format("%d", subj.getCollect_count()));
            text_collect_count.append("人评价)");
            text_title.setText(subj.getTitle());
            if (subj.getOriginal_title().equals(subj.getTitle())) {
                text_original_title.setVisibility(View.GONE);
            } else {
                text_original_title.setText(subj.getOriginal_title());
            }
            text_genres.setText(StringUtil.getListString(subj.getGenres(), ','));
            text_director.setText("导演: ");
            text_director.append(CelebrityUtil.list2String(subj.getDirectors(), '/'));
            text_cast.setText("主演: ");
            text_cast.append(CelebrityUtil.list2String(subj.getCasts(), '/'));
            Picasso.with(mContext)
                    .load(subj.getImages().getLarge())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(image_film);
        }
        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                mCallback.onItemClick(mData.get(position).getId(),
                        mData.get(position).getImages().getLarge());
            }
        }
    }
}
