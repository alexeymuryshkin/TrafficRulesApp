package muryshkin.alexey.pdd.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import muryshkin.alexey.pdd.Activity.TestPlayActivity;
import muryshkin.alexey.pdd.Adapter.AnswersListViewAdapter;
import muryshkin.alexey.pdd.Data.DataHolder;
import muryshkin.alexey.pdd.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    private static final String ARG_PARAM1 = "questionPosition";

    private int questionPosition;
    private AnswersListViewAdapter adapter;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(int param1) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionPosition = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question, container, false);

        final TextView questionNumberTextView = (TextView) v.findViewById(R.id.questionNumberTextView);
        final TextView questionTextView = (TextView) v.findViewById(R.id.questionTextView);
        final ImageView questionImageView = (ImageView) v.findViewById(R.id.questionImageView);
        final ListView answersListView = (ListView) v.findViewById(R.id.answersListView);

        try {
            final JSONObject question = DataHolder.getDataHolder().test.getJSONArray("questions").getJSONObject(questionPosition);

            questionNumberTextView.setText("Вопрос " + (questionPosition + 1) + " из " + DataHolder.getDataHolder().answers.size());

            if (question.getString("imageURL").length() > 0) {
                questionTextView.setText("Загрузка вопроса...");

                Glide.with(getContext()).load(question.getString("imageURL")).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        try {
                            questionTextView.setText(question.getString("question"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }).into(questionImageView);
            } else
                questionTextView.setText(question.getString("question"));

            answersListView.setAdapter(displayAnswers(question));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        answersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataHolder.getDataHolder().answers.get(questionPosition) == 100) {
                    DataHolder.getDataHolder().answered++;
                    DataHolder.getDataHolder().answers.set(questionPosition, i);

                    if (i == DataHolder.getDataHolder().correctAnswers.get(questionPosition))
                        DataHolder.getDataHolder().result++;

                    ((TestPlayActivity)getActivity()).setCurrentItem(questionPosition + 1, true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return v;
    }

    private AnswersListViewAdapter displayAnswers(JSONObject question) throws JSONException {

        List<String> answers = new LinkedList<>();
        int position;

        if (DataHolder.getDataHolder().correctAnswers.get(questionPosition) == 100) {
            Random rn = new Random();
            position = rn.nextInt(question.getJSONArray("incorrect").length() + 1);
            DataHolder.getDataHolder().correctAnswers.set(questionPosition, position);
        } else
            position = DataHolder.getDataHolder().correctAnswers.get(questionPosition);

        for (int i = 0; i < question.getJSONArray("incorrect").length(); i++) {
            if (i == position)
                answers.add(question.getString("correct"));
            answers.add(question.getJSONArray("incorrect").getJSONObject(i).getString("answer"));
        }

        if (position == question.getJSONArray("incorrect").length())
            answers.add(question.getString("correct"));

        adapter = new AnswersListViewAdapter(getContext(), answers, questionPosition);
        return adapter;
    }
}
