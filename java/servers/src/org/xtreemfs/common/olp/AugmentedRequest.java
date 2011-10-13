/*
 * Copyright (c) 2011 by Felix Langner,
 *               Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */
package org.xtreemfs.common.olp;

import org.xtreemfs.common.olp.ProtectionAlgorithmCore.RequestExpiredException;

/**
 * <p>Interface for instrumented requests that provide monitoring and control information for the 
 * <b>O</b>ver<b>l</b>oad-<b>P</b>rotection algorithm. Contains metadata for applying overload protection to the 
 * application using control information gathered by this field.</p> 
 * 
 * @author fx.langner
 * @version 1.00, 08/18/11
 */
public abstract class AugmentedRequest {
    
    /**
     * <p>Identifier for requests of this type.</p>
     */
    private final int           type;
    
    /**
     * <p>Maximal response time delta for this request.</p>
     */
    private final long          deltaMaxTime;
    
    /**
     * <p>The Unix time stamp this request was initially received.</p>
     */
    private final long          startTime;
    
    /**
     * <p>Flag to determine whether this request has high priority or not.</p>
     */
    private final boolean       highPriority;
    
    /**
     * <p>Constructor for requests that do not require a certain amount of bandwidth for being
     * processed and do not have high priority.</p>
     * 
     * @param type - identifier for this kind of request.
     * @param deltaMaxTime - the time to live for this request.
     */
    public AugmentedRequest(int type, long deltaMaxTime) {
        
        this(type, deltaMaxTime, false);
    }  
    
    /**
     * <p>Constructor for requests that do not require a certain amount of bandwidth for being
     * processed.</p>
     * 
     * @param type - identifier for this kind of request.
     * @param deltaMaxTime - the time to live for this request.
     * @param highPriority - if true request will be treated as with high priority, false otherwise.
     */
    public AugmentedRequest(int type, long deltaMaxTime, boolean highPriority) {
        
        this(type, 0L, deltaMaxTime, highPriority);
    }
    
    /**
     * <p>Constructor for requests that will require a specific amount of bandwidth during processing, that is 
     * proportional to the processing time.</p>
     * 
     * @param type - identifier for this kind of request.
     * @param size - amount of bandwidth occupied during processing of this request.
     * @param deltaMaxTime - the time to live for this request.
     * @param highPriority - if true request will be treated as with high priority, false otherwise.
     */
    public AugmentedRequest(int type, long size, long deltaMaxTime, boolean highPriority) {
        
        assert (deltaMaxTime > -1);
        assert (type > -1);
        
        this.type = type;
        this.deltaMaxTime = deltaMaxTime;
        this.highPriority = highPriority;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * <p>Constructor for internal requests. Internal requests have unlimited remaining processing time and must not be
     * prioritized.</p>
     * 
     * @param type
     */
    public AugmentedRequest(int type) {
        
        this.type = type;
        this.deltaMaxTime = 0;
        this.highPriority = false;
        this.startTime = 0L;
    }
    
/*
 * package internal methods
 */
    
    /**
     * @return the identifier of the type of this request.
     */
    int getType() {
        
        return type;
    }
    
    /**
     * @return true if this request has high priority, false otherwise.
     */
    public boolean hasHighPriority() {
        
        return highPriority;
    }
        
    /**
     * <p>Calculates the remaining processing time for this request. If the request has already been expired an 
     * Exception is thrown.</p>
     * 
     * @return the remaining processing time for this request in ms.
     * @throws RequestExpiredException if the processing time for this request has already been exhausted.
     */
    public final long getRemainingProcessingTime() throws RequestExpiredException {
        
        if (isNativeInternalRequest()) {
            throw new UnsupportedOperationException("The request is internal and therefore has unlimited remaining " +
            		"processing time.");
        } else if (isUnrefusable()) {
            throw new UnsupportedOperationException("The request is unrefusable and therefore has unlimited " +
            		"remaining processing time.");
        }
        
        long remaining = (deltaMaxTime + startTime) - System.currentTimeMillis();
        if (remaining <= 0) {
            throw new RequestExpiredException(this);
        }
        return remaining;
    }

    /**
     * @return true, if this request is an internal request, false if not.
     */
    boolean isNativeInternalRequest() {
        
        return startTime == 0L;
    }
 
    /**
     * @return true, if this request is unrefusable. false otherwise.
     */
    boolean isUnrefusable() {
        
        return deltaMaxTime == 0L;
    }
}