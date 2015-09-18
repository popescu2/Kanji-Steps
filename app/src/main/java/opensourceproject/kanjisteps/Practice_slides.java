/*
COPYRIGHT (c) 2015 Matthew Popescu
This is licensed under GNU General Public License
Detailed Licensing information can be found in the COPYING file
 */
package opensourceproject.kanjisteps;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import android.view.View.OnClickListener;
import static android.view.GestureDetector.*;



public class Practice_slides extends ActionBarActivity implements
        OnClickListener {

    public String level_marker = "";
    private String correctAnswer = "";
    private boolean Switch = false;
    private GestureDetector gd;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_slides);

        gd = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                return gd.onTouchEvent(e);
            }
        };

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            level_marker = extras.getString("INITIALIZE_LEVEL");
        }
        if (quizByLevelOnyomi() == 1 && quizByLevelMeaning() == 1)
        {
            resetButtons();

            //no review notice.
        }
    }

    public void onClick(View v)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_slides, menu);
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



    public void setLevel_marker(String p)
    {
        level_marker = p;
    }


    /*
    This function grabs all the items from the database which are of a certain level.
    It also orders the items randomly.

    -Grab items of <level> randomly
    -Grab the first item. (this first item will be random
    -Set the text of the main page display to the text retrieved from database.
    -That's it. The program only displays one "kanji", and will return.
    -The user will then select the correct reading from several options.
     */
    public int quizByLevelOnyomi() {
        KanjiToStudyAdapter dbAdapter = new KanjiToStudyAdapter(this);
        Cursor cursor = dbAdapter.getItemsByLevelRandom(level_marker, 0);
        TextView txt = (TextView) findViewById(R.id.textToDisplay);
        TextView txtDblTap = (TextView)findViewById(R.id.textDoubleTap);
        txtDblTap.setOnClickListener(this);
        txtDblTap.setOnTouchListener(gestureListener);
        txtDblTap.setVisibility(View.INVISIBLE);

        String temp = "";
        if (cursor.moveToNext()) {
            int indexOfKanji = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_KANJI);
            temp = cursor.getString(indexOfKanji);

            txt.setText(temp);
            txt.setTextSize(50);
            txt.setTextColor(Color.GRAY);
            txt.setTag("2"); //2 means japanese READING
            populateButtonChoicesOnyomi(cursor, dbAdapter);
            return 0;
        }
        else
        {
            /*
            txt.setTextSize(20);
            txt.setTextColor(Color.GRAY);
            txt.setText("You don't have any items to review yet! Check back later.");
            resetButtons();
            */
            return 1;
        }
    }



    public void populateButtonChoicesOnyomi(Cursor cursor, KanjiToStudyAdapter dbAdapter)
    {
        //randomly place the correct answer in a button
        int[] answer_array = {1,2,3,4};
        shuffleArray(answer_array);
        int indexOfKanji = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_KANJI);
        String kanjiToExclude = cursor.getString(indexOfKanji);
        boolean correct_answer_set = false;

        for(int i=0; i<4; i++)
        {
            Button btn;
            if(answer_array[i] == 1) {
                btn = (Button) findViewById(R.id.btnMultipleChoice1);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else if (answer_array[i] ==2)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice2);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else if(answer_array[i]==3)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice3);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice4);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }

            //indexes of onyomi and kanji to access columns.
            int indexOfOnyomi = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_ON_READING);

            //grab the onyomi and kanji strings from where the cursor is pointing RIGHT NOW
            String onyomi = cursor.getString(indexOfOnyomi);

            //reset cursor to grab new set of data, this time it can have all levels
            //but it will EXCLUDE the row with the kanji we are loading in below.
            if(!correct_answer_set) {
                cursor = dbAdapter.getItemsByLevel_excludeReading(level_marker, onyomi);
                correctAnswer = Integer.toString(answer_array[i]);
            }
            correct_answer_set = true;

            cursor.moveToNext();

            btn.setText(onyomi);
        }

    }

    //This code was pulled from StackOverFlow.com
    // Implementing Fisher–Yates shuffle
    public void shuffleArray(int[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }


    /*
    This function is called when the user selects one of the multiple choice options.
    It grabs the text displayed (the kanji), grabs what the onyomi reading should be,
    and compares it with the onyomi reading that the user selected.

    If the user selected the correct answer, we increase the progress for that kanji in
    the database by one.
    else, decrease the progress by one.
     */
    private long lastClick = 0;

    public void btnAnswer(View view)
    {


        /*
        String userAnswer;
        String kanjiDisplayed;
        String correctAnswer;

        TextView textView = (TextView)findViewById(R.id.textToDisplay);
        Button button = (Button)view;

        userAnswer = button.getText().toString();
        kanjiDisplayed = textView.getText().toString();

        KanjiToStudyAdapter dbAdapter = new KanjiToStudyAdapter(this);
        correctAnswer = dbAdapter.getOnyomiByKanji(kanjiDisplayed);
        */

        String kanjiDisplayed;
        TextView textView = (TextView)findViewById(R.id.textToDisplay);
        kanjiDisplayed = textView.getText().toString();

        if(kanjiDisplayed.equals("You don't have any items to review yet! Check back later.")) return;

        String userAnswer;
        String quizTag; //1 means ENGLISH MEANING, 2 means JAPANESE READING
        Button button = (Button)view;
        userAnswer = button.getTag().toString();
        quizTag = textView.getTag().toString();

        new asyncCaller().execute(userAnswer, correctAnswer, kanjiDisplayed, quizTag);

        //quizByLevel();
    }

    public void populateButtonChoicesMeaning(Cursor cursor, KanjiToStudyAdapter dbAdapter)
    {
        int[] answer_array = {1,2,3,4};
        shuffleArray(answer_array);
        int indexOfKanji = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_KANJI);
        String kanjiToExclude = cursor.getString(indexOfKanji);
        boolean correct_answer_set = false;

        for(int i=0; i<4; i++)
        {
            Button btn;
            if(answer_array[i] == 1) {
                btn = (Button) findViewById(R.id.btnMultipleChoice1);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else if (answer_array[i] ==2)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice2);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else if(answer_array[i]==3)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice3);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }
            else
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice4);
                btn.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            }

            //indexes of onyomi and kanji to access columns.
            int indexOfMeaning = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_MEANING);

            //grab the onyomi and kanji strings from where the cursor is pointing RIGHT NOW
            String meaning = cursor.getString(indexOfMeaning);

            //reset cursor to grab new set of data, this time it can have all levels
            //but it will EXCLUDE the row with the kanji we are loading in below.
            if(!correct_answer_set) {
                cursor = dbAdapter.getItemsByLevel_excludeMeaning(level_marker, kanjiToExclude);
                correctAnswer = Integer.toString(answer_array[i]);
            }
            correct_answer_set = true;

            cursor.moveToNext();

            btn.setText(meaning);
        }
    }

    public int quizByLevelMeaning()
    {
        KanjiToStudyAdapter dbAdapter = new KanjiToStudyAdapter(this);
        Cursor cursor = dbAdapter.getItemsByLevelRandom(level_marker, 1);
        TextView txt = (TextView) findViewById(R.id.textToDisplay);
        TextView txtDblTap = (TextView)findViewById(R.id.textDoubleTap);
        txtDblTap.setOnClickListener(this);
        txtDblTap.setOnTouchListener(gestureListener);
        txtDblTap.setVisibility(View.INVISIBLE);
        String temp = "";
        if (cursor.moveToNext()) {
            int indexOfKanji = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_KANJI);
            temp = cursor.getString(indexOfKanji);

            txt.setText(temp);
            txt.setTextSize(50);
            txt.setTextColor(Color.GRAY);
            txt.setTag("1"); //1 means ENGLISH meaning
            populateButtonChoicesMeaning(cursor, dbAdapter);
            return 0;
        }
        else
        {
            /*
            txt.setText("You don't have any items to review yet! Check back later.");
            txt.setTextSize(20);
            txt.setTextColor(Color.GRAY);

            resetButtons();
            */
            return 1; //display notice that there is nothing to review yet.
        }

    }


    class asyncCaller extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... params)
        {
            KanjiToStudyAdapter myKanjidb = new KanjiToStudyAdapter(Practice_slides.this);
            if(params[0].equals(params[1]))
            {
                //do code here
                //textView.setTextColor(Color.GREEN);
                publishProgress("#7fff00", params[1]);
                myKanjidb.correctAnswer(params[2], params[3]);
            }
            else
            {
                publishProgress("#ff0000", params[1]);
                myKanjidb.incorrectAnswer(params[2], params[3]);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            TextView txtTest = (TextView)findViewById(R.id.textToDisplay);
            TextView txtDblTap = (TextView)findViewById(R.id.textDoubleTap);
            Button btn;

            if (values[0].equals("#ff0000"))
            {
                if(values[1].equals("1"))
                {
                    btn = (Button)findViewById(R.id.btnMultipleChoice1);
                    btn.getBackground().setColorFilter(Color.parseColor("#04C91B"), PorterDuff.Mode.SRC_ATOP);
                }
                else if (values[1].equals("2"))
                {
                    btn = (Button)findViewById(R.id.btnMultipleChoice2);
                    btn.getBackground().setColorFilter(Color.parseColor("#04C91B"), PorterDuff.Mode.SRC_ATOP);
                }
                else if (values[1].equals("3"))
                {
                    btn = (Button)findViewById(R.id.btnMultipleChoice3);
                    btn.getBackground().setColorFilter(Color.parseColor("#04C91B"), PorterDuff.Mode.SRC_ATOP);
                }
                else if (values[1].equals("4"))
                {
                    btn = (Button)findViewById(R.id.btnMultipleChoice4);
                    btn.getBackground().setColorFilter(Color.parseColor("#04C91B"), PorterDuff.Mode.SRC_ATOP);
                }
            }

            txtDblTap.setVisibility(View.VISIBLE);
            txtTest.setTextColor(Color.parseColor(values[0]));
            //onPostExecute(values[1]);

        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }

    }

    public void resetButtons()
    {
        TextView txt = (TextView)findViewById(R.id.textToDisplay);
        txt.setText("You don't have any items to review yet! Check back later.");
        txt.setTextSize(20);
        txt.setTextColor(Color.GRAY);


        Button btn1 = (Button)findViewById(R.id.btnMultipleChoice1);
        Button btn2 = (Button)findViewById(R.id.btnMultipleChoice2);
        Button btn3 = (Button)findViewById(R.id.btnMultipleChoice3);
        Button btn4 = (Button)findViewById(R.id.btnMultipleChoice4);

        btn1.setText("");
        btn2.setText("");
        btn3.setText("");
        btn4.setText("");
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            if (Switch) {
                Switch = false;
                if (quizByLevelOnyomi() == 1 && quizByLevelMeaning() == 1)
                    resetButtons();
            } else {
                Switch = true;
                if (quizByLevelMeaning() == 1 && quizByLevelOnyomi() == 1)
                    resetButtons();
            }

            return true;
        }
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }
    }

}
