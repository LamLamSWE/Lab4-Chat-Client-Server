package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver implements Runnable {

    private int port;

    public FileReceiver(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        try (ServerSocket fileServer =
                     new ServerSocket(port)) {

            System.out.println(
                    "[FILE] Dang cho nhan file tai port "
                    + port
            );

            while (true) {

                Socket socket = fileServer.accept();

                // Moi file xu ly tren mot Thread rieng
                new Thread(() -> {
                    receiveFile(socket);
                }).start();
            }

        } catch (IOException e) {

            System.out.println(
                    "[FILE] Loi FileReceiver: "
                    + e.getMessage()
            );
        }
    }

    private void receiveFile(Socket socket) {

        try {

            DataInputStream input =
                    new DataInputStream(
                            new BufferedInputStream(
                                    socket.getInputStream()
                            )
                    );

            // Nhan ten file
            String fileName = input.readUTF();

            // Nhan kich thuoc file
            long fileSize = input.readLong();

            System.out.println();
            System.out.println(
                    "[FILE] Dang nhan: " + fileName
            );

            System.out.println(
                    "[FILE] Kich thuoc: "
                    + fileSize
                    + " bytes"
            );

            // Tao folder downloads
            File downloadFolder =
                    new File("downloads");

            if (!downloadFolder.exists()) {
                downloadFolder.mkdirs();
            }

            File outputFile =
                    new File(
                            downloadFolder,
                            fileName
                    );

            FileOutputStream fileOutput =
                    new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];

            long totalRead = 0;

            while (totalRead < fileSize) {

                int bytesToRead =
                        (int) Math.min(
                                buffer.length,
                                fileSize - totalRead
                        );

                int bytesRead =
                        input.read(
                                buffer,
                                0,
                                bytesToRead
                        );

                if (bytesRead == -1) {
                    break;
                }

                fileOutput.write(
                        buffer,
                        0,
                        bytesRead
                );

                totalRead += bytesRead;

                int percent =
                        (int) (
                                totalRead
                                * 100
                                / fileSize
                        );

                System.out.print(
                        "\r[FILE] Progress: "
                        + percent
                        + "%"
                );
            }

            fileOutput.close();
            input.close();
            socket.close();

            System.out.println();
            System.out.println(
                    "[FILE] Nhan file thanh cong!"
            );

            System.out.println(
                    "[FILE] Luu tai: "
                    + outputFile.getAbsolutePath()
            );

            System.out.print("> ");

        } catch (IOException e) {

            System.out.println(
                    "[FILE] Nhan file that bai: "
                    + e.getMessage()
            );
        }
    }
}