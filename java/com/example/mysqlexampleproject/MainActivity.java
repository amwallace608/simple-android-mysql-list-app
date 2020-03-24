package com.example.mysqlexampleproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ItemAdapter itemAdapter;    //item adapter variable
    Context thisContext;        //context to pass to item adapter
    ListView myListView;        //listView variable
    TextView progressTextView;  //progress text variable
    //DB parts Map, linked hash map always stored in the same order
    Map<String, Double> partsMap = new LinkedHashMap<String, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize
        Resources res = getResources();
        myListView = (ListView) findViewById(R.id.myListView);
        progressTextView = (TextView) findViewById(R.id.progressTextView);
        thisContext = this;
        //blank out progress text until used
        progressTextView.setText(" ");
        //find button
        Button dataBtn = (Button) findViewById(R.id.getDataBtn);
        //Button listener to get data from DB on click
        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });

    }

    //Private inner class for retrieving data from DB
    private class GetData extends AsyncTask<String,String,String> {
        String msg = "";
        //JDBC Driver name and DB URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        //DB URL and name from DbStrings class
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;
        //Set progress text while connecting to DB (before actually connecting)
        @Override
        protected void onPreExecute() {
            progressTextView.setText("Connecting to database...");
        }

        @Override
        protected String doInBackground(String... strings) {
            //create connection and statement to SQL DB
            Connection conn = null;
            Statement stmt = null;

            //try block for attempting the connection to the DB
            //catch block for connection errors, print stack trace of error
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);
                //create statement to select all from DB parts table
                stmt = conn.createStatement();
                String sql = "SELECT * FROM parts";
                ResultSet rs = stmt.executeQuery(sql);
                //loop through results of query as long as the result set has another record
                while(rs.next()){
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    //place DB items into partsMap
                    partsMap.put(name, price);
                }
                //success message
                msg = "Data Import Complete";
                //close open resources
                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException | ClassNotFoundException connError){
                msg = "An exception was thrown for JDBC.";
                connError.printStackTrace();
            } finally {
                //try to close resources if open, in case exception was thrown
                try {
                    if(stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if(conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }
        //method for setting progress text after execution, and displaying items
        @Override
        protected void onPostExecute(String msg){
            progressTextView.setText(this.msg);
            //populate list with item adapter if any data was retrieved from the DB
            if(partsMap.size() > 0){
                itemAdapter = new ItemAdapter(thisContext, partsMap);
                myListView.setAdapter(itemAdapter);
            }
        }
    }
}
