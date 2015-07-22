package opensourceproject.kanjisteps;

import android.app.ActionBar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class Study_Menu extends ActionBarActivity {

    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study__menu);
        layout = (LinearLayout) View.inflate(this, R.layout.activity_study__menu, null);
        loadLevels();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study__menu, menu);
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

    public void loadLevels()
    {
        KanjiToStudyAdapter db = new KanjiToStudyAdapter(this);
        int i = 1;
        Cursor cursor = db.getItemsByLevel(i);
        int lvlColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_LEVEL);

        while(cursor.moveToNext())
        {
            Button btn = new Button(this);
            btn.setText(Integer.toString(cursor.getInt(lvlColumn)));
            btn.setId(1+i);
            btn.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(btn);
            setContentView(layout);
            i++;
            cursor = db.getItemsByLevel(i);
        }
    }
}
