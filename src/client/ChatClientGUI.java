package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClientGUI extends JFrame {

    // =========================================================
    // NETWORK CONFIG
    // =========================================================

    private static final int SERVER_PORT = 5000;

    private String serverIP = "10.60.74.225";

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private String username;
    private int filePort;

    // =========================================================
    // CHAT STATE
    // =========================================================

    private boolean privateMode = false;
    private String privateUser = null;

    // =========================================================
    // COMPONENTS
    // =========================================================

    private JTextArea chatArea;
    private JTextField messageField;

    private DefaultListModel<String> userListModel;
    private JList<String> userList;

    private JButton sendButton;
    private JButton sendFileButton;
    private JButton publicChatButton;

    private JLabel statusLabel;
    private JLabel chatModeLabel;
    private JLabel chatDescriptionLabel;
    private JLabel onlineCountLabel;

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color APP_BACKGROUND =
            new Color(241, 245, 249);

    private static final Color CARD_BACKGROUND =
            Color.WHITE;

    private static final Color SIDEBAR =
            new Color(15, 23, 42);

    private static final Color SIDEBAR_HOVER =
            new Color(30, 41, 59);

    private static final Color PRIMARY =
            new Color(79, 70, 229);

    private static final Color PRIMARY_DARK =
            new Color(67, 56, 202);

    private static final Color PRIMARY_LIGHT =
            new Color(238, 242, 255);

    private static final Color TEXT_PRIMARY =
            new Color(15, 23, 42);

    private static final Color TEXT_SECONDARY =
            new Color(100, 116, 139);

    private static final Color BORDER =
            new Color(226, 232, 240);

    private static final Color INPUT_BACKGROUND =
            new Color(248, 250, 252);

    private static final Color ONLINE_GREEN =
            new Color(34, 197, 94);

    private static final Color ERROR_RED =
            new Color(239, 68, 68);

    private static final Color INFO_BLUE =
            new Color(37, 99, 235);

    // =========================================================
    // FONTS
    // =========================================================

    private static final Font FONT_NORMAL =
            new Font("Segoe UI", Font.PLAIN, 14);

    private static final Font FONT_MEDIUM =
            new Font("Segoe UI", Font.BOLD, 14);

    private static final Font FONT_SMALL =
            new Font("Segoe UI", Font.PLAIN, 12);

    private static final Font FONT_TITLE =
            new Font("Segoe UI", Font.BOLD, 22);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ChatClientGUI() {

        setupLookAndFeel();

        showLoginDialog();
    }

    // =========================================================
    // LOOK AND FEEL
    // =========================================================

    private void setupLookAndFeel() {

        try {

            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception ignored) {
        }

        UIManager.put(
                "OptionPane.messageFont",
                FONT_NORMAL
        );

        UIManager.put(
                "OptionPane.buttonFont",
                FONT_NORMAL
        );

        UIManager.put(
                "FileChooser.font",
                FONT_NORMAL
        );
    }

    // =========================================================
    // LOGIN WINDOW
    // =========================================================

    private void showLoginDialog() {

    JDialog dialog =
            new JDialog(
                    (Frame) null,
                    "P2P Chat - Connect",
                    true
            );

    dialog.setDefaultCloseOperation(
            JDialog.DISPOSE_ON_CLOSE
    );

    dialog.setSize(
            520,
            650
    );

    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);

    // =====================================================
    // ROOT
    // =====================================================

    JPanel root =
            new JPanel(
                    new GridBagLayout()
            );

    root.setBackground(
            APP_BACKGROUND
    );

    // =====================================================
    // MAIN CARD
    // =====================================================

    JPanel card =
            new JPanel();

    card.setLayout(
            new BoxLayout(
                    card,
                    BoxLayout.Y_AXIS
            )
    );

    card.setBackground(
            CARD_BACKGROUND
    );

    card.setPreferredSize(
            new Dimension(
                    420,
                    550
            )
    );

    card.setBorder(
            new CompoundBorder(
                    new LineBorder(
                            BORDER,
                            1,
                            true
                    ),
                    new EmptyBorder(
                            30,
                            35,
                            28,
                            35
                    )
            )
    );

    // =====================================================
    // LOGO
    // =====================================================

    JLabel logo =
            new JLabel(
                    "P2P"
            );

    logo.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    38
            )
    );

    logo.setForeground(
            PRIMARY
    );

    logo.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    // =====================================================
    // TITLE
    // =====================================================

    JLabel title =
            new JLabel(
                    "P2P Chat"
            );

    title.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    25
            )
    );

    title.setForeground(
            TEXT_PRIMARY
    );

    title.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    // =====================================================
    // SUBTITLE
    // =====================================================

    JLabel subtitle =
            new JLabel(
                    "Client-Server + Peer-to-Peer"
            );

    subtitle.setFont(
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    14
            )
    );

    subtitle.setForeground(
            TEXT_SECONDARY
    );

    subtitle.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    card.add(logo);

    card.add(
            Box.createVerticalStrut(5)
    );

    card.add(title);

    card.add(
            Box.createVerticalStrut(5)
    );

    card.add(subtitle);

    card.add(
            Box.createVerticalStrut(30)
    );

    // =====================================================
    // FORM CONTAINER
    // =====================================================

    JPanel form =
            new JPanel();

    form.setLayout(
            new BoxLayout(
                    form,
                    BoxLayout.Y_AXIS
            )
    );

    form.setOpaque(false);

    form.setMaximumSize(
            new Dimension(
                    310,
                    300
            )
    );

    form.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    // =====================================================
    // SERVER IP
    // =====================================================

    JLabel serverLabel =
            createCenteredFormLabel(
                    "Server IP"
            );

    JTextField serverField =
            createCenteredInputField(
                    "172.20.10.4"
            );

    form.add(serverLabel);

    form.add(
            Box.createVerticalStrut(7)
    );

    form.add(serverField);

    form.add(
            Box.createVerticalStrut(20)
    );

    // =====================================================
    // USERNAME
    // =====================================================

    JLabel usernameLabel =
            createCenteredFormLabel(
                    "Username"
            );

    JTextField usernameField =
            createCenteredInputField(
                    ""
            );

    form.add(usernameLabel);

    form.add(
            Box.createVerticalStrut(7)
    );

    form.add(usernameField);

    form.add(
            Box.createVerticalStrut(20)
    );

    // =====================================================
    // FILE PORT
    // =====================================================

    JLabel portLabel =
            createCenteredFormLabel(
                    "File Port"
            );

    JTextField portField =
            createCenteredInputField(
            		"6001"
            );

    form.add(portLabel);

    form.add(
            Box.createVerticalStrut(7)
    );

    form.add(portField);

    card.add(form);

    card.add(
            Box.createVerticalStrut(28)
    );

    // =====================================================
    // CONNECT BUTTON
    // =====================================================

    JButton connectButton =
            createPrimaryButton(
                    "CONNECT"
            );

    connectButton.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    connectButton.setPreferredSize(new Dimension(310,48));

    connectButton.setMaximumSize(
            new Dimension(
                    310,
                    48
            )
    );

    connectButton.setMinimumSize(
            new Dimension(
                    310,
                    48
            )
    );

    card.add(connectButton);

    card.add(
            Box.createVerticalStrut(20)
    );

    // =====================================================
    // NOTE
    // =====================================================

    JLabel note =
            new JLabel(
                    "<html>"
                            + "<div style='text-align:center;'>"
                            + "All clients connect to the same Server IP.<br>"
                            + "Each client uses a different File Port."
                            + "</div>"
                            + "</html>"
            );

    note.setFont(
            FONT_SMALL
    );

    note.setForeground(
            TEXT_SECONDARY
    );

    note.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    card.add(note);

    // =====================================================
    // ADD CARD TO CENTER
    // =====================================================

    root.add(card);

    dialog.setContentPane(root);

    // =====================================================
    // CONNECT ACTION
    // =====================================================

    Runnable connectAction =
            () -> {

                String inputServerIP =
                        serverField
                                .getText()
                                .trim();

                String inputUsername =
                        usernameField
                                .getText()
                                .trim();

                String inputPort =
                        portField
                                .getText()
                                .trim();

                if (
                        inputServerIP.isEmpty()
                ) {

                    showError(
                            dialog,
                            "Server IP cannot be empty."
                    );

                    return;
                }

                if (
                        inputUsername.isEmpty()
                ) {

                    showError(
                            dialog,
                            "Username cannot be empty."
                    );

                    return;
                }

                int parsedPort;

                try {

                    parsedPort =
                            Integer.parseInt(
                                    inputPort
                            );

                    if (
                            parsedPort < 1
                                    ||
                            parsedPort > 65535
                    ) {

                        throw new NumberFormatException();
                    }

                } catch (
                        NumberFormatException e
                ) {

                    showError(
                            dialog,
                            "File Port must be from 1 to 65535."
                    );

                    return;
                }

                serverIP =
                        inputServerIP;

                username =
                        inputUsername;

                filePort =
                        parsedPort;

                dialog.dispose();

                createGUI();

                connectToServer();
            };

    connectButton.addActionListener(
            e -> connectAction.run()
    );

    serverField.addActionListener(
            e -> usernameField.requestFocus()
    );

    usernameField.addActionListener(
            e -> portField.requestFocus()
    );

    portField.addActionListener(
            e -> connectAction.run()
    );

    // Focus username when opened
    SwingUtilities.invokeLater(
            usernameField::requestFocusInWindow
    );

    dialog.setVisible(true);

    if (
            username == null
    ) {

        System.exit(0);
    }
}

    // =========================================================
    // MAIN WINDOW
    // =========================================================

    private void createGUI() {

        setTitle(
                "P2P Chat - "
                        + username
        );

        setSize(
                1180,
                720
        );

        setMinimumSize(
                new Dimension(
                        950,
                        600
                )
        );

        setLocationRelativeTo(
                null
        );

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        getContentPane()
                .setBackground(
                        APP_BACKGROUND
                );

        setLayout(
                new BorderLayout()
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        add(
                createSidebar(),
                BorderLayout.WEST
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                0
                        )
                );

        content.setBackground(
                APP_BACKGROUND
        );

        content.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                CARD_BACKGROUND
        );

        card.setBorder(
                new LineBorder(
                        BORDER,
                        1,
                        true
                )
        );

        card.add(
                createHeader(),
                BorderLayout.NORTH
        );

        card.add(
                createChatArea(),
                BorderLayout.CENTER
        );

        card.add(
                createMessageBar(),
                BorderLayout.SOUTH
        );

        content.add(
                card,
                BorderLayout.CENTER
        );

        add(
                content,
                BorderLayout.CENTER
        );

        // =====================================================
        // EVENTS
        // =====================================================

        sendButton.addActionListener(
                e -> sendMessage()
        );

        messageField.addActionListener(
                e -> sendMessage()
        );

        sendFileButton.addActionListener(
                e -> chooseAndSendFile()
        );

        publicChatButton.addActionListener(
                e -> switchToPublicChat()
        );

        userList.addListSelectionListener(
                e -> {

                    if (
                            !e.getValueIsAdjusting()
                    ) {

                        String selected =
                                userList
                                        .getSelectedValue();

                        if (
                                selected != null
                        ) {

                            switchToPrivateChat(
                                    selected
                            );
                        }
                    }
                }
        );

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e
                    ) {

                        disconnect();
                    }
                }
        );

        setVisible(true);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setBackground(
                SIDEBAR
        );

        sidebar.setPreferredSize(
                new Dimension(
                        245,
                        0
                )
        );

        sidebar.setBorder(
                new EmptyBorder(
                        26,
                        18,
                        20,
                        18
                )
        );

        // =====================================================
        // TOP
        // =====================================================

        JPanel top =
                new JPanel();

        top.setLayout(
                new BoxLayout(
                        top,
                        BoxLayout.Y_AXIS
                )
        );

        top.setOpaque(false);

        JLabel appTitle =
                new JLabel(
                        "P2P CHAT"
                );

        appTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        appTitle.setForeground(
                Color.WHITE
        );

        appTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel signedIn =
                new JLabel(
                        "Signed in as "
                                + username
                );

        signedIn.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        signedIn.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        signedIn.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        top.add(appTitle);

        top.add(
                Box.createVerticalStrut(5)
        );

        top.add(signedIn);

        top.add(
                Box.createVerticalStrut(30)
        );

        // =====================================================
        // ONLINE HEADER
        // =====================================================

        JPanel onlineHeader =
                new JPanel(
                        new BorderLayout()
                );

        onlineHeader.setOpaque(false);

        onlineHeader.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        30
                )
        );

        onlineHeader.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel onlineTitle =
                new JLabel(
                        "ONLINE USERS"
                );

        onlineTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        onlineTitle.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        onlineCountLabel =
                new JLabel(
                        "0"
                );

        onlineCountLabel.setFont(
                FONT_MEDIUM
        );

        onlineCountLabel.setForeground(
                ONLINE_GREEN
        );

        onlineHeader.add(
                onlineTitle,
                BorderLayout.WEST
        );

        onlineHeader.add(
                onlineCountLabel,
                BorderLayout.EAST
        );

        top.add(
                onlineHeader
        );

        sidebar.add(
                top,
                BorderLayout.NORTH
        );

        // =====================================================
        // USER LIST
        // =====================================================

        userListModel =
                new DefaultListModel<>();

        userList =
                new JList<>(
                        userListModel
                );

        userList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        userList.setFont(
                FONT_NORMAL
        );

        userList.setFixedCellHeight(
                48
        );

        userList.setBackground(
                SIDEBAR
        );

        userList.setForeground(
                Color.WHITE
        );

        userList.setSelectionBackground(
                PRIMARY
        );

        userList.setSelectionForeground(
                Color.WHITE
        );

        userList.setBorder(
                new EmptyBorder(
                        12,
                        0,
                        12,
                        0
                )
        );

        userList.setCellRenderer(
                new OnlineUserRenderer()
        );

        JScrollPane userScroll =
                new JScrollPane(
                        userList
                );

        userScroll.setBorder(
                null
        );

        userScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        userScroll.getViewport()
                .setBackground(
                        SIDEBAR
                );

        sidebar.add(
                userScroll,
                BorderLayout.CENTER
        );

        // =====================================================
        // BOTTOM
        // =====================================================

        JPanel bottom =
                new JPanel();

        bottom.setLayout(
                new BoxLayout(
                        bottom,
                        BoxLayout.Y_AXIS
                )
        );

        bottom.setOpaque(
                false
        );

        publicChatButton =
                createSidebarButton(
                        "Public Chat"
                );

        publicChatButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        bottom.add(
                publicChatButton
        );

        bottom.add(
                Box.createVerticalStrut(20)
        );

        JSeparator separator =
                new JSeparator();

        separator.setForeground(
                new Color(
                        51,
                        65,
                        85
                )
        );

        separator.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        bottom.add(
                separator
        );

        bottom.add(
                Box.createVerticalStrut(16)
        );

        JLabel serverTitle =
                new JLabel(
                        "SERVER"
                );

        serverTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        serverTitle.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        serverTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel serverValue =
                new JLabel(
                        serverIP
                                + ":"
                                + SERVER_PORT
                );

        serverValue.setFont(
                FONT_SMALL
        );

        serverValue.setForeground(
                Color.WHITE
        );

        serverValue.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel portValue =
                new JLabel(
                        "File Port: "
                                + filePort
                );

        portValue.setFont(
                FONT_SMALL
        );

        portValue.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        portValue.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        bottom.add(
                serverTitle
        );

        bottom.add(
                Box.createVerticalStrut(5)
        );

        bottom.add(
                serverValue
        );

        bottom.add(
                Box.createVerticalStrut(4)
        );

        bottom.add(
                portValue
        );

        sidebar.add(
                bottom,
                BorderLayout.SOUTH
        );

        return sidebar;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(
                CARD_BACKGROUND
        );

        header.setBorder(
                new CompoundBorder(
                        new MatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDER
                        ),
                        new EmptyBorder(
                                18,
                                24,
                                18,
                                24
                        )
                )
        );

        // =====================================================
        // LEFT
        // =====================================================

        JPanel left =
                new JPanel();

        left.setLayout(
                new BoxLayout(
                        left,
                        BoxLayout.Y_AXIS
                )
        );

        left.setOpaque(false);

        chatModeLabel =
                new JLabel(
                        "Public Chat"
                );

        chatModeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        19
                )
        );

        chatModeLabel.setForeground(
                TEXT_PRIMARY
        );

        chatModeLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        chatDescriptionLabel =
                new JLabel(
                        "Chat with everyone currently online"
                );

        chatDescriptionLabel.setFont(
                FONT_SMALL
        );

        chatDescriptionLabel.setForeground(
                TEXT_SECONDARY
        );

        chatDescriptionLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        left.add(
                chatModeLabel
        );

        left.add(
                Box.createVerticalStrut(4)
        );

        left.add(
                chatDescriptionLabel
        );

        // =====================================================
        // STATUS
        // =====================================================

        statusLabel =
                new JLabel(
                        "CONNECTING"
                );

        statusLabel.setFont(
                FONT_MEDIUM
        );

        statusLabel.setForeground(
                TEXT_SECONDARY
        );

        statusLabel.setBorder(
                new EmptyBorder(
                        7,
                        12,
                        7,
                        12
                )
        );

        header.add(
                left,
                BorderLayout.WEST
        );

        header.add(
                statusLabel,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // CHAT AREA
    // =========================================================

    private JPanel createChatArea() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                CARD_BACKGROUND
        );

        // =====================================================
        // WELCOME
        // =====================================================

        JPanel welcome =
                new JPanel();

        welcome.setLayout(
                new BoxLayout(
                        welcome,
                        BoxLayout.Y_AXIS
                )
        );

        welcome.setBackground(
                CARD_BACKGROUND
        );

        welcome.setBorder(
                new EmptyBorder(
                        20,
                        24,
                        8,
                        24
                )
        );

        JLabel welcomeTitle =
                new JLabel(
                        "Welcome, "
                                + username
                                + "!"
                );

        welcomeTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        welcomeTitle.setForeground(
                TEXT_PRIMARY
        );

        welcomeTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel welcomeHint =
                new JLabel(
                        "Select an online user for private chat, or stay here for public chat."
                );

        welcomeHint.setFont(
                FONT_SMALL
        );

        welcomeHint.setForeground(
                TEXT_SECONDARY
        );

        welcomeHint.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        welcome.add(
                welcomeTitle
        );

        welcome.add(
                Box.createVerticalStrut(4)
        );

        welcome.add(
                welcomeHint
        );

        panel.add(
                welcome,
                BorderLayout.NORTH
        );

        // =====================================================
        // CHAT LOG
        // =====================================================

        chatArea =
                new JTextArea();

        chatArea.setEditable(
                false
        );

        chatArea.setLineWrap(
                true
        );

        chatArea.setWrapStyleWord(
                true
        );

        chatArea.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        chatArea.setForeground(
                TEXT_PRIMARY
        );

        chatArea.setBackground(
                CARD_BACKGROUND
        );

        chatArea.setMargin(
                new Insets(
                        14,
                        24,
                        20,
                        24
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        chatArea
                );

        scroll.setBorder(
                null
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // MESSAGE BAR
    // =========================================================

    private JPanel createMessageBar() {

        JPanel outer =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        outer.setBackground(
                CARD_BACKGROUND
        );

        outer.setBorder(
                new CompoundBorder(
                        new MatteBorder(
                                1,
                                0,
                                0,
                                0,
                                BORDER
                        ),
                        new EmptyBorder(
                                15,
                                18,
                                15,
                                18
                        )
                )
        );

        messageField =
                new JTextField();

        messageField.setFont(
                FONT_NORMAL
        );

        messageField.setForeground(
                TEXT_PRIMARY
        );

        messageField.setBackground(
                INPUT_BACKGROUND
        );

        messageField.setToolTipText(
                "Type a message and press Enter"
        );

        messageField.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                10,
                                14,
                                10,
                                14
                        )
                )
        );

        sendFileButton =
                createSecondaryButton(
                        "Send File"
                );

        sendButton =
                createPrimaryButton(
                        "Send"
                );

        sendFileButton.setPreferredSize(
                new Dimension(
                        115,
                        44
                )
        );

        sendButton.setPreferredSize(
                new Dimension(
                        95,
                        44
                )
        );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        actions.setOpaque(
                false
        );

        actions.add(
                sendFileButton
        );

        actions.add(
                sendButton
        );

        outer.add(
                messageField,
                BorderLayout.CENTER
        );

        outer.add(
                actions,
                BorderLayout.EAST
        );

        return outer;
    }

    // =========================================================
    // CONNECT TO SERVER
    // =========================================================

    private void connectToServer() {

        Thread connectionThread =
                new Thread(
                        () -> {

                            try {

                                socket =
                                        new Socket(
                                                serverIP,
                                                SERVER_PORT
                                        );

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

                                // Send login information
                                writer.println(
                                        username
                                                + "|"
                                                + filePort
                                );

                                String loginResponse =
                                        reader.readLine();

                                if (
                                        loginResponse == null
                                ) {

                                    showLoginError(
                                            "Server closed the connection."
                                    );

                                    return;
                                }

                                // =================================================
                                // LOGIN ERROR
                                // =================================================

                                if (
                                        loginResponse.startsWith(
                                                "LOGIN_ERROR|"
                                        )
                                ) {

                                    String error =
                                            loginResponse.substring(
                                                    "LOGIN_ERROR|".length()
                                            );

                                    showLoginError(
                                            error
                                    );

                                    return;
                                }

                                // =================================================
                                // INVALID RESPONSE
                                // =================================================

                                if (
                                        !loginResponse.startsWith(
                                                "LOGIN_OK|"
                                        )
                                ) {

                                    showLoginError(
                                            "Invalid login response from server."
                                    );

                                    return;
                                }

                                // =================================================
                                // SUCCESS
                                // =================================================

                                SwingUtilities.invokeLater(
                                        () -> {

                                            statusLabel.setText(
                                                    "ONLINE"
                                            );

                                            statusLabel.setForeground(
                                                    ONLINE_GREEN
                                            );
                                        }
                                );

                                appendChat(
                                        "SYSTEM  Connection established."
                                );

                                appendChat(
                                        "SYSTEM  Server: "
                                                + serverIP
                                                + ":"
                                                + SERVER_PORT
                                );

                                appendChat(
                                        "SYSTEM  File receiver port: "
                                                + filePort
                                );

                                appendChat(
                                        ""
                                );

                                // =================================================
                                // START FILE RECEIVER
                                // =================================================

                                FileReceiver fileReceiver =
                                        new FileReceiver(
                                                filePort
                                        );

                                Thread fileReceiverThread =
                                        new Thread(
                                                fileReceiver
                                        );

                                fileReceiverThread.setDaemon(
                                        true
                                );

                                fileReceiverThread.start();

                                // =================================================
                                // RECEIVE MESSAGES
                                // =================================================

                                receiveMessages();

                            } catch (
                                    IOException e
                            ) {

                                showLoginError(
                                        "Cannot connect to Server.\n"
                                                + e.getMessage()
                                );
                            }
                        }
                );

        connectionThread.setDaemon(
                true
        );

        connectionThread.start();
    }

    // =========================================================
    // LOGIN ERROR
    // =========================================================

    private void showLoginError(
            String message
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    if (
                            statusLabel != null
                    ) {

                        statusLabel.setText(
                                "OFFLINE"
                        );

                        statusLabel.setForeground(
                                ERROR_RED
                        );
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            message,
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    try {

                        if (
                                socket != null
                                        &&
                                !socket.isClosed()
                        ) {

                            socket.close();
                        }

                    } catch (
                            IOException ignored
                    ) {
                    }

                    dispose();

                    new ChatClientGUI();
                }
        );
    }

    // =========================================================
    // RECEIVE MESSAGES
    // =========================================================

    private void receiveMessages() {

        try {

            String message;

            while (
                    (
                            message =
                                    reader.readLine()
                    ) != null
            ) {

                // =================================================
                // USER LIST
                // =================================================

                if (
                        message.startsWith(
                                "USER_LIST|"
                        )
                ) {

                    updateUserList(
                            message
                    );
                }

                // =================================================
                // FILE REQUEST
                // =================================================

                else if (
                        message.startsWith(
                                "FILE_REQUEST|"
                        )
                ) {

                    handleFileRequest(
                            message
                    );
                }

                // =================================================
                // FILE INFO
                // =================================================

                else if (
                        message.startsWith(
                                "FILE_INFO|"
                        )
                ) {

                    handleFileInfo(
                            message
                    );
                }

                // =================================================
                // NORMAL MESSAGE
                // =================================================

                else {

                    appendChat(
                            formatIncomingMessage(
                                    message
                            )
                    );
                }
            }

        } catch (
                IOException e
        ) {

            if (
                    socket != null
                            &&
                    !socket.isClosed()
            ) {

                appendChat(
                        "SYSTEM  Lost connection to Server."
                );

                SwingUtilities.invokeLater(
                        () -> {

                            statusLabel.setText(
                                    "OFFLINE"
                            );

                            statusLabel.setForeground(
                                    ERROR_RED
                            );
                        }
                );
            }
        }
    }

    // =========================================================
    // FORMAT INCOMING MESSAGE
    // =========================================================

    private String formatIncomingMessage(
            String message
    ) {

        if (
                message.startsWith(
                        "[SERVER]"
                )
        ) {

            return "SYSTEM  "
                    + message.substring(
                            "[SERVER]".length()
                    ).trim();
        }

        if (
                message.startsWith(
                        "[PRIVATE]"
                )
        ) {

            return "PRIVATE  "
                    + message.substring(
                            "[PRIVATE]".length()
                    ).trim();
        }

        if (
                message.startsWith(
                        "[PRIVATE ->"
                )
        ) {

            return message;
        }

        if (
                message.startsWith(
                        "[FILE]"
                )
        ) {

            return "FILE  "
                    + message.substring(
                            "[FILE]".length()
                    ).trim();
        }

        return message;
    }

    // =========================================================
    // UPDATE USER LIST
    // =========================================================

    private void updateUserList(
            String message
    ) {

        String data =
                message.substring(
                        "USER_LIST|".length()
                );

        SwingUtilities.invokeLater(
                () -> {

                    String oldPrivateUser =
                            privateUser;

                    userListModel.clear();

                    if (
                            !data.trim().isEmpty()
                    ) {

                        String[] users =
                                data.split(",");

                        for (
                                String user :
                                users
                        ) {

                            String cleanUser =
                                    user.trim();

                            if (
                                    !cleanUser.isEmpty()
                                            &&
                                    !cleanUser
                                            .equalsIgnoreCase(
                                                    username
                                            )
                            ) {

                                userListModel.addElement(
                                        cleanUser
                                );
                            }
                        }
                    }

                    // Count other online users
                    onlineCountLabel.setText(
                            String.valueOf(
                                    userListModel.size()
                            )
                    );

                    // =================================================
                    // RESTORE PRIVATE USER
                    // =================================================

                    if (
                            privateMode
                                    &&
                            oldPrivateUser != null
                    ) {

                        boolean found =
                                false;

                        for (
                                int i = 0;
                                i < userListModel.size();
                                i++
                        ) {

                            if (
                                    userListModel
                                            .getElementAt(i)
                                            .equalsIgnoreCase(
                                                    oldPrivateUser
                                            )
                            ) {

                                userList.setSelectedIndex(
                                        i
                                );

                                found =
                                        true;

                                break;
                            }
                        }

                        if (
                                !found
                        ) {

                            appendChat(
                                    "SYSTEM  "
                                            + oldPrivateUser
                                            + " is now offline."
                            );

                            switchToPublicChat();
                        }
                    }
                }
        );
    }

    // =========================================================
    // SWITCH TO PRIVATE
    // =========================================================

    private void switchToPrivateChat(
            String selectedUser
    ) {

        privateMode =
                true;

        privateUser =
                selectedUser;

        chatModeLabel.setText(
                selectedUser
        );

        chatDescriptionLabel.setText(
                "Private conversation with "
                        + selectedUser
        );

        sendFileButton.setEnabled(
                true
        );

        messageField.requestFocus();
    }

    // =========================================================
    // SWITCH TO PUBLIC
    // =========================================================

    private void switchToPublicChat() {

        privateMode =
                false;

        privateUser =
                null;

        userList.clearSelection();

        chatModeLabel.setText(
                "Public Chat"
        );

        chatDescriptionLabel.setText(
                "Chat with everyone currently online"
        );

        messageField.requestFocus();
    }

    // =========================================================
    // SEND MESSAGE
    // =========================================================

    private void sendMessage() {

        if (
                writer == null
        ) {

            return;
        }

        String message =
                messageField
                        .getText()
                        .trim();

        if (
                message.isEmpty()
        ) {

            return;
        }

        // =====================================================
        // PRIVATE MESSAGE
        // =====================================================

        if (
                privateMode
                        &&
                privateUser != null
        ) {

            writer.println(
                    "/msg "
                            + privateUser
                            + " "
                            + message
            );
        }

        // =====================================================
        // PUBLIC MESSAGE
        // =====================================================

        else {

            writer.println(
                    message
            );

            appendChat(
                    "You:  "
                            + message
            );
        }

        messageField.setText(
                ""
        );

        messageField.requestFocus();
    }

    // =========================================================
    // CHOOSE AND SEND FILE
    // =========================================================

    private void chooseAndSendFile() {

        if (
                writer == null
        ) {

            return;
        }

        if (
                !privateMode
                        ||
                privateUser == null
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select an online user before sending a file.",
                    "Select User",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Send file to "
                        + privateUser
        );

        int result =
                fileChooser.showOpenDialog(
                        this
                );

        if (
                result !=
                        JFileChooser.APPROVE_OPTION
        ) {

            return;
        }

        File file =
                fileChooser
                        .getSelectedFile();

        if (
                !file.exists()
                        ||
                !file.isFile()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid file.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        writer.println(
                "/sendfile "
                        + privateUser
                        + " "
                        + file.getAbsolutePath()
        );

        appendChat(
                "FILE  Request sent to "
                        + privateUser
                        + ": "
                        + file.getName()
        );
    }

    // =========================================================
    // HANDLE FILE REQUEST
    // =========================================================

    private void handleFileRequest(
            String message
    ) {

        String[] parts =
                message.split(
                        "\\|",
                        3
                );

        if (
                parts.length < 3
        ) {

            appendChat(
                    "FILE  Invalid incoming file request."
            );

            return;
        }

        String sender =
                parts[1];

        String fileName =
                parts[2];

        appendChat(
                "FILE  Incoming file from "
                        + sender
                        + ": "
                        + fileName
        );

        SwingUtilities.invokeLater(
                () -> {

                    JPanel content =
                            new JPanel();

                    content.setLayout(
                            new BoxLayout(
                                    content,
                                    BoxLayout.Y_AXIS
                            )
                    );

                    content.setBorder(
                            new EmptyBorder(
                                    10,
                                    10,
                                    10,
                                    10
                            )
                    );

                    JLabel title =
                            new JLabel(
                                    "Incoming File"
                            );

                    title.setFont(
                            new Font(
                                    "Segoe UI",
                                    Font.BOLD,
                                    18
                            )
                    );

                    title.setForeground(
                            PRIMARY
                    );

                    title.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );

                    JLabel senderLabel =
                            new JLabel(
                                    "From: "
                                            + sender
                            );

                    senderLabel.setFont(
                            FONT_NORMAL
                    );

                    senderLabel.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );

                    JLabel fileLabel =
                            new JLabel(
                                    "File: "
                                            + fileName
                            );

                    fileLabel.setFont(
                            FONT_MEDIUM
                    );

                    fileLabel.setAlignmentX(
                            Component.LEFT_ALIGNMENT
                    );

                    content.add(
                            title
                    );

                    content.add(
                            Box.createVerticalStrut(
                                    14
                            )
                    );

                    content.add(
                            senderLabel
                    );

                    content.add(
                            Box.createVerticalStrut(
                                    7
                            )
                    );

                    content.add(
                            fileLabel
                    );

                    content.add(
                            Box.createVerticalStrut(
                                    10
                            )
                    );

                    Object[] options = {
                            "Accept",
                            "Reject"
                    };

                    int result =
                            JOptionPane.showOptionDialog(
                                    this,
                                    content,
                                    "Incoming File",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]
                            );

                    if (
                            result ==
                                    JOptionPane.YES_OPTION
                    ) {

                        writer.println(
                                "/accept "
                                        + sender
                        );

                        appendChat(
                                "FILE  Accepted "
                                        + fileName
                                        + " from "
                                        + sender
                        );

                    } else {

                        writer.println(
                                "/reject "
                                        + sender
                        );

                        appendChat(
                                "FILE  Rejected "
                                        + fileName
                                        + " from "
                                        + sender
                        );
                    }
                }
        );
    }

    // =========================================================
    // HANDLE FILE INFO
    // =========================================================

    private void handleFileInfo(
            String message
    ) {

        String[] parts =
                message.split(
                        "\\|",
                        4
                );

        if (
                parts.length < 4
        ) {

            appendChat(
                    "FILE  Invalid FILE_INFO response."
            );

            return;
        }

        String ip =
                parts[1];

        int port;

        try {

            port =
                    Integer.parseInt(
                            parts[2]
                    );

        } catch (
                NumberFormatException e
        ) {

            appendChat(
                    "FILE  Invalid peer File Port."
            );

            return;
        }

        String filePath =
                parts[3];

        File file =
                new File(
                        filePath
                );

        appendChat(
                "FILE  Peer accepted "
                        + file.getName()
        );

        appendChat(
                "FILE  Starting direct P2P transfer..."
        );

        Thread sendFileThread =
                new Thread(
                        () -> {

                            FileSender.sendFile(
                                    ip,
                                    port,
                                    filePath
                            );

                            appendChat(
                                    "FILE  Transfer process finished: "
                                            + file.getName()
                            );
                        }
                );

        sendFileThread.setDaemon(
                true
        );

        sendFileThread.start();
    }

    // =========================================================
    // APPEND CHAT
    // =========================================================

    private void appendChat(
            String message
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    chatArea.append(
                            message
                                    + "\n"
                    );

                    chatArea.setCaretPosition(
                            chatArea
                                    .getDocument()
                                    .getLength()
                    );
                }
        );
    }

    // =========================================================
    // DISCONNECT
    // =========================================================

    private void disconnect() {

        try {

            if (
                    writer != null
            ) {

                writer.println(
                        "exit"
                );
            }

            if (
                    socket != null
                            &&
                    !socket.isClosed()
            ) {

                socket.close();
            }

        } catch (
                IOException ignored
        ) {
        }

        dispose();

        System.exit(0);
    }

    // =========================================================
    // FORM LABEL
    // =========================================================

    private JLabel createFormLabel(
            String text
    ) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setFont(
                FONT_MEDIUM
        );

        label.setForeground(
                TEXT_PRIMARY
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // =========================================================
    // INPUT FIELD
    // =========================================================

    private JTextField createInputField(
            String value
    ) {

        JTextField field =
                new JTextField(
                        value
                );

        field.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        field.setForeground(
                TEXT_PRIMARY
        );

        field.setBackground(
                INPUT_BACKGROUND
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        44
                )
        );

        field.setPreferredSize(
                new Dimension(
                        350,
                        44
                )
        );

        field.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDER,
                                1,
                                true
                        ),
                        new EmptyBorder(
                                9,
                                13,
                                9,
                                13
                        )
                )
        );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return field;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                FONT_MEDIUM
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                PRIMARY
        );

        button.setFocusPainted(
                false
        );

        button.setOpaque(
                true
        );

        button.setContentAreaFilled(
                true
        );

        button.setBorderPainted(
                false
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        if (
                                button.isEnabled()
                        ) {

                            button.setBackground(
                                    PRIMARY_DARK
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        button.setBackground(
                                PRIMARY
                        );
                    }
                }
        );

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    private JButton createSecondaryButton(
            String text
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                FONT_MEDIUM
        );

        button.setForeground(
                PRIMARY
        );

        button.setBackground(
                PRIMARY_LIGHT
        );

        button.setFocusPainted(
                false
        );

        button.setOpaque(
                true
        );

        button.setContentAreaFilled(
                true
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(
                                        199,
                                        210,
                                        254
                                ),
                                1,
                                true
                        ),
                        new EmptyBorder(
                                9,
                                16,
                                9,
                                16
                        )
                )
        );

        return button;
    }

    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private JButton createSidebarButton(
            String text
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                FONT_MEDIUM
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                SIDEBAR_HOVER
        );

        button.setFocusPainted(
                false
        );

        button.setOpaque(
                true
        );

        button.setContentAreaFilled(
                true
        );

        button.setBorderPainted(
                false
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        44
                )
        );

        button.setPreferredSize(
                new Dimension(
                        205,
                        44
                )
        );

        button.setBorder(
                new EmptyBorder(
                        11,
                        14,
                        11,
                        14
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        button.setBackground(
                                PRIMARY
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        button.setBackground(
                                SIDEBAR_HOVER
                        );
                    }
                }
        );

        return button;
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            Component parent,
            String message
    ) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =========================================================
    // ONLINE USER RENDERER
    // =========================================================

    private class OnlineUserRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {

            JLabel label =
                    (JLabel)
                            super.getListCellRendererComponent(
                                    list,
                                    value,
                                    index,
                                    isSelected,
                                    cellHasFocus
                            );

            label.setText(
                    "ONLINE    "
                            + value
            );

            label.setFont(
                    FONT_NORMAL
            );

            label.setBorder(
                    new EmptyBorder(
                            9,
                            12,
                            9,
                            12
                    )
            );

            label.setOpaque(
                    true
            );

            if (
                    isSelected
            ) {

                label.setBackground(
                        PRIMARY
                );

                label.setForeground(
                        Color.WHITE
                );

            } else {

                label.setBackground(
                        SIDEBAR
                );

                label.setForeground(
                        new Color(
                                226,
                                232,
                                240
                        )
                );
            }

            return label;
        }
    }
    
    private JLabel createCenteredFormLabel(
        String text
) {

    JLabel label =
            new JLabel(
                    text,
                    SwingConstants.CENTER
            );

    label.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    14
            )
    );

    label.setForeground(
            TEXT_PRIMARY
    );

    label.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    label.setMaximumSize(
            new Dimension(
                    310,
                    25
            )
    );

    return label;
}


private JTextField createCenteredInputField(
        String value
) {

    JTextField field =
            new JTextField(
                    value
            );

    field.setFont(
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    15
            )
    );

    field.setForeground(
            TEXT_PRIMARY
    );

    field.setBackground(
            INPUT_BACKGROUND
    );

    field.setHorizontalAlignment(
            JTextField.LEFT
    );

    Dimension size =
            new Dimension(
                    310,
                    46
            );

    field.setPreferredSize(size);
    field.setMaximumSize(size);
    field.setMinimumSize(size);

    field.setAlignmentX(
            Component.CENTER_ALIGNMENT
    );

    field.setBorder(
            new CompoundBorder(
                    new LineBorder(
                            BORDER,
                            1,
                            true
                    ),
                    new EmptyBorder(
                            10,
                            14,
                            10,
                            14
                    )
            )
    );

    return field;
}
    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                ChatClientGUI::new
        );
    }
}