/**
 * Question: 二维数组中的查找
 * 
 * url：https://www.nowcoder.com/practice/abc3fe2ce8e146608e868a70efebf62e?tpId=13&tqId=11154&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking
 * 
 * Description: 在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，
 * 每一列都按照从上到下递增的顺序排序。请完成一个函数， 输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
 * 
 * 
 * Thought: 1. 从最下角或者右上角开始，根据对比结果决定下一步方向
 * 
 * 
 */

public class Solution {
    public boolean Find(int target, int[][] array) {
        if (array.length == 0 || array[0].length == 0) {
            return false;
        }
        int XIdx = 0;
        int YIdx = array.length - 1;
        while (XIdx < array.length && YIdx >= 0) {
            if (array[XIdx][YIdx] == target) {
                return true;
            } else if (array[XIdx][YIdx] < target) {
                XIdx++;
            } else
                YIdx--;
        }
        return false;
    }
}