package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */

public class Condition {
    public Comparison op;   //操作符 [0,1,2,3,4,5] -> [=,<>,<,>,<=,>=]
    public int columnNum;   //在哪个属性上的比较
    public String value;    //比较的这个特定值
}
