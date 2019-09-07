package com.mds.cm.service;

import com.google.common.collect.Maps;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Created by kevin on 16/07/15 for Podcast Server
 */
@Service
public class MimeTypeService {

    private final Map<String, String> mimeMap;
    private final TikaProbeContentType tikaProbeContentType;

    @Autowired
    public MimeTypeService(TikaProbeContentType tikaProbeContentType) {
        this.tikaProbeContentType = tikaProbeContentType;
        mimeMap = Maps.newHashMap();
        mimeMap.put("mp4", "video/mp4");
        mimeMap.put("mov", "video/mov");
        //mimeMap.put("wmv", "video/x-ms-wmv");
        mimeMap.put("mp3", "audio/mp3");
        mimeMap.put("flv", "video/flv");
        mimeMap.put("webm", "video/webm");
        mimeMap.put("png", "image/png");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("gif", "image/gif");
        mimeMap.put("bmp", "image/bmp");
        mimeMap.put("", "video/mp4");
    }

    public String getMimeType(String extension) {
        if (extension.isEmpty())
            return "application/octet-stream";

        if (mimeMap.containsKey(extension)) {
            return mimeMap.get(extension);
        } else {
            return "unknown/" + extension;
        }
    }

    /*public String getExtension(String mineType) {
        if (mineType != null) {
            return mineType.replace("audio/", ".").replace("video/", ".");
        }

        if ("Youtube".equals(item.getPodcast().getType()) || item.getUrl().lastIndexOf(".") == -1 ) {
            return ".mp4";
        } else {
            return "."+FilenameUtils.getExtension(item.getUrl());
        }
    }*/

    // https://odoepner.wordpress.com/2013/07/29/transparently-improve-java-7-mime-type-recognition-with-apache-tika/
    public String probeContentType(Path file) {
        return filesProbeContentType(file)
                .orElseGet(() -> tikaProbeContentType.probeContentType(file)
                .orElseGet(() -> getMimeType(FilenameUtils.getExtension(String.valueOf(file.getFileName())))));
    }

    private Optional<String> filesProbeContentType(Path file) {
        String mimeType = null;

        try { mimeType = Files.probeContentType(file); } catch (IOException ignored) {}

        return Optional.ofNullable(mimeType);
    }
    
    public static class TikaProbeContentType {

        private final Tika tika = new Tika();

        Optional<String> probeContentType(Path file) {
            try {
                return Optional.of(tika.detect(file.toFile()));
            } catch (IOException ignored) {
                return Optional.empty();
            }
        }
    }
}
