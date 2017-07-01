package com.wanyueliang.stickerdemo.wediget.sticker;

public class StickerModel {

    private int alpha = 255;//整体的透明度
    private String textColor = "000000";//绘制的颜色
    private int align = -1;//自定义文字 对齐方式,左:0,中间：1，右：2
    private int roundRectColor;//外框的颜色
    protected boolean isFlipped;//是否镜像
    protected boolean canEdit = true;//是否可以编辑
    protected String timeLength;
    protected String mode = "0";//0：不循环，自由对齐；1: 不循环，头对齐；2: 不循环，尾对齐；3:循环，头对齐；4:不循环，头尾对齐

    protected String imagePath;
    protected String imageUrl;
    /**
     * 预设文字才使用的属性
     */
    private float left;
    private float top;
    private float width;
    private float height;

    public StickerModel() {
    }

    public StickerModel(int alpha, String textColor, int roundRectColor) {
        this.alpha = alpha;
        this.textColor = textColor;
        this.roundRectColor = roundRectColor;
    }

    public StickerModel(int alpha, String textColor, int align, int roundRectColor) {
        this.alpha = alpha;
        this.textColor = textColor;
        this.align = align;
        this.roundRectColor = roundRectColor;
    }

    public StickerModel clone() {
        StickerModel stickerModel = new StickerModel(alpha, textColor, align, roundRectColor);
        stickerModel.isFlipped = isFlipped;

        return stickerModel;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getRoundRectColor() {
        return roundRectColor;
    }

    public void setRoundRectColor(int roundRectColor) {
        this.roundRectColor = roundRectColor;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }


    @Override
    public String toString() {
        return "StickerModel{" +
                ", alpha=" + alpha +
                ", textColor='" + textColor + '\'' +
                ", align=" + align +
                ", roundRectColor=" + roundRectColor +
                ", isFlipped=" + isFlipped +
                ", canEdit=" + canEdit +
                ", mode='" + mode + '\'' +
                ", left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}