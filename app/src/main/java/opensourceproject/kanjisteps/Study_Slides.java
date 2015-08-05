/*
COPYRIGHT (c) 2015 Matthew Popescu
This is licensed under GNU General Public License
Detailed Licensing information can be found in the COPYING file
 */

package opensourceproject.kanjisteps;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.GestureDetector.*;


public class Study_Slides extends ActionBarActivity implements
            OnGestureListener,OnDoubleTapListener{

    public String level_marker;
    KanjiToStudyAdapter db;
    Cursor cursor;
    LinearLayout layout;

    private GestureDetectorCompat gestureDetect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study__slides);
        gestureDetect = new GestureDetectorCompat(this, this);
        gestureDetect.setOnDoubleTapListener(this);
        layout = (LinearLayout) View.inflate(this, R.layout.activity_study__slides, null);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            level_marker = extras.getString("INITIALIZE_LEVEL");
        }

        db = new KanjiToStudyAdapter(this);
        cursor = db.getItemsByLevel(level_marker);
        cursor.moveToNext();

        StudyByLevel(cursor, db);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study__slides, menu);
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

    public void StudyByLevel(Cursor cursor, KanjiToStudyAdapter db)
    {
        //KanjiToStudyAdapter db = new KanjiToStudyAdapter(this);
        //Cursor cursor = db.getItemsByLevel(level_marker);

        int kanjiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_KANJI);
        int meaningColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_MEANING);
        int onyomiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_ON_READING);
        int kunyomiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_KUN_READING);

        //cursor.moveToNext();
        TextView txtKanji = (TextView)findViewById(R.id.txtKanji);
        TextView txtMeaning = (TextView)findViewById(R.id.txtEngMeaning);
        TextView txtOnyomi = (TextView)findViewById(R.id.txtOnyomi);
        TextView txtKunyomi = (TextView)findViewById(R.id.txtKunyomi);

        txtKanji.setText("Kanji: " + cursor.getString(kanjiColumn));
        txtMeaning.setText("English Definition: " + cursor.getString(meaningColumn));
        txtOnyomi.setText("Onyomi Reading: " + cursor.getString(onyomiColumn));
        txtKunyomi.setText("Kunyomi Reading: " + cursor.getString(kunyomiColumn));


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //TextView txt = (TextView)findViewById(R.id.txtKanji);
        Button btn = (Button) findViewById(R.id.btnLastItem);

        if(velocityX < -2500) {
            if(cursor.moveToNext())
            {
                //cursor.moveToNext();
                StudyByLevel(cursor, db);
                btn.setVisibility(View.INVISIBLE);
            }
            else
            {
                cursor.moveToPrevious();
                btn.setVisibility(View.VISIBLE);

            }
        }
        else if(velocityX > 2500) {
            if(cursor.moveToPrevious())
            {
                StudyByLevel(cursor, db);
                btn.setVisibility(View.INVISIBLE);
            }
            else cursor.moveToNext();
        }
        return false;
    }

    public void goToQuizByLevel(View view)
    {
        KanjiToStudyAdapter myKanjiDb = new KanjiToStudyAdapter(this);
        myKanjiDb.upgradeToReview(level_marker);
        Intent openPSlides = new Intent("android.intent.action.PRACTICE_SLIDES");
        openPSlides.putExtra("INITIALIZE_LEVEL", level_marker);
        startActivity(openPSlides);
    }

    public void lastItemPressed(View view)
    {
        goToQuizByLevel(view);
    }
}
