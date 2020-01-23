package com.example.bihu.tool;

public class Answer {
    private int id;
    private String content;
    private String images;
    private String date;
    private int best;
    private int exciting;
    private int naive;
    private int authorId;
    private String authorName;
    private String authorAvatar;
    private Boolean isExciting;
    private Boolean isNaive;
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

    public int getBest() {
        return best;
    }

    public void setBest(int best) {
        this.best = best;
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
