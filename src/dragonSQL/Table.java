package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */
public class Table {
    public String tableName;
    public String primaryKey;
    public Attribute[] attrlist;

    public int blockNum;	    //number of block the datas of the table occupied in the file name.table
    public int attrNum;	        //the number of attributes in the tables
    public int recordLength;	//total length of one record, should be equal to sum(attributes[i].length)
    public int maxRecordsPerBlock;

    Table()
    {
        tableName = null;
        primaryKey = null;
        blockNum = 1;
        recordLength = 0;
        maxRecordsPerBlock = 0;

        this.attrlist = new Attribute[40];
        for (int i = 0 ; i < 40 ; i ++ ){
            this.attrlist[i] = new Attribute();
        }
    }
}
