package com.svs57.myworkerslistedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> myWorkers = new ArrayList<String>();
    ArrayList<String> selectedWorkers = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView myWorkersList;
    final String TAG = "workmng";
    private UUID workerId = null;
    final String FILENAME = "workers";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String str;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myWorkersList = findViewById(R.id.myWorkersList);
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, myWorkers);
        myWorkersList.setAdapter(adapter);

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            while ((str = br.readLine()) != null)
            {
                adapter.add(str);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        myWorkersList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // получаем нажатый элемент
                String myWorker = adapter.getItem(position);
                if(myWorkersList.isItemChecked(position))
                    selectedWorkers.add(myWorker);
                else
                    selectedWorkers.remove(myWorker);
            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"onDestroy", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        String tmpString = "";
        BufferedWriter bw = null;
        int items = myWorkers.size();
        try
        {
            bw = new BufferedWriter(new
                    OutputStreamWriter(openFileOutput(FILENAME, MODE_PRIVATE)));
            for (int j=0;j<items;j++)
            {
                tmpString = myWorkers.get(j).toString();
                bw.write(tmpString);
                bw.write("\n");
            }
            bw.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
    }

    public void add(View view)
    {
        Log.d(TAG, "\n=============\n");
        OneTimeWorkRequest.Builder myWorkRequest =
                new OneTimeWorkRequest.Builder(MyWorker.class);
        OneTimeWorkRequest myWork = myWorkRequest.build();
        WorkManager.getInstance(this).enqueue(myWork);
        workerId = myWork.getId();
        adapter.add(workerId.toString());
        adapter.notifyDataSetChanged();
    }

    public void remove(View view)
    {
        // получаем и удаляем выделенные элементы
        for(int i=0; i< selectedWorkers.size();i++)
        {
            String work = selectedWorkers.get(i);
            adapter.remove(work);
            Operation status =
                    WorkManager.getInstance(this).cancelWorkById(UUID.fromString(work));
            Log.d(TAG, "Killing " + work);
        }
        // снимаем все ранее установленные отметки
        myWorkersList.clearChoices();
        // очищаем массив выбраных объектов
        selectedWorkers.clear();
        adapter.notifyDataSetChanged();
    }
    public void goodbye(View view)
    {
        finish();
    }
}