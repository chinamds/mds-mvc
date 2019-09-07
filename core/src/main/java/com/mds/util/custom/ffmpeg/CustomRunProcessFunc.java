package com.mds.util.custom.ffmpeg;

import io.vavr.collection.List;
import net.bramp.ffmpeg.RunProcessFunction;

import java.io.IOException;


/**
 * Created by kevin on 24/07/2016.
 */
public class CustomRunProcessFunc extends RunProcessFunction {

    private List<ProcessListener> listeners = List.empty();

    @Override
    public Process run(java.util.List<String> args) throws IOException {
        Process p = super.run(args);

        this.listeners = listeners.remove(
            listeners
                    .find(pl -> args.contains(pl.getUrl()))
                    .map(pl -> pl.setProcess(p))
                    .getOrElse(ProcessListener.DEFAULT_PROCESS_LISTENER)
        );

        return p;
    }

    public CustomRunProcessFunc add(ProcessListener pl) {
        this.listeners = listeners.append(pl);
        return this;
    }
}
