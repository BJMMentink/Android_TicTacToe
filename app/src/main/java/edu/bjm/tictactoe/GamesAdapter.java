package edu.bjm.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GamesAdapter extends RecyclerView.Adapter {
    private ArrayList<Game> gameData;
    private View.OnClickListener onItemClickListener;
    private CompoundButton.OnCheckedChangeListener onItemCheckedChangeListener;

    public static final String TAG = "TeamAdapter";

    private Context parentContext;

    private boolean isDeleting;
    private boolean isCompleted = false;

    public void setDelete(boolean b)
    {
        isDeleting = b;
    }
    public class TeamViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPlayer1;
        public TextView tvPlayer2;
        public TextView tvName;
        private Button btnDelete;
        private CheckBox chkComplete;
        private View.OnClickListener onClickListener;
        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
        public TextView getTvPlayer1()
        {
            return tvPlayer1;
        }
        public TextView getTvPlayer2()
        {
            return tvPlayer2;
        }
        public TextView getTvName()
        {
            return tvName;
        }
        public CheckBox getChlComplete() { return chkComplete; }
        public Button getBtnDelete() {return btnDelete; }

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayer1 = itemView.findViewById(R.id.tvPlayer1);
            tvPlayer2 = itemView.findViewById(R.id.tvPlayer2);
            tvName = itemView.findViewById(R.id.tvName);
            chkComplete = itemView.findViewById(R.id.chkCompleted);
            chkComplete.setTag(this);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }


    }

    public GamesAdapter(ArrayList<Game> data, Context context)
    {
        gameData = data;
        Log.d(TAG, "TeamAdapter: " + data.size());
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener)
    {
        Log.d(TAG, "setOnItemClickListener: ");
        onItemClickListener = itemClickListener;
    }

    public void setOnItemCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener)
    {
        Log.d(TAG, "setOnItemCheckedChangeListener: ");
        onItemCheckedChangeListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new TeamViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + gameData.get(position));
        TeamViewHolder teamViewHolder = (TeamViewHolder) holder;
        teamViewHolder.getTvPlayer1().setText(gameData.get(position).getPlayer1());
        teamViewHolder.getTvPlayer2().setText(gameData.get(position).getPlayer2());
        teamViewHolder.getTvName().setText(gameData.get(position).getConnectionId());
        isCompleted = gameData.get(position).isCompleted();
        teamViewHolder.getChlComplete().setChecked(isCompleted);

        if(isDeleting)
            teamViewHolder.getBtnDelete().setVisibility(View.VISIBLE);
        else
            teamViewHolder.getBtnDelete().setVisibility(View.INVISIBLE);
        teamViewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Delete");
                deleteItem(position);
            }
        });

    }

    private void favoriteChanged(boolean isChecked) {
        notifyDataSetChanged();
    }

    private void deleteItem(int position) {
        Log.d(TAG, "deleteItem: " + position);
        Game game = gameData.get(position);
        gameData.remove(position);
        RestClient.execDeleteRequest(game, parentContext.getString(R.string.gameTracker) + game.getId(), parentContext, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<Game> results) {
                gameData.remove(game);
                notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " + game.getId());

            }
        });
        Log.d(TAG, "deleteItem: parentContext: " + parentContext);
        Log.d(TAG, "deleteItem: " + game.toString());
        ((GamesListActivity) parentContext).RebindGames();
    }
    @Override
    public int getItemCount() {
        return gameData.size();
    }
}
