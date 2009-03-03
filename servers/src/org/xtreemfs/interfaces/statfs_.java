package org.xtreemfs.interfaces;

import org.xtreemfs.interfaces.*;
import org.xtreemfs.interfaces.utils.*;

import org.xtreemfs.foundation.oncrpc.utils.ONCRPCBufferWriter;
import org.xtreemfs.common.buffer.ReusableBuffer;
import org.xtreemfs.common.buffer.BufferPool;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


         
   
public class statfs_ implements org.xtreemfs.interfaces.utils.Serializable
{
    public statfs_() { bsize = 0; bfree = 0; fsid = ""; namelen = 0; }
    public statfs_( int bsize, long bfree, String fsid, int namelen ) { this.bsize = bsize; this.bfree = bfree; this.fsid = fsid; this.namelen = namelen; }

    public int getBsize() { return bsize; }
    public void setBsize( int bsize ) { this.bsize = bsize; }
    public long getBfree() { return bfree; }
    public void setBfree( long bfree ) { this.bfree = bfree; }
    public String getFsid() { return fsid; }
    public void setFsid( String fsid ) { this.fsid = fsid; }
    public int getNamelen() { return namelen; }
    public void setNamelen( int namelen ) { this.namelen = namelen; }

    // Object
    public String toString()
    {
        return "statfs_( " + Integer.toString( bsize ) + ", " + Long.toString( bfree ) + ", " + "\"" + fsid + "\"" + ", " + Integer.toString( namelen ) + " )";
    }    

    // Serializable
    public String getTypeName() { return "xtreemfs::interfaces::statfs_"; }    
    
    public void serialize(ONCRPCBufferWriter writer) {
        writer.putInt( bsize );
        writer.putLong( bfree );
        { org.xtreemfs.interfaces.utils.XDRUtils.serializeString(fsid,writer); }
        writer.putInt( namelen );        
    }
    
    public void deserialize( ReusableBuffer buf )
    {
        bsize = buf.getInt();
        bfree = buf.getLong();
        { fsid = org.xtreemfs.interfaces.utils.XDRUtils.deserializeString(buf); }
        namelen = buf.getInt();    
    }
    
    public int calculateSize()
    {
        int my_size = 0;
        my_size += ( Integer.SIZE / 8 );
        my_size += ( Long.SIZE / 8 );
        my_size += 4 + ( fsid.length() + 4 - ( fsid.length() % 4 ) );
        my_size += ( Integer.SIZE / 8 );
        return my_size;
    }

    private int bsize;
    private long bfree;
    private String fsid;
    private int namelen;

}

