package com.example.nikolay.testtask;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private ArrayList<String> listContent;
    private ArrayAdapter<String> listViewAdapter;
    private ListView list;
    private RequestQueue.RequestFinishedListener finishedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listContent = new ArrayList<String>();
        //Инициализация очереди запросов, для работы с сетью используется библиотека Volley(https://github.com/google/volley)
        mQueue = Volley.newRequestQueue(this);

        list = (ListView)findViewById(R.id.mainList);
        listViewAdapter = new ArrayAdapter<String>(this, R.layout.list_view_style, listContent);
        //Сортирует распарсенный список и связывает адаптер с контейнером ListView после выполнения запроса
        finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                Collections.sort(listContent);
                list.setAdapter(listViewAdapter);
            }
        };
        //добавляет подписчика
        mQueue.addRequestFinishedListener(finishedListener);
        //создает запрос, и добавляет его в очередь
        jsonParse();
    }

    public void jsonParse() {
        String url = "http://www.mocky.io/v2/56fa31e0110000f920a72134";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Парсит полученный JSON
                JSONArray jsonArray = null;
                String res = "";
                try {
                    JSONObject company = response.getJSONObject("company");
                    JSONArray competences = company.getJSONArray("competences");

                    for(int i = 0; i < competences.length(); ++i){
                        res += competences.getString(i) + " ";
                    }
                    listContent.add("Company name: " + company.getString("name"));
                    listContent.add("Company age: " + company.getString("age"));
                    listContent.add("Сompetences: " + res);

                    JSONArray employees = company.getJSONArray("employees");
                    listContent.add("Employees:");
                    JSONArray skills;
                    JSONObject employee;

                    for(int i = 0; i < employees.length(); ++i){
                        employee = employees.getJSONObject(i);
                        skills = employee.getJSONArray("skills");
                        listContent.add("\t" + employee.getString("name"));
                        listContent.add("\t" + "Phone number: " + employee.getString("phone_number"));
                        res = "";
                        for(int j = 0; j < skills.length(); ++j) {
                            res += skills.getString(j) + " ";
                        }
                        listContent.add("\t" + "Skills: " + res);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        //Добавляет сформированный запрос в очередь
        mQueue.add(request);
    }

}
