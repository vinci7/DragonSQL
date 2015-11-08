package dragonSQL;

import java.io.UnsupportedEncodingException;

/**
 * Created by qi on 15/11/2.
 */

public class BufferManager {

    public static int blockNum=0;// the block number of the block,
    private static final int maxBlockNumber = 88;//最大block数量
    private static BufferBlock head = new BufferBlock();//head is a null block
    private static BufferBlock tail =  new BufferBlock();//头尾指针连接成block

    public  BufferManager() {}

    //删除与indexName有关的buffer
    public static void dropIndex(String indexName){
        BufferBlock block = head.next;
        while(block!=null){
            if(block.fileName.equals(indexName)){
                if(block.next!=null)
                    block.next.previous=block.previous;
                block.previous.next=block.next;
            }
            block=block.next;
        }
    }

    //将所有块写回到文件中去
    public static void writeBufferToFile(){
        BufferBlock block = head.next;
        while(block != null){
            if( block.dirtyBit ){
                try {
                    DBfile.BlocktoFile(block.fileName, block.values, block.blockOffset, block.recordNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            block.dirtyBit = false; //设为脏位
            block = block.next;
        }
    }

    //根据文件名和偏移量获取块，并将块上锁
    static public BufferBlock readBlock( String FileName, int Offset,boolean lock){
        BufferBlock block =  readBlock(FileName, Offset);
        block.lock=true;
        return block;
    }

    //根据文件名和偏移量获取块，不将块上锁
    static public BufferBlock readBlock( String FileName, int Offset){
        BufferBlock tempblock = head.next;
        while(tempblock != null){
            if(tempblock.fileName.equals(FileName) && (tempblock.blockOffset == Offset)){
                if(tempblock!=tail){
                    tempblock.previous.next=tempblock.next;
                    tempblock.next.previous=tempblock.previous;
                    tempblock.previous=tail;
                    tail.next=tempblock;
                    tempblock.next=null;
                    tail = tempblock;}
                return tempblock;
            }
            tempblock=tempblock.next;
        }

        if(isFull()){
            writeBlockToFile(head.next);
        } else blockNum++;

        tempblock = new BufferBlock();
        try {
            tempblock=DBfile.FiletoBlock(FileName, Offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tempblock.fileName = FileName;
        tempblock.blockOffset=Offset;
        tempblock.next=null;
        tempblock.dirtyBit=false;
        if(head.next==null){
            head.next=tempblock;
            tempblock.previous=head;
        }
        else {
            tail.next=tempblock;
            tempblock.previous=tail;
        }

        tail=tempblock;
        return tempblock;
    }

    //将块写回文件
    public static void writeBlockToFile(BufferBlock block) {
        if(block.lock){
            writeBlockToFile(block.next);
            return;
        } else
        if(block.dirtyBit){
            try {
                DBfile.BlocktoFile( block.fileName,block.values, block.blockOffset,block.recordNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(block.next!=null) block.next.previous = block.previous;
        block.previous.next = block.next;
    }


    //创建一个缺少value的block
    public static BufferBlock createBlock(String fileName,int blockOffset){
        if(isFull()){
            writeBlockToFile(head.next);
        }
        else blockNum++;

        BufferBlock block = new BufferBlock(fileName,blockOffset);

        if(head.next==null){
            head.next=block;
            block.previous=head;
        }
        else{
            tail.next=block;
            block.previous=tail;
        }
        tail = block;
        return block;
    }

    //获取表的可插入的位置，找到最后一个块，如果仍有空间则返回块，如果没有则返回NULL
    static BufferBlock getInsertPosition(Table tableInfo,String filename){
        BufferBlock block = readBlock(filename, tableInfo.blockNum-1);
        return block.recordNum<tableInfo.maxRecordsPerBlock?block:null;
    }

    //删除某表某块某位移的一条记录
    static void deleteValues(int Offset,int offset,Table tableInfo) throws UnsupportedEncodingException{
        String filename = tableInfo.tableName+".table";
        int length = tableInfo.recordLength;
        BufferBlock block = readBlock(filename, Offset);
        block.delete(offset, length);
    }

    //查看buff是否为满
    static public boolean isFull(){
        return blockNum == maxBlockNumber?true:false;
    }

    //删除表
    public static void dropTable(String filename){
        BufferBlock block = head.next;
        while(block != null){
            if(block.fileName.equals(filename)){
                if(block.next != null)
                    block.next.previous=block.previous;
                block.previous.next=block.next;
            }
            block=block.next;
        }
    }
}
