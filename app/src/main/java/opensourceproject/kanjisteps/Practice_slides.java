package opensourceproject.kanjisteps;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Practice_slides extends ActionBarActivity {

    public int level_marker = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_slides);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            level_marker = extras.getInt("INITIALIZE_LEVEL");
        }
        quizByLevel();
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



    public void setLevel_marker(int p)
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
    public void quizByLevel() {
        KanjiToStudyAdapter dbAdapter = new KanjiToStudyAdapter(this);
        Cursor cursor = dbAdapter.getItemsByLevel(level_marker);
        TextView txt = (TextView) findViewById(R.id.textToDisplay);
        String temp = "";
        if (cursor.moveToNext()) {
            int indexOfKanji = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_KANJI);
            temp = cursor.getString(indexOfKanji);

            txt.setText(temp);
            txt.setTextSize(50);
            txt.setTextColor(Color.GRAY);
            populateButtonChoices(cursor, dbAdapter);
        }
    }

    public void populateButtonChoices(Cursor cursor, KanjiToStudyAdapter dbAdapter)
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
            }
            else if (answer_array[i] ==2)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice2);
            }
            else if(answer_array[i]==3)
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice3);
            }
            else
            {
                btn = (Button) findViewById(R.id.btnMultipleChoice4);
            }

            //indexes of onyomi and kanji to access columns.
            int indexOfOnyomi = cursor.getColumnIndex(dbAdapter.myKanjiDb.COLUMN_ON_READING);

            //grab the onyomi and kanji strings from where the cursor is pointing RIGHT NOW
            String onyomi = cursor.getString(indexOfOnyomi);

            //reset cursor to grab new set of data, this time it can have all levels
            //but it will EXCLUDE the row with the kanji we are loading in below.
            if(!correct_answer_set) dbAdapter.getItemsByLevel_excludeItem(level_marker, kanjiToExclude);
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
    public void btnAnswer(View view)
    {
        String userAnswer;
        String kanjiDisplayed;
        String correctAnswer;

        TextView textView = (TextView)findViewById(R.id.textToDisplay);
        Button button = (Button)view;

        userAnswer = button.getText().toString();
        kanjiDisplayed = textView.getText().toString();

        KanjiToStudyAdapter dbAdapter = new KanjiToStudyAdapter(this);
        correctAnswer = dbAdapter.getOnyomiByKanji(kanjiDisplayed);

        new asyncCaller().execute(userAnswer, correctAnswer);

        //quizByLevel();
    }


    class asyncCaller extends AsyncTask<String, String, Void>{


        @Override
        protected Void doInBackground(String... params)
        {
            if(params[0].equals(params[1]))
            {
                //do code here
                //textView.setTextColor(Color.GREEN);
                publishProgress("#7fff00");
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            else
            {
                publishProgress("#ff0000");
                try {
                    Thread.sleep(2000);
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            TextView txt = (TextView)findViewById(R.id.textToDisplay);

            txt.setTextColor(Color.parseColor(values[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            quizByLevel();
        }
    }
}
