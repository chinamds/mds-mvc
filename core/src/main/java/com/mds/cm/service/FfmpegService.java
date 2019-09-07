package com.mds.cm.service;

import io.vavr.control.Try;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mds.util.custom.ffmpeg.CustomRunProcessFunc;
import com.mds.util.custom.ffmpeg.ProcessListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.vavr.API.List;
import static io.vavr.API.Try;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Created by kevin on 19/07/2014 for Podcast Server
 */

@Component("FfmpegService")
public class FfmpegService {
	
	private final static Logger log = LoggerFactory.getLogger(FfmpegService.class);

    public static final String AUDIO_BITSTREAM_FILTER_AAC_ADTSTOASC = "aac_adtstoasc";
    public static final String CODEC_COPY = "copy";
    private static final String FORMAT_CONCAT = "concat";

    private final CustomRunProcessFunc runProcessFunc;
    private final FFmpegExecutor ffmpegExecutor;
    private final FFprobe ffprobe;
    
    public FfmpegService(FFmpegExecutor ffmpegExecutor, FFprobe ffprobe, CustomRunProcessFunc runProcessFunc) {
    	this.ffmpegExecutor = ffmpegExecutor;
    	this.ffprobe = ffprobe;
    	this.runProcessFunc = runProcessFunc;
    }

    /* Concat files */
    public void concat(Path target, Path... files) {
        Path listOfFiles = null;
        try {
            Files.deleteIfExists(target);

            String filesStrings = List(files)
                    .map(f -> f.getFileName().toString())
                    .map(p -> "file '" + p + "'")
                    .mkString(System.getProperty("line.separator"));

            listOfFiles = Files.createTempFile(target.getParent(), "ffmpeg-list-", ".txt");
            Files.write(listOfFiles, filesStrings.getBytes());

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(listOfFiles.toAbsolutePath().toString())
                    .setFormat(FORMAT_CONCAT)
                    .addOutput(target.toAbsolutePath().toString())
                    .setAudioCodec(CODEC_COPY)
                    .setVideoCodec(CODEC_COPY)
                    .setFormat("mp4")
                    .done();

            ffmpegExecutor.createJob(builder).run();

        } catch (IOException e) {
            log.error("Error during Ffmpeg conversion", e);
        } finally {
            Path finalListOfFiles = listOfFiles;
            if (nonNull(listOfFiles)) Try(() -> Files.deleteIfExists(finalListOfFiles)); }
    }

    /* Merge Audio and Video Files */
    public Path mergeAudioAndVideo(Path videoFile, Path audioFile, Path dest) {
        Path convertedAudio = convert(audioFile, audioFile.resolveSibling(changeExtension(audioFile, "aac")));

        Path tmpFile = generateTempFileFor(dest, videoFile);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(convertedAudio.toAbsolutePath().toString())
                .addInput(videoFile.toAbsolutePath().toString())
                .addOutput(tmpFile.toAbsolutePath().toString())
                .setAudioBitStreamFilter(AUDIO_BITSTREAM_FILTER_AAC_ADTSTOASC)
                .setAudioCodec(CODEC_COPY)
                .setVideoCodec(CODEC_COPY)
                .done();

        ffmpegExecutor.createJob(builder).run();

        Try(() -> Files.deleteIfExists(convertedAudio));
        Try(() -> Files.move(tmpFile, dest, StandardCopyOption.REPLACE_EXISTING));

        return dest;
    }

    private Path convert(Path source, Path dest) {
        FFmpegBuilder converter = new FFmpegBuilder()
                .setInput(source.toAbsolutePath().toString())
                .addOutput(dest.toAbsolutePath().toString())
                .disableVideo()
                .done();

        ffmpegExecutor.createJob(converter).run();
        return dest;
    }

    private Path generateTempFileFor(Path dest, Path video) {
        return Try(() -> Files.createTempFile(dest.getParent(), dest.getFileName().toString(), "." + FilenameUtils.getExtension(video.getFileName().toString())))
                .onFailure(e -> log.error("Error during generation of tmp file for {}", video.toAbsolutePath().toString()))
                .getOrElseThrow(e -> new RuntimeException(e));
    }

    private Path changeExtension(Path audioFile, String ext) {
        String newName = FilenameUtils.getBaseName(audioFile.getFileName().toString()).concat(".").concat(ext);
        return audioFile.resolveSibling(newName);
    }

    /* Get duration of a File */
    public double getDurationOf(String url, String userAgent) {

        CompletableFuture<Double> duration = supplyAsync(() -> Try(() -> ffprobe.probe(url, userAgent).getFormat().duration)
                .map(d -> d * 1_000_000)
                .getOrElseThrow((Function<Throwable, RuntimeException>) RuntimeException::new));

        return Try(() -> duration.get(60, TimeUnit.SECONDS))
                .onFailure(e -> duration.cancel(true))
                .getOrElseThrow(e -> new RuntimeException("Error during probe operation", e));
    }

    /* Download delegation to ffmpeg-cli-wrapper */
    public Process download(String url, FFmpegBuilder ffmpegBuilder, ProgressListener progressListener) {
        ProcessListener pl = new ProcessListener(url);
        runProcessFunc.add(pl);

        runAsync(ffmpegExecutor.createJob(ffmpegBuilder, progressListener));

        Future<Process> process = pl.getProcess();
        return Try(() -> process.get(2, TimeUnit.SECONDS))
                .onFailure(e -> process.cancel(true))
                .getOrElseThrow(e -> new UncheckedIOException(IOException.class.cast(e)));
    }
}
