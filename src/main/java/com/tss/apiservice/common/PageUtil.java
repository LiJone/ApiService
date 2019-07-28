package com.tss.apiservice.common;

import java.util.List;

public class PageUtil {

    /**
     * 数据里面的数据总行数
     */
    private int rowcount;

    /**
     * 一页多少行数据时
     */
    private int pagesize = 10;//像百度

    /**
     * limit startrow,pagesize;(5,5)
     * 查询起始行
     */
    private int startrow;

    /**
     * 当前点击的导航
     */
    private int currnav;

    /**
     * 页面维护的固定导航个数(百度的十个,例如62到71)
     */
    private int navnum;


    /**
     * 页面数据整体导航个数(例如76,从状态算出来)
     */
    private int navcount;


    /**
     * 页面展示的起始导航 (65的是60和69)
     */
    private int begin;


    /**
     * 页面维护展示的结束导航
     */
    private int end;

    /**
     * 上一页
     */
    private int prev;
    /**
     * 下一页
     */
    private int next;

    /**
     * 首页
     */
    private int first = 1;

    /**
     * 尾页
     */
    private int last;

    /**
     * 存放分页的数据
     */
    private List pageData;

    /**
     * @param rowcount 数据总行数
     * @param pagesize 一页显示多少行数据
     * @param currnav  用户点击的导航
     * @param navnum   页面你想维护多少个导航
     */
    public PageUtil(int rowcount, int pagesize, int currnav, int navnum) {
        this.rowcount = rowcount;
        this.pagesize = pagesize;
        this.currnav = currnav;
        this.navnum = navnum;

//		导航的总个数
        this.navcount = (int) Math.ceil(rowcount * 1.0 / pagesize);
        this.last = this.navcount;
//		大于第一页
        this.currnav = Math.max(this.first, currnav);//维护小于第一页 维护固定第一页
        this.currnav = Math.min(this.currnav, this.last);//超过最后一页，维护最后一页

        this.prev = Math.max(this.first, this.currnav - 1);
        this.next = Math.min(this.last, this.currnav + 1);

        this.startrow = Math.max(0, (this.currnav - 1) * this.pagesize);
        //数据库756 行数据
        //根据点击去动态变化
        //1-5=-4  begin=1
        //currnav=10  begin=5 end=14
        //currnav=14  begin=9 end=18
        //currnav=72  begin=67 end=76
        //currnav=73  begin=67 end=76

        this.begin = Math.max(this.first, this.currnav - this.navnum / 2);
        //68  76
        this.end = Math.min(this.last, this.begin + this.navnum - 1);

        if ((this.end - this.begin) < (this.navnum - 1)) {
            this.begin = Math.max(this.first, this.last - this.navnum + 1);
        }

    }


    public int getRowcount() {
        return rowcount;
    }


    public void setRowcount(int rowcount) {
        this.rowcount = rowcount;
    }


    public int getPagesize() {
        return pagesize;
    }


    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }


    public int getStartrow() {
        return startrow;
    }


    public void setStartrow(int startrow) {
        this.startrow = startrow;
    }


    public int getCurrnav() {
        return currnav;
    }


    public void setCurrnav(int currnav) {
        this.currnav = currnav;
    }


    public int getNavnum() {
        return navnum;
    }


    public void setNavnum(int navnum) {
        this.navnum = navnum;
    }


    public int getNavcount() {
        return navcount;
    }


    public void setNavcount(int navcount) {
        this.navcount = navcount;
    }


    public int getBegin() {
        return begin;
    }


    public void setBegin(int begin) {
        this.begin = begin;
    }


    public int getEnd() {
        return end;
    }


    public void setEnd(int end) {
        this.end = end;
    }


    public int getPrev() {
        return prev;
    }


    public void setPrev(int prev) {
        this.prev = prev;
    }


    public int getNext() {
        return next;
    }


    public void setNext(int next) {
        this.next = next;
    }


    public int getFirst() {
        return first;
    }


    public void setFirst(int first) {
        this.first = first;
    }


    public int getLast() {
        return last;
    }


    public void setLast(int last) {
        this.last = last;
    }


    public List getPageData() {
        return pageData;
    }


    public void setPageData(List pageData) {
        this.pageData = pageData;
    }


    public static void main(String[] args) {
        PageUtil pageUtil = new PageUtil(756, 10, 4, 5);
        System.out.println(pageUtil.getStartrow());
        System.out.println(pageUtil.getBegin());
        System.out.println(pageUtil.getEnd());
        System.out.println(pageUtil.getCurrnav());
    }


}
