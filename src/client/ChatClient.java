package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER_IP = "172.20.10.4";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {

            System.out.println("============================");
            System.out.println("P2P CHAT APPLICATION");
            System.out.println("============================");

            // =========================
            // NHAP USERNAME
            // =========================

            System.out.print("Nhap username: ");
            String username = scanner.nextLine();

            // =========================
            // NHAP FILE PORT
            // =========================

            System.out.print("Nhap File Port (VD 6001): ");
            int filePort = Integer.parseInt(scanner.nextLine());

            // =========================
            // BAT FILE RECEIVER
            // =========================

            FileReceiver fileReceiver =
                    new FileReceiver(filePort);

            Thread fileReceiverThread =
                    new Thread(fileReceiver);

            fileReceiverThread.start();

            // =========================
            // KET NOI CHAT SERVER
            // =========================

            Socket socket =
                    new Socket(
                            SERVER_IP,
                            SERVER_PORT
                    );

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            /*
             * Gui thong tin dang nhap.
             *
             * Vi du:
             * Khoa|6001
             */

            writer.println(
                    username + "|" + filePort
            );

            System.out.println();
            System.out.println("Da ket noi voi Server.");

            // =========================
            // MENU
            // =========================

            System.out.println();
            System.out.println("CAC LENH:");

            System.out.println("/users");
            System.out.println(
                    "   -> Xem nguoi dang online"
            );

            System.out.println(
                    "/msg <username> <noi dung>"
            );
            System.out.println(
                    "   -> Gui tin nhan rieng"
            );

            System.out.println(
                    "/sendfile <username> <duong_dan_file>"
            );
            System.out.println(
                    "   -> Gui file P2P"
            );

            System.out.println("exit");
            System.out.println(
                    "   -> Thoat"
            );

            System.out.println(
                    "----------------------------"
            );

            // =========================
            // THREAD NHAN DU LIEU
            // =========================

            Thread receiveThread =
                    new Thread(() -> {

                try {

                    String message;

                    while ((message =
                            reader.readLine()) != null) {

                        // =========================
                        // FILE INFO
                        // =========================

                        if (message.startsWith(
                                "FILE_INFO|")) {

                            /*
                             * Format:
                             *
                             * FILE_INFO|IP|PORT|FILE_PATH
                             */

                            String[] parts =
                                    message.split(
                                            "\\|",
                                            4
                                    );

                            if (parts.length < 4) {

                                System.out.println(
                                        "[FILE] Thong tin file khong hop le."
                                );

                                continue;
                            }

                            String receiverIP =
                                    parts[1];

                            int receiverPort =
                                    Integer.parseInt(
                                            parts[2]
                                    );

                            String filePath =
                                    parts[3];

                            System.out.println();
                            System.out.println(
                                    "[FILE] Dang ket noi den Peer "
                                    + receiverIP
                                    + ":"
                                    + receiverPort
                            );

                            // Gui file bang Thread rieng
                            new Thread(() -> {

                                FileSender.sendFile(
                                        receiverIP,
                                        receiverPort,
                                        filePath
                                );

                                System.out.print("> ");

                            }).start();

                        }

                        // =========================
                        // MESSAGE THONG THUONG
                        // =========================

                        else {

                            System.out.println();
                            System.out.println(message);
                            System.out.print("> ");
                        }
                    }

                } catch (IOException e) {

                    if (!socket.isClosed()) {

                        System.out.println(
                                "Da ngat ket noi Server."
                        );
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Loi nhan du lieu: "
                            + e.getMessage()
                    );
                }
            });

            receiveThread.start();

            // =========================
            // GUI MESSAGE / COMMAND
            // =========================

            while (true) {

                System.out.print("> ");

                String message =
                        scanner.nextLine();

                writer.println(message);

                if (message.equalsIgnoreCase(
                        "exit")) {

                    break;
                }
            }

            // =========================
            // DONG KET NOI
            // =========================

            socket.close();

            System.out.println(
                    "Da thoat khoi Chat."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "File Port phai la mot so!"
            );

        } catch (IOException e) {

            System.out.println(
                    "Khong the ket noi Server."
            );

            System.out.println(
                    "Chi tiet: "
                    + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "Loi: "
                    + e.getMessage()
            );
        }
    }
}