package com.example.bihu.tool;

public class Question {
    private int id;
    private String title;
    private String content;
    private String images;
    private String date;
    private int exciting;
    private int naive;
    private String recent;
    private int answerCount;
    private int authorId;
    private String authorName;
    private String authorAvatar;
    private Boolean isExciting;
    private Boolean isNaive;
    private Boolean isFavorite;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getExciting() {
        return exciting;
    }

    public Boolean getIsExciting(){
        return isExciting;
    }

    public void setExciting(Boolean exciting) {
        isExciting = exciting;
    }

    public void setExciting(int exciting) {
        this.exciting = exciting;
    }

    public int getNaive() {
        return naive;
    }

    public Boolean getIsNaive(){
        return isNaive;
    }

    public void setNaive(Boolean naive) {
        isNaive = naive;
    }

    public void setNaive(int naive) {
        this.naive = naive;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }
}
