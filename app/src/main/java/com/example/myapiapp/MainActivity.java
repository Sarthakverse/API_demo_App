package com.example.myapiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapiapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    TextView textview;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.tv_output);
        EditText etSearchTerm = findViewById(R.id.et_search_term);
        Button btnSearch = findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchterm = etSearchTerm.getText().toString();
                if (TextUtils.isEmpty(searchterm)) {
                    textview.setText("Please enter a search term.");
                } else {
                    URL searchUrl = NetworkUtil.buildRepoSearch(searchterm); // Use user input as search term
                    new GitHubQuerryTask().execute(searchUrl);
                }
            }
        });
    }

    public class  GitHubQuerryTask extends AsyncTask<URL,Void,String>
    {
        //The GitHubQuerryTask class is an asynchronous task
        // that queries the GitHub API for search results.
        // It has two methods


        //doInBackground(): This method is called in the background thread
        // and performs the actual query. It takes an array of URLs as input
        // and returns a string containing the search results.
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl= params[0]; //aray of urls as input
            String githubsearchresults = null;
            try{
                githubsearchresults=NetworkUtil.getResponseFromHttp(searchUrl);
            }catch(IOException e){
                e.printStackTrace();
            }
            return githubsearchresults; //returning in string format
        }


        //onPostExecute(): This method is called after the query has finished
        // and it displays the search results in a text view.
        @Override
        protected void onPostExecute(String str) {
            if(str!=null && !str.equals(""))
            {
                parseandDisplayRepos(str);
            }
        }

        private void parseandDisplayRepos(String json) {
            List<GithubRepository> repositories = NetworkUtil.parseGithubRepos(json);
            StringBuilder sb = new StringBuilder();

            for (GithubRepository repository : repositories) {
                sb.append("id: ").append(repository.getId()).append("\n")
                        .append("Name: ").append(repository.getName()).append("\n")
                        .append("Description: ").append(repository.getDescription()).append("\n\n");
            }

            textview.setText(sb.toString()); // Set text directly on the TextView
        }
    }
}