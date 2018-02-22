package com.phantasic7.projects.lights;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HuynhHuu on 17-Feb-18.
 */

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;
    static final String DATABASE_NAME = "Lights";
    private static final int DATABASE_VERSION = 2;

    private static final String LIGHT_DATABASE_TABLE = "Lights";
    private static final String GROUP_DATABASE_TABLE = "Groups";
    private static final String SCENE_DATABASE_TABLE = "Scenes";

    //Lights
    private static final String FIELD_LIGHT_ADDRESS = "light_address";
    private static final String FIELD_LIGHT_NAME = "light_name";
    private static final String FIELD_LIGHT_GROUPIDS = "light_group_ids";

    //Groups
    private static final String FIELD_GROUP_GROUPID = "group_id";
    private static final String FIELD_GROUP_NAME = "group_name";
    private static final String FIELD_GROUP_COLOR = "group_color";
    private static final String FIELD_GROUP_SCENEID = "group_scene_id";
    private static final String FIELD_GROUP_LIGHTIDS = "group_light_ids";

    //Scenes
    private static final String FIELD_SCENE_SCENEID = "scene_id";
    private static final String FIELD_SCENE_NAME = "scene_name";
    private static final String FIELD_SCENE_COLOR = "scene_color";
    private static final String FIELD_SCENE_BRIGHTNESS = "scene_brightness";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDatabase = "CREATE TABLE " + LIGHT_DATABASE_TABLE + "("
                + FIELD_LIGHT_ADDRESS + " TEXT PRIMARY KEY , "
                + FIELD_LIGHT_NAME + " TEXT, "
                + FIELD_LIGHT_GROUPIDS + " TEXT"
                + ")";
        db.execSQL(createDatabase);

        createDatabase = "CREATE TABLE " + GROUP_DATABASE_TABLE + "("
                + FIELD_GROUP_GROUPID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_GROUP_NAME + " TEXT, "
                + FIELD_GROUP_COLOR + " TEXT, "
                + FIELD_GROUP_SCENEID + " INTEGER, "
                + FIELD_GROUP_LIGHTIDS + " TEXT"
                + ")";
        db.execSQL(createDatabase);

        createDatabase = "CREATE TABLE " + SCENE_DATABASE_TABLE + "("
                + FIELD_SCENE_SCENEID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_SCENE_NAME + " TEXT, "
                + FIELD_SCENE_COLOR + " TEXT, "
                + FIELD_SCENE_BRIGHTNESS + " INTEGER"
                + ")";
        db.execSQL(createDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LIGHT_DATABASE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_DATABASE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SCENE_DATABASE_TABLE);

        onCreate(db);
    }

    //--------------------------Lights starts here--------------------------
    public Light getLight(String lightId)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(LIGHT_DATABASE_TABLE, new String[]{FIELD_LIGHT_ADDRESS, FIELD_LIGHT_NAME,
                        FIELD_LIGHT_GROUPIDS},
                FIELD_LIGHT_ADDRESS + " = ?", new String[]{lightId},
                null,null,null);

        Light light = null;
        if(cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            light = new Light(cursor.getString(0),cursor.getString(1),cursor.getString(2));
        }
        return light;
    }


    public List<Light> getAllLights(long groupID) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor1 = db.query(GROUP_DATABASE_TABLE, new String[]{FIELD_GROUP_LIGHTIDS},
                FIELD_GROUP_GROUPID + " = ?",new String[]{String.valueOf(groupID)},null,null,null);

        String []lightIDs = null;
        if(cursor1.moveToFirst())
            lightIDs = cursor1.getString(0).split("\\|");

        int size = lightIDs.length;

        List<Light> lights = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            Cursor cursor2 = db.query(LIGHT_DATABASE_TABLE, new String[]{FIELD_LIGHT_ADDRESS, FIELD_LIGHT_NAME,
                            FIELD_LIGHT_GROUPIDS},
                    FIELD_LIGHT_ADDRESS + " = ?",new String[]{lightIDs[i]}, null, null, null);

            if (cursor2.moveToFirst()) {
                Light light = new Light(cursor2.getString(0), cursor2.getString(1), cursor2.getString(2));
                lights.add(light);
            }
            cursor2.close();
        }

        db.close();
        cursor1.close();

        return lights;
    }

    public void addLight(Light light) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_LIGHT_ADDRESS, light.getAddress());
        values.put(FIELD_LIGHT_NAME, light.getName());
        values.put(FIELD_LIGHT_GROUPIDS, light.getGroupIDs());
        db.insert(LIGHT_DATABASE_TABLE, null, values);

        db.close();
    }

    public void deleteAllLights() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(LIGHT_DATABASE_TABLE, null, null);
        db.close();
    }
    //--------------------------Lights ends here--------------------------

    //--------------------------Groups starts here--------------------------
    public Group getGroup(long groupID)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(GROUP_DATABASE_TABLE, new String[]{FIELD_GROUP_GROUPID, FIELD_GROUP_NAME,
                        FIELD_GROUP_COLOR,FIELD_GROUP_SCENEID, FIELD_GROUP_LIGHTIDS},
                FIELD_GROUP_GROUPID + " = ?", new String[]{groupID+""},
                null,null,null);

        Group group = null;
        if(cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            group = new Group(cursor.getLong(0),cursor.getString(1),cursor.getString(2),
                    cursor.getLong(3), createStringListfromString(cursor.getString(4)));
        }
        return group;
    }

    public void addGroup(Group group) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_GROUP_GROUPID, group.getGroupID());
        values.put(FIELD_GROUP_NAME, group.getName());
        values.put(FIELD_GROUP_COLOR, group.getColor());
        values.put(FIELD_GROUP_SCENEID, group.getSceneID());
        values.put(FIELD_GROUP_LIGHTIDS, createStringfromStringList(group.getLightIDs()));
        db.insert(GROUP_DATABASE_TABLE, null, values);

        db.close();
    }

    public List<Group> getAllGroups() {
        SQLiteDatabase db = getReadableDatabase();

        List<Group> groups = new ArrayList<>();

        Cursor cursor = db.query(GROUP_DATABASE_TABLE, new String[]{FIELD_GROUP_GROUPID, FIELD_GROUP_NAME,
                        FIELD_GROUP_COLOR, FIELD_GROUP_SCENEID, FIELD_GROUP_LIGHTIDS},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Group group = new Group(cursor.getLong(0), cursor.getString(1),cursor.getString(2),
                        cursor.getLong(3),createStringListfromString(cursor.getString(4)));
                groups.add(group);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return groups;
    }

    public void deleteAllGroups() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(GROUP_DATABASE_TABLE, null, null);
        db.close();
    }
    //--------------------------Groups ends here--------------------------

    //--------------------------Scenes starts here--------------------------

    public Scene getScene(long sceneId)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(SCENE_DATABASE_TABLE, new String[]{FIELD_SCENE_SCENEID, FIELD_SCENE_NAME,
                        FIELD_SCENE_COLOR, FIELD_SCENE_BRIGHTNESS},
                FIELD_SCENE_SCENEID + " = ?", new String[]{sceneId+""},
                null,null,null);

        Scene scene = null;
        if(cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            scene = new Scene(cursor.getLong(0),cursor.getString(1),cursor.getString(2),
                    cursor.getInt(3));
        }
        return scene;
    }

    public void addScene(Scene scene) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIELD_SCENE_SCENEID, scene.getSceneID());
        values.put(FIELD_SCENE_NAME, scene.getName());
        values.put(FIELD_SCENE_COLOR, scene.getColor());
        values.put(FIELD_SCENE_BRIGHTNESS, scene.getBrightness());
        db.insert(SCENE_DATABASE_TABLE, null, values);

        db.close();
    }

    public void deleteAllScenes() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(SCENE_DATABASE_TABLE, null, null);
        db.close();
    }
    //--------------------------Scenes ends here--------------------------


    public static List<String> createStringListfromString(String stringWithDelim)
    {
        List<String> result = new ArrayList<>();
        String[] strings = stringWithDelim.split("\\|");
        Collections.addAll(result, strings);
        return result;
    }

    public static String createStringfromStringList(List<String> stringList) {
        String result ="";
        for (String s : stringList)
            result += (s +"|");
        return result.substring(0,result.length()-1);
    }
}
