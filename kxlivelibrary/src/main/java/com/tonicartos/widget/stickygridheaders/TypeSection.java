package com.tonicartos.widget.stickygridheaders;

/**
 * Created by gjr on 2016/3/2.
 */
public class TypeSection {
    private int startIndex;
    private int endIndex;
    private int size;
    private int typeIndex;
    private String dishesTypeCode;
    private String dishesName;

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public String getDishesTypeCode() {
        return dishesTypeCode;
    }

    public void setDishesTypeCode(String dishesTypeCode) {
        this.dishesTypeCode = dishesTypeCode;
    }

    public String getDishesName() {
        return dishesName;
    }

    public void setDishesName(String dishesName) {
        this.dishesName = dishesName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
