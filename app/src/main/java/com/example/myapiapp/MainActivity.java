package com.example.myapiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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

        private void parseandDisplayRepos(String json) 
        {
           List<GithubRepository> repositories = NetworkUtil.parseGithubRepos(json);
            SpannableStringBuilder sb = new SpannableStringBuilder();

            for (GithubRepository repository : repositories) {
                String idText = "ID: " + repository.getId() + "\n";
                String nameText = "Name: " + repository.getName() + "\n";
                String descriptionText = "Description: " + repository.getDescription() + "\n\n";

                // Create spans to set different text colors
                ForegroundColorSpan idColor = new ForegroundColorSpan(Color.YELLOW); // Change to your desired color
                ForegroundColorSpan nameColor = new ForegroundColorSpan(Color.RED); // Change to your desired color
                ForegroundColorSpan descriptionColor = new ForegroundColorSpan(Color.GREEN); // Change to your desired color

                // Apply spans to the respective parts of the text
                sb.append(idText);
                sb.setSpan(idColor, sb.length() - idText.length(), sb.length(), 0);

                sb.append(nameText);
                sb.setSpan(nameColor, sb.length() - nameText.length(), sb.length(), 0);

                sb.append(descriptionText);
                sb.setSpan(descriptionColor, sb.length() - descriptionText.length(), sb.length(), 0);
            }

            // Set the styled text to the TextView
            TextView textView = findViewById(R.id.tv_output);
            textView.setText(sb);
        }
    }
}
