package saveteam.com.ridesharing.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.here.android.mpa.search.DiscoveryResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.SearchPlaceHolder> {
    Context context;
    List<DiscoveryResult> results;
    OnClickItemSearchPlaceListener listener;

    public interface OnClickItemSearchPlaceListener {
        void selected(int position);
    }

    public SearchPlaceAdapter(Context context, List<DiscoveryResult> results) {
        this.context = context;
        this.results = results;
    }

    public void setOnClickItemSearchPlaceListener(OnClickItemSearchPlaceListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchPlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_where_search_place, viewGroup, false);
        return new SearchPlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPlaceHolder searchPlaceHolder, int i) {
        final DiscoveryResult result = results.get(i);
        final int position = i;
        searchPlaceHolder.tv_place_name.setText(result.getTitle());
        searchPlaceHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.selected(position);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class SearchPlaceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_place_name_where_item_search_place)
        TextView tv_place_name;
        @BindView(R.id.layout_where_item_search_place)
        LinearLayout layout;

        public SearchPlaceHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}