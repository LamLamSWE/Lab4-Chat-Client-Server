package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static final int PORT = 5000;

    private static final List<ClientHandler> clients =
            new ArrayList<>();

    private static final List<FileRequest> fileRequests =
            new ArrayList<>();


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        try (ServerSocket serverSocket =
                     new ServerSocket(PORT)) {

            System.out.println("================================");
            System.out.println("       P2P CHAT SERVER");
            System.out.println("================================");
            System.out.println("Server dang chay tai port: " + PORT);
            System.out.println("Dang cho Client ket noi...");
            System.out.println("================================");


            while (true) {

                Socket socket =
                        serverSocket.accept();


                System.out.println();

                System.out.println(
                        "[SERVER] Co Client ket noi tu: "
                                + socket.getInetAddress()
                                .getHostAddress()
                );


                ClientHandler client =
                        new ClientHandler(socket);


                synchronized (clients) {

                    clients.add(client);
                }


                Thread thread =
                        new Thread(client);

                thread.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "[SERVER] Loi Server: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // CHECK USERNAME
    // =====================================================

    public static boolean isUsernameTaken(
            String username,
            ClientHandler currentClient
    ) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client == currentClient) {

                    continue;
                }


                if (client.getUsername() != null
                        &&
                        client.getUsername()
                                .equalsIgnoreCase(username)) {

                    return true;
                }
            }
        }


        return false;
    }


    // =====================================================
    // BROADCAST CHAT
    // =====================================================

    public static void broadcast(
            String message,
            ClientHandler sender
    ) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client != sender
                        &&
                        client.getUsername() != null) {

                    client.sendMessage(
                            message
                    );
                }
            }
        }
    }


    // =====================================================
    // PRIVATE MESSAGE
    // =====================================================

    public static boolean privateMessage(
            String username,
            String message,
            ClientHandler sender
    ) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getUsername() != null
                        &&
                        client.getUsername()
                                .equalsIgnoreCase(username)) {

                    client.sendMessage(
                            "[PRIVATE] "
                                    + sender.getUsername()
                                    + ": "
                                    + message
                    );

                    return true;
                }
            }
        }


        return false;
    }


    // =====================================================
    // ONLINE USERS - CONSOLE CLIENT
    // =====================================================

    public static String getOnlineUsers() {

        StringBuilder result =
                new StringBuilder();


        result.append(
                "===== ONLINE USERS =====\n"
        );


        int count = 0;


        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getUsername() != null) {

                    result.append("- ")
                            .append(
                                    client.getUsername()
                            )
                            .append("\n");

                    count++;
                }
            }
        }


        result.append(
                "Tong: "
        );

        result.append(
                count
        );

        result.append(
                " user(s)"
        );


        return result.toString();
    }


    // =====================================================
    // BROADCAST USER LIST
    //
    // USER_LIST|Khoa,TuanAnh,HaiDang
    // =====================================================

    public static void broadcastUserList() {

        StringBuilder userList =
                new StringBuilder();


        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getUsername() != null) {

                    if (userList.length() > 0) {

                        userList.append(",");
                    }


                    userList.append(
                            client.getUsername()
                    );
                }
            }


            String message =
                    "USER_LIST|"
                            + userList;


            for (ClientHandler client : clients) {

                if (client.getUsername() != null) {

                    client.sendMessage(
                            message
                    );
                }
            }
        }
    }


    // =====================================================
    // FIND CLIENT
    // =====================================================

    public static ClientHandler findClient(
            String username
    ) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client.getUsername() != null
                        &&
                        client.getUsername()
                                .equalsIgnoreCase(username)) {

                    return client;
                }
            }
        }


        return null;
    }


    // =====================================================
    // REMOVE CLIENT
    // =====================================================

    public static void removeClient(
            ClientHandler client
    ) {

        synchronized (clients) {

            clients.remove(
                    client
            );
        }
    }


    // =====================================================
    // FILE REQUEST CLASS
    // =====================================================

    public static class FileRequest {

        private final String sender;

        private final String receiver;

        private final String filePath;


        public FileRequest(
                String sender,
                String receiver,
                String filePath
        ) {

            this.sender = sender;

            this.receiver = receiver;

            this.filePath = filePath;
        }


        public String getSender() {

            return sender;
        }


        public String getReceiver() {

            return receiver;
        }


        public String getFilePath() {

            return filePath;
        }
    }


    // =====================================================
    // ADD FILE REQUEST
    // =====================================================

    public static void addFileRequest(
            String sender,
            String receiver,
            String filePath
    ) {

        synchronized (fileRequests) {

            fileRequests.removeIf(
                    request ->

                            request.getSender()
                                    .equalsIgnoreCase(sender)

                                    &&

                                    request.getReceiver()
                                            .equalsIgnoreCase(receiver)
            );


            fileRequests.add(
                    new FileRequest(
                            sender,
                            receiver,
                            filePath
                    )
            );
        }
    }


    // =====================================================
    // GET FILE REQUEST
    // =====================================================

    public static FileRequest getFileRequest(
            String sender,
            String receiver
    ) {

        synchronized (fileRequests) {

            for (FileRequest request :
                    fileRequests) {

                if (
                        request.getSender()
                                .equalsIgnoreCase(sender)

                                &&

                                request.getReceiver()
                                        .equalsIgnoreCase(receiver)
                ) {

                    return request;
                }
            }
        }


        return null;
    }


    // =====================================================
    // REMOVE FILE REQUEST
    // =====================================================

    public static void removeFileRequest(
            String sender,
            String receiver
    ) {

        synchronized (fileRequests) {

            fileRequests.removeIf(
                    request ->

                            request.getSender()
                                    .equalsIgnoreCase(sender)

                                    &&

                                    request.getReceiver()
                                            .equalsIgnoreCase(receiver)
            );
        }
    }


    // =====================================================
    // REMOVE REQUESTS OF USER
    // =====================================================

    public static void removeFileRequestsOfUser(
            String username
    ) {

        if (username == null) {

            return;
        }


        synchronized (fileRequests) {

            fileRequests.removeIf(
                    request ->

                            request.getSender()
                                    .equalsIgnoreCase(username)

                                    ||

                                    request.getReceiver()
                                            .equalsIgnoreCase(username)
            );
        }
    }
}