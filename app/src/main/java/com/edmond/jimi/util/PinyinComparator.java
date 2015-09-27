package com.edmond.jimi.util;

import com.edmond.jimi.entity.Entity;

import java.util.Comparator;

/**
 *
 * @author Edmond
 *
 */
public class PinyinComparator implements Comparator<Entity> {

    public int compare(Entity o1, Entity o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}