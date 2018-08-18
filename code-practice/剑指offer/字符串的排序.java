
/**
 * Question: 字符串的排序/permutation
 * 
 * url：https://www.nowcoder.com/practice/fe6b651b66ae47d7acce78ffdd9a96c7?tpId=13&tqId=11180&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking
 * 
 * Description: 输入一个字符串,长度不超过9(可能有字符重复),字符只包括大小写字母。
 * 
 * 
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    public ArrayList<String> Permutation(String str) {
        List<String> res = new ArrayList<>();
        if (str != null && str.length() != 0) {
            Permutationhelp(str.toCharArray(), 0, res);
            Collections.sort(res);
        }

        return (ArrayList) res;
    }

    public void Permutationhelp(char[] cs, int i, List<String> list) {
        if (i == cs.length - 1) {
            String val = String.valueOf(cs);
            if (!list.contains(val))
                list.add(val);
        } else {
            for (int j = i; j < cs.length; j++) {
                swap(cs, i, j);
                Permutationhelp(cs, i + 1, list);
                swap(cs, i, j);
            }
        }
    }

    public void swap(char[] cs, int i, int j) {
        char tmp = cs[i];
        cs[i] = cs[j];
        cs[j] = tmp;
    }
}