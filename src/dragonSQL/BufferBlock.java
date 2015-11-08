package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */


public class BufferBlock {
    public String fileName;         //文件名
    public int blockOffset;         //偏移量

    public byte[] values;           //= new byte[4096];
    public int recordNum;           //记录record数量

    public boolean dirtyBit;        //=false; false 为脏位
    public BufferBlock next;        //=null 指向下一块block的指针
    public BufferBlock previous;    //=null 指向上一块block的指针


    public	boolean lock;           //=false prevent the block from replacing 锁定

    public BufferBlock() {          //init 初始化数据
        fileName = null;
        values = new byte[4096];
        dirtyBit = false;
        next = null;
        previous = null;
        lock = false;
    }

    public  BufferBlock(String FileName,int Offset){ //带有文件名和偏移量的init初始化
        fileName = FileName;
        blockOffset = Offset;
        dirtyBit = true;
        next = null;
        values=new byte[4096];
        lock = false;
        recordNum = 0;
    }

    public void insert(String s,int pos){           //插入数据
        byte[] b =s.getBytes(/*"ISO-8859-1"*/);
        for(int i = 0;i<b.length;i++)
            values[pos+i]=b[i];
        dirtyBit = true;                            //设置脏位
    }

    public void insert(int sourceInt, int pos ,int length){
        for(int i=0;i<length;i++){
            values[i+pos]=(byte)(sourceInt>>8*(3-i)&0xFF);
        }
        dirtyBit = true;
    }

    public void delete(int pos, int length){        //删除数据
        for(int i = 0;i<4096-pos-length;i++){
            values[i+pos]=values[i+length+pos];
        }
        for(int i =0;i<length;i++){
            values[4095-i]=0;
        }
        dirtyBit = true;
    }

    public byte[] getBytes(int startpos,int length){ //获取
        byte[] b = new byte[length];
        for(int i =0;i<length;i++){
            b[i]=values[startpos+i];
        }
        return b;
    }

    public  void setBytes(int startpos, byte[] sourcebyte){  //设置
        //byte[] b = new byte[length];
        for(int i =0;i<sourcebyte.length;i++){
            values[startpos+i]=sourcebyte[i];
        }
        dirtyBit = true;
    }

    public  String getString(int startpos, int length){
        byte[] b = new byte[length];
        for(int i =0;i<length;i++){
            b[i]=values[startpos+i];
        }
        String tmpt = new String(b);
        return tmpt;
    }

    public  int getInt(int pos, int length){
        int k=0;
        for(int i=0;i<length;i++){
            k  +=(values[i+pos] & 0xFF)<<(8*(3-i));
        }
        return k;
    }

    public  void setKeyValues(int pos,byte[] insertKey,int blockOffset,int offset) {
        setInt(pos,4,blockOffset);
        setInt(pos+4,4,offset);
        setBytes(pos+8,insertKey);
        dirtyBit=true;
    }

    public  void setInternalKey(int pos,byte[] key,int offset) {
        setBytes(pos,key);
        setInt(pos+key.length,4,offset);
        dirtyBit=true;
    }

    public  void setInt(int pos, int length,int sourceInt){

        for(int i=0;i<length;i++){
            values[i+pos]=(byte)(sourceInt>>8*(3-i)&0xFF);
        }
        dirtyBit=true;
    }

    public  void setFloat(int pos,float sourcefloat){

        int l = Float.floatToIntBits(sourcefloat);
        for (int j = 0; j < 4; j++) {
            values[pos+j] = new Integer(l).byteValue();
            l = l >> 8;
        }
        dirtyBit=true;
    }

    public  void setString(int pos,String sourceString){
        byte[] b = sourceString.getBytes(/*"ISO-8859-1"*/);
        for(int i =0;i<b.length;i++){
            values[pos+i]=b[i];
        }
        dirtyBit=true;
    }

}