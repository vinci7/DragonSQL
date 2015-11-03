package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */
public class IndexManager {

    public static void dropIndex(String indexName){
        System.out.println("here's IndexManager.indexName();");
    }

    //插入新索引值，已有索引则更新位置信息
    static public void insertKey(Index indexInfo,byte[] key,int blockOffset,int offset) throws Exception{
        System.out.println("here's IndexManager.insertKey();");
    }

    //创建索引
    public static void createIndex(Table tableInfo,Index indexInfo){ //需要API提供表和索引信息结构
        System.out.println("here's IndexManager.createIndex();");
    }

    //删除索引值，没有该索引则什么也不做
    static public void deleteKey(Index indexInfo,byte[] deleteKey) throws Exception{
        System.out.println("here's IndexManager.deleteKey();");
    }

}
