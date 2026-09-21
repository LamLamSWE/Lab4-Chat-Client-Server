package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private String username;

    private int filePort;

    private boolean loginSuccess = false;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ClientHandler(
            Socket socket
    ) {

        this.socket = socket;
    }


    // =====================================================
    // RUN
    // =====================================================

    @Override
    public void run() {

        try {

            reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );
            writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


            // =============================================
            // LOGIN
            // username|filePort
            // =============================================

            String loginInfo =
                    reader.readLine();


            if (loginInfo == null) {

                return;
            }


            String[] loginParts =
                    loginInfo.split(
                            "\\|",
                            2
                    );


            if (loginParts.length < 2) {

                writer.println(
                        "LOGIN_ERROR|Thong tin dang nhap khong hop le."
                );

                return;
            }


            String requestedUsername =
                    loginParts[0]
                            .trim();


            if (requestedUsername.isEmpty()) {

                writer.println(
                        "LOGIN_ERROR|Username khong duoc de trong."
                );

                return;
            }


            int requestedFilePort;


            try {

                requestedFilePort =
                        Integer.parseInt(
                                loginParts[1]
                                        .trim()
                        );

            } catch (NumberFormatException e) {

                writer.println(
                        "LOGIN_ERROR|File Port khong hop le."
                );

                return;
            }


            if (requestedFilePort < 1
                    ||
                    requestedFilePort > 65535) {

                writer.println(
                        "LOGIN_ERROR|File Port phai tu 1 den 65535."
                );

                return;
            }


            // =============================================
            // CHECK DUPLICATE USERNAME
            // =============================================

            if (
                    ChatServer.isUsernameTaken(
                            requestedUsername,
                            this
                    )
            ) {

                writer.println(
                        "LOGIN_ERROR|Username "
                                + requestedUsername
                                + " dang duoc su dung."
                );

                System.out.println(
                        "[SERVER] Tu choi username trung: "
                                + requestedUsername
                );

                return;
            }


            // =============================================
            // LOGIN SUCCESS
            // =============================================

            username =
                    requestedUsername;


            filePort =
                    requestedFilePort;


            loginSuccess =
                    true;


            writer.println(
                    "LOGIN_OK|"
                            + username
            );


            System.out.println();

            System.out.println(
                    "[SERVER] "
                            + username
                            + " da ket noi."
            );


            System.out.println(
                    "[SERVER] IP: "
                            + getClientIP()
            );


            System.out.println(
                    "[SERVER] File Port: "
                            + filePort
            );


            // =============================================
            // NOTIFY OTHER CLIENTS
            // =============================================

            ChatServer.broadcast(
                    "[SERVER] "
                            + username
                            + " da tham gia.",
                    this
            );


            ChatServer.broadcastUserList();


            // =============================================
            // RECEIVE COMMANDS
            // =============================================

            String message;


            while ((message =
                    reader.readLine()) != null) {


                // =========================================
                // EXIT
                // =========================================

                if (
                        message.equalsIgnoreCase(
                                "exit"
                        )
                ) {

                    break;
                }


                // =========================================
                // USERS
                // =========================================

                else if (
                        message.equalsIgnoreCase(
                                "/users"
                        )
                ) {

                    writer.println(
                            ChatServer.getOnlineUsers()
                    );
                }


                // =========================================
                // PRIVATE MESSAGE
                // =========================================

                else if (
                        message.startsWith(
                                "/msg "
                        )
                ) {

                    handlePrivateMessage(
                            message
                    );
                }


                // =========================================
                // SEND FILE
                // =========================================

                else if (
                        message.startsWith(
                                "/sendfile "
                        )
                ) {

                    handleSendFile(
                            message
                    );
                }


                // =========================================
                // ACCEPT FILE
                // =========================================

                else if (
                        message.startsWith(
                                "/accept "
                        )
                ) {

                    handleAcceptFile(
                            message
                    );
                }


                // =========================================
                // REJECT FILE
                // =========================================

                else if (
                        message.startsWith(
                                "/reject "
                        )
                ) {

                    handleRejectFile(
                            message
                    );
                }


                // =========================================
                // PUBLIC MESSAGE
                // =========================================

                else {

                    System.out.println(
                            "[CHAT] "
                                    + username
                                    + ": "
                                    + message
                    );


                    ChatServer.broadcast(
                            username
                                    + ": "
                                    + message,
                            this
                    );
                }
            }

        } catch (IOException e) {

            if (loginSuccess) {

                System.out.println(
                        "[SERVER] "
                                + username
                                + " mat ket noi."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "[SERVER] Loi ClientHandler: "
                            + e.getMessage()
            );

        } finally {

            // Client da duoc add vao clients
            // ngay luc accept socket,
            // nen luon remove khi ket thuc.

            ChatServer.removeClient(
                    this
            );


            if (loginSuccess
                    &&
                    username != null) {

                ChatServer.removeFileRequestsOfUser(
                        username
                );


                System.out.println(
                        "[SERVER] "
                                + username
                                + " da ngat ket noi."
                );


                ChatServer.broadcast(
                        "[SERVER] "
                                + username
                                + " da roi phong.",
                        this
                );


                ChatServer.broadcastUserList();
            }


            try {

                socket.close();

            } catch (IOException e) {

                // Khong can xu ly
            }
        }
    }


    // =====================================================
    // PRIVATE MESSAGE
    // =====================================================

    private void handlePrivateMessage(
            String command
    ) {

        String[] parts =
                command.split(
                        " ",
                        3
                );


        if (parts.length < 3) {

            writer.println(
                    "[SERVER] Sai cu phap!"
            );

            return;
        }


        String receiver =
                parts[1];


        String content =
                parts[2];


        boolean success =
                ChatServer.privateMessage(
                        receiver,
                        content,
                        this
                );


        if (success) {

            writer.println(
                    "[PRIVATE -> "
                            + receiver
                            + "] "
                            + content
            );

        } else {

            writer.println(
                    "[SERVER] Khong tim thay user: "
                            + receiver
            );
        }
    }


    // =====================================================
    // SEND FILE REQUEST
    // =====================================================

    private void handleSendFile(
            String command
    ) {

        String[] parts =
                command.split(
                        " ",
                        3
                );


        if (parts.length < 3) {

            writer.println(
                    "[SERVER] Sai cu phap gui file!"
            );

            return;
        }


        String receiverName =
                parts[1];


        String filePath =
                parts[2];


        ClientHandler receiver =
                ChatServer.findClient(
                        receiverName
                );


        if (receiver == null) {

            writer.println(
                    "[SERVER] Khong tim thay user: "
                            + receiverName
            );

            return;
        }


        if (receiver == this) {

            writer.println(
                    "[SERVER] Ban khong the gui file cho chinh minh."
            );

            return;
        }


        ChatServer.addFileRequest(
                username,
                receiverName,
                filePath
        );


        File file =
                new File(
                        filePath
                );


        String fileName =
                file.getName();


        // =============================================
        // FILE REQUEST PROTOCOL
        // =============================================

        receiver.sendMessage(
                "FILE_REQUEST|"
                        + username
                        + "|"
                        + fileName
        );


        writer.println(
                "[FILE] Da gui yeu cau gui file "
                        + fileName
                        + " den "
                        + receiverName
        );


        System.out.println(
                "[FILE] "
                        + username
                        + " yeu cau gui "
                        + fileName
                        + " cho "
                        + receiverName
        );
    }


    // =====================================================
    // ACCEPT FILE
    // =====================================================

    private void handleAcceptFile(
            String command
    ) {

        String[] parts =
                command.split(
                        " ",
                        2
                );


        if (parts.length < 2) {

            writer.println(
                    "[SERVER] Sai cu phap Accept."
            );

            return;
        }


        String senderName =
                parts[1]
                        .trim();


        ChatServer.FileRequest request =
                ChatServer.getFileRequest(
                        senderName,
                        username
                );


        if (request == null) {

            writer.println(
                    "[SERVER] Khong co yeu cau gui file tu "
                            + senderName
            );

            return;
        }


        ClientHandler sender =
                ChatServer.findClient(
                        senderName
                );


        if (sender == null) {

            writer.println(
                    "[SERVER] Nguoi gui da offline."
            );


            ChatServer.removeFileRequest(
                    senderName,
                    username
            );


            return;
        }


        // =============================================
        // SEND PEER INFORMATION TO SENDER
        //
        // FILE_INFO|IP|PORT|PATH
        // =============================================

        sender.sendMessage(
                "FILE_INFO|"
                        + getClientIP()
                        + "|"
                        + getFilePort()
                        + "|"
                        + request.getFilePath()
        );


        writer.println(
                "[FILE] Da chap nhan file tu "
                        + senderName
        );


        sender.sendMessage(
                "[FILE] "
                        + username
                        + " da chap nhan file."
        );


        ChatServer.removeFileRequest(
                senderName,
                username
        );


        System.out.println(
                "[FILE] "
                        + username
                        + " ACCEPT file tu "
                        + senderName
        );
    }


    // =====================================================
    // REJECT FILE
    // =====================================================

    private void handleRejectFile(
            String command
    ) {

        String[] parts =
                command.split(
                        " ",
                        2
                );


        if (parts.length < 2) {

            writer.println(
                    "[SERVER] Sai cu phap Reject."
            );

            return;
        }


        String senderName =
                parts[1]
                        .trim();


        ChatServer.FileRequest request =
                ChatServer.getFileRequest(
                        senderName,
                        username
                );


        if (request == null) {

            writer.println(
                    "[SERVER] Khong co yeu cau gui file tu "
                            + senderName
            );

            return;
        }


        ClientHandler sender =
                ChatServer.findClient(
                        senderName
                );


        if (sender != null) {

            sender.sendMessage(
                    "[FILE] "
                            + username
                            + " da tu choi nhan file."
            );
        }


        writer.println(
                "[FILE] Da tu choi file tu "
                        + senderName
        );


        ChatServer.removeFileRequest(
                senderName,
                username
        );


        System.out.println(
                "[FILE] "
                        + username
                        + " REJECT file tu "
                        + senderName
        );
    }


    // =====================================================
    // SEND MESSAGE
    // =====================================================

    public void sendMessage(
            String message
    ) {

        if (writer != null) {

            writer.println(
                    message
            );
        }
    }


    // =====================================================
    // GETTERS
    // =====================================================

    public String getUsername() {

        return username;
    }


    public int getFilePort() {

        return filePort;
    }


    public String getClientIP() {

        return socket
                .getInetAddress()
                .getHostAddress();
    }
}