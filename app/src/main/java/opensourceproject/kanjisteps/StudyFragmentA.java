package opensourceproject.kanjisteps;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Bob on 9/21/2015.
 */
public class StudyFragmentA extends Fragment{
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        populateCard(savedInstanceState, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }


    public void populateCard(Bundle args, View view)
    {

        TextView txtKanji = (TextView)view.findViewById(R.id.txtKanji);
        TextView txtMeaning = (TextView)view.findViewById(R.id.txtEngMeaning);
        TextView txtOnyomi = (TextView)view.findViewById(R.id.txtOnyomi);
        TextView txtKunyomi = (TextView)view.findViewById(R.id.txtKunyomi);

        txtKanji.setText("Kanji: " + getArguments().getString("kanji"));
        txtMeaning.setText("English Definition: " + getArguments().getString("meaning"));
        txtOnyomi.setText("Onyomi Reading: " + getArguments().getString("onyomi"));
        txtKunyomi.setText("Kunyomi Reading: " + getArguments().getString("kunyomi"));

    }
}
