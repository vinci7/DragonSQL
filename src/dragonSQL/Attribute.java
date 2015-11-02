package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */
public class Attribute {
    public boolean unique;      //是否unique
    public String name;         //属性名
    public String value;            //属性值
    public int length;          //长度
    public int type; //1->int 2->float 3->char 属性类型
    public boolean isPrimaryKey;//是否主键
}
