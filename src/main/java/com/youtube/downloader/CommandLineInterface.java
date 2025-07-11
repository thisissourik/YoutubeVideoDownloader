package com.youtube.downloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CommandLineInterface {
    private final YouTubeDownloader downloader;
    private final Scanner scanner;

    public CommandLineInterface(YouTubeDownloader downloader) {
        this.downloader = downloader;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printWelcomeMessage();

        while (true) {
            try {
                System.out.print("\nEnter command (download/location/quit): ");
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "download":
                        handleDownload();
                        break;
                    case "location":
                        changeDownloadLocation();
                        break;
                    case "quit":
                        exitProgram();
                        return;
                    default:
                        System.out.println("Unknown command. Available commands: download, location, quit");
                }
            } catch (Exception e) {
                System.err.println("\nError: " + e.getMessage());
            }
        }
    }

    private void exitProgram() {
        System.out.println("\n====================================");
        System.out.println("      YouTube Downloader v1.0");
        System.out.println("------------------------------------");
        System.out.println("  Developed by: Sourik Karmakar");
        System.out.println("  Contact: sourikkarmakar2018@gmail.com");
//        System.out.println("  GitHub: github.com/[yourusername]");
        System.out.println("====================================");
        System.out.println("\nThank you for using this application!");
    }

    private void printWelcomeMessage() {
        System.out.println("====================================");
        System.out.println("    YouTube Video Downloader");
        System.out.println("====================================");
        System.out.println("Current download location: " + downloader.getDownloadDirectory());
        System.out.println("\nCommands:");
        System.out.println("  download  - Download a video");
        System.out.println("  location  - Change download location");
        System.out.println("  quit      - Exit the program");
    }

    private void handleDownload() throws Exception {
        System.out.print("\nEnter YouTube URL: ");
        String url = scanner.nextLine().trim();

        if (url.isEmpty()) {
            System.out.println("URL cannot be empty");
            return;
        }

        String format = selectFormat();
        String quality = format.equalsIgnoreCase("mp3") ? "best" : selectQuality();

        System.out.println("\nStarting download...");
        downloader.download(url, format, quality);
        System.out.println("\nDownload completed successfully!");
    }

    private String selectFormat() {
        while (true) {
            System.out.println("\nAvailable formats:");
            System.out.println("1. MP4 (Video)");
            System.out.println("2. MP3 (Audio only)");
            System.out.println("3. WEBM");
            System.out.print("Select format (1-3): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    return "mp4";
                case "2":
                    return "mp3";
                case "3":
                    return "webm";
                default:
                    System.out.println("Invalid selection. Please enter 1, 2, or 3.");
            }
        }
    }

    private String selectQuality() {
        while (true) {
            System.out.println("\nAvailable qualities:");
            System.out.println("1. Best quality");
            System.out.println("2. 1080p");
            System.out.println("3. 720p");
            System.out.println("4. 480p");
            System.out.println("5. 360p");
            System.out.print("Select quality (1-5): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    return "best";
                case "2":
                    return "1080p";
                case "3":
                    return "720p";
                case "4":
                    return "480p";
                case "5":
                    return "360p";
                default:
                    System.out.println("Invalid selection. Please enter a number between 1 and 5.");
            }
        }
    }

    private void changeDownloadLocation() throws IOException {
        System.out.print("\nEnter new download location (absolute or relative path): ");
        String newLocation = scanner.nextLine().trim();

        if (newLocation.isEmpty()) {
            System.out.println("Location cannot be empty");
            return;
        }

        Path newPath = Paths.get(newLocation).toAbsolutePath();
        Files.createDirectories(newPath);

        downloader.updateDownloadDirectory(newPath);
        System.out.println("Download location changed to: " + newPath);
    }
}