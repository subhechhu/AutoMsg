package com.subhechhu.automessage.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.subhechhu.automessage.Details;

/**
 * Created by subhechhu on 3/5/2017.
 */

public class DBhelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "automessage";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_DATE = "dateTV";
    private static final String COLUMN_TIME = "timeTV";
    private static final String COLUMN_MESSAGE = "messageTV";
    private static final String COLUMN_MEDIUM = "medium";
    private static final String COLUMN_TIMELONG = "timeLong";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_SIMCOUNT = "simCount";

    private static final String DATABASE_NAME = "automessage.db";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_NUMBER + " text not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_TIME + " text not null, "
            + COLUMN_MESSAGE + " text not null, "
            + COLUMN_MEDIUM + " text not null, "
            + COLUMN_TIMELONG + " text not null,"
            + COLUMN_SENDER + " text not null,"
            + COLUMN_SIMCOUNT + " integer not null"
            + ");";

    DBhelper(Context context) {
        super(context, DATABASE_NAME, null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(getClass().getSimpleName(), "create: " + DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(getClass().getSimpleName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    long addRemainder(Details details) {
        SQLiteDatabase db = null;
        long id = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, details.getName());
            values.put(COLUMN_NUMBER, details.getNumber());
            values.put(COLUMN_DATE, details.getDate());
            values.put(COLUMN_TIME, details.getTime());
            values.put(COLUMN_MESSAGE, details.getMessage());
            values.put(COLUMN_MEDIUM, details.getMediumSelected());
            values.put(COLUMN_TIMELONG, "" + details.getTimelong());
            values.put(COLUMN_SENDER, "" + details.getSimSelected());
            values.put(COLUMN_SIMCOUNT, "" + details.getSimCount());

            id = db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return id;
    }

    Cursor getAllRemainders() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
//        } finally {
//            if (db != null) {
//                db.close();
//            }
//            if (cursor != null) {
//                cursor.close();
//            }
        }
        return cursor;
    }

    int deleteRemainder(String id) {
        SQLiteDatabase db = null;
        int returnValue = 0;
        String whereClause = "_id" + "=" + id;
        try {
            db = this.getWritableDatabase();
            returnValue = db.delete(TABLE_NAME, whereClause, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return returnValue;
    }
//
//    long editRemainder(Details details, String id, int position) {
//        Log.d(getClass().getSimpleName(),"inside EditRemainder id: "+id);
//
//        SQLiteDatabase db = null;
//        int returnValue = 0;
//        String whereClause = "_id" + "=" + id;
//        try {
//            db = this.getWritableDatabase();
//
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_NAME, details.getName());
//            values.put(COLUMN_NUMBER, details.getNumber());
//            values.put(COLUMN_DATE, details.getDate());
//            values.put(COLUMN_TIME, details.getTime());
//            values.put(COLUMN_MESSAGE, details.getMessage());
//            values.put(COLUMN_MEDIUM, details.getMedium());
//            values.put(COLUMN_TIMELONG, "true");
//            returnValue = db.update(TABLE_NAME, values, whereClause,null);
//            Log.d(getClass().getSimpleName(),"inside EditRemainder returnValue: "+returnValue);
//            Log.d(getClass().getSimpleName(),"266 medium inside EditRemainder details.getMedium(): "+details.getMedium());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return (long) returnValue;
//    }
}
