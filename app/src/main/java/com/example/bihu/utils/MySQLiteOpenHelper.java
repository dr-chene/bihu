package com.example.bihu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bihu.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, int version) {
        super(context, "bihu.db", null, version);
    }

    public static void deletePerson(Context context) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        sqLiteDatabase.delete("person", null, null);
    }
public static void changePassword(Context context,String password,String token){
    MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
    SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("password", password);contentValues.put("token", token);
    sqLiteDatabase.update("person",contentValues,null,null);sqLiteDatabase.close();
}
    public static void addPerson(Context context, int uid, String username, String password, String avatar, String token) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        if (searchPerson(context, sqLiteDatabase)) {
            sqLiteDatabase.delete("person", null, null);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid", uid);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("avatar", avatar);
        contentValues.put("token", token);
        sqLiteDatabase.insert("person", null, contentValues);
        sqLiteDatabase.close();
    }

    public static void modifyAvatar(Context context, String avatar) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        if (searchPerson(context, sqLiteDatabase)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("avatar", avatar);
            sqLiteDatabase.update("person", contentValues, null, null);
            sqLiteDatabase.close();
        }
    }

    public static Boolean searchPerson(Context context, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from person", null);
        while (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static void addAnswer(Context context, int aid, int qid, String content, String images, String date, int best, int exciting, int naive, int authorId, String authorName, String authorAvatar, int isExciting, int isNaive) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        if (!searchAnswer( aid, sqLiteDatabase)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("aid", aid);
            contentValues.put("qid", qid);
            contentValues.put("content", content);
            contentValues.put("images", images);
            contentValues.put("date", date);
            contentValues.put("best", best);
            contentValues.put("exciting", exciting);
            contentValues.put("naive", naive);
            contentValues.put("authorId", authorId);
            contentValues.put("authorName", authorName);
            contentValues.put("authorAvatar", authorAvatar);
            contentValues.put("isExciting", isExciting);
            contentValues.put("isNaive", isNaive);
            sqLiteDatabase.insert("answer", null, contentValues);
            sqLiteDatabase.close();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("best", best);
            contentValues.put("exciting", exciting);
            contentValues.put("naive", naive);
            contentValues.put("authorAvatar", authorAvatar);
            contentValues.put("isExciting", isExciting);
            contentValues.put("isNaive", isNaive);
            sqLiteDatabase.update("answer", contentValues, "aid = ?", new String[]{aid + ""});
            sqLiteDatabase.close();
        }
    }

    public static Boolean searchAnswer(int aid, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where aid = ?", new String[]{aid + ""});
        while (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static void questionChange(final Context context, final int qid, final String column, final int change) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
                SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(column, change);

                String name = null;
                if (column.equals("isNaive")) {
                    name = "naive";
                } else if (column.equals("isExciting")) {
                    name = "exciting";
                }
                Cursor cursor = sqLiteDatabase.rawQuery("select * from question where qid = ?", new String[]{qid + ""});
                int count = 0;
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex(name));
                    if (change == 1) {
                        count++;
                    } else if (change == 0) {
                        count--;
                    }
                }
                contentValues.put(name, count);

                sqLiteDatabase.update("question", contentValues, "qid = ?", new String[]{qid + ""});
                cursor.close();
                sqLiteDatabase.close();
            }
        }).start();
    }
public static void answerAccept(final Context context, final int aid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
                SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("best", 1);
                sqLiteDatabase.update("answer", contentValues, "aid = ?", new String[]{aid + ""});
                sqLiteDatabase.close();
            }
        }).start();
}
    public static void answerChange(final Context context, final int aid, final String column, final int change) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
                SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(column, change);

                String name = null;
                if (column.equals("isNaive")) {
                    name = "naive";
                } else if (column.equals("isExciting")) {
                    name = "exciting";
                }
                Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where aid = ?", new String[]{aid + ""});
                int count = 0;
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex(name));
                    if (change == 1) {
                        count++;
                    } else if (change == 0) {
                        count--;
                    }
                }
                contentValues.put(name, count);

                sqLiteDatabase.update("answer", contentValues, "aid = ?", new String[]{aid + ""});
                cursor.close();
                sqLiteDatabase.close();
            }
        }).start();
    }

    public static void addQuestion(Context context, int qid, String title, String content, String images, String date, int exciting, int naive, String recent, int answerCount, int authorId, String authorName, String authorAvatar, int isExciting, int isNaive, int isFavorite) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        if (!searchQuestion(context, qid, sqLiteDatabase)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("qid", qid);
            contentValues.put("title", title);
            contentValues.put("content", content);
            contentValues.put("images", images);
            contentValues.put("date", date);
            contentValues.put("exciting", exciting);
            contentValues.put("naive", naive);
            contentValues.put("recent", recent);
            contentValues.put("answerCount", answerCount);
            contentValues.put("authorId", authorId);
            contentValues.put("authorName", authorName);
            contentValues.put("authorAvatar", authorAvatar);
            contentValues.put("isExciting", isExciting);
            contentValues.put("isNaive", isNaive);
            contentValues.put("isFavorite", isFavorite);
            sqLiteDatabase.insert("question", null, contentValues);
            sqLiteDatabase.close();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("exciting", exciting);
            contentValues.put("naive", naive);
            contentValues.put("answerCount", answerCount);
            contentValues.put("isExciting", isExciting);
            contentValues.put("isNaive", isNaive);
            contentValues.put("authorAvatar", authorAvatar);
            contentValues.put("isFavorite", isFavorite);
            sqLiteDatabase.update("question", contentValues, "qid = ?", new String[]{qid + ""});
            sqLiteDatabase.close();
        }
    }

    public static Boolean searchQuestion(Context context, int qid, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question where qid = ?", new String[]{qid + ""});
        while (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static void readPerson(Context context, Person person) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from person", null);
        while (cursor.moveToNext()) {
            person.setId(cursor.getInt(cursor.getColumnIndex("uid")));
            person.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            person.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            person.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            person.setToken(cursor.getString(cursor.getColumnIndex("token")));
        }
        sqLiteDatabase.close();
        cursor.close();
    }

    public static void searchQuestion(Context context, int qid, Question question) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question where qid = ?", new String[]{qid + ""});
        while (cursor.moveToNext()) {
            question.setId(cursor.getInt(cursor.getColumnIndex("qid")));
            question.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            question.setContent(cursor.getString(cursor.getColumnIndex("content")));
            question.setImages(cursor.getString(cursor.getColumnIndex("images")));
            question.setDate(cursor.getString(cursor.getColumnIndex("date")));
            question.setExciting(cursor.getInt(cursor.getColumnIndex("exciting")));
            question.setNaive(cursor.getInt(cursor.getColumnIndex("naive")));
            question.setRecent(cursor.getString(cursor.getColumnIndex("recent")));
            question.setAnswerCount(cursor.getInt(cursor.getColumnIndex("answerCount")));
            question.setAuthorId(cursor.getInt(cursor.getColumnIndex("authorId")));
            question.setAuthorName(cursor.getString(cursor.getColumnIndex("authorName")));
            question.setAuthorAvatar(cursor.getString(cursor.getColumnIndex("authorAvatar")));
            question.setExciting(cursor.getInt(cursor.getColumnIndex("isExciting")) == 1);
            question.setNaive(cursor.getInt(cursor.getColumnIndex("isNaive")) == 1);
            question.setFavorite(cursor.getInt(cursor.getColumnIndex("isFavorite")) == 1);
        }
        sqLiteDatabase.close();
        cursor.close();
        Log.d("first","question refresh");
    }

    public static int getAnswerCount(Context context, int qid) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where qid = ?", new String[]{qid + ""});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static void readAnswer(Context context, List<Answer> answerList, int qid) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where qid = ?", new String[]{qid + ""});
        answerList.clear();
        while (cursor.moveToNext()) {
            Log.d("three","load");
            Answer answer = new Answer();
            answer.setId(cursor.getInt(cursor.getColumnIndex("aid")));
            answer.setContent(cursor.getString(cursor.getColumnIndex("content")));
            answer.setImages(cursor.getString(cursor.getColumnIndex("images")));
            answer.setDate(cursor.getString(cursor.getColumnIndex("date")));
            answer.setBest(cursor.getInt(cursor.getColumnIndex("best")));
            answer.setExciting(cursor.getInt(cursor.getColumnIndex("exciting")));
            answer.setNaive(cursor.getInt(cursor.getColumnIndex("naive")));
            answer.setAuthorId(cursor.getInt(cursor.getColumnIndex("authorId")));
            answer.setAuthorName(cursor.getString(cursor.getColumnIndex("authorName")));
            answer.setAuthorAvatar(cursor.getString(cursor.getColumnIndex("authorAvatar")));
            answer.setExciting(cursor.getInt(cursor.getColumnIndex("isExciting")) == 1);
            answer.setNaive(cursor.getInt(cursor.getColumnIndex("isNaive")) == 1);
            answerList.add(answer);
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.d("first","answer fresh");
    }

    public static int getQuestionCount(Context context) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question", null);
        int count = cursor.getCount();
        sqLiteDatabase.close();
        cursor.close();
        return count;
    }

    public static void readQuestion(Context context, List<Question> questionList, int size, int type) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question", null);
        cursor.moveToFirst();
        int preQuestionLiseSize = questionList.size();
        questionList.clear();
        if (type == MainActivity.TYPE_LOAD_MORE) {
            question(questionList, size, cursor);
        }
        if (type == MainActivity.TYPE_REFRESH) {
            Log.d("first","type == MainActivity.TYPE_REFRESH");
            List<Question> questions = new ArrayList<>();
            question(questions, preQuestionLiseSize, cursor);
            question(questionList, MainActivity.count, cursor);
            questionList.addAll(questions);
        }
        Log.d("first","  success ");
        sqLiteDatabase.close();
        cursor.close();
    }

    private static void question(List<Question> questionList, int size, Cursor cursor) {
        for (int i = 0; i < size; i++) {
            if (cursor.moveToNext()) {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex("qid")));
                question.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                question.setContent(cursor.getString(cursor.getColumnIndex("content")));
                question.setImages(cursor.getString(cursor.getColumnIndex("images")));
                question.setDate(cursor.getString(cursor.getColumnIndex("date")));
                question.setExciting(cursor.getInt(cursor.getColumnIndex("exciting")));
                question.setNaive(cursor.getInt(cursor.getColumnIndex("naive")));
                question.setRecent(cursor.getString(cursor.getColumnIndex("recent")));
                question.setAnswerCount(cursor.getInt(cursor.getColumnIndex("answerCount")));
                question.setAuthorId(cursor.getInt(cursor.getColumnIndex("authorId")));
                question.setAuthorName(cursor.getString(cursor.getColumnIndex("authorName")));
                question.setAuthorAvatar(cursor.getString(cursor.getColumnIndex("authorAvatar")));
                question.setExciting(cursor.getInt(cursor.getColumnIndex("isExciting")) == 1);
                question.setNaive(cursor.getInt(cursor.getColumnIndex("isNaive")) == 1);
                question.setFavorite(cursor.getInt(cursor.getColumnIndex("isFavorite")) == 1);
                questionList.add(question);
            }
        }
    }

    public static int getFavoriteCount(Context context) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question where isFavorite = ?", new String[]{"1"});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static void readFavorite(Context context, List<Question> favoriteList, int size) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question where isFavorite = ?", new String[]{"1"});
        favoriteList.clear();
        while (cursor.moveToNext()) {
            Question question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndex("qid")));
            question.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            question.setContent(cursor.getString(cursor.getColumnIndex("content")));
            question.setImages(cursor.getString(cursor.getColumnIndex("images")));
            question.setDate(cursor.getString(cursor.getColumnIndex("date")));
            question.setExciting(cursor.getInt(cursor.getColumnIndex("exciting")));
            question.setNaive(cursor.getInt(cursor.getColumnIndex("naive")));
            question.setRecent(cursor.getString(cursor.getColumnIndex("recent")));
            question.setAnswerCount(cursor.getInt(cursor.getColumnIndex("answerCount")));
            question.setAuthorId(cursor.getInt(cursor.getColumnIndex("authorId")));
            question.setAuthorName(cursor.getString(cursor.getColumnIndex("authorName")));
            question.setAuthorAvatar(cursor.getString(cursor.getColumnIndex("authorAvatar")));
            question.setExciting(cursor.getInt(cursor.getColumnIndex("isExciting")) == 1);
            question.setNaive(cursor.getInt(cursor.getColumnIndex("isNaive")) == 1);
            favoriteList.add(question);
        }
        sqLiteDatabase.close();
        cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table person (_id integer primary key autoincrement,uid integer unique,username text,password text,avatar text,token text)");
        db.execSQL("create table answer (_id integer primary key autoincrement,aid integer unique,qid integer,content text,images text,date text,best integer,exciting integer,naive integer,authorId integer,authorName text,authorAvatar text,isExciting integer,isNaive integer)");
        db.execSQL("create table question (_id integer primary key autoincrement,qid integer unique,title text,content text,images text,date text,exciting integer,naive integer,recent text,answerCount integer,authorId integer,authorName text,authorAvatar text,isExciting integer,isNaive integer,isFavorite integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
