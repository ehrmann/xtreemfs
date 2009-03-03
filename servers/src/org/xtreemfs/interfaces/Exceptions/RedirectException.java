package org.xtreemfs.interfaces.Exceptions;

import org.xtreemfs.interfaces.*;
import org.xtreemfs.interfaces.Exceptions.*;
import org.xtreemfs.interfaces.utils.*;

import org.xtreemfs.foundation.oncrpc.utils.ONCRPCBufferWriter;
import org.xtreemfs.common.buffer.ReusableBuffer;
import org.xtreemfs.common.buffer.BufferPool;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


         
   
public class RedirectException implements org.xtreemfs.interfaces.utils.Serializable
{
    public RedirectException() { to_uuid = ""; }
    public RedirectException( String to_uuid ) { this.to_uuid = to_uuid; }

    public String getTo_uuid() { return to_uuid; }
    public void setTo_uuid( String to_uuid ) { this.to_uuid = to_uuid; }

    // Object
    public String toString()
    {
        return "RedirectException( " + "\"" + to_uuid + "\"" + " )";
    }    

    // Serializable
    public String getTypeName() { return "xtreemfs::interfaces::Exceptions::RedirectException"; }    
    
    public void serialize(ONCRPCBufferWriter writer) {
        { org.xtreemfs.interfaces.utils.XDRUtils.serializeString(to_uuid,writer); }        
    }
    
    public void deserialize( ReusableBuffer buf )
    {
        { to_uuid = org.xtreemfs.interfaces.utils.XDRUtils.deserializeString(buf); }    
    }
    
    public int calculateSize()
    {
        int my_size = 0;
        my_size += 4 + ( to_uuid.length() + 4 - ( to_uuid.length() % 4 ) );
        return my_size;
    }

    private String to_uuid;

}

