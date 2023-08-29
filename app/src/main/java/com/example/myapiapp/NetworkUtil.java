package com.example.myapiapp;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//THIS CLASS Works IN CREATING A VALID Uri AND THEN CONVERTING IT INTO URL....
//LATER WE OPEN HTTP CONNECTION TO RECIEVE THE RESPONSE(DATA)
//AND THEN CLOSE IT
public class NetworkUtil
{
    //whole networking logic will be here
    //url building , response vagerah sb kuch

    private static final String GITHUB_BASE_URL = "https://api.github.com";
    //this is base url , in order to call an api...its base url never changes
    private static final String GITHUB_USER ="users";
    private static final String GITHUB_REPOSITORY="repositories";
    private static final String GITHUB_SEARCH="search";
    private static final String PARAM_QUERY="q";

    //we created these variables so that in future, use can be easy

    private NetworkUtil()
    {

    }

    public static URL buildRepoSearch(String query)
    {
        // it is a function that builds a URL for searching repositories on GitHub.
        // It takes a string as input representing the query string,
        // and returns a URL as output


        Uri buildUri = Uri.parse(GITHUB_BASE_URL).buildUpon().appendPath(GITHUB_SEARCH)
                .appendPath(GITHUB_REPOSITORY).appendQueryParameter(PARAM_QUERY,query).build();
        //these two line will build the URL --> https://api.github.com/search/repositories/?q=querry



        URL url= null;
        try{
            url = new URL(buildUri.toString()); //converting the valid Uri into URL
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }


        return url;
    }


    //Taking response form http
    public static String getResponseFromHttp(URL url) throws IOException
    {
        //this function takes a URL as input and returns a string as output


        //first establishes a HTTP connection to the URL
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try
        {
            InputStream input = urlConnection.getInputStream();
            Scanner scanner = new Scanner(input);
            // opens the input stream from the connection and creates a Scanner object
            // to read the data from the stream(server)
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
            {
                return scanner.next();
            }
            else
            {
                return null;
            }
        }

        finally
        {
            urlConnection.disconnect(); //function finally closes the connection to the URL.
        }


    }

    public static List<GithubRepository> parseGithubRepos(String repoJson) {
        List<GithubRepository> repositories = new ArrayList<>();
        if(TextUtils.isEmpty(repoJson)) return repositories;
        try{
            JSONObject root = new JSONObject(repoJson);
            JSONArray repoArray = root.getJSONArray("items");
            for(int i=0 ; i<repoArray.length() ; ++i)
            {
                JSONObject repository = repoArray.getJSONObject(i);
                Integer id = repository.getInt("id");
                String name = repository.getString("name");
                String desc = repository.getString("description");
                repositories.add(new GithubRepository(id,name,desc));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return repositories;
    }

}





