package opensourceproject.kanjisteps;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class Practice_Activity extends ActionBarActivity {

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_layout);
        layout = (LinearLayout) View.inflate(this, R.layout.activity_practice_layout, null);
        loadLevels();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This is the prototype function for when a LEVEL is pressed in the practice
    //Menu. This is just a place holder. Eventually, the user will be able to create
    //their own levels, in which case we need to pass in the ID, or something from
    //the button that will determine what we do in the function.
    /*
    public void lvlPressed(View view, *btnID_Level)
    {
        *create the intent here*
        *Call the new activity and pass in the values from the btnID_Level*
    }
     */
    public void lvl1Pressed(View view) {
        Button b = (Button) view;
        String btnID = b.getText().toString();

        /*
        The following statement is performed in order to extract the level from the button,
        which is the only way I know how to grab the level based on what button the user
        clicked. As of now, the level buttons will be hardcoded as "LEVEL <number>",
        so doing a substring on the 6th index to the rest is sufficient for now to only
        grab the integer.
         */


        Intent openPSlides = new Intent("android.intent.action.PRACTICE_SLIDES");
        openPSlides.putExtra("INITIALIZE_LEVEL", btnID);
        startActivity(openPSlides);

    }

    public void loadLevels() {
        KanjiToStudyAdapter db = new KanjiToStudyAdapter(this);

        ArrayList<String> levels = db.getLevels();

        for (int i = 0; i < levels.size(); i++) {
            final Button btn = new Button(this);
            btn.setText(levels.get(i));
            btn.setId(i);
            btn.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lvl1Pressed(v);
                }
            });
            layout.addView(btn);
            setContentView(layout);
        }
    }
}
