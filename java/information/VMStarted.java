/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
//import com.jcraft.jsch.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author monkeyboz
 */
public class VMStarted extends javax.swing.JFrame {
    private VBox parent;
    private SwingWorker worker;
    private String vboxdir;
    private String vmName;
    private ArrayList<String> activeIPAddresses;
    private Boolean cancelledPing;
    private Map<String,String> osCommands;
    private DefaultListModel serverIPModel;
    //private JSch serverTest;
    private Map<String,Map> serverCommandsMap;
    
    /**
     * Creates new form VMStarted
     * @param parent
     * @param vmName
     */
    public VMStarted(VBox parent,String vmName){
        initComponents();
        cancelledPing = false;
        
        serverIPModel = new DefaultListModel();
        
        iplists.setModel(serverIPModel);
        
        this.parent = parent;
        this.vmName = vmName;
        this.activeIPAddresses = new ArrayList<>();
        
        osCommands = this.parent.getOSCommands();
        getVBoxDir();
        
        worker = pingIPAddresses();
        worker.execute();
    }
    
    private void getVBoxDir(){
        vboxdir = parent.getVBoxDir();
        serverCommandsMap = getServerCommands();
        for(int i = 0; i < serverCommandsMap.size(); ++i){
            serverCommandsMap.get(i);
        }
    }
    
    private void startTerminal(String ipaddress){
        //serverTest = new JSch();
        String ipaddressInfo = ipaddress;
        
        //frame and panels storing various information for the layout
        JFrame terminalCFrame = new JFrame("Enter your Credentials for "+ipaddress);
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        
        //textfields used in the connection to the server
        JTextField usernameField = new JTextField();
        JTextField portField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton submitButton = new JButton("submit");
        
        //labels used for displaying field option names
        JLabel usernameLabel = new JLabel();
        JLabel passwordLabel = new JLabel();
        JLabel portLabel = new JLabel();
        
        usernameLabel.setText("Username:");
        passwordLabel.setText("Password:");
        portLabel.setText("Port:");
        
        //layered pane used to control the overall layout
        JLayeredPane layeredPane = new JLayeredPane();
        
        //formating parameters and function setup
        Dimension dv = new Dimension(300,20);
        usernameField.setColumns(15);
        passwordField.setColumns(15);
        portField.setColumns(2);
        
        //set bounds for panels
        panel1.setBounds(0,0,300,30);
        panel2.setBounds(0,30,300,30);
        panel3.setBounds(0,60,300,30);
        panel4.setBounds(0,90,300,30);
        
        //set dimensions for formatting
        Dimension dm = new Dimension(100,20);
        portLabel.setSize(dm);
        passwordLabel.setSize(dm);
        portLabel.setSize(dm);
        
        //panels used for holding the fields for connecting to server
        panel1.add(usernameLabel);
        panel1.add(usernameField);
        panel2.add(passwordLabel);
        panel2.add(passwordField);
        panel3.add(portLabel);
        panel3.add(portField);
        panel4.add(submitButton);
        layeredPane.add(panel1);
        layeredPane.add(panel2);
        layeredPane.add(panel3);
        layeredPane.add(panel4);
        terminalCFrame.add(layeredPane);
        
        //option setup for displaying the frame
        terminalCFrame.setVisible(true);
        terminalCFrame.setBounds(0,0,300,300);
        terminalCFrame.setLocationRelativeTo(null);
        
        String usernameText = "Enter Username";
        String passwordText = "Enter Password";
        String portValue = "22";
        usernameField.setText(usernameText);
        passwordField.setText("password");
       
        //used in the focus actions of the port field
        portField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
                updateFocus(portValue,portField);
            }

