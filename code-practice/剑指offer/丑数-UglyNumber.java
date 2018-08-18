/**
 * Question: 丑数/UglyNumber
 * 
 * url：https://www.nowcoder.com/practice/6aa9e04fc3794f68acf8778237ba065b?tpId=13&tqId=11186&tPage=2&rp=2&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking
 * 
 * Description: 把只包含质因子2、3和5的数称作丑数（Ugly Number）。
 * 例如6、8都是丑数，但14不是，因为它包含质因子7。习惯上我们把1当做是第一个丑数。 求按从小到大的顺序的第N个丑数。
 * 
 * 
 * Thought: 1. 每一个数字都可以分别乘以2、3、5,从而得到三个结果，但是前一个数字乘以5的结果
 * 可能比后一个数字乘以2的结果要大，所以他可能是无序的，在这种情况下，我们可以 将这些结果存储在一个set中，（PS：
 * et的优点，自动去重）， 并取出set中最小的一个数，作为下一个数字
 * 
 * 2. 每个数字都可以拆分为 2*(i2)+3*(i3)+5*(i5)
 * 
 */

public class Solution {
    public int GetUglyNumber_SolutionA(int index) {
        if (index == 0)
            return 0;
        Set<Integer> set = new HashSet<>();
        int curr = 1;
        int idx = 1;
        int tmp;
        while (idx != index) {
            set.add(curr * 2);
            set.add(curr * 3);
            set.add(curr * 5);
            tmp = Integer.MAX_VALUE;
            // 求set中的最小值
            for (Integer val : set) {
                // 这样处理的目的是发现最大的数超过了MAX_VALUE,变成了负数，需要除去
                tmp = (val < tmp && val > 0) ? val : tmp;
            }
            curr = tmp;
            set.remove(tmp);
            idx++;
        }
        return curr;
    }

    // 方法二
    public int GetUglyNumber_SolutionB(int index) {

        if (index <= 0)
            return 0;
        int[] result = new int[index];
        int count = 0;
        int i2 = 0;
        int i3 = 0;
        int i5 = 0;

        result[0] = 1;
        int tmp = 0;
        while (count < index - 1) {
            // 比较三个数的大小
            tmp = min(result[i2] * 2, min(result[i3] * 3, result[i5] * 5));
            if (tmp == result[i2] * 2)
                i2++;// 三条if防止值是一样的，不要改成else的
            if (tmp == result[i3] * 3)
                i3++;
            if (tmp == result[i5] * 5)
                i5++;
            result[++count] = tmp;
        }
        return result[index - 1];
    }

    private int min(int a, int b) {
        return (a > b) ? b : a;
    }
}