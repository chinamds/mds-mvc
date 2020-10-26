/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;


import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import static io.vavr.API.Try;
/**
 * Created by kevin on 25/01/2016 for Podcast Server
 */
@Service
public class ProcessService {

    public ProcessBuilder newProcessBuilder(String... command) { return new ProcessBuilder(command); }

    public Try<Process> start(ProcessBuilder processBuilder) {
        return Try(processBuilder::start);
    }

    public int pidOf(Process p) {
        return Try(() -> p.getClass().getSimpleName())
            .filter(className -> className.contains("UNIXProcess"))
            .mapTry(className -> p.getClass().getDeclaredField("pid"))
            .andThenTry(c -> c.setAccessible(true))
            .mapTry(c -> c.getInt(p))
            .getOrElse(-1);
    }

    public Try<Integer> waitFor(Process process) {
        return Try(process::waitFor);
    }
}
