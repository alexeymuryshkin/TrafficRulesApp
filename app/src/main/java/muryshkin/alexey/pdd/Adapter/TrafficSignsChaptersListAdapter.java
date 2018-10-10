package muryshkin.alexey.pdd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;

import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

/**
 * Created by 123 on 7/7/2016.
 */
public class TrafficSignsChaptersListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    public TrafficSignsChaptersListAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DataHolder.getDataHolder().signs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.row_chapter_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            viewHolder.titleTextView.setText(DataHolder.getDataHolder().signs.get(i).getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class ViewHolder {
        TextView titleTextView;

        public ViewHolder(View v) {
            titleTextView = v.findViewById(R.id.titleTextView);
        }
    }
}
