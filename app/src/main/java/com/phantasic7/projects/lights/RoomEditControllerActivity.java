package com.phantasic7.projects.lights;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RoomEditControllerActivity extends AppCompatActivity {
    public static int EDIT_REQUEST_CODE = 3333;
    private TextView roomTypeNameTextView;
    private RecyclerView roomtypeRecyclerView;

    private RoomTypeAdapter mRoomTypeAdapter;
    private int drawableRes;
    private boolean changed = false;
    private String currentName;
    private String newName;
    private Intent fromMainControllerIntent;
    private String roomTypeName = "";
    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_edit_controller);

        Utils.changeStatusBarIconColor(this, true);

        roomTypeNameTextView = findViewById(R.id.roomTypeNameTextView);
        roomtypeRecyclerView = findViewById(R.id.roomtypeRecyclerView);

        fromMainControllerIntent = getIntent();
        drawableRes = fromMainControllerIntent.getIntExtra("drawableRes", 0);
        currentName = fromMainControllerIntent.getStringExtra("name");
        groupId = fromMainControllerIntent.getIntExtra("groupid", 0);
        newName = currentName;
        roomTypeNameTextView.setText(currentName);

        roomtypeRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRoomTypeAdapter = new RoomTypeAdapter(RoomEditControllerActivity.this, drawableRes, new RecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                roomTypeName = mRoomTypeAdapter.highlightItemPosition(position, v);
                changed = true;
            }
        });
        roomtypeRecyclerView.setAdapter(mRoomTypeAdapter);
    }

    public void rename(View v) {
        final EditText renameEditText = new EditText(RoomEditControllerActivity.this);
        renameEditText.setHint("Ex: Bill's room");
        renameEditText.setText(roomTypeNameTextView.getText());

        //noinspection RestrictedApi
        new AlertDialog.Builder(RoomEditControllerActivity.this).setTitle("Rename").setView(renameEditText, 16, 16, 16, 16)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!currentName.equals(renameEditText.getText())) {
                            changed = true;
                            newName = renameEditText.getText().toString();
                            roomTypeNameTextView.setText(newName);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void applyEdit(View v) {
        if (changed) {
            Intent intent = new Intent(this, MainControllerActivity.class);
            String name = newName.equals(currentName) ? currentName : newName;
            if (!roomTypeName.equals("")) {
                LibraryLoader.changeGroupType(groupId, roomTypeName);
                intent.putExtra("type", roomTypeName);
            }
            LibraryLoader.changeGroupName(groupId, name);
            intent.putExtra("name", name);
            intent.putExtra("groupid", fromMainControllerIntent.getIntExtra("groupid", 0));
            intent.putExtra("position", fromMainControllerIntent.getIntExtra("position", 0));
            setResult(RESULT_OK, intent);
        }

        finish();
    }
}
