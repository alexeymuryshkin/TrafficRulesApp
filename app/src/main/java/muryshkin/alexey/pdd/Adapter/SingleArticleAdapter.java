package muryshkin.alexey.pdd.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import muryshkin.alexey.pdd.R;

/**
 * Created by 123 on 7/12/2016.
 */
public class SingleArticleAdapter extends BaseAdapter {

    private JSONArray paragraphs;
    private Context context;
    private LayoutInflater inflater;

    public SingleArticleAdapter(Context context, JSONArray paragraphs) {
        this.context = context;
        this.paragraphs = paragraphs;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return paragraphs.length();
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

        String image = null;

        try {
            image = paragraphs.getJSONObject(i).getString("imageURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ViewHolder viewHolder;

        if (view == null) {
            if (image == null)
                view = inflater.inflate(R.layout.row_article_item, viewGroup, false);
            else
                view = inflater.inflate(R.layout.row_article_image_item, viewGroup, false);
            viewHolder = new ViewHolder(view, image != null);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            viewHolder.articleTextView.setText(paragraphs.getJSONObject(i).getString("paragraph"));

            if (image != null) {
                Glide.with(context).load(context.getResources().getIdentifier("@drawable/" + image, null, context.getPackageName())).into(viewHolder.imageView);

                String str = "";
                for (int j = 1; j < image.length(); j++)
                    if (image.charAt(j) == '_')
                        str += '.';
                    else
                        str += image.charAt(j);

                viewHolder.imageTextView.setText(str);

                viewHolder.separatorView.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class ViewHolder {
        TextView articleTextView;
        ImageView imageView;
        TextView imageTextView;
        View separatorView;

        public ViewHolder(View v, boolean isImage) {
            articleTextView = (TextView) v.findViewById(R.id.articleTextView);
            if (isImage) {
                imageView = (ImageView) v.findViewById(R.id.imageView);
                imageTextView = (TextView) v.findViewById(R.id.imageTextView);
                separatorView = v.findViewById(R.id.separatorView);
            }
        }
    }
}
