
/**
 * Question: 数据流中的中位数
 * 
 * url：https://www.nowcoder.com/practice/459bd355da1549fa8a49e350bf3df484?tpId=13&tqId=11183&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking
 * 
 * Description:
 * 
 * 如何得到一个数据流中的中位数？如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。如果从数据流中读出偶数个数值，那么中位数就是所有数值排序之后中间两个数的平均值。我们使用Insert()方法读取数据流，使用GetMedian()方法获取当前读取数据的中位数。
 * 
 * 
 * 
 */
import java.util.ArrayList;

public class Solution {

    ArrayList<Integer> maxmin = new ArrayList<>();
    ArrayList<Integer> minmax = new ArrayList<>();

    public void Insert(Integer num) {
        if (maxmin.size() == 0) {
            maxmin.add(0);
            minmax.add(0);
        }
        if (maxmin.size() == minmax.size()) {
            if (minmax.size() == 1) {
                minmax.add(num);
            } else {
                if (maxmin.get(1) >= num) {
                    minmax.add(num);
                    minup(minmax, minmax.size() - 1);
                } else {
                    minmax.add(maxmin.get(1));
                    minup(minmax, minmax.size() - 1);
                    maxmin.set(1, num);
                    maxdown(maxmin, 1);
                }
            }

        } else {
            if (maxmin.size() == 1) {
                if (minmax.get(1) > num) {
                    maxmin.add(minmax.get(1));
                    minmax.set(1, num);
                } else {
                    maxmin.add(num);
                }

            } else {
                if (minmax.get(1) <= num) {
                    maxmin.add(num);
                    maxup(maxmin, maxmin.size() - 1);
                } else {
                    maxmin.add(minmax.get(1));
                    maxup(maxmin, maxmin.size() - 1);
                    minmax.set(1, num);
                    mindown(minmax, 1);
                }
            }

        }
    }

    public void maxdown(ArrayList<Integer> maxmin, int index) {
        if (index * 2 == maxmin.size() - 1) {
            int indexV = (Integer) maxmin.get(index);
            int left = index * 2;
            int leftV = (Integer) maxmin.get(left);
            if (leftV < indexV) {
                maxmin.set(index, leftV);
                maxmin.set(left, indexV);
            }
        }
        if (index * 2 < maxmin.size() - 1) {
            int left = index * 2;
            int right = index * 2 + 1;
            int indexV = (Integer) maxmin.get(index);
            int leftV = (Integer) maxmin.get(left);
            int rightV = (Integer) maxmin.get(right);
            if (leftV > rightV) {
                if (indexV > rightV) {
                    maxmin.set(index, rightV);
                    maxmin.set(right, indexV);
                    maxdown(maxmin, right);
                }
            } else if (leftV < rightV) {
                if (indexV > leftV) {
                    maxmin.set(index, leftV);
                    maxmin.set(left, indexV);
                    maxdown(maxmin, left);
                }
            }

        }
    }

    public void mindown(ArrayList<Integer> minmax, int index) {
        if (index * 2 == minmax.size() - 1) {
            int indexV = (Integer) minmax.get(index);
            int left = index * 2;
            int leftV = (Integer) minmax.get(left);
            if (leftV > indexV) {
                minmax.set(index, leftV);
                minmax.set(left, indexV);
            }
        }
        if (index * 2 < minmax.size() - 1) {
            int left = index * 2;
            int right = index * 2 + 1;
            int indexV = (Integer) minmax.get(index);
            int leftV = (Integer) minmax.get(left);
            int rightV = (Integer) minmax.get(right);
            if (leftV < rightV) {
                if (indexV < rightV) {
                    minmax.set(index, rightV);
                    minmax.set(right, indexV);
                    maxdown(minmax, right);
                }
            } else if (leftV > rightV) {
                if (indexV < leftV) {
                    minmax.set(index, leftV);
                    minmax.set(left, indexV);
                    maxdown(minmax, left);
                }
            }

        }
    }

    public void minup(ArrayList<Integer> minmax, int index) {
        if (index > 1) {
            int parent = index / 2;
            int parentV = (Integer) minmax.get(parent);
            int indexV = (Integer) minmax.get(index);
            if (indexV > parentV) {
                minmax.set(parent, indexV);
                minmax.set(index, parentV);
                minup(minmax, parent);
            }

        }
    }

    public void maxup(ArrayList<Integer> maxmin, int index) {
        if (index > 1) {
            int parent = index / 2;
            int parentV = (Integer) maxmin.get(parent);
            int indexV = (Integer) maxmin.get(index);
            if (indexV < parentV) {
                maxmin.set(parent, indexV);
                maxmin.set(index, parentV);
                maxup(maxmin, parent);
            }

        }
    }

    public Double GetMedian() {
        if (minmax.size() == maxmin.size())
            return (double) (minmax.get(1) + maxmin.get(1)) / 2;
        else
            return (double) minmax.get(1);
    }

}