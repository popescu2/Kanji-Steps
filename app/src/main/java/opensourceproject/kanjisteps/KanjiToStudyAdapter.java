/*
COPYRIGHT (c) 2015 Matthew Popescu
This is licensed under GNU General Public License
Detailed Licensing information can be found in the COPYING file
 */

package opensourceproject.kanjisteps;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
//branch master
/**
 * Created by Bob on 7/6/2015.
 */
public class KanjiToStudyAdapter {

    KanjiToStudy myKanjiDb;

    public KanjiToStudyAdapter(Context context)    {
        myKanjiDb = new KanjiToStudy(context);
    }

    //I think this will be redundant. Maybe I can use this for the user creating quizzes...
    public long insertData(String kanji, String meaning, String onyomi, String kunyomi, int progress)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KanjiToStudy.COLUMN_KANJI, kanji);
        contentValues.put(KanjiToStudy.COLUMN_MEANING, meaning);
        contentValues.put(KanjiToStudy.COLUMN_ON_READING, onyomi);
        contentValues.put(KanjiToStudy.COLUMN_KUN_READING, kunyomi);
        contentValues.put(KanjiToStudy.COLUMN_PROGRESS, progress);
        long id=db.insert(KanjiToStudy.TABLE_NAME, null, contentValues);
        return id;
    }

    public String getOnyomiByKanji(String kanji)
    {
        //select _onReading from KANJI_TABLE where _kanji = '*some value*'
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        String[] columns = {myKanjiDb.COLUMN_ON_READING};
        //String[] whereArgs = {kanji};
        String where_clause = "_kanji = '" + kanji +"'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, where_clause, null, null, null, null);

        int indexOfOnyomi = cursor.getColumnIndex(myKanjiDb.COLUMN_ON_READING);
        cursor.moveToNext();
        String returnValue = cursor.getString(indexOfOnyomi);
        return returnValue;
    }

    //The way I am encoding the "progress" for the user for a particular
    //item is rather complicated.
    /*
    Basically, a regular number with no decimal values means an actual progress
    level. A number with some decimal value means it's a partial progress level.
    Since I am quizzing the user on Kanji reading AND kanji definition, they need
    to be linked. But the quizzing environment quizzes these at the same time, but
    I will only upgrade the progress if the user gets BOTH of them correct.

    The quiz tag means what kind of correct answer we got. 1 means ENGLISH MEANING,
    2 means JAPANESE READING.
    If you get the correct MEANING, I upgrade progress by +0.25
    If you get the correct READING, I upgrade progress by +0.5
     */

    public long correctAnswer(String correctAnswer, String quizTag)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        String[] columns = {myKanjiDb.COLUMN_PROGRESS};
        String where_clause = "_kanji = '" + correctAnswer + "'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, where_clause, null, null, null, null);

        int progressIndex = cursor.getColumnIndex(myKanjiDb.COLUMN_PROGRESS);

        cursor.moveToNext();
        float previousProgress = cursor.getFloat(progressIndex);
        float partial_progress = (float) (previousProgress - Math.floor(previousProgress));
        ContentValues cv = new ContentValues();

        if(quizTag.equals("1")) //answered MEANING correctly
        {
            if(partial_progress == 0) //fresh progress level.
            {
                previousProgress += 0.25;
                cv.put(myKanjiDb.COLUMN_PROGRESS, previousProgress);
                db.update(myKanjiDb.TABLE_NAME, cv, where_clause, null);
            }
            else if (partial_progress == 0.5) //previously got correct READING
            {
                previousProgress += 0.5;
                long nextReviewTime = getNextReviewTime(previousProgress);
                cv.put(myKanjiDb.COLUMN_PROGRESS, previousProgress);
                cv.put(myKanjiDb.COLUMN_TIME, nextReviewTime);
                db.update(myKanjiDb.TABLE_NAME, cv, where_clause, null);
            }
        }
        else                //answered READING correctly
        {
            if(partial_progress == 0) //fresh progress level.
            {
                previousProgress += 0.5;
                cv.put(myKanjiDb.COLUMN_PROGRESS, previousProgress);
                db.update(myKanjiDb.TABLE_NAME, cv, where_clause, null);
            }
            else if (partial_progress == 0.25) //previously got correct MEANING
            {
                previousProgress += 0.75;
                long nextReviewTime = getNextReviewTime(previousProgress);
                cv.put(myKanjiDb.COLUMN_PROGRESS, previousProgress);
                cv.put(myKanjiDb.COLUMN_TIME, nextReviewTime);
                db.update(myKanjiDb.TABLE_NAME, cv, where_clause, null);
            }
        }
        return 0;
    }

    public long incorrectAnswer(String correctAnswer, String quizTag)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        String[] columns = {myKanjiDb.COLUMN_PROGRESS};
        String where_clause = "_kanji = '" + correctAnswer + "'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, where_clause, null, null, null, null);

        int progressIndex = cursor.getColumnIndex(myKanjiDb.COLUMN_PROGRESS);

        cursor.moveToNext();
        float previousProgress = cursor.getFloat(progressIndex);
        float partial_progress = (float) (previousProgress - Math.floor(previousProgress));
        ContentValues cv = new ContentValues();

        if (previousProgress == 1) return 0; //progress cannot go lower than 1, so do nothing in this case.
        else
        {
            previousProgress = previousProgress - 1;
            cv.put(myKanjiDb.COLUMN_PROGRESS, previousProgress);

            db.update(myKanjiDb.TABLE_NAME, cv, "_kanji = '"+ correctAnswer + "'", null);
        }
        return 0;
    }

    public long getNextReviewTime(float previousProgress)
    {
        return (long) (new Date().getTime()+ (1000 * 60 * 60)*(1 << (int) previousProgress));
    }

    public Cursor getItemsByLevel(String lvl)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        //select _kanji, _meaning, _onReading, _kunReading, _progress, _level
        String[] columns = {myKanjiDb.COLUMN_KANJI, myKanjiDb.COLUMN_MEANING,
                myKanjiDb.COLUMN_KUN_READING, myKanjiDb.COLUMN_ON_READING, myKanjiDb.COLUMN_PROGRESS, myKanjiDb.COLUMN_LEVEL};
        //order by random()
        String where_clause = "_level = '" + lvl +"'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, where_clause, null, null, null, null);
        return cursor;
    }

    //the quiz flag tells what kind of question it is. 1 = MEANING, 0 = MEANING.
    public Cursor getItemsByLevelRandom(String lvl, int quizFlag)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        //select _kanji, _meaning, _onReading, _kunReading, _progress, _level
        String[] columns = {myKanjiDb.COLUMN_KANJI, myKanjiDb.COLUMN_MEANING,
                myKanjiDb.COLUMN_KUN_READING, myKanjiDb.COLUMN_ON_READING, myKanjiDb.COLUMN_PROGRESS, myKanjiDb.COLUMN_LEVEL};
        //order by random()
        long current_time = (long) (new Date().getTime());
        String where_clause;

        if(quizFlag == 1)
            where_clause = "_level = '" + lvl +"'" + " AND _time <= " + current_time + " AND _progress - (cast (_progress as int)) <> 0.25";
        else
            where_clause = "_level = '" + lvl +"'" + " AND _time <= " + current_time + " AND _progress - (cast (_progress as int)) <> 0.5";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, where_clause, null, null, null, "random()");
        return cursor;
    }

    public void upgradeToReview(String lvl)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long timeForNextReview = (long) (new Date().getTime());
        cv.put(KanjiToStudy.COLUMN_TIME, timeForNextReview);

        String whereClause = "_level = '" + lvl +"'";

        db.update(myKanjiDb.TABLE_NAME, cv, whereClause, null);
    }



    public Cursor getItemsByLevel_excludeMeaning(String lvl, String exclude)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        //select _onReading where _kanji != exclude order by random()
        String[] columns = {myKanjiDb.COLUMN_ON_READING, myKanjiDb.COLUMN_MEANING};
        String exclusion_clause = "_kanji <> '" + exclude + "'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, exclusion_clause, null, null, null, "random()");
        return cursor;
    }

    public Cursor getItemsByLevel_excludeReading(String lvl, String reading)
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();

        String[] columns = {myKanjiDb.COLUMN_ON_READING, myKanjiDb.COLUMN_MEANING};
        String exclusion_clause = "_onReading <> '" + reading + "'";
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, exclusion_clause, null, null, null, "random()");

        return cursor;
    }

    public ArrayList<String> getLevels()
    {
        SQLiteDatabase db = myKanjiDb.getWritableDatabase();
        //select _level from table
        ArrayList<String> levels = new ArrayList<String>();
        String[] columns = {myKanjiDb.COLUMN_LEVEL};
        Cursor cursor = db.query(myKanjiDb.TABLE_NAME, columns, null, null, null, null, null);
        int levelColumn = cursor.getColumnIndex(myKanjiDb.COLUMN_LEVEL);
        while(cursor.moveToNext())
        {
            String levelToLoad = cursor.getString(levelColumn);
            if(!(levels.contains(levelToLoad)))
            {
                levels.add(levelToLoad);
            }
        }

        return levels;
    }

    //THIS IS AN INNER STATIC CLASS
    //This way it creates a single database the program can use.
    //
    static class KanjiToStudy extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 13;
        private static final String DATABASE_NAME = "kanjiDB";
        private static final String TABLE_NAME = "KANJI_TABLE";

        //COLUMN NAMES
        public static final String KID = "_id";
        public static final String COLUMN_KANJI = "_kanji";
        public static final String COLUMN_MEANING = "_meaning";
        public static final String COLUMN_ON_READING = "_onReading";
        public static final String COLUMN_KUN_READING = "_kunReading";
        public static final String COLUMN_PROGRESS = "_progress";
        public static final String COLUMN_LEVEL = "_level";
        public static final String COLUMN_TIME = "_time";

        private Context context;

        //SQL Statements
        public static final String CREATE_TABLE = "CREATE TABLE KANJI_TABLE(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " _kanji VARCHAR(20), _meaning VARCHAR(100), _onReading VARCHAR(10), _kunReading VARCHAR(10)," +
                " _progress REAL, _level VARCHAR(100), _time VARCHAR(100));";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public KanjiToStudy(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context=context;
            //Message.message(context, "Constructor Called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //CREATE TABLE KANJI_TABLE(_id INTEGER PRIMARY KEY AUTOINCREMENT, _kanji VARCHAR(20));
            db.execSQL(CREATE_TABLE);

            Message.message(context, "onCreate Called");

            AssetManager am = context.getAssets();
            try {
                InputStream is = am.open("kanjipreload.txt");
                Scanner sc = new Scanner(is).useDelimiter(";");
                String kanji_in;
                String meaning_in;
                String onyomi_in;
                String kunyomi_in;
                float progress_in;
                String level_in;
                String time_in;
                while(sc.hasNext()==true)
                {
                    kanji_in = sc.next();
                    meaning_in = sc.next();
                    onyomi_in = sc.next();
                    kunyomi_in = sc.next();
                    level_in = sc.next();
                    progress_in = sc.nextFloat();
                    time_in = sc.next();
                    sc.nextLine();
                    long id = preloadData(kanji_in, meaning_in, onyomi_in, kunyomi_in, progress_in, level_in, time_in, db);
                    //if(id>=0){Message.message(context, "success");}
                    //else{Message.message(context, "failed");}
                    if(id==-1){Message.message(context, "failed");}
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
            //DROP TABLE IF EXISTS KANJI_TABLE
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Message.message(context, "Upgrade Called");
        }

        public long preloadData(String kanji, String meaning, String onyomi, String kunyomi,
                                float progress, String level, String time, SQLiteDatabase db)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KanjiToStudy.COLUMN_KANJI, kanji);
            contentValues.put(KanjiToStudy.COLUMN_MEANING, meaning);
            contentValues.put(KanjiToStudy.COLUMN_ON_READING, onyomi);
            contentValues.put(KanjiToStudy.COLUMN_KUN_READING, kunyomi);
            contentValues.put(KanjiToStudy.COLUMN_PROGRESS, progress);
            contentValues.put(KanjiToStudy.COLUMN_LEVEL, level);
            long id=db.insert(KanjiToStudy.TABLE_NAME, null, contentValues);
            return id;
        }
    }
}
