package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */

public class Query{
    public String cmd;          //传递指令
    public String tableName;    //与操作有关的表名
    public String indexName;    //列名
    public String primarykey;   //主键
    public String fileName;     //文件名
    public int attrNum;         //属性数量
    public Attribute[] attrList;//属性数组

    Query() {
        this.attrList = new Attribute[40];
        for (int i = 0 ; i < 40 ; i ++ ){
            this.attrList[i] = new Attribute();
        }
    }
}