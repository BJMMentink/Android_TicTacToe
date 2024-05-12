package edu.bjm.tictactoe;

import static android.provider.Settings.System.getString;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class Navbar {
    public static final String TAG = "Navbar";
    public static void initGameButton(Activity activity){
        ImageButton ibMap = activity.findViewById(R.id.imageButtonMap);
        setupListenerEvent(ibMap, activity, MainActivity.class);
    }
    public static void initListButton(Activity activity){
        ImageButton ibList = activity.findViewById(R.id.imageButtonList);
        setupListenerEvent(ibList, activity, GamesListActivity.class);
    }
    private static void setupListenerEvent(ImageButton imageButton,
                                           Activity fromActivity,
                                           Class<?> destinationActivityClass)
    {
        imageButton.setEnabled(fromActivity.getClass() != destinationActivityClass);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fromActivity, destinationActivityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d(TAG, "onClick: ");
                fromActivity.startActivity(intent);

            }
        });
    }
}
