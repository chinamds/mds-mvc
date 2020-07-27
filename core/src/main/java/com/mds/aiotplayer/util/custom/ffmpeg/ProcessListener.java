package com.mds.aiotplayer.util.custom.ffmpeg;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

/**
 * Created by kevin on 24/07/2016.
 */
public class ProcessListener {

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static final ProcessListener DEFAULT_PROCESS_LISTENER = new ProcessListener(StringUtils.EMPTY);

    private final String url;
    private Process process;
    
    public ProcessListener(String url) {
    	this.url = url;
    }

    public Future<Process> getProcess() {
        return pool.submit(() -> {
            while (true) {
                if(nonNull(process)) return process;
                TimeUnit.MILLISECONDS.sleep(100);
            }
        });
    }
    
    public ProcessListener setProcess(Process process) {
        this.process = process;
        
        return this;
    }

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
}
