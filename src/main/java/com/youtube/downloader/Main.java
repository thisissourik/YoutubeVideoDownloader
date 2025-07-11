package com.youtube.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {
    private static final String BIN_DIR = "bin";
    private static final String DEFAULT_DOWNLOAD_DIR = "downloads";
    private static final String RESOURCE_PATH = "/bin/";

    public static void main(String[] args) {
        try {
            // Extract the appropriate yt-dlp binary
            String binaryPath = extractBinary();

            // Set up download directory
            Path downloadDir = getDownloadDirectory(args);

            // Prepare the downloader
            YouTubeDownloader downloader = new YouTubeDownloader(binaryPath, downloadDir);

            // Start interactive CLI
            new CommandLineInterface(downloader).start();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Path getDownloadDirectory(String[] args) throws IOException {
        if (args.length > 0) {
            return Paths.get(args[0]).toAbsolutePath();
        }
        Path defaultDir = Paths.get(DEFAULT_DOWNLOAD_DIR);
        Files.createDirectories(defaultDir);
        return defaultDir;
    }

    private static String extractBinary() throws IOException {
        Files.createDirectories(Paths.get(BIN_DIR));

        String osName = System.getProperty("os.name").toLowerCase();
        String binaryName;
        String resourcePath;

        if (osName.contains("win")) {
            binaryName = "yt-dlp.exe";
            resourcePath = RESOURCE_PATH + "windows/yt-dlp.exe";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            binaryName = "yt-dlp";
            resourcePath = RESOURCE_PATH + "linux/yt-dlp";
        } else if (osName.contains("mac")) {
            binaryName = "yt-dlp";
            resourcePath = RESOURCE_PATH + "mac/yt-dlp";
        } else {
            throw new IOException("Unsupported operating system");
        }

        Path outputPath = Paths.get(BIN_DIR, binaryName);
        try (InputStream is = Main.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Could not find embedded yt-dlp binary");
            }
            Files.copy(is, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!osName.contains("win")) {
            outputPath.toFile().setExecutable(true);
        }

        return outputPath.toString();
    }
}