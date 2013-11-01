/*
 * Copyright (c) 2012-2013 by Jens V. Fischer, Zuse Institute Berlin
 *
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */

package org.xtreemfs.common.benchmark;

import org.xtreemfs.common.libxtreemfs.AdminClient;
import org.xtreemfs.common.libxtreemfs.FileHandle;
import org.xtreemfs.pbrpc.generatedinterfaces.GlobalTypes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

/**
 * Class implementing a sequential write benchmark.
 * 
 * @author jensvfischer
 */
class SequentialWriteBenchmark extends SequentialBenchmark {

    private LinkedList<String> filenames;

    SequentialWriteBenchmark(Config config, AdminClient client, VolumeManager volumeManager) throws Exception {
        super(config, client, volumeManager);
        filenames = new LinkedList<String>();
    }

    /* Called within the benchmark method. Performs the actual writing of data to the volume. */
    @Override
    long performIO(byte[] data, long numberOfBlocks) throws IOException {
        Random random = new Random();
        int flags = GlobalTypes.SYSTEM_V_FCNTL.SYSTEM_V_FCNTL_H_O_CREAT.getNumber()
                | GlobalTypes.SYSTEM_V_FCNTL.SYSTEM_V_FCNTL_H_O_TRUNC.getNumber()
                | GlobalTypes.SYSTEM_V_FCNTL.SYSTEM_V_FCNTL_H_O_RDWR.getNumber();
        FileHandle fileHandle = volume.openFile(config.getUserCredentials(), BENCHMARK_FILENAME + 0, flags, 511);
        this.filenames.add(BENCHMARK_FILENAME + 0);
        long byteCounter = 0;
        for (long j = 0; j < numberOfBlocks; j++) {
            long nextOffset = j * chunkSize;
            assert nextOffset >= 0 : "Offset < 0 not allowed";
            random.nextBytes(data);
            byteCounter += fileHandle.write(config.getUserCredentials(), data, chunkSize, nextOffset);
        }
        fileHandle.close();
        return byteCounter;
    }

    @Override
    void finalizeBenchmark() throws Exception {
        volumeManager.setSequentialFilelistForVolume(volume, filenames);
        volumeManager.addCreatedFiles(volume, filenames);
    }

}
