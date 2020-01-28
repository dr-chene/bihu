package com.example.bihu.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bihu.MainActivity;

import java.util.List;


public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context, int version) {
        super(context, "bihu.db", null, version);
    }

    public static void deletePerson(Context context) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
        sqLiteDatabase.delete("person", null, null);
    }

    public static void addPerson(Context context, int uid, String username, String password, String avatar, String token) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
        if (!searchAnswer(context, aid, sqLiteDatabase)) {
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

    public static Boolean searchAnswer(Context context, int aid, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where aid = ?", new String[]{aid + ""});
        while (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static void addQuestion(Context context, int qid, String title, String content, String images, String date, int exciting, int naive, String recent, int answerCount, int authorId, String authorName, String authorAvatar, int isExciting, int isNaive, int isFavorite) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
    }

    public static void readAnswer(Context context, List<Answer> answerList, int qid) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from answer where qid = ?", new String[]{qid + ""});
        answerList.clear();
        while (cursor.moveToNext()) {
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
        sqLiteDatabase.close();
        cursor.close();
    }

    public static void readQuestion(Context context, List<Question> questionList) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from question", null);
        questionList.clear();
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
            question.setFavorite(cursor.getInt(cursor.getColumnIndex("isFavorite")) == 1);
            questionList.add(question);
        }
        sqLiteDatabase.close();
        cursor.close();
    }

    public static void readFavorite(Context context, List<Question> favoriteList) {
        MyHelper myHelper = new MyHelper(context, MainActivity.vision);
        SQLiteDatabase sqLiteDatabase = myHelper.getReadableDatabase();
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
