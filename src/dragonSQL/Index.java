package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */
public class Index {
    public String indexName;	//all the datas is store in file index_name.index
    public String tableName;	//the name of the table on which the index is create
    public int column;			//on which column the index is created
    public int columnLength;
    public int rootNum;
    public int blockNum=1;		//number of block the datas of the index occupied in the file index_name.table
}

