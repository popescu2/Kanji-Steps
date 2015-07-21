package opensourceproject.kanjisteps;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class MainActivity extends ActionBarActivity {

    KanjiToStudyAdapter myKanjiDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myKanjiDB = new KanjiToStudyAdapter(this);
        //exportDB();
        SQLiteDatabase db = myKanjiDB.myKanjiDb.getWritableDatabase();

        exportDB();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void practicePressed(View view)
    {
        //TextView txt = (TextView) findViewById(R.id.txtNotImplemented);
        //txt.setVisibility(view.VISIBLE);

        Intent openPractice = new Intent("android.intent.action.PRACTICE");
        startActivity(openPractice);
    }
    public void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "opensourceproject.kanjisteps" +"/databases/"+"kanjiDB";
        String backupDBPath = "/kanjiStuff";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
            Message.message(this, "DB Exported!");
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
