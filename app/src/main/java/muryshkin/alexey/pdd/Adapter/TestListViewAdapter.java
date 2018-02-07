package muryshkin.alexey.pdd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.backendless.Backendless;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import muryshkin.alexey.pdd.Activity.TestActivity;
import muryshkin.alexey.pdd.Activity.TestPlayActivity;
import muryshkin.alexey.pdd.Activity.TestResultActivity;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.Data.Test;
import muryshkin.alexey.pdd.R;

/**
 * Created by 123 on 7/8/2016.
 */
public class TestListViewAdapter extends BaseAdapter {

    private static final String TAG = "TestActivityAdapter";

    private Context context;
    private LayoutInflater inflater;

    public TestListViewAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DataHolder.getDataHolder().bTests.size();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        int layout;

        if (DataHolder.getDataHolder().isLoggedIn)
            layout = R.layout.row_test_item_loggedin;
        else
            layout = R.layout.row_test_item;

        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.testNumberTextView.setText("" + (i + 1));
        viewHolder.testTitleTextView.setText(DataHolder.getDataHolder().bTests.get(i).getTitle().toString());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)context).getJSONTest(i);
            }
        });

        view.findViewById(R.id.goToTestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)context).getJSONTest(i);
            }
        });

        if (Backendless.UserService.CurrentUser() != null) {
            int maxCorrect = DataHolder.getDataHolder().maxCorrect.get(i);

            if (maxCorrect >= 0)
                viewHolder.progressTextView.setText("Правильно " + maxCorrect + " из 40");
            else if (maxCorrect == -1)
                viewHolder.progressTextView.setText("40 вопросов");

            if (maxCorrect >= 16) {
                viewHolder.firstStarImageView.setImageResource(context.getResources().getIdentifier("@drawable/yellow_star", null, context.getPackageName()));

                if (maxCorrect >= 24) {
                    viewHolder.secondStarImageView.setImageResource(context.getResources().getIdentifier("@drawable/yellow_star", null, context.getPackageName()));

                    if (maxCorrect >= 32) {
                        viewHolder.thirdStarImageView.setImageResource(context.getResources().getIdentifier("@drawable/yellow_star", null, context.getPackageName()));
                    }
                }
            }
        }

        return view;
    }

    private class ViewHolder {
        TextView testTitleTextView;
        TextView testNumberTextView;
        TextView progressTextView;
        ImageView firstStarImageView;
        ImageView secondStarImageView;
        ImageView thirdStarImageView;

        public ViewHolder(View v) {
            testTitleTextView = (TextView) v.findViewById(R.id.testTitleTextView);
            testNumberTextView = (TextView) v.findViewById(R.id.testNumberTextView);
            progressTextView = (TextView) v.findViewById(R.id.progressTextView);
            firstStarImageView = (ImageView) v.findViewById(R.id.firstStarImageView);
            secondStarImageView = (ImageView) v.findViewById(R.id.secondStarImageView);
            thirdStarImageView = (ImageView) v.findViewById(R.id.thirdStarImageView);
        }
    }
}
