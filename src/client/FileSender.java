package client;

import java.io.*;
import java.net.Socket;

public class FileSender {

    public static void sendFile(
            String ip,
            int port,
            String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {

            System.out.println(
                    "[FILE] Khong tim thay file!"
            );

            return;
        }

        if (!file.isFile()) {

            System.out.println(
                    "[FILE] Duong dan khong phai file!"
            );

            return;
        }

        try {

            Socket socket =
                    new Socket(ip, port);

            DataOutputStream output =
                    new DataOutputStream(
                            new BufferedOutputStream(
                                    socket.getOutputStream()
                            )
                    );

            FileInputStream fileInput =
                    new FileInputStream(file);

            // Gui ten file
            output.writeUTF(file.getName());

            // Gui kich thuoc
            output.writeLong(file.length());

            byte[] buffer =
                    new byte[4096];

            int bytesRead;

            long totalSent = 0;

            while ((bytesRead =
                    fileInput.read(buffer)) != -1) {

                output.write(
                        buffer,
                        0,
                        bytesRead
                );

                totalSent += bytesRead;

                int percent =
                        (int) (
                                totalSent
                                * 100
                                / file.length()
                        );

                System.out.print(
                        "\r[FILE] Sending: "
                        + percent
                        + "%"
                );
            }

            output.flush();

            fileInput.close();
            output.close();
            socket.close();

            System.out.println();

            System.out.println(
                    "[FILE] Gui file thanh cong!"
            );

        } catch (IOException e) {

            System.out.println(
                    "[FILE] Gui file that bai: "
                    + e.getMessage()
            );
        }
    }
}