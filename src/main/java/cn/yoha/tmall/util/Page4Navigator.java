package cn.yoha.tmall.util;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 包装分页类，对页码显示进行的优化
 * @param <T>
 */
public class Page4Navigator<T> {
    private Page<T> pageFromJPA;
    //前端页码显示的数量
    private int navigatorPages;
    //总页数
    private int totalPages;
    //当前页码
    private int number;
    //总记录数
    private long totalElements;
    //每页有多少条数据
    private int size;
    //当前页有多少条数据
    private int numberOfElements;
    //
    private List<T> content;
    private boolean isHasContent;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private int[] navigatePageNums;

    public Page4Navigator(){
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }

    public Page4Navigator(Page<T> pageFromJPA, int navigatorPages){
        this.pageFromJPA = pageFromJPA;
        this.navigatorPages = navigatorPages;
        totalPages = pageFromJPA.getTotalPages();
        number = pageFromJPA.getNumber();
        totalElements = pageFromJPA.getTotalElements();
        size = pageFromJPA.getSize();
        numberOfElements = pageFromJPA.getNumberOfElements();
        content = pageFromJPA.getContent();
        isHasContent = pageFromJPA.hasContent();
        first = pageFromJPA.isFirst();
        last = pageFromJPA.isLast();
        hasNext = pageFromJPA.hasNext();
        hasPrevious = pageFromJPA.hasPrevious();
        calcNavigatePageNums();
    }

    private void calcNavigatePageNums() {
        int navigatePageNums[];
        int totalPages = getTotalPages();
        int num = getNumber();
        if (totalPages <= navigatorPages){//如果总页数比要求显示的页数少，就只显示实际的总页数
            navigatePageNums = new int[totalPages];
            for (int i=0; i<totalPages; i++){
                navigatePageNums[i] = i+1;
            }
        } else {//如果总页数比要求显示的页数多，按要求填充
            navigatePageNums = new int[navigatorPages];
            int start = num - navigatorPages / 2;
            int end = num + navigatorPages / 2;
            if (start < 1){//要求显示的页数的左边界不符合要求，就直接从1开始。
                start = 1;
                for (int i=0; i<navigatorPages; i++){
                    navigatePageNums[i] = start++;
                }
            } else if (end > totalPages){//如果要求显示的页数的右边界不符合要求，从总页码开始倒推
                end = totalPages;
                for (int i=navigatorPages-1; i>=0; i--){
                    navigatePageNums[i] = end--;
                }
            } else {//不存在边界问题，正常计算
                for (int i=0; i<navigatorPages; i++){
                    navigatePageNums[i] = start++;
                }
            }
        }
        this.navigatePageNums = navigatePageNums;
    }

    public int getNavigatorPages() {
        return navigatorPages;
    }

    public void setNavigatorPages(int navigatorPages) {
        this.navigatorPages = navigatorPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return isHasContent;
    }

    public void setHasContent(boolean hasContent) {
        isHasContent = hasContent;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int[] getNavigatePageNums() {
        return navigatePageNums;
    }

    public void setNavigatePageNums(int[] navigatePageNums) {
        this.navigatePageNums = navigatePageNums;
    }
}