            @Override
            public void focusLost(FocusEvent e) {
                loseFocus(portValue,portField);
            }
            
        });
        
        //used in the focus action of the username field
        usernameField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
                updateFocus(usernameText,usernameField);
            }

            @Override
            public void focusLost(FocusEvent e) {
                loseFocus(usernameText,usernameField);
            }
        });
        
        //submit button to attempt a connection to the server as well as using the various text fields needed for the connection.
        submitButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String portStr = portField.getText();
            String passwordStr = "";
            CustomOutputStream output = new CustomOutputStream(terminal);
            CustomInputStream input = new CustomInputStream(commandline);
            
            //user information used for various connections with the server. These processes are used in connection with prompts
            //to display several options when connecting to the server.
            /*UserInfo ui = new CreateServerConnection.MyUserInfo(){
                @Override
                public void showMessage(String message){
                    JOptionPane.showMessageDialog(null, message);
                }
                @Override
                public boolean promptYesNo(String message){
                    Object[] options={ "yes", "no" };
                    int foo=JOptionPane.showOptionDialog(null,
                            message,
                            "Warning",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);
                    return foo==0;
                }
                @Override
                public String[] promptKeyboardInteractfaceive(String destination,
                        String name,
                        String instruction,
                        String[] prompt,
                        boolean[] echo){
                    return null;
                }
                
                @Override
                public String[] promptKeyboardInteractive(String string, String string1, String string2, String[] strings, boolean[] blns) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };*/
            
            char[] password;
            password = passwordField.getPassword();
            
            for(int v = 0; v < password.length; ++v){
                passwordStr += password[v];
            }
            
            /*try{
                //Session sshSession = serverTest.getSession(username,ipaddressInfo,Integer.parseInt(portStr));
                sshSession.setPassword(passwordStr);
                sshSession.setUserInfo(ui);
                sshSession.connect();
                
                Channel channel = sshSession.openChannel("shell");
                String outputString = "";
                
                commandline.addKeyListener(new KeyListener(){
                    //used for sending commands to the server (used with the input read system)
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if('\n' == e.getKeyChar()){
                            try {
                                if(commandline.getText() == "exit"){
                                    channel.disconnect();
                                } else {
                                    input.read(commandline.getText().getBytes());
                                }
                            } catch (IOException ex) {
                                java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                });
                
                //used in the setup of the JSCH(terminal) channel connection with the server specified by the ipaddress 
                //parameter when attempting to connect to the server
                channel.setInputStream(input);
                channel.setOutputStream(output);
                channel.connect();
            }catch(JSchException | NumberFormatException ex){
                System.out.println(ex);
            }*/
        });
    }
    
    //used in conjunction with the server commands that will automate installs as well as other server setup options and 
    //server health options.
    public Map<String,Map> getServerCommands(){
        File file = new File(getClass().getResource("/information/folder/server_commands").getFile());
        Map<String,Map> maps = new HashMap<>();
        
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            Map<String,ArrayList<String>> mp = new HashMap<>();
            ArrayList<String> string = new ArrayList<>();
            
            String testing = "";
            ArrayList<String> something = new ArrayList<String>();
            Boolean change = true;
            String area = "";
            String currArea = "";
            int count = 0;
            int checkCount = 0;
            
            while((testing = reader.readLine()) != null){
                if(testing.length() > 0){
                    if(testing.charAt(0) != '-' && testing.charAt(0) != '#'){
                        something.add(testing);
                        change = true;
                    } else {
                        switch(testing.charAt(0)){
                            case '-':
                                area = "variables";
                                change = true;
                                break;
                            case '#':
                                area = "installs";
                                change = true;
                                break;
                        }
                        if(change){
                            mp.put(currArea, something);
                            maps.put(area,mp);
                            change = false;
                        } else {
                            mp = new HashMap<>();
                            currArea = testing;
                            change = true;
                        }
                    }
                }
            }
            return maps;
        } catch(IOException ex){
        }
        return maps;
    }
    
    public Map<String,String> addVariables(BufferedReader reader,String variable){
        Map<String,String> map = new HashMap<>();
        String readIn;
        try{
            while((readIn = reader.readLine()) != null){
                map.put(variable,readIn);
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        return map;
    }
    
    public Map<String,String> addInstalls(BufferedReader reader,String variable){
        Map<String,String> map = new HashMap<>();
        String readIn;
        try{
            while((readIn = reader.readLine()) != null){
                map.put(variable,readIn);
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        return map;
    }
    
    public class CustomInputStream extends InputStream implements ActionListener {
        final JTextField field;
        final BlockingQueue<String> q;

        public CustomInputStream(JTextField field) {
            this.field = field;
            q = new LinkedBlockingQueue<>();
            loadInfo();
        }

        private String s;
        int pos;
        
        private void loadInfo(){
            field.addActionListener(this);
        }

        @Override
        public int read() throws IOException {
            while (null == s || s.length() <= pos) {
                try {
                    s = q.take();
                    pos = 0;
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            int ret = (int) s.charAt(pos);
            pos++;
            return ret;
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytes_copied = 0;
            while (bytes_copied < 1) {
                while (null == s || s.length() <= pos) {
                    try {
                        s = q.take();
                        pos = 0;
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                }
                int bytes_to_copy = len < s.length()-pos ? len : s.length()-pos;
                System.arraycopy(s.getBytes(), pos, b, off, bytes_to_copy);
                pos += bytes_to_copy;
                bytes_copied += bytes_to_copy;
            }
            return bytes_copied;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            q.add(field.getText() + "\r\n");
            field.setText("");
        }
    }

// This is the code for making textarea as output stream
    public class CustomOutputStream extends OutputStream {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text area
            textArea.append(String.valueOf((char) b));
            String textAreaString = textArea.getText();
            // scrolls the text area to the end of data
            textArea.setCaretPosition(textArea.getText().length());
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String s = new String(b,off,len);
            textArea.append(s);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
    }
    
    private void updateFocus(String value,JTextField field){
        if(field.getText().equals(value)){
            field.setText("");
        }
    }
    
    private void loseFocus(String value,JTextField field){
        if(field.getText().equals("")){
            field.setText(value);
        }
    }
    
    private SwingWorker pingIPAddresses(){
        SwingWorker swingWorker = new SwingWorker(){
            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            /*  variables used for to test pinging for automatically created ip addresses*/
            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
            String ipaddress;           //address holder for pinging
            String pingCMD;             //string used for the ping command for the OS, pulled from the VBox class
            Process testConnection;     //used to execuse the process for pinging using the Java Runtime
            Boolean pinged;             //checks to see if the ping was successful using the Hash table for the OS
            Boolean falsePing;          //checks to see if the ping was a false ping from the Hash table for the OS
            Pattern t;                  //used to check the Hash table ping regex using the OS pingoutput
            Matcher mt;                 //checks for the exact matches used in the ping runtime execution
            Boolean[] portTesting;      //checks to see which ports are available depending on ping accesses
            ArrayList<String[]> ipaddresses = parent.getIPAddresses(); //gets all IP addresses available from VBoxManager.exe
            
            @Override
            protected Object doInBackground() throws Exception {
                setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
                
                Rectangle bounds = serverInformation.getBounds();
                String[] command = new String[]{vboxdir,"showvminfo","\""+vmName+"\""};
                Process run = Runtime.getRuntime().exec(command);
                InputStream in = run.getInputStream();
                
                serverInformation.setText("");
                serverInformation.setEditable(false);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String l;  //string used for reading the inputstream from the execution of the command variable
                
                iplists.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent evt){
                        if(iplists.getSelectedValue() != null){
                            String[] info = iplists.getSelectedValue().split(" ");
                            startTerminal(info[0]);
                        }
                    }
                });
                
                while((l = reader.readLine()) != null){
                    String[] split = l.split(":",2);
                    
                    if(split[1].contains("Host-only Interface")){
                        ipaddress = "";
                        for(int i = 0; i < ipaddresses.size(); ++i){
                            split[1] = split[1].trim();
                            if(split[1].contains(ipaddresses.get(i)[0].replace("HostInterfaceNetworking-VirtualBox",""))){
                                ipaddress = ipaddresses.get(i)[1].substring(0,ipaddresses.get(i)[1].length()-1);
                                break;
                            }
                        }   
                        networkCardActiveAnswer.setText("active");
                        serverRunningAnswer.setText("active");
                        serverIPAddressAnswer.setText("checking (click to restart)");
                        
                        for(int r = 101;r <= 254;++r){
                            if(cancelledPing){
                                cancelledPing = false;
                                serverIPAddressAnswer.setText("checking (click to restart)");
                                r = 101;
                            }
                            pingCMD = osCommands.get("ping").replace("[ipaddress]",ipaddress+r);
                            testConnection = Runtime.getRuntime().exec(pingCMD);
                            
                            serverIPAddressAnswer.setText("pinging ip "+ipaddress+r);
                            BufferedReader connectionReader = new BufferedReader(new InputStreamReader(testConnection.getInputStream()));
                            String m;
                            
                            pinged = false;
                            falsePing = false;
                            while((m = connectionReader.readLine()) != null){
                                m = m.trim();
                                int falsePos = 1;
                                while(osCommands.get("pingFalsePos"+falsePos) != null){
                                    if(m.matches(osCommands.get("pingFalsePos"+falsePos).trim()) != false){
                                        falsePing = true;
                                    }
                                    ++falsePos;
                                }
                                
                                if(m.matches(osCommands.get("pingOutput").trim())){
                                    t = Pattern.compile(osCommands.get("pingOutput").trim());
                                    mt = t.matcher(m);
                                    if(mt.find()){
                                        if(mt.group(2).contains("1")){
                                            pinged = true;
                                        }
                                    }
                                }
                            }
                            
                            if(pinged && !falsePing){
                                serverIPModel.addElement(ipaddress+r);
                            }
                        }
                    }
                    serverInformation.setText(serverInformation.getText()+split[0]+":\n"+split[1].trim()+"\n");
                }
                
                serverInformation.setSize(100, 100);
                return null;
            }
            @Override
            protected void done(){
                System.out.println("Process Done ... VM Started");
            }
            
        };
        return swingWorker;
    }
    
    private Boolean checkSocket(String ipaddress,int port){
        try{
            try (Socket socket = new Socket(ipaddress,port)) {
                socket.setSoTimeout(1000);
            }
        }catch(IOException ex){
            return true;
        }
        return false;
    }
    
    private Boolean[] testConnections(String ipaddress,int[] ports){
        Boolean[] testing = new Boolean[3];
        for(int i = 0; i < ports.length; ++i){
            testing[i] = checkSocket(ipaddress,ports[i]);
        }
        return testing;
    }

    private VMStarted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        commandline = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        serverInformation = new javax.swing.JTextPane();
        networkcardsActiveLabel = new javax.swing.JLabel();
        networkCardActiveAnswer = new javax.swing.JLabel();
        serverRunningLabel = new javax.swing.JLabel();
        serverRunningAnswer = new javax.swing.JLabel();
        serverIPAddress = new javax.swing.JLabel();
        serverIPAddressAnswer = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        currentScannedIPAddress = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        iplists = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        serverCommands = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        terminal = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLayeredPane1.setOpaque(true);

        commandline.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        commandline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandlineActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Terminal (inactive)");

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setMaximumSize(new java.awt.Dimension(299, 446));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Server Health");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Server Information");

        jButton1.setText("Restart Server");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Shutdown Server");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        serverInformation.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        serverInformation.setMaximumSize(new java.awt.Dimension(299, 446));
        serverInformation.setPreferredSize(new java.awt.Dimension(299, 446));
        jScrollPane4.setViewportView(serverInformation);

        networkcardsActiveLabel.setForeground(new java.awt.Color(204, 204, 204));
        networkcardsActiveLabel.setText("Network Cards Active");

        networkCardActiveAnswer.setText("Server");

        serverRunningLabel.setForeground(new java.awt.Color(204, 204, 204));
        serverRunningLabel.setText("Server Running");

        serverRunningAnswer.setText("Server");

        serverIPAddress.setForeground(new java.awt.Color(204, 204, 204));
        serverIPAddress.setText("Server IP Address");

        serverIPAddressAnswer.setText("Server");
        serverIPAddressAnswer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                serverIPAddressAnswerMouseClicked(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Adapter IP Addresses");

        currentScannedIPAddress.setForeground(new java.awt.Color(255, 255, 255));
        currentScannedIPAddress.setText("Server Commands (use once connected *inactive*)");
        currentScannedIPAddress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                currentScannedIPAddressMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(iplists);

        serverCommands.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(serverCommands);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(currentScannedIPAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(serverRunningLabel)
                                    .addComponent(networkcardsActiveLabel)
                                    .addComponent(serverIPAddress))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(serverIPAddressAnswer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(serverRunningAnswer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(networkCardActiveAnswer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                        .addGap(0, 159, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(networkcardsActiveLabel)
                    .addComponent(networkCardActiveAnswer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverRunningLabel)
                    .addComponent(serverRunningAnswer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverIPAddress)
                    .addComponent(serverIPAddressAnswer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentScannedIPAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        terminal.setColumns(20);
        terminal.setRows(5);
        terminal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        terminal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jScrollPane2.setViewportView(terminal);

        jLayeredPane1.setLayer(commandline, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 259, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(commandline))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commandline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void serverIPAddressAnswerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_serverIPAddressAnswerMouseClicked
        serverIPAddressAnswer.setText("Restarting search ...");
        cancelledPing = true;
        
        serverIPModel.removeAllElements();
        
        worker.cancel(true);
        worker = null;
        worker = pingIPAddresses();
    }//GEN-LAST:event_serverIPAddressAnswerMouseClicked

    private void commandlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandlineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_commandlineActionPerformed

    private void currentScannedIPAddressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currentScannedIPAddressMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_currentScannedIPAddressMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VMStarted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VMStarted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VMStarted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VMStarted.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new VMStarted().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField commandline;
    private javax.swing.JLabel currentScannedIPAddress;
    private javax.swing.JList<String> iplists;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel networkCardActiveAnswer;
    private javax.swing.JLabel networkcardsActiveLabel;
    private javax.swing.JList<String> serverCommands;
    private javax.swing.JLabel serverIPAddress;
    private javax.swing.JLabel serverIPAddressAnswer;
    private javax.swing.JTextPane serverInformation;
    private javax.swing.JLabel serverRunningAnswer;
    private javax.swing.JLabel serverRunningLabel;
    private javax.swing.JTextArea terminal;
    // End of variables declaration//GEN-END:variables
}
