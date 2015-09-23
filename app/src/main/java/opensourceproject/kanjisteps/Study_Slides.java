/*
COPYRIGHT (c) 2015 Matthew Popescu
This is licensed under GNU General Public License
Detailed Licensing information can be found in the COPYING file
 */

package opensourceproject.kanjisteps;

import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.GestureDetector.*;


public class Study_Slides extends FragmentActivity {

    ViewPager viewPager = null;
    RelativeLayout relativeLayout = null;

    public String level_marker;
    KanjiToStudyAdapter db;
    Cursor cursor;
    int currentCursorPosition;


    //private GestureDetectorCompat gestureDetect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study__slides);
        //gestureDetect = new GestureDetectorCompat(this, this);
        //gestureDetect.setOnDoubleTapListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            level_marker = extras.getString("INITIALIZE_LEVEL");
        }

        db = new KanjiToStudyAdapter(this);
        cursor = db.getItemsByLevel(level_marker);
        cursor.moveToNext();

        currentCursorPosition = cursor.getPosition();

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new CardAdapter(fragmentManager));

        //StudyByLevel(cursor, db);
    }

    class CardAdapter extends FragmentStatePagerAdapter {

        public CardAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            Log.d("Matt", "get count called");
            int temp = cursor.getCount();
            return temp;
        }

        @Override
        public Fragment getItem(int i) {
            Log.d("Matt", "get item is called " + i);

            cursor.moveToPosition(i);

            int count = cursor.getCount();

            Fragment fragment = null;

            Bundle args = new Bundle();

            if(i==cursor.getPosition()) {
                //cursor.moveToPrevious();
                setBundleArgs(cursor, args);
                if (i == count -1)
                    fragment = new StudyFragmentB();
                else fragment = new StudyFragmentA();
                fragment.setArguments(args);
                //cursor.moveToNext();

            }
            /*
            else if(i==currentCursorPosition+1)
            {
                setBundleArgs(cursor, args);
                fragment = new StudyFragmentA();
                fragment.setArguments(args);
                cursor.moveToNext();
            }
            else if(i==currentCursorPosition+2)
            {
                //cursor.moveToNext();
                setBundleArgs(cursor, args);
                fragment = new StudyFragmentA();
                fragment.setArguments(args);
                //cursor.moveToPrevious();
                cursor.moveToNext();
            }
            */
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            int currentCursorLocation = cursor.getPosition();
            int kanjiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_KANJI);
            Log.d("Matt", "get title called on " + position);
            cursor.moveToPosition(position);
            String title = cursor.getString(kanjiColumn);
            cursor.moveToPosition(currentCursorLocation);

            return title;
        }
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

    public void setBundleArgs(Cursor cursor, Bundle args)
    {
        int kanjiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_KANJI);
        int meaningColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_MEANING);
        int onyomiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_ON_READING);
        int kunyomiColumn = cursor.getColumnIndex(db.myKanjiDb.COLUMN_KUN_READING);

        args.putString("kanji", cursor.getString(kanjiColumn));
        args.putString("meaning", cursor.getString(meaningColumn));
        args.putString("onyomi", cursor.getString(onyomiColumn));
        args.putString("kunyomi", cursor.getString(kunyomiColumn));

    }
}
