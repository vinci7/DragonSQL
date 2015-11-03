package dragonSQL;

import java.io.UnsupportedEncodingException;

/**
 * Created by qi on 15/11/2.
 */

public class BufferManager {

    static public int blockNum=0;	// the block number of the block,
    static private final int maxBlockNumber = 88;
    static private BufferBlock head = new BufferBlock();//head is a null block
    static private BufferBlock tail =  new BufferBlock();

    public static void dropIndex(String indexName){
        System.out.println("Here's BufferManager.dropIndex();");
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

    public  BufferManager() {

    }

    //将所有块写回到文件中去
    public static void writeBufferToFile(){
        BufferBlock block = head.next;
        while(block!=null){
            if(block.dirtyBit){
                try {
                    DBfile.BlocktoFile(block.fileName, block.values, block.blockOffset, block.recordNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            block.dirtyBit=false;
            block=block.next;
        }
    }

    //根据文件名和偏移量获取块，并将块上锁
    static public BufferBlock readBlock( String FileName, int Offset,boolean lock){
        BufferBlock block =  readBlock(  FileName,  Offset);
        block.lock=true;
        return block;
    }

    //根据文件名和偏移量获取块
    static public BufferBlock readBlock( String FileName, int Offset){
        BufferBlock tempblock = head.next;

        //search if it is in the buffer
        while(tempblock!=null){
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

        //if it is not in the buffer
        //read from the file
        if(isFull()){
            writeBlockToFile(head.next);
        }
        else blockNum++;

        tempblock = new BufferBlock();
        try {
            tempblock=DBfile.FiletoBlock(FileName, Offset);
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
    static public void writeBlockToFile(BufferBlock block) {
        if(block.lock){
            writeBlockToFile(block.next);
            return;
        }

        if(!block.dirtyBit);
        else
            try {
                DBfile.BlocktoFile( block.fileName,block.values, block.blockOffset,block.recordNum);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        if(block.next!=null)
            block.next.previous=block.previous;
        block.previous.next=block.next;
    }


    //return a block only lack values;
    static public BufferBlock createBlock(String fileName,int blockOffset){
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
        BufferBlock block = readBlock(/*tableInfo.tableName*/filename, tableInfo.blockNum-1);
        if(block.recordNum<tableInfo.maxRecordsPerBlock)
            return block;
        else
            return null;

    }

    //删除给定表的给定块的给定位移处的一条记录
    static void deleteValues(int blockOffset,int offset,Table tableInfo) throws UnsupportedEncodingException{
        String filename = tableInfo.tableName+".table";
        int length = tableInfo.recordLength;
        BufferBlock b = readBlock(filename, blockOffset);
        b.delete(offset, length);
    }


    //查看buff是否为满
    static public boolean isFull(){
        if(blockNum == maxBlockNumber)
            return true;
        else return false;
    }

    static public void dropTable(String filename){
        BufferBlock block = head.next;
        while(block!=null){
            if(block.fileName.equals(filename)){
                if(block.next!=null)
                    block.next.previous=block.previous;
                block.previous.next=block.next;
            }
            block=block.next;
        }
    }
}

