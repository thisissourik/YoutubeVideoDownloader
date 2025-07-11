package com.youtube.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class YouTubeDownloader {
    private final String ytDlpPath;
    private Path downloadDirectory;

    public YouTubeDownloader(String ytDlpPath, Path downloadDirectory) throws IOException {
        this.ytDlpPath = ytDlpPath;
        this.downloadDirectory = downloadDirectory.toAbsolutePath();
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() throws IOException {
        if (!Files.exists(downloadDirectory)) {
            Files.createDirectories(downloadDirectory);
        }
        if (!Files.isWritable(downloadDirectory)) {
            throw new IOException("Download directory is not writable: " + downloadDirectory);
        }
    }

    public void updateDownloadDirectory(Path newDirectory) throws IOException {
        this.downloadDirectory = newDirectory.toAbsolutePath();
        ensureDirectoryExists();
    }

    public void download(String url, String format, String quality) throws IOException, InterruptedException {
        List<String> command = buildCommand(url, format, quality);

        System.out.println("Downloading to: " + downloadDirectory);
        System.out.println("Command: " + String.join(" ", command));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Download failed with exit code " + exitCode);
        }
    }

    private List<String> buildCommand(String url, String format, String quality) {
        List<String> command = new ArrayList<>();
        command.add(ytDlpPath);
        command.add("--no-warnings");
        command.add("--ignore-errors");
        command.add("-o");
        command.add(downloadDirectory.resolve("%(title)s.%(ext)s").toString());

        if (format.equalsIgnoreCase("mp3")) {
            command.add("--extract-audio");
            command.add("--audio-format");
            command.add("mp3");
            command.add("--audio-quality");
            command.add("0");
        } else {
            command.add("-f");
            command.add(getFormatString(format, quality));
        }

        command.add(url);
        return command;
    }

    private String getFormatString(String format, String quality) {
        if (format.equalsIgnoreCase("mp3")) {
            return "bestaudio/best";
        }

        switch (quality.toLowerCase()) {
            case "highest":
                return "bestvideo[ext=" + format + "]+bestaudio/best";
            case "lowest":
                return "worstvideo[ext=" + format + "]+worstaudio/worst";
            case "1080p":
                return "bestvideo[height<=1080][ext=" + format + "]+bestaudio/best";
            case "720p":
                return "bestvideo[height<=720][ext=" + format + "]+bestaudio/best";
            case "480p":
                return "bestvideo[height<=480][ext=" + format + "]+bestaudio/best";
            case "360p":
                return "bestvideo[height<=360][ext=" + format + "]+bestaudio/best";
            default:
                return "bestvideo[ext=" + format + "]+bestaudio/best";
        }
    }

    public Path getDownloadDirectory() {
        return downloadDirectory;
    }
}