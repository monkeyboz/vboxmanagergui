/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
    private ServerCreationSystem serverCreation;
    private ArrayList<String> serverArray;
    private ArrayList<String> commandOutput;
    private ArrayList<String[]> ipAddresses;
    private CommandWorker worker;
    private String tmpServerName;
    private String downloadDir;
    private Boolean downloading;
    private String[] newVMInformation;
    private ArrayList<String[]> commandList;
    private int count;
    
    public VBox() {
        initComponents();
        initVariables();
    }
    
    private void initVariables(){
        downloading = false;
        
        jLabel2.setVisible(false);
        worker = new CommandWorker(this);
        
        commandOutput = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        
        config = new ConfigSettings();
        config.setVisible(false);
        config.setParent(this);
        
        createServer = new CreateServer();
        createServer.setVisible(false);
        createServer.setParent(this);
        
        serverCreation = new ServerCreationSystem();
        serverCreation.setVisible(false);
        serverCreation.setParent(this);
        
        serverListTree = new ServerList();
        serverListTree.setVisible(false);
        serverListTree.setParent(this);
        
        serverList = new ArrayList<>();
        
        downloadDir = "C:\\Users\\monkeyboz\\Downloads\\";
        
        jLayeredPane2.setVisible(false);
    }
    
    public void setVBDir(String dirString){
        setVBoxDir(dirString);
    }
    
    public void setTempServerName(String serverName){
        tmpServerName = serverName;
    }
    
    public void setupServerHome(){
        setupVBoxDir();
        serverArray = new ArrayList<>();
        serverList.add("Home Server");
        runCommand(new String[]{vboxdir,"list","vms"},true,"addServerInfoToServerList",true);
    }
    
    public String getServerList(int i){
        return serverList.get(i);
    }

    public void setVBoxDir(String vbox){
        vboxdir = vbox;
    }   
    
    private void readConfigFile(){
        JFrame jframe2 = new JFrame();
        jframe2.add(jFileChooser1);
        jframe2.setVisible(true);
        
        jframe2.setBounds(20,20,400,400);
        
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
    }
    
    public void downloadFile(String  file, String target) throws MalformedURLException, IOException{
        if(!downloading){
            URL website = new URL(file);
            
            int contentLength = website.openConnection().getContentLength();
            
            File checkIfFile = new File(downloadDir+target);
            FileOutputStream writer;
            int totalBytesRead;
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            
            if(checkIfFile.exists()){
                if(contentLength == checkIfFile.length()){
                    System.out.println("File already exists");
                    if(target.contains(".7z")){
                        System.out.println("Extracting existing file....");
                        extractFile(downloadDir+target);
                    }
                    return;
                }
                totalBytesRead = (int)checkIfFile.length();
                
                connection.setRequestProperty("Range", "bytes="+(totalBytesRead)+"-");
                writer = new FileOutputStream(downloadDir+target,true);
            } else {
                totalBytesRead = 0;
                writer = new FileOutputStream(downloadDir+target);
            }
            
            byte[] buffer = new byte[153600];
            
            jLayeredPane2.setVisible(true);

            jProgressBar1.setVisible(true);
            jLayeredPane1.setVisible(false);
            jProgressBar1.setMinimum(0);
            jProgressBar1.setMaximum(contentLength);
            jProgressBar1.setValue(300);
            
            int bytesRead;

            String[] downloadFileName = file.split("/");
            jLabel5.setText("Beginning Download of "+downloadFileName[downloadFileName.length-1]);
            
            InputStream input = connection.getInputStream();
            
            while ((bytesRead = input.read(buffer)) > 0){
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[153600];
                totalBytesRead += bytesRead;
                
                jProgressBar1.setValue(totalBytesRead);
                jLabel4.setText(totalBytesRead+"/"+contentLength+" ");
                jProgressBar1.setString((int)(((double)totalBytesRead/(double)contentLength) * 100)+"%");
            }
            
            writer.close();
            jLayeredPane2.setVisible(false);
            
            if(target.contains(".7z")){
                extractFile(downloadDir+target);
            }
        } else {
            jLayeredPane2.setVisible(true);
            jLayeredPane1.setVisible(false);
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
        runCommand(new String[]{vboxdir,"list","hostonlyifs"},true,"updateIPInformation",true);
    }
    
    public void addServerInfoToServerList(){
        String tmp = (tmpServerName != null)?tmpServerName:"";
        
        for(int i = 0; i < commandOutput.size(); ++i){
            tmp += ","+commandOutput.get(i);
        }
        serverArray.add(tmp);
        serverListTree.setupServerModel();
        serverListTree.setVisible(true);
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
            runCommand(new String[]{vboxdir,"list","vms"},true,"setupVMList|"+server,true);
        } else {
            setupVBoxDir();
        }
    }
    
    public String getVBDir(){
        setupVBoxDir();
        return vboxdir;
    }
    
    public ArrayList<String> getServers(){
        return serverArray;
    }
    
    private void setupVMList(){
        for(int i = 0; i < commandOutput.size(); ++i){
            String serverInfo = commandOutput.get(i).split(" ")[0];
        }
    }

    private void readConfigInformation(){
        //setupVBoxDir();
        setupIPTable();
    }
    
    public void setCommandOutput(ArrayList<String> command){
        this.commandOutput = command;
    }
    
    public void runCommand(String[] command,Boolean showOutput,String function,Boolean showAfter) {
        worker = new CommandWorker(this,jLabel2,jLayeredPane1,command,showOutput,function);
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
        
        runCommand(vboxHolder,true,"setupIPTable",true);
    } 
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel4 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        jFileChooser1.setOpaque(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VBox Layout");
        setBackground(new java.awt.Color(0, 0, 0));
        setLocation(new java.awt.Point(50, 50));
        setResizable(false);

        jLayeredPane3.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLayeredPane3.setOpaque(true);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Loading...");

        jProgressBar1.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar1.setForeground(new java.awt.Color(0, 204, 51));
        jProgressBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jProgressBar1.setOpaque(true);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Beginning Download");

        jLayeredPane2.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jProgressBar1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setAlignmentX(0.0F);
        jLayeredPane1.setAlignmentY(0.0F);
        jLayeredPane1.setOpaque(true);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Submit");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Setup File");
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

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

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Add Adapter");
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Refresh IP List");
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("List of Adapters Currently Active for Virtual Box");

        jLayeredPane1.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap(375, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 349, Short.MAX_VALUE)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton2))
                .addContainerGap())
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(44, Short.MAX_VALUE)))
        );

        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Loading ...");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/information/loading_icon.png"))); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLayeredPane3.setLayer(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jLayeredPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(180, 180, 180)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(181, Short.MAX_VALUE)))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(73, 73, 73)
                    .addComponent(jLabel3)
                    .addContainerGap(73, Short.MAX_VALUE)))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jMenu2.setBorder(javax.swing.BorderFactory.createCompoundBorder());
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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane3)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane3)
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
    
    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        System.out.println("Taurean Wooley testing");
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
       config.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void addServer(String serverName,String serverIP,String serverMask,String serverGateway){
        
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

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        setupIPTable();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        processVboxmanage("create","null");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //read config file for rewriting
        readConfigFile();
        //setup vbox directory for processing processes
        setupVBoxDir();
        //run list hostonlyifs command from the VBoxManage.exe file or the /etc/init.d/vboxmanage process
        runCommand(new String[]{vboxdir,"list","hostonlyifs"},false,"setupIPTable",true);
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int n = JOptionPane.showConfirmDialog(
            this,
            "Do you really want to delete the IPAddress",
            "Delete Clicked IP Address",
            JOptionPane.YES_NO_OPTION);
        if(n == JOptionPane.YES_OPTION){
            String adapterName = ipAddresses.get(jTable1.getSelectedRow())[0];
            setupVBoxDir();
            runCommand(new String[]{vboxdir,"hostonlyif","remove","\""+adapterName.replace("HostInterfaceNetworking-","")+"\""},true,"setupIPTable",false); 
        }
    }//GEN-LAST:event_jTable1MouseClicked

    public String getVBoxDir(){
        return vboxdir;
    }
    
    public void getNewVMInformation(String vmname,String vmhdsize,String vmtype){
        newVMInformation = new String[]{vmname,vmhdsize,vmtype};
    }
    
    public void createVM(){
        runCommand(new String[]{vboxdir,"createvm","--name","\""+newVMInformation[0]+"_"+newVMInformation[2]+"\"","--ostype","\""+newVMInformation[2]+"\"","--register"},true,"setupVMBox",false);
    }
    
    public void setupVMBox(){
        String l = getClass().getResource("folder/comd").getFile();
        File commands = new File(l);
        try{
            BufferedReader out = new BufferedReader(new FileReader(commands));
            ArrayList<String[]> arraylist = new ArrayList<String[]>();
            File download = new File(getClass().getResource("folder/operating_systems").getFile());
            BufferedReader stream = new BufferedReader(new FileReader(download));
            String m;
            String downloadString = "";
            while((m = stream.readLine()) != null){
                if(m.contains(newVMInformation[2].toLowerCase())){
                    String[] check = m.split(",");
                    downloadString = check[1];
                }
            }
            
            if(downloadString != ""){
                String[] t = downloadString.split("/");
                downloadFile(downloadString,t[t.length-1]);
            }
            
            while((l = out.readLine()) != null){
                l = l.replace("[vmnamefile]",newVMInformation[0]+"_"+newVMInformation[2]);
                l = l.replace("[vmname]","\'"+newVMInformation[0]+"_"+newVMInformation[2]+"\'");
                l = l.replace("[vmhdsize]",newVMInformation[1]);
                l = l.replace("[vmtype_path_to_iso]",newVMInformation[2]+".iso");
                
                String[] commandLines  = l.split(" ");
                String[] mainCommandLine = new String[commandLines.length+1];
                                
                System.arraycopy(new String[]{vboxdir},0,mainCommandLine,0,1);
                System.arraycopy(commandLines,0,mainCommandLine,1,commandLines.length);
                
                arraylist.add(mainCommandLine);
            }
            commandList = arraylist;
            count = 0;
            
            runCommandsSync();
        } catch(IOException ex){
            System.out.println(ex);
        }
    }
    
    public void runCommandsSync(){
        if(count == commandList.size()){
            return;
        } else {
            runCommand(commandList.get(count),true,"runCommandsSync",false);
            ++count;
        }
    }
    
    private void createLinuxUpdate(String type){
        serverCreation.setServerType(type);
        vboxdir = vboxdir.replace("VBoxManage.exe","");
        vboxdir = vboxdir+"VBoxManage.exe";
        
        String[] vboxHolder;
        
        vboxHolder = new String[]{vboxdir,"createvm","--name","testing","--ostype","Linux","--register"};
        runCommand(vboxHolder,true,"createVM",true);
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
