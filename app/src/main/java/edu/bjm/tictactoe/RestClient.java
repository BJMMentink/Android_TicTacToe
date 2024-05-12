package edu.bjm.tictactoe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RestClient {
    public static final String TAG = "RestClient";
    public static void execGetRequest(String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<Game> games = new ArrayList<Game>();
        Log.d(TAG, "execGetRequest: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);

                            try {
                                JSONArray items = new JSONArray(response);
                                for(int i = 0; i < items.length(); i++)
                                {
                                    JSONObject object = items.getJSONObject(i);
                                    Game game = new Game();
                                    game.setId(object.getString("id"));
                                    game.setConnectionId(object.getString("connectionId"));
                                    game.setPlayer1(object.getString("player1"));
                                    game.setPlayer2(object.getString("player2"));
                                    game.setWinner(object.getString("winner"));
                                    game.setNextTurn(object.getString("nextTurn"));
                                    game.setCompleted(object.getBoolean("completed"));
                                    game.setGameState(object.getString("gameState"));
                                    String dateString = object.getString("lastUpdateDate");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                                    Date date = dateFormat.parse(dateString);
                                    game.setLastUpdateDate(date);
                                    games.add(game);

                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(games);
                            Log.d(TAG, "onResponse: Items " + games.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);
            volleyCallback.onSuccess(games);

        } catch (Exception e) {
            Log.d(TAG, "execGetRequest: " + e);
            throw new RuntimeException(e);

        }
    }
    public static void execDeleteRequest(Game game,
                                         String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        try {
            executeRequest(game, url, context, volleyCallback, Request.Method.DELETE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execPutRequest(Game game,
                                      String url,
                                      Context context,
                                      VolleyCallback volleyCallback)
    {
        try {
            executeRequest(game, url, context, volleyCallback, Request.Method.PUT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void execPostRequest(Game game,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback)
    {
        try {
            executeRequest(game, url, context, volleyCallback, Request.Method.POST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeRequest(Game game,
                                       String url,
                                       Context context,
                                       VolleyCallback volleyCallback,
                                       int method)
    {
        Log.d(TAG, "executeRequest: " + method + ":" + url);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject object = new JSONObject();

            object.put("id", game.getId());
            object.put("connectionId", game.getConnectionId());
            object.put("player1", game.getPlayer1());
            object.put("player2", game.getPlayer2());
            object.put("winner", game.getWinner());
            object.put("lastUpdateDate", game.getLastUpdateDate());

            final String requestBody = object.toString();

            Log.d(TAG, "executeRequest: " + requestBody);

            JsonObjectRequest request = new JsonObjectRequest(method, url, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<Game> games = new ArrayList<Game>();
                            if(method == Request.Method.POST)
                            {
                                try {
                                    game.setId(response.getString("id"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            games.add(game);
                            Log.d(TAG, "onResponse: " + games);
                            volleyCallback.onSuccess(games);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                }
            })
            {
                @Override
                public byte[] getBody(){
                    Log.i(TAG, "getBody: " + object.toString());
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            };

            requestQueue.add(request);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void execGetOneRequest(String url,
                                         Context context,
                                         VolleyCallback volleyCallback)
    {
        Log.d(TAG, "execGetOneRequest: Start");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        ArrayList<Game> games = new ArrayList<Game>();
        Log.d(TAG, "execGetOneRequest: " + url);

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);



                            try {
                                JSONObject object = new JSONObject(response);
                                Game game = new Game();
                                game.setId(object.getString("id"));
                                game.setConnectionId(object.getString("connectionId"));
                                game.setPlayer1(object.getString("player1"));
                                game.setPlayer2(object.getString("player2"));
                                game.setWinner(object.getString("winner"));
                                String dateString = object.getString("lastUpdateDate");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss"); // Change the pattern as per your date format
                                Date date = dateFormat.parse(dateString);
                                game.setLastUpdateDate(date);
                                games.add(game);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            volleyCallback.onSuccess(games);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: " + error.getMessage());
                        }
                    });

            // Important!!!
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            Log.d(TAG, "execGetOneRequest: Error" + e.getMessage());
        }
    }
}