/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 *
 * @author monkeyboz
 */
public class VBox extends javax.swing.JFrame {
    ArrayList<JFrame> testing = new ArrayList<>();
    
    private String information;
    private Boolean[] reachable;
    private String vboxdir;
    private ConfigSettings config;
    private CreateServer createServer;
    private ServerList serverListTree;
    private ArrayList<String> serverList;
    private VMServerCreationSystem serverCreation;
    private ArrayList<String> vmArray;
    private ArrayList<String> commandOutput;
    private ArrayList<String[]> ipAddresses;
    private CommandWorker worker;
    private JComponent loadingImage;
    private String tmpServerName;
    
    private String downloadDir;
    private String configDir;
    private String isoFile;
    private String virtualboxdir;
    
    private int count;
    private int serverProcessCount;
    private int downloadCount;
    private int[] mouseOrigin;
    
    private Boolean downloading;
    private String[] newVMInformation;
    private ArrayList<String[]> commandList;
    private ArrayList<String> newServerList;
    private ArrayList<String> currentDownloads;
    private ArrayList<String> commandText;
    
    private Boolean setupIPAddress;
    final private Boolean connectionTimedout;
    
    SwingWorker worker1;
    
    public VBox() {
        initComponents();
        initVariables();
        connectionTimedout = false;
    }
    
    private void initVariables(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(getClass().getResource("/information/images/vmicon.png").getFile());
        this.setIconImage(img);
        downloading = false;
        isoFile = "";
        
        setupIPAddress = false;
        this.setLocationRelativeTo(null);
        
        worker = new CommandWorker(this);
        
        commandOutput = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        commandText = new ArrayList<>();
        
        config = new ConfigSettings();
        config.setVisible(false);
        config.setParent(this);
        reconfigurePlacement(config);
        
        createServer = new CreateServer();
        createServer.setVisible(false);
        createServer.setParent(this);
        reconfigurePlacement(createServer);
        
        serverCreation = new VMServerCreationSystem();
        serverCreation.setVisible(false);
        serverCreation.setParent(this);
        reconfigurePlacement(serverCreation);
        
        serverListTree = new ServerList();
        serverListTree.setVisible(false);
        serverListTree.setParent(this);
        reconfigurePlacement(serverListTree);
        
        serverList = new ArrayList<>();
        vmArray = new ArrayList<>();
        
        String userdir = System.getProperty("user.home");
        
        downloadDir = userdir+"\\Downloads\\";
        configDir = userdir+"\\VMManagerConfig\\";
        
        readInstallFile();
        
        File configCheck = new File(configDir);
        if(!configCheck.isDirectory()){
            new File(configDir).mkdir();
        }
        
        worker1 = new SwingWorker<String,Void>(){
            @Override
            protected String doInBackground() throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        
        jMenuItem7.setIcon(new ImageIcon(getClass().getResource("/information/images/install.gif")));
        jMenuItem8.setIcon(new ImageIcon(getClass().getResource("/information/images/vbox.png")));
        jMenuItem9.setIcon(new ImageIcon(getClass().getResource("/information/images/questionmark.png")));
        jMenu4.setIcon(new ImageIcon(getClass().getResource("/information/images/questionmark.png")));
        
        jLabel8.setIcon(new ImageIcon(getClass().getResource("/information/images/close_button.png")));
        
        RunThread run = new RunThread();
        run.setParent(this);
        Thread newThread = new Thread(run);
        newThread.setDaemon(true);
        newThread.start();
    }
    
    public class RunThread implements Runnable{
        VBox parent;
        Boolean continueRun;
        @Override
        public void run() {
            while(continueRun){
                try {
                    Thread.sleep(10000);
                    System.out.println("Refreshing IP Addresses...");
                    parent.setupIPTable();
                } catch (InterruptedException ex) {
                    Logger.getLogger(VBox.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        public void setParent(VBox parent){
            continueRun = true;
            this.parent = parent;
        }
    }
    
    private void reconfigurePlacement(JFrame a){
        a.setLocationRelativeTo(null);
    }
    
    public void setVBDir(String dirString){
        setVBoxDir(dirString);
    }
    
    public void setTempServerName(String serverName){
        tmpServerName = serverName;
    }
    
    public void setVirtualboxdir(String virtualboxdir){
        this.virtualboxdir = virtualboxdir+"\\";
    }
    
    public void setupServerHome(){
        setupVBoxDir();
        serverList = new ArrayList<>();
        serverList.add("Home");
        runCommand(new String[]{vboxdir,"list","vms"},true,"addServerInfoToServerList",true,"Listing VMs");
    }
    
    public void setupServerHome(Boolean displayFrame){
        setupVBoxDir();
        serverList = new ArrayList<>();
        serverList.add("Home");
        if(displayFrame){
            runCommand(new String[]{vboxdir,"list","vms"},true,"addServerInfoToServerList",true,"Listing VMs");
        } else {
            runCommand(new String[]{vboxdir,"list","vms"},true,"addInfoToServer",true,"Listing VMs");
        }
    }
    
    public void writeInstallFile(){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(configDir+"resume.vmm",false));
            out.write("Current Configurations:\r\n");
            for(int i = 0; i < newServerList.size(); ++i){
                out.write(newServerList.get(i)+"\r\n");
            }
            out.write("Current Downloads:\r\n");
            for(int i = 0; i < currentDownloads.size(); ++i){
                out.write(currentDownloads.get(i)+"\r\n");
            }
            out.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void readInstallFile(){
        newServerList = new ArrayList<String>();
        currentDownloads = new ArrayList<String>();
        try{
            BufferedReader read = new BufferedReader(new FileReader(configDir+"resume.vmm"));
            String m;
            String currentSection = "";
            while((m = read.readLine()) != null){
                if("Current Downloads:".equals(m)){
                    currentSection = "CurrentDownloads";
                }
                if("Current Configurations:".equals(m)){
                    currentSection = "CurrentConfigurations";
                }
                
                if(!"Current Configurations:".equals(m) && !"Current Downloads:".equals(m)){
                    switch(currentSection){
                        case "CurrentDownloads":
                            currentDownloads.add(m);
                            break;
                        case "CurrentConfigurations":
                            newServerList.add(m);
                        default:
                            break;
                    }
                }
            }
            read.close();
        } catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    public String getISOFile(){
        return isoFile;
    }
    
    public void processPreviousInstalls(){
        if(serverProcessCount != newServerList.size()){
            Boolean serverListTest = false;
            String[] t = newServerList.get(serverProcessCount).split(",");
            for(int i = 0; i < vmArray.size(); ++i){
                String[] m = vmArray.get(i).split(",");
                for(int j = 0; j < m.length; ++j){
                    String[] contentTester = m[j].split(" ");
                    String info = contentTester[0].replace("\"","");
                    
                    Boolean test = info.contains(t[2].toLowerCase());
                    if(test){
                        serverListTest = true;
                    }
                }
            }
            if(serverListTest){
                newVMInformation = new String[]{t[0],t[1],t[2]};
                setupVMBox("folder/comd",true);
            } else {
                
            }
            ++this.serverProcessCount;
        } else {
            this.serverProcessCount = 0;
        }
    }
    
    public void resumeDownloads(){
        readInstallFile();
        if(downloadCount != currentDownloads.size()){
            try{
                String[] targetHolder = currentDownloads.get(downloadCount).split("/");
                String target = targetHolder[targetHolder.length-1];
                
                downloadFile(currentDownloads.get(downloadCount),target);
            } catch(IOException ex){
                System.out.println(ex);
            }
        } else {
            this.downloadCount = 0;
        }
    }
    
    public String getVMList(int i){
        return vmArray.get(i);
    }

    public void setVBoxDir(String vbox){
        vboxdir = vbox;
    }
    
    public void setISO(String isoFile){
        this.isoFile = isoFile;
    }
    
    private void readConfigFile(){
        JFrame jframe2 = new JFrame();
        jframe2.add(jFileChooser1);
        
        jframe2.setBounds(20,20,400,400);
        jframe2.setLocationRelativeTo(null);
        
        jFileChooser1.addActionListener((java.awt.event.ActionEvent evt) -> {
            if("CancelSelection".equals(evt.getActionCommand())){
                jframe2.setVisible(false);
            } else {
                File selectedFile = jFileChooser1.getSelectedFile();
                if(selectedFile != null){
                    information = selectedFile.getAbsolutePath();
                    readConfigInformation();
                    jframe2.setVisible(false);
                }
            }
        });
        jframe2.setVisible(true);
    }
    
    public ArrayList<String> getCommandOutput(){
        return commandOutput;
    }
    
    public void downloadFile(String  file, String target) throws MalformedURLException, IOException{
        if(!downloading){
            downloading = true;
            URL website = new URL(file);
            currentDownloads.add(file);
            int currentDownload = currentDownloads.size()-1;
            writeInstallFile();
            
            int contentLength = website.openConnection().getContentLength();
            Boolean connectionTimedout = false;
            
            SwingWorker worker = new SwingWorker<String,Void>(){
                @Override
                protected String doInBackground() throws Exception {
                    File checkIfFile = new File(downloadDir+target);
                    FileOutputStream writer;
                    int totalBytesRead;
                    int connectionTries = 0;
                    HttpURLConnection connection = (HttpURLConnection) website.openConnection();
                    int contentLengthHolder = contentLength;
                    
                    if(checkIfFile.exists()){
                        if(contentLengthHolder == checkIfFile.length()){
                            if(target.contains(".7z")){
                                extractFile(downloadDir+target);
                            }
                            return "Already downloaded...";
                        } else if(contentLength < checkIfFile.length()){
                            contentLengthHolder = website.openConnection().getContentLength();
                        }
                        totalBytesRead = (int)checkIfFile.length();
                        connection.setRequestProperty("Range", "bytes="+(totalBytesRead)+"-");
                        writer = new FileOutputStream(downloadDir+target,true);
                    } else {
                        totalBytesRead = 0;
                        writer = new FileOutputStream(downloadDir+target);
                    }
                    
                    byte[] buffer = new byte[153600];
                    
                    jProgressBar1.setVisible(true);
                    
                    jProgressBar1.setMinimum(0);
                    jProgressBar1.setMaximum(contentLength);
                    jProgressBar1.setValue(300);
                    
                    int bytesRead;
                    
                    jLabel5.setText("Beginning Download of "+target);
                    
                    InputStream input;
                    try{
                        connection.setConnectTimeout(5000);
                        input = connection.getInputStream();
                    }catch(IOException ex){
                        jLayeredPane1.setVisible(true);
                        this.cancel(true);
                        return "Try again later";
                    }
                    Timer timer = new Timer();
                    
                    while ((bytesRead = input.read(buffer)) > 0){
                        if(contentLengthHolder < totalBytesRead){
                            if(connectionTries == 3) return "Unable to connect";
                            TimeUnit.SECONDS.sleep(5);
                            ++connectionTries;
                            contentLengthHolder = website.openConnection().getContentLength();
                        } else {
                            writer.write(buffer, 0, bytesRead);
                            buffer = new byte[153600];
                            totalBytesRead += bytesRead;
                            
                            timer.schedule(new TimerTask(){
                                @Override
                                public void run() {
                                    this.cancel();
                                }
                            },10000);
                            
                            jProgressBar1.setValue(totalBytesRead);
                            DecimalFormat d = new DecimalFormat("###,###,###,###,###.##");
                            jLabel4.setText(d.format((float)totalBytesRead/(1024*1024))+"MB/"+d.format((float)contentLengthHolder/(1024*1024))+"MB ");
                            jProgressBar1.setString((int)(((double)totalBytesRead/(double)contentLength) * 100)+"%");
                        }
                    }
                    
                    writer.close();
                    if(totalBytesRead < contentLength){
                        Class[] cArg = new Class[2];
                        cArg[1] = String.class;
                        cArg[2] = String.class;
                        downloadFile(file,target);
                        try{
                            getClass().getMethod("downloadFile",cArg).invoke(getClass(),file,target);
                        }catch(IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex){
                            System.out.println(ex);
                        }
                        System.out.println("restarting");
                        return "restarting process";
                    }
                    
                    if(target.contains(".7z")){
                        extractFile(downloadDir+target);
                    }
                    currentDownloads.remove(currentDownload);
                    writeInstallFile();
                    return "Finished...";
                }
                
                @Override
                protected void done(){
                    
                }
            };
            worker.execute();
        }
    }
    
    private void extractFile(String target){
        try{
            SevenZip.initSevenZipFromPlatformJAR();
            RandomAccessFile randomAccessFile = new RandomAccessFile(target, "r");
            IInArchive inArchive = SevenZip.openInArchive(null,new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[] { 0 };
                if (!item.isFolder()) {
                    File itemFile = new File(downloadDir+item.getPath());
                    if(!itemFile.exists()){
                        ExtractOperationResult result;
                        FileOutputStream writer = new FileOutputStream(downloadDir+item.getPath(),true);
                        final long[] sizeArray = new long[1];
                        if(item.getSize() != itemFile.length()){
                            result = item.extractSlow((byte[] data) -> {
                                hash[0] ^= Arrays.hashCode(data); // Consume data
                                try {
                                    writer.write(data);
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(VBox.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(VBox.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                sizeArray[0] += data.length;
                                return data.length; // Return amount of consumed
                                // data
                            });

                            if (result == ExtractOperationResult.OK) {
                                System.out.println(String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
                            } else {
                                System.err.println("Error extracting item: " + result);
                            }
                        }
                        writer.close();
                    }
                }
            }
        } catch(IOException | SevenZipNativeInitializationException ex){
            System.out.println(ex);
        }
    }
    
    public void setupVBoxDir(){
        vboxdir = vboxdir.replace("VBoxManage.exe","");
        vboxdir = vboxdir+"VBoxManage.exe";
    }
    
    private void setupIPRows() throws UnknownHostException, IOException{
        setupVBoxDir();
        ArrayList commandList = new ArrayList<String>();
        runCommand(new String[]{vboxdir,"list","hostonlyifs"},true,"updateIPInformation",true,"Listing Host Only Adapters");
    }
    
    public void addInfoToServerProcess(Boolean showFrame){
        String tmp = (tmpServerName != null)?tmpServerName:"";
        
        for(int i = 0; i < commandOutput.size(); ++i){
            tmp += ","+commandOutput.get(i);
        }
        vmArray.add(tmp);
        if(!showFrame){
            processPreviousInstalls();
        }
    }
    
    public void addServerInfoToServerList(Boolean showFrame){
        addInfoToServerProcess(showFrame);
        if(showFrame){
            serverListTree.setupServerModel();
        }
    }
    
    public void addServerInfoToServerList(){
        addServerInfoToServerList(true);
    }
    
    private Document parseConfigFile() throws ParserConfigurationException, SAXException, IOException{
        File jfile = new File(information);
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbf = xml.newDocumentBuilder();
        Document doc = dbf.parse(jfile);
        return doc;
    }
    
    public void setupIPTable(){
        try{
            setupIPRows();
        } catch(IOException ex){
            System.out.println("IP table Iusse");
        }
    }
    
    private void readVMsForServer(String server){
        if(server.contains("main")){
            setupVBoxDir();
            runCommand(new String[]{vboxdir,"list","vms"},true,"setupVMList",true,"List VM's");
        } else {
            setupVBoxDir();
        }
    }
    
    public String getVBDir(){
        setupVBoxDir();
        return vboxdir;
    }
    
    public ArrayList<String> getServers(){
        return serverList;
    }
    
    private void setupVMList(){
        for(int i = 0; i < commandOutput.size(); ++i){
            String serverInfo = commandOutput.get(i).split(" ")[0];
        }
    }

    private void readConfigInformation(){
        setupIPTable();
    }
    
    public void setCommandOutput(ArrayList<String> command){
        this.commandOutput = command;
    }
    
    public void runCommand(String[] command,Boolean showOutput,String function,Boolean showAfter,String commandText) {
        worker = new CommandWorker(this,jLabel2,jLayeredPane1,command,showOutput,function,commandText);
        worker.showDirectlyAfter(showAfter);
        worker.execute();
    }
    
    public void processVboxmanage(String process,String attribute){
        vboxdir = vboxdir.replace("VBoxManage.exe","");
        vboxdir = vboxdir+"VBoxManage.exe";
        
        String[] vboxHolder;
        
        switch(process){
            case "remove":
                vboxHolder = new String[]{vboxdir,"VBoxManager.exe","hostonlyif","remove","\""+attribute+"\""};
                break;
            default:
                vboxHolder = new String[]{vboxdir,"hostonlyif","create"};
                break;
        }
        
        setupVMBox("folder/networkupdate",false);
    } 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jLabel5 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();

        jFileChooser1.setOpaque(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VBox Layout");
        setBackground(new java.awt.Color(0, 0, 0));
        setLocation(new java.awt.Point(50, 50));
        setUndecorated(true);
        setResizable(false);

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setAlignmentX(0.0F);
        jLayeredPane1.setAlignmentY(0.0F);
        jLayeredPane1.setOpaque(true);

        jLayeredPane4.setBackground(new java.awt.Color(204, 204, 204));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Beginning Download");

        jProgressBar1.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar1.setForeground(new java.awt.Color(0, 204, 51));
        jProgressBar1.setOpaque(true);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Loading...");

        jLayeredPane4.setLayer(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane4.setLayer(jProgressBar1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane4.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(32, 32, 32))
        );

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Current Running Commands:");

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Setup File");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Add Adapter");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Refresh IP List");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("None");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLayeredPane2.setLayer(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(12, Short.MAX_VALUE)))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(116, Short.MAX_VALUE)))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap())
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(50, Short.MAX_VALUE)))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel6)
                    .addContainerGap(118, Short.MAX_VALUE)))
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IP Address", "Network Type", "Connected"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("testing");
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setGridColor(new java.awt.Color(51, 51, 51));
        jTable1.setSelectionBackground(new java.awt.Color(204, 204, 204));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
        }

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("close");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel8MouseEntered(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("List of Adapters Currently Active for Virtual Box");

        jLayeredPane1.setLayer(jLayeredPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLayeredPane4))
                .addContainerGap())
        );

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));
        jMenuBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuBar1.setForeground(new java.awt.Color(255, 255, 255));

