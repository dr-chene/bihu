package com.example.bihu.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;


public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context, int version) {
        super(context, "bihu.db", null, version);
//        Log.d("MyHelper", "创建成功");
    }

//    private static int answerId=1;
//    private static int questionId=1;
//    private static int favoriteId=1;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table person (uid integer primary key,username text,password text,avatar text,token text)");
        db.execSQL("create table answer (id integer primary key,aid integer unique,qid integer,content text,images text,date text,best integer,exciting integer,naive integer,authorId integer,authorName text,authorAvatar text,isExciting integer,isNaive integer)");
        db.execSQL("create table question (id integer primary key,qid integer unique,title text,content text,images text,date text,exciting integer,naive integer,recent text,answerCount integer,authorId integer,authorName text,authorAvatar text,isExciting integer,isNaive integer,isFavorite integer)");
//        db.execSQL("create table favorite (id integer primary key autoincrement,title varchar(100),content varchar(500),images varchar(50),date varchar(15),exciting integer,naive integer,recent varchar(15),answerCount integer,authorId integer,authorName varchar(20),authorAvatar varchar(50),isExciting integer,isNaive integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPerson(SQLiteDatabase db, String username, String password, String avatar, String token, int uid) {
        StringBuffer values = new StringBuffer();
        values.append("insert or replace into person values");
        values.append("(");
        values.append(uid);
        values.append(",\'" + username + "\'");
        values.append(",\'" + password + "\'");
        values.append(",\'" + avatar + "\'");
        values.append(",\'" + token + "\'");
        values.append(")");
        db.execSQL(values.toString());
    }

    public void addAnswer(SQLiteDatabase db, int aid, int qid, String content, String images, String date, int best, int exciting, int naive, int authorId, String authorName, String authorAvatar, int isExciting, int isNaive) {

//        if (db.rawQuery("select * from answer where aid = ?", new String[]{aid + ""})!=null){
////            db.execSQL("update answer set best = "+best+", isExciting = "+isExciting+", isNaive = "+isNaive+" where aid = ?",new String[]{aid+""});
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("best",best);
//            contentValues.put("isExciting",isExciting);
//            contentValues.put("isNaive",isNaive);
//            db.update("answer",contentValues,"aid = "+aid,null);
//            Log.d("debug","进入update语句");
////            Log.d("debug",db.rawQuery("select * from answer where aid = ?", new String[]{aid + ""}).toString());
//        }else{
        StringBuffer values = new StringBuffer();
        values.append("insert or replace into answer values");
        values.append("(");
        values.append(aid);
        values.append("," + aid);
        values.append("," + qid);
        values.append(",\'" + content + "\'");
        values.append(",\'" + images + "\'");
        values.append(",\'" + date + "\'");
        values.append("," + best);
        values.append("," + exciting);
        values.append("," + naive);
        values.append("," + authorId);
        values.append(",\'" + authorName + "\'");
        values.append(",\'" + authorAvatar + "\'");
        values.append("," + isExciting);
        values.append("," + isNaive);
        values.append(")");
        db.execSQL(values.toString());
    }
//    }

    public void addQuestion(SQLiteDatabase db, int qid, String title, String content, String images, String date, int exciting, int naive, String recent, int answerCount, int authorId, String authorName, String authorAvatar, int isExciting, int isNaive, int isFavorite) {
        StringBuffer values = new StringBuffer();
        values.append("insert or replace into question values");
        values.append("(");
        values.append(qid);
        values.append("," + qid);
        values.append(",\'" + title + "\'");
        values.append(",\'" + content + "\'");
        values.append(",\'" + images + "\'");
        values.append(",\'" + date + "\'");
        values.append("," + exciting);
        values.append("," + naive);
        values.append(",\'" + recent + "\'");
        values.append("," + answerCount);
        values.append("," + authorId);
        values.append(",\'" + authorName + "\'");
        values.append(",\'" + authorAvatar + "\'");
        values.append("," + isExciting);
        values.append("," + isNaive);
        values.append("," + isFavorite);
        values.append(")");
        db.execSQL(values.toString());
    }


    public void readPerson(SQLiteDatabase db, Person person) {
        Cursor cursor = db.rawQuery("select * from person", null);
        while (cursor.moveToNext()) {
            person.setId(cursor.getInt(cursor.getColumnIndex("uid")));
            person.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            person.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            person.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            person.setToken(cursor.getString(cursor.getColumnIndex("token")));
        }
    }

    public void searchQuestion(SQLiteDatabase db, int qid, Question question) {
        Cursor cursor = db.rawQuery("select * from question where qid = ?", new String[]{qid + ""});
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
    }

    public void readAnswer(SQLiteDatabase db, List<Answer> answerList, int qid) {
        Cursor cursor = db.rawQuery("select * from answer where qid = ?", new String[]{qid + ""});
        answerList.clear();
        Log.d("chene", answerList.size() + "");
        while (cursor.moveToNext()) {
            Answer answer = new Answer();
            Log.d("chene", "id = " + cursor.getInt(cursor.getColumnIndex("id")));
            Log.d("chene", "aid = " + cursor.getInt(cursor.getColumnIndex("aid")));

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
//                Log.d("debug",answer.toString());
            answerList.add(answer);
        }
        Log.d("chene", "刷新完成");
//        Log.d("debug",answerList.size()+"");
//        for (int i = 0; i < answerList.size(); i++) {
//            Log.d("question","content = "+answerList.get(i).getContent());
//        }
    }


    public void readQuestion(SQLiteDatabase db, List<Question> questionList) {
        Cursor cursor = db.rawQuery("select * from question", null);
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
    }

    public void readFavorite(SQLiteDatabase db, List<Question> favoriteList) {
        Cursor cursor = db.rawQuery("select * from question where isFavorite = ?", new String[]{"1"});
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
    }

}
