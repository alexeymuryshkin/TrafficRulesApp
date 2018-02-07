package muryshkin.alexey.pdd.Adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import muryshkin.alexey.pdd.R;

/**
 * Created by 123 on 7/12/2016.
 */
public class SearchResultsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Pair<JSONObject, Integer>> entries;

    public SearchResultsAdapter(Context context, List<Pair<JSONObject, Integer>> entries) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
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

        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(R.layout.row_search_result_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            JSONArray paragraphs = entries.get(i).first.getJSONArray("paragraphs");

            int j = 0;
            while (paragraphs.getJSONObject(j).getString("paragraph").length() == 0 && j < paragraphs.length())
                j++;

            viewHolder.titleTextView.setText("    " + paragraphs.getJSONObject(j).getString("paragraph").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class ViewHolder {
        TextView titleTextView;

        public ViewHolder(View v) {
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        }
    }
}