        jMenu2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu2.setForeground(new java.awt.Color(255, 255, 255));
        jMenu2.setText("Setup");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItem1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem1.setText("Configuration");
        jMenuItem1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuItem1.setBorderPainted(true);
        jMenuItem1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jMenuItem1.setOpaque(true);
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu1.setBackground(new java.awt.Color(255, 255, 255));
        jMenu1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu1.setForeground(new java.awt.Color(255, 255, 255));
        jMenu1.setText("Server Setup");

        jMenuItem2.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem2.setText("Connect To Server");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem3.setText("Display Servers");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem4.setText("Create New Server");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu3.setForeground(new java.awt.Color(255, 255, 255));
        jMenu3.setText("Continue Installs");
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        jMenuItem5.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem5.setText("Resume Configurations");
        jMenuItem5.setOpaque(true);
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem6.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem6.setText("Resume Downloads");
        jMenuItem6.setOpaque(true);
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        jMenu4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenu4.setForeground(new java.awt.Color(255, 255, 255));
        jMenu4.setText("Help");
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });

        jMenuItem7.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem7.setText("How To's to Linux Installs...");
        jMenuItem7.setOpaque(true);
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem7);

        jMenuItem8.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem8.setText("Virtual Box Tutorials");
        jMenuItem8.setOpaque(true);
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem9.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem9.setText("VManager Help");
        jMenuItem9.setOpaque(true);
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    public void updateIPInformation(){
        ipAddresses = new ArrayList<>();
        ArrayList<String> ipAddressArray = new ArrayList<>();
        ArrayList<String> adapterNameArray = new ArrayList<>();
        int count = 0;
        for(int i = 0; i < commandOutput.size(); ++i){
            String getInfo = commandOutput.get(i);
            if(getInfo.contains("IPAddress:")){
                ipAddressArray.add(getInfo.replace("IPAddress:       ",""));
            }
            if(getInfo.contains("VBoxNetworkName:")){
                adapterNameArray.add(getInfo.replace("VBoxNetworkName: ",""));
            }
            if(i % 11 == 0 && i != 0){
                ++count;
            }
        }
        
        List<String[]> obj = new ArrayList<>();
        String[] str = new String [] {
                "IP Address", "Network Type","Connected"
            };
        for(int i = 0; i < adapterNameArray.size();++i){
            ipAddresses.add(new String[]{adapterNameArray.get(i),ipAddressArray.get(i)});
        }
        
        ArrayList<String[]> node = new ArrayList<>();
        for(int i = 0; i < ipAddresses.size(); ++i){
            node.add(new String[]{ipAddresses.get(i)[1],ipAddresses.get(i)[0],"testng"});
        }
        
        updateDHCP();
        
        int total_host_only = 0;
        for(int i = 0; i < node.size(); ++i){
            String[] elm = node.get(i);
            ++total_host_only;
            try{
                InetAddress inet = InetAddress.getByName(elm[0]);
                String f = (inet.isReachable(5000))?"true":"false";
                String[] obj_holder = {elm[0],elm[1],f};
                obj.add(obj_holder);
            } catch(UnknownHostException ex){
                System.out.println(ex);
            } catch(IOException ex){
                System.out.println(ex);
            }
        }   
        
        Object[][] string = new Object[obj.toArray().length][];
        
        for(int i = 0; i < obj.toArray().length; ++i){
            String[] info = obj.get(i);
            string[i] = info;
        }
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                string,str
        ));
        jTable1.repaint();   
        
        reachable = new Boolean[string.length];
        jLabel1.setText("List of Adapters Currently Active for Virtual Box ("+ipAddresses.size()+")");
    }
    
    private void updateDHCP(){
        for(int i = 0; i < ipAddresses.size(); ++i){
            String dhcp = "dhcpserver modify --netname \"[hostonlyname]\" -ip [ipaddress] --netmask 255.255.255.0 --lowerip [ipaddresslower] --upperip [ipaddressupper] --enable";
            dhcp = dhcp.replace("[hostonlyname]",ipAddresses.get(i)[0]);
            dhcp = dhcp.replace("[ipaddress]",ipAddresses.get(i)[1]);
            dhcp = dhcp.replace("[ipaddressupper]",ipAddresses.get(i)[1].substring(0,ipAddresses.get(i)[1].lastIndexOf('.'))+".101");
            dhcp = dhcp.replace("[ipaddresslower]",ipAddresses.get(i)[1].substring(0,ipAddresses.get(i)[1].lastIndexOf('.'))+".254");
            
            String[] m = dhcp.split(" ");
            String[] n = new String[m.length+1];
            n[0] = vboxdir;
            
            System.arraycopy(m,0,n,1,m.length);
            SwingWorker workerDHCP = new SwingWorker<String,Void>(){
                @Override
                protected String doInBackground() throws Exception {
                    try{
                        Process run = Runtime.getRuntime().exec(n);
                    } catch(IOException ex){
                        System.out.println(ex);
                    }
                    return "";
                }
            };
            workerDHCP.execute();
        }
    }
    
    private void testing(){
        System.out.println("testing");
    }
    
    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       config.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void addServer(String serverName,String serverIP,String serverMask,String serverGateway){
        System.out.println(serverName);
    }
    
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        setupServerHome();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        createServer.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        serverCreation.setVisible(true);
        serverCreation.setupFrame();
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        setupServerHome(false);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        resumeDownloads();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu4ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        setupIPTable();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel8MouseEntered

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        Runtime.getRuntime().halt(0);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        processVboxmanage("create","null");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int n = JOptionPane.showConfirmDialog(
            this,
            "Do you really want to delete the IPAddress",
            "Delete Clicked IP Address",
            JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.YES_OPTION){
            String adapterName = ipAddresses.get(jTable1.getSelectedRow())[0];
            setupVBoxDir();
            runCommand(new String[]{vboxdir,"hostonlyif","remove","\""+adapterName.replace("HostInterfaceNetworking-","")+"\""},true,"setupIPTable",false,"Removing Host-Only Adapter");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //read config file for rewriting
        readConfigFile();
        //setup vbox directory for processing processes
        setupVBoxDir();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /* used to grab virtual box directory */
    public String getVBoxDir(){
        return vboxdir;
    }
    
    /* needed to grab more VMInformation */
    public void getNewVMInformation(String vmname,String vmhdsize,String vmtype,String vmram){
        newVMInformation = new String[]{vmname,vmhdsize,vmtype,vmram};
    }
    
    /* needed to create vm instance in VirtualBox */
    public void createVM(){
        newServerList.add(newVMInformation[0]+","+newVMInformation[1]+","+newVMInformation[2]);
        writeInstallFile();
        setupVMBox("folder/install",true);
    }
    
    public void setupVMBox(String commandFile,Boolean isoSystem){
        if(commandFile == "folder/install"){
            File t = new File(getClass().getResource("folder/tmpinstall").getFile());
            try{
                BufferedWriter bm = new BufferedWriter(new FileWriter(t));
                BufferedReader br = new BufferedReader(new FileReader(getClass().getResource(commandFile).getFile()));
                
                String reader = "";
                while((reader = br.readLine()) != null){
                    reader = reader.replace("[vmname]",newVMInformation[0]+"_"+newVMInformation[2]);
                    reader = reader.replace("[osType]",newVMInformation[2]);
                    bm.write(reader+"\r\n");
                }
                br = new BufferedReader(new FileReader(getClass().getResource("folder/comd").getFile()));
                while((reader = br.readLine()) != null){
                    bm.write(reader+"\r\n");
                }
                bm.close();
                br.close();
            }catch(IOException ex){
                System.out.println(ex);
            }
            commandFile = "folder/tmpinstall";
        }
        String v = getClass().getResource(commandFile).getFile();
        File commands = new File(v);
        
        try{
            BufferedReader out = new BufferedReader(new FileReader(commands));
            ArrayList<String[]> arraylist = new ArrayList<String[]>();
            String isoHolder = "";
            if(isoFile.length() > 0){
                File fileChecker = new File(isoFile);
                if(fileChecker.exists()){
                    isoHolder = isoFile;
                    isoFile = "";
                } else {
                    System.out.println("File does not exist.");
                }
            } else {
                File download = new File(getClass().getResource("folder/operating_systems").getFile());
                String downloadString;
                if(isoSystem){
                    try (BufferedReader stream = new BufferedReader(new FileReader(download))) {
                        String m;
                        downloadString = "";
                        while((m = stream.readLine()) != null){
                            if(m.contains(newVMInformation[2].toLowerCase())){
                                String[] check = m.split(",");
                                downloadString = check[1];
                            }
                        }
                    }
                    
                    if(!"".equals(downloadString)){
                        String[] t = downloadString.split("/");
                        downloadFile(downloadString,t[t.length-1]);
                    }
                }
            }
            
            String l = "";
            while((l = out.readLine()) != null){
                if(isoSystem){
                    l = l.replace("[vmnamefile]",virtualboxdir+newVMInformation[0]+"_"+newVMInformation[2]);
                    l = l.replace("[vmname]",newVMInformation[0]+"_"+newVMInformation[2]);
                    l = l.replace("[vmhdsize]",newVMInformation[1]);
                    l = l.replace("[vmtype_path_to_iso]",isoHolder);
                    l = l.replace("[vmramsetting]",newVMInformation[3]);
                }
                
                if(!ipAddresses.isEmpty()){
                    String[] ipSetup = ipAddresses.get(0)[1].split("\\.");
                    if(ipSetup.length > 0){
                        l = l.replace("[hostonlyname]","\""+ipAddresses.get(0)[0].replace("HostInterfaceNetworking-","")+"\"");
                    }
                }
                
                String[] commandLines = l.split(" ");
                String[] mainCommandLine = new String[commandLines.length+1];
                                
                System.arraycopy(new String[]{vboxdir},0,mainCommandLine,0,1);
                System.arraycopy(commandLines,0,mainCommandLine,1,commandLines.length);
                
                arraylist.add(mainCommandLine);
                commandText.add(arraylist.get(0)[1]);
            }
            commandList = arraylist;
            count = 0;
            
            runCommandsSync();
            out.close();
        } catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    public void runCommandsSync(){
        if(count == commandList.size()){
            return;
        } else {
            runCommand(commandList.get(count),true,"runCommandsSync",true,commandText.get(count));
            ++count;
        }
    }
    
    private void createLinuxUpdate(String type){
        serverCreation.setServerType(type);
        vboxdir = vboxdir.replace("VBoxManage.exe","");
        vboxdir = vboxdir+"VBoxManage.exe";
        
        String[] vboxHolder;
        
        vboxHolder = new String[]{vboxdir,"createvm","--name","testing","--ostype","Linux","--register"};
        runCommand(vboxHolder,true,"createVM",true,"Creating VMs");
    }
    
    public void startVM(){
        System.out.println("VM Starting ...");
    }
    
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VBox.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new VBox().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private void formMouseDragged(java.awt.event.MouseEvent evt) {                                  
        this.setBounds(evt.getX()+this.getX()-mouseOrigin[0],evt.getY()+this.getY()-mouseOrigin[1],this.getWidth(),this.getHeight());
    }                                 

    private void formMousePressed(java.awt.event.MouseEvent evt) {                                  
        mouseOrigin = new int[]{evt.getX(),evt.getY()};
    }
    
    private static class ImageImpl extends Image {

        public ImageImpl() {
        }

        @Override
        public int getWidth(ImageObserver observer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getHeight(ImageObserver observer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ImageProducer getSource() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Graphics getGraphics() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object getProperty(String name, ImageObserver observer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}