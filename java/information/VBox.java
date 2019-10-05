 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author monkeyboz
 */
public class VBox extends javax.swing.JFrame {
    ArrayList<JFrame> testing = new ArrayList<>();
    
    private Color mainBackground;
    private Color darkBackground;
    private Color lightBackground;
    
    private String information;
    private Boolean[] reachable;
    private Integer selectedTableRow;
    private String vboxdir;
    private ConfigSettings config;
    private CreateServerConnection createServer;
    private ServerList serverListTree;
    private ArrayList<String> serverList;
    private VMServerCreationSystem serverCreation;
    private ArrayList<String> vmArray;
    private ArrayList<String> commandOutput;
    private ArrayList<String[]> ipAddresses;
    private CommandWorker worker;
    private JComponent loadingImage;
    private String tmpServerName;
    private String mostRecentVM;
    private Map<String,String> startedVM;
    
    private ServersList ServersList;
    
    private ArrayList<java.awt.Color> TabSelectColors;
    
    private String downloadDir;
    private String configDir;
    private String settingsDir;
    private String isoFile;
    private String virtualboxdir;
    private String osInformation;
    private String osType;
    private ArrayList<String> fixedHost;
    private int fixedCount;
    private Map<String,String> osFileInfo;
    
    private int count;
    private int serverProcessCount;
    private int downloadCount;
    private int[] mouseOrigin;
    
    private Boolean downloading;
    private Boolean configOK;
    private Boolean recreateDHCPServer;
    private JFrame updateDHCPFrameVisible;
    private JFrame readConfigVisible;
    
    private String[] newVMInformation;
    private ArrayList<String[]> commandList;
    private ArrayList<String> newServerList;
    private ArrayList<String> currentDownloads;
    private ArrayList<String> commandText;
    private RunThread run;
    
    private Map<String,String> mapServers;
    
    private Boolean setupIPAddress;
    final private Boolean connectionTimedout;
    
    SwingWorker worker1;
    
    public VBox() {
        selectedTableRow = 0;
        mainBackground = new java.awt.Color(25,25,25);
        darkBackground = new java.awt.Color(0,0,0);
        lightBackground = new java.awt.Color(51,51,51);
        
        initComponents();        
        initVariables();
        
        ArrayList<String[]> items = new ArrayList<>();
        items.add(new String[]{"File","Configure Settings"});
        items.add(new String[]{"Create","Server Creation System","Create Server Connection"});
        items.add(new String[]{"Help"});
        CustomMenu menu = new CustomMenu(items,mainBackground,lightBackground,TabSelectColors.get(1),this);
        
        menu.setBounds(200,0,400,100);
        jPanel3.add(menu, 0);
        
        menu.setParentComponentCount();
        connectionTimedout = false;
    }
    
    public void ConfigureSettings(){
        String[] str = config.setParent(this);
        configOK = setVBoxDir(str[0]);
        settingsDir = str[1];
        if(configOK){
            run = new RunThread();
            run.setParent(this);
            Thread newThread = new Thread(run);
            newThread.setDaemon(true);
            newThread.start();
            
            setupIPTable();
            this.setVisible(true);
        } else {
            this.setVisible(false);
            config.setVisible(true);
        }
    }
    
    public void ConfigureSettingsMenu(){
        config.setParent(this);
        config.setVisible(true);
    }
    
    public void ServerCreationSystemMenu(){
        serverCreation.setParent(this);
        serverCreation.setVisible(true);
    }
    
    public void CreateServerConnectionMenu(){
        createServer.setParent(this);
        createServer.setVisible(true);
    }
    
    private void initVariables(){
        Toolkit kit = Toolkit.getDefaultToolkit();
        System.out.println(this.getClass().getResource("/images/vmicon.png").getFile());
        Image img = kit.createImage(this.getClass().getResource("/images/vmicon.png").getFile());
        this.setIconImage(img);
        
        String userdir = System.getProperty("user.home");
        downloadDir = userdir+"\\Downloads\\";
        configDir = userdir+"\\VMManagerConfig\\";
        vboxdir = "C:\\Program Files\\Oracle\\VirtualBox\\";
        
        File configCheck = new File(configDir);
        if(!configCheck.isDirectory()){
            new File(configDir).mkdir();
        }
        
        config = new ConfigSettings(mainBackground);
        config.setVisible(false);
        config.setParent(this);
        reconfigurePlacement(config);
        
        fixedHost = new ArrayList<>();
        downloading = false;
        updateDHCPFrameVisible = new JFrame();
        readConfigVisible = new JFrame();
        isoFile = "";
        fixedCount = 0;
        
        TabSelectColors = new ArrayList<>();
        TabSelectColors.add(new java.awt.Color(25,25,25));
        TabSelectColors.add(new java.awt.Color(0,125,0));
                        
        jPanel1.setVisible(true);
        jPanel2.setVisible(false);
        
        jLayeredPane5.setVisible(true);
        
        OutputStream osOutput = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                osInformation += (char)b;
            }
        };
        
        PrintStream osPrinter = new PrintStream(osOutput);
        System.getProperties().list(osPrinter);
        
        String[] osInfo = osInformation.split("\n");
        StringBuilder builder = new StringBuilder("");
        builder.append("/folder/");
        for(int i = 0; i < osInfo.length; ++i){
            if(osInfo[i].contains("sun.desktop")){
                String[] checkInfo = osInfo[i].split("=");
                osType = checkInfo[1];
            }
        }
        
        String osCommands = "/folder/";
        osType = osType.trim();
        
        switch(osType){
            case "windows":
                osCommands+= "windows_cmds";
                break;
            case "linux":
                osCommands+= "linux_cmds";
                break;
            case "apple":
                osCommands+= "apple_cmds";
                break;
            default:
                osCommands+= "combinded_cmds";
                break;
        }
        
        recreateDHCPServer = false;
        osFileInfo = new HashMap<>();
        this.startedVM = new HashMap<>();
        
        System.out.println(osCommands);
        File osCommandFile = new File(this.getClass().getResource("/folder/tmpinstall").getFile());
        try{
            BufferedReader osReader = new BufferedReader(new FileReader(osCommandFile));
            String m = "";
            while((m = osReader.readLine()) != null){
                String[] infoHolder = m.split(":",2);
                if(infoHolder.length > 1){
                    osFileInfo.put(infoHolder[0], infoHolder[1]);
                }
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        
        setupIPAddress = false;
        this.setLocationRelativeTo(null);
        
        worker = new CommandWorker(this);
        
        commandOutput = new ArrayList<>();
        ipAddresses = new ArrayList<>();
        commandText = new ArrayList<>();
        
        createServer = new CreateServerConnection();
        createServer.setVisible(false);
        createServer.setParent(this);
        reconfigurePlacement(createServer);
        
        serverCreation = new VMServerCreationSystem();
        serverCreation.setVisible(false);
        serverCreation.setParent(this);
        reconfigurePlacement(serverCreation);
        
        ipTable.getModel().addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println(e.getFirstRow()+" "+e.getColumn());
            } 
        });
        
        serverList = new ArrayList<>();        
        vmArray = new ArrayList<>();
        ServersList = new ServersList(this);
        
        this.setBackground(java.awt.Color.BLACK);
        
        readInstallFile();
        
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
        
        ConfigureSettings();
        setupServerHome();
    }
    
    public JPanel getJPanel2(){
        return jPanel2;
    }
    
    public void jPanel2SetLayout(javax.swing.GroupLayout layout){
        jPanel2.setLayout(layout);
    }
    
    public Map<String,String> getOSCommands(){
        return osFileInfo;
    }
    
    public class RunThread implements Runnable{
        VBox parent;
        Boolean continueRun;
        @Override
        public void run() {
            while(continueRun){
                try {
                    Thread.sleep(100000);
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
    
    public String[] readConfigFileMain() throws FileNotFoundException, IOException{
        File configFile = new File(configDir+"configuration.vmm");
        if(!configFile.isFile()){
            configFile.createNewFile();
        }
        BufferedReader b = new BufferedReader(new FileReader(configDir+"configuration.vmm"));
        String m = " ";
        String[] holder = new String[2];
        int count = 0;
        while((m = b.readLine()) != null){
            holder[count] = m;
            ++count;
        }
        b.close();
        return holder;
    }
    
    public void writeConfigFileMain(String[] str) throws IOException{
        BufferedWriter b = new BufferedWriter(new FileWriter(configDir+"configuration.vmm",false));
        for(int i = 0; i < str.length; ++i){
            b.write(str[i]+"\r\n");
        }
        b.close();
    }
    
    public void setVBDir(String dirString,boolean configuration){
        try{
            File file = new File(configDir+"configuration.vmm");
            if(!file.isFile()){
                file.createNewFile();
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        this.ConfigureSettings();
    }
    
    public Map<String,String> compileServers(){
        Map<String,String> servers = new HashMap<>();
        try{
            Process process = Runtime.getRuntime().exec(new String[]{vboxdir,"list","vms"});
            BufferedReader vmreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String l = "";
            while((l = vmreader.readLine()) != null){
                Pattern m = Pattern.compile("\"(.*)\" \\{(.*)\\}");
                Matcher t = m.matcher(l);
                if(t.find()){
                    Process vmprocess = Runtime.getRuntime().exec(new String[]{vboxdir,"showvminfo","\""+t.group(1)+"\""});
                    BufferedReader mb = new BufferedReader(new InputStreamReader(vmprocess.getInputStream()));
                    String v = "";
                    while((v = mb.readLine()) != null){
                        if(v.contains("Host-only Interface")){
                            for(int i = 0; i < ipAddresses.size(); ++i){
                                String interfaceName = ipAddresses.get(i)[0].replace("HostInterfaceNetworking-","");
                                if(v.contains(interfaceName)){
                                    if(servers.get(interfaceName) == null){
                                        if(t.group(0) != null){
                                            servers.put(interfaceName,t.group(1));
                                        }
                                    } else {
                                        servers.put(interfaceName,servers.get(interfaceName)+"|"+t.group(1));
                                    }
                                }
                            }
                        }
                    }
                    servers.put(t.group(1),t.group(1));
                }
            }
        }catch(IOException ex){
            System.out.println("Error getting servers "+ex);
        }
        return servers;
    }
    
    public void setTempServerName(String serverName){
        tmpServerName = serverName;
    }
    
    public void setVirtualboxdir(String virtualboxdir){
        this.virtualboxdir = virtualboxdir+"\\";
    }
    
    public void setupServerHome(){
        serverList = new ArrayList<>();
        serverList.add("Home");
        runCommand(new String[]{vboxdir,"list","vms"},true,"addServerInfoToServerList",true,"Listing VMs");
    }
    
    public void setupServerHome(Boolean displayFrame){
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
            File file = new File(configDir+"resume.vmm");
            if(!file.isFile()){
                file.createNewFile();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(configDir+"resume.vmm",true));
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
        newServerList = new ArrayList<>();
        currentDownloads = new ArrayList<>();
        try{
            File file = new File(configDir+"resume.vmm");
            if(!file.isFile()){
                file.createNewFile();
            }
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

    public Boolean setVBoxDir(String vbox){
        if(vbox != null){
            vbox = vbox.replace("VBoxManage.exe","");
            vbox = vbox+"\\VBoxManage.exe";
            System.out.println(vbox);
            File file = new File(vbox);
            if(!file.isFile()){
                return false;
            }else{
                vboxdir = vbox;
                return true;
            }
        } else {
            return false;
        }
    }
    
    public void setISO(String isoFile){
        this.isoFile = isoFile;
    }
    
    private void readConfigFile(){
        readConfigVisible = new JFrame();
        readConfigVisible.add(jFileChooser1);

        readConfigVisible.setBounds(20,20,400,400);
        readConfigVisible.setLocationRelativeTo(null);

        jFileChooser1.addActionListener((java.awt.event.ActionEvent evt) -> {
            if("CancelSelection".equals(evt.getActionCommand())){
                readConfigVisible.setVisible(false);
            } else {
                File selectedFile = jFileChooser1.getSelectedFile();
                if(selectedFile != null){
                    information = selectedFile.getAbsolutePath();
                    readConfigInformation();
                    readConfigVisible.dispose();
                }
            }
        });
        readConfigVisible.setVisible(true);
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
            
            SwingWorker rework = new SwingWorker<String,Void>(){
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
                                //extractFile(downloadDir+target);
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
                        connection.setConnectTimeout(50000);
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
                            System.out.println("Information: "+ex);
                        }
                        return "restarting process";
                    }
                    
                    if(target.contains(".7z")){
                        //extractFile(downloadDir+target);
                    }
                    currentDownloads.remove(currentDownload);
                    writeInstallFile();
                    return "Finished...";
                }
                
                @Override
                protected void done(){
                    
                }
            };
            rework.execute();
        }
    }
    
    public String getConfigDir(){
        return configDir;
    }
    
    private void setupIPRows() throws UnknownHostException, IOException{
        ArrayList commandList = new ArrayList<String>();
        runCommand(new String[]{vboxdir,"list","hostonlyifs"},false,"updateIPInformation",false,"Listing Host Only Adapters");
    }
    
    public void addInfoToServerProcess(){
        String tmp = (tmpServerName != null)?tmpServerName:"";
        
        for(int i = 0; i < commandOutput.size(); ++i){
            tmp += ","+commandOutput.get(i);
        }
        vmArray.add(tmp);
        processPreviousInstalls();
    }
    
    public void addServerInfoToServerList(){
        addInfoToServerProcess();
        ServersList.setupServerModel();
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
            if(!configOK){
                config.setVisible(true);
                config.toFront();
            }else{
                jLayeredPane5.setVisible(true);
                setupIPRows();
            }
        } catch(IOException ex){
            System.out.println("IP table Iusse");
        }
    }
    
    private void readVMsForServer(String server){
        if(server.contains("main")){
            runCommand(new String[]{vboxdir,"list","vms"},true,"setupVMList",true,"List VM's");
        }
    }
    
    public String getVBDir(){
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
        String[] vboxHolder;
        
        switch(process){
            case "remove":
                vboxHolder = new String[]{vboxdir,"hostonlyif","remove","\""+attribute+"\""};
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
        jPanel3 = new javax.swing.JPanel();
        AdapterTab = new javax.swing.JPanel();
        AdapterTabText = new javax.swing.JLabel();
        ServerTab = new javax.swing.JPanel();
        ServerTabText = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JLayeredPane();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ipTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        jLayeredPane5 = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();

        jFileChooser1.setOpaque(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VBox Layout");
        setBackground(new java.awt.Color(0, 0, 0));
        setLocation(new java.awt.Point(50, 50));
        setMinimumSize(new java.awt.Dimension(801, 582));
        setUndecorated(true);
        setResizable(false);

        jPanel3.setBackground(new java.awt.Color(25, 25, 25));

        AdapterTab.setBackground(new java.awt.Color(0, 153, 0));
        AdapterTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AdapterTabMousePressed(evt);
            }
        });

        AdapterTabText.setBackground(new java.awt.Color(0, 153, 0));
        AdapterTabText.setForeground(new java.awt.Color(255, 255, 255));
        AdapterTabText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AdapterTabText.setText("DHCP Adapters");
        AdapterTabText.setAlignmentX(0.5F);
        AdapterTabText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AdapterTabText.setIconTextGap(90);
        AdapterTabText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                AdapterTabTextMousePressed(evt);
            }
        });

        javax.swing.GroupLayout AdapterTabLayout = new javax.swing.GroupLayout(AdapterTab);
        AdapterTab.setLayout(AdapterTabLayout);
        AdapterTabLayout.setHorizontalGroup(
            AdapterTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AdapterTabText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
        );
        AdapterTabLayout.setVerticalGroup(
            AdapterTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AdapterTabLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AdapterTabText)
                .addContainerGap())
        );

        ServerTab.setBackground(new java.awt.Color(25, 25, 25));
        ServerTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ServerTabMouseClicked(evt);
            }
        });

        ServerTabText.setBackground(new java.awt.Color(0, 153, 0));
        ServerTabText.setForeground(new java.awt.Color(255, 255, 255));
        ServerTabText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ServerTabText.setText("Servers");
        ServerTabText.setAlignmentX(0.5F);
        ServerTabText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ServerTabText.setIconTextGap(90);
        ServerTabText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ServerTabTextMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout ServerTabLayout = new javax.swing.GroupLayout(ServerTab);
        ServerTab.setLayout(ServerTabLayout);
        ServerTabLayout.setHorizontalGroup(
            ServerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerTabLayout.createSequentialGroup()
                .addComponent(ServerTabText, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ServerTabLayout.setVerticalGroup(
            ServerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ServerTabLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ServerTabText)
                .addContainerGap())
        );

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("close");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jPanel4.setMaximumSize(new java.awt.Dimension(369, 32));
        jPanel4.setMinimumSize(new java.awt.Dimension(369, 32));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel4MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AdapterTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ServerTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ServerTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AdapterTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLayeredPane3.setPreferredSize(new java.awt.Dimension(801, 582));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setMaximumSize(new java.awt.Dimension(801, 582));
        jPanel2.setMinimumSize(new java.awt.Dimension(801, 582));
        jPanel2.setPreferredSize(new java.awt.Dimension(801, 582));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 801, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 582, Short.MAX_VALUE)
        );

        jPanel1.setMaximumSize(new java.awt.Dimension(801, 582));
        jPanel1.setMinimumSize(new java.awt.Dimension(801, 582));
        jPanel1.setPreferredSize(new java.awt.Dimension(801, 582));

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        jLayeredPane1.setAlignmentX(0.0F);
        jLayeredPane1.setAlignmentY(0.0F);
        jLayeredPane1.setMaximumSize(new java.awt.Dimension(801, 582));
        jLayeredPane1.setMinimumSize(new java.awt.Dimension(801, 582));
        jLayeredPane1.setOpaque(true);

        jLayeredPane4.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jLayeredPane4Layout = new javax.swing.GroupLayout(jLayeredPane4);
        jLayeredPane4.setLayout(jLayeredPane4Layout);
        jLayeredPane4Layout.setHorizontalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        jLayeredPane4Layout.setVerticalGroup(
            jLayeredPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Current Running Commands:");

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Add Adapter");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
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
        jLayeredPane2.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(166, Short.MAX_VALUE))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addGap(0, 86, Short.MAX_VALUE)
                .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(40, Short.MAX_VALUE)))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel6)
                    .addContainerGap(84, Short.MAX_VALUE)))
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        ipTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        ipTable.setModel(new javax.swing.table.DefaultTableModel(
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
        ipTable.setToolTipText("testing");
        ipTable.setColumnSelectionAllowed(true);
        ipTable.setGridColor(new java.awt.Color(51, 51, 51));
        ipTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
        ipTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ipTable.getTableHeader().setReorderingAllowed(false);
        ipTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ipTableMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(ipTable);
        ipTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (ipTable.getColumnModel().getColumnCount() > 0) {
            ipTable.getColumnModel().getColumn(0).setResizable(false);
        }

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("List of Adapters Currently Active for Virtual Box");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(" ");

        jProgressBar1.setBackground(new java.awt.Color(255, 255, 255));
        jProgressBar1.setForeground(new java.awt.Color(0, 204, 51));
        jProgressBar1.setOpaque(true);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(" ");

        jLayeredPane1.setLayer(jLayeredPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jProgressBar1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel5, javax.swing.JLayeredPane.DEFAULT_LAYER);

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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 65, Short.MAX_VALUE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayeredPane4)
                        .addGap(33, 33, 33))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 801, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 582, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jLayeredPane5.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane5.setMaximumSize(new java.awt.Dimension(801, 582));
        jLayeredPane5.setMinimumSize(new java.awt.Dimension(801, 582));
        jLayeredPane5.setOpaque(true);

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Running IP Table Update ...");

        jLayeredPane5.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane5Layout = new javax.swing.GroupLayout(jLayeredPane5);
        jLayeredPane5.setLayout(jLayeredPane5Layout);
        jLayeredPane5Layout.setHorizontalGroup(
            jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
        );
        jLayeredPane5Layout.setVerticalGroup(
            jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane5Layout.createSequentialGroup()
                .addGap(257, 257, 257)
                .addComponent(jLabel3)
                .addContainerGap(311, Short.MAX_VALUE))
        );

        jLayeredPane3.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane3.setLayer(jLayeredPane5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane3Layout = new javax.swing.GroupLayout(jLayeredPane3);
        jLayeredPane3.setLayout(jLayeredPane3Layout);
        jLayeredPane3Layout.setHorizontalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLayeredPane5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane3Layout.setVerticalGroup(
            jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane3Layout.createSequentialGroup()
                .addComponent(jLayeredPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)))
            .addGroup(jLayeredPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 583, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)))
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
                "IP Address", "Network Type","Connected","Servers"
            };
        for(int i = 0; i < adapterNameArray.size();++i){
            ipAddresses.add(new String[]{adapterNameArray.get(i),ipAddressArray.get(i)});
        }
        
        ArrayList<String[]> node = new ArrayList<>();
        for(int i = 0; i < ipAddresses.size(); ++i){
            node.add(new String[]{ipAddresses.get(i)[1],ipAddresses.get(i)[0]});
        }
        
        //updateDHCP();
        
        int total_host_only = 0;
        String servers = new String();
        mapServers = compileServers();
        
        for(int i = 0; i < node.size(); ++i){
            String[] elm = node.get(i);
            ++total_host_only;
            try{
                InetAddress inet = InetAddress.getByName(elm[0]);
                String f = (inet.isReachable(5000))?"true":"false";
                
                elm[1] = elm[1].replace("HostInterfaceNetworking-","");
                String[] obj_holder = {elm[0],elm[1].replace("VirtualBox Host-Only",""),f,mapServers.get(elm[1])};
                obj.add(obj_holder);
            } catch(UnknownHostException ex){
                System.out.println("UnknownHostException "+ex);
            } catch(IOException ex){
                System.out.println("IOException "+ex);
            }
        }   
        
        Object[][] string = new Object[obj.toArray().length][];
        
        for(int i = 0; i < obj.toArray().length; ++i){
            String[] info = obj.get(i);
            string[i] = info;
        }
        
        ipTable.setModel(new javax.swing.table.DefaultTableModel(
                string,str
        ));
        ipTable.repaint();
        
        reachable = new Boolean[string.length];
        jLabel1.setText("List of Adapters Currently Active for Virtual Box ("+ipAddresses.size()+")");
        jLayeredPane5.setVisible(false);
    }
    
    public ArrayList<String[]> getIPAddresses(){
        return ipAddresses;
    }
    
    public void createHostOnlyDHCP(){
        if(recreateDHCPServer){
            --fixedCount;
            if(fixedCount == 0){
                recreateDHCPServer = false;
            }
        }
        
        runCommand(new String[]{vboxdir,"hostonlyif","create"},true,"updateDHCPServer,testing,testing,testing",false,"Creating New Adapter");
    }
    
    private void updateDHCPServer(String adapterName, String ipAddress) throws IOException{
            String adapterInfo = adapterName.replace("HostInterfaceNetworking-","");
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    try{
                        Process process = Runtime.getRuntime().exec(new String[]{vboxdir,"list","dhcpservers"});
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String string = "";
                        
                        ArrayList adapters = new ArrayList<>();
                        while((string = br.readLine()) != null){
                            if(string.contains("NetworkName: ")){
                                String[] info = string.split(":    ");
                                adapters.add(info[1]);
                            }
                        }
                        
                        Boolean adapterAdded = false;
                        for(int i = 0; i < adapters.size(); ++i){
                            if(adapterName.contains(adapters.get(i).toString())){
                                String[] s = new String[]{vboxdir,"dhcpserver","modify","--netmask","255.255.255.0","--netname","\""+adapterName+"\"","--ip",ipAddress,"--upperip",ipAddress.substring(0,ipAddress.lastIndexOf('.')+1)+"254","--lowerip",ipAddress.substring(0,ipAddress.lastIndexOf('.')+1)+"101","--enable"};
                                Process dhcpProcess = Runtime.getRuntime().exec(s);
                                
                                for(String v : s){
                                    System.out.print(v+" ");
                                }
                                System.out.println();
                                
                                adapterAdded = true;
                                break;
                            }
                        }
                        
                        if(!adapterAdded){
                            Process dhcpserver = Runtime.getRuntime().exec(new String[]{vboxdir,"dhcpserver","add","--netmask","255.255.255.0","--netname",adapterName,"--ip",ipAddress,"--lowerip",ipAddress.substring(0,ipAddress.lastIndexOf('.')+1)+"101","--upperip",ipAddress.substring(0,ipAddress.lastIndexOf('.')+1)+"255"});
                            BufferedReader bh = new BufferedReader(new InputStreamReader(dhcpserver.getInputStream()));
                            String str = "";
                        }
                    }catch(IOException ex){
                        System.out.println(ex);
                    }
                    return "";
                }
               
                @Override
                public void done(){
                    
                }
            };
            worker.execute();
    }
    
    private void removeDHCP(String adapterName){
        try{
            String[] testing = new String[]{vboxdir,"dhcpserver","remove","\""+adapterName+"\""};
            Process dhcpProcess = Runtime.getRuntime().exec(new String[]{vboxdir,"dhcpserver","remove","\""+adapterName+"\""});
            BufferedReader br = new BufferedReader(new InputStreamReader(dhcpProcess.getInputStream()));
        }catch(IOException ex){
            System.out.println("Exception info: "+ex);
        }
    }
    
    private void updateDHCP(){
        ArrayList<SwingWorker> workerDHCP = new ArrayList<>();
        for(int i = 0; i < ipAddresses.size(); ++i){
            String dhcp;
            Boolean removeIP = false;
            String ipaddressName = ipAddresses.get(i)[0];
            String ipaddress = ipAddresses.get(i)[1];
            if(ipAddresses.get(i)[1].contains("169.254")){
                Boolean testReset = false;
                if(fixedHost.size() > 0){
                    for(int u = 0; u < fixedHost.size(); ++u){
                        if(fixedHost.get(u).equals(ipaddressName)) {
                            testReset = true;
                        } else {
                        }
                    }
                }
                
                if(!testReset){
                    fixedHost.add(ipaddressName);
                    recreateDHCPServer = true;
                    ++fixedCount;
                    removeIP = true;

                    dhcp = "dhcpserver remove --netname \"[hostonlyname]\"";
                    dhcp = dhcp.replace("[hostonlyname]",ipaddressName.replace("HostInterfaceNetworking-",""));
                } else {
                    dhcp = "";
                }
            }
            
            try{
                if(removeIP){
                    removeAdapter(ipaddressName,ipaddress);
                } else {
                    updateDHCPServer(ipaddressName,ipaddress);
                }
            }catch(IOException ex){
                System.out.println("something ... "+ex);
            }
        }
    }
    
    public void addServer(String serverName,String serverIP,String serverMask,String serverGateway){
        System.out.println(serverName);
    }
        
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        setupIPTable();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        createHostOnlyDHCP();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void removeAdapter(String hostName,String dhcp){
        runCommand(new String[]{vboxdir,"hostonlyif","remove","\""+hostName.replace("HostInterfaceNetworking-","")+"\""},true,"removeDHCPServer,"+hostName,true,"Removing Host-Only Adapter");
    }
    
    public void removeDHCPServer(String t){
        String continuedFunction;
        
        if(recreateDHCPServer){
            continuedFunction = "createHostOnlyDHCP";
        } else {
            continuedFunction = "testing";
        }
        
        runCommand(new String[]{vboxdir,"dhcpserver","remove","--netname","\""+t+"\""},true,continuedFunction,true,"Delete registered DCHP Server");
    }
        
    private void ipTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ipTableMousePressed
        selectedTableRow = ipTable.getSelectedRow();
        ipTable.getSelectionModel().clearSelection();
        showDHCPFrame();
    }//GEN-LAST:event_ipTableMousePressed
    
    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel7MouseClicked

    private void AdapterTabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdapterTabMousePressed
        tabSelect(1);
    }//GEN-LAST:event_AdapterTabMousePressed

    private void AdapterTabTextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AdapterTabTextMousePressed
        tabSelect(1);
    }//GEN-LAST:event_AdapterTabTextMousePressed

    private void ServerTabTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ServerTabTextMouseClicked
        tabSelect(0);
    }//GEN-LAST:event_ServerTabTextMouseClicked

    private void ServerTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ServerTabMouseClicked
        tabSelect(0);
    }//GEN-LAST:event_ServerTabMouseClicked

    private void jPanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseEntered
        
    }//GEN-LAST:event_jPanel4MouseEntered

    private void tabSelect(int option){
        int deselect = 1;
        if(option == 1){
            deselect = 0;
            jPanel1.setVisible(true);
            jPanel2.setVisible(false);
        } else {
            jPanel1.setVisible(false);
            jPanel2.setVisible(true);
        }
        
        AdapterTab.setBackground(TabSelectColors.get(option));
        ServerTab.setBackground(TabSelectColors.get(deselect));
    }
    
    private JComboBox getServerList(){
        JComboBox serverSelect = new JComboBox();
        try{
            mapServers.keySet().forEach((str) -> {
                serverSelect.addItem(mapServers.get(str));
            });
        }catch(Exception ex){
            System.out.println(ex);
        }
        return serverSelect;
    }
    
    private void showDHCPFrame(){
        updateDHCPFrameVisible.dispose();
        updateDHCPFrameVisible = new JFrame("Select Server "+ipAddresses.get(selectedTableRow)[0].replace("HostInterfaceNetworking-Virtual",""));
        
        JPanel jpanel = new JPanel();
        
        JLayeredPane layeredPane = new JLayeredPane();
        JComboBox serverSelect = getServerList();
        JTextField ipAddress = new JTextField();
        Dimension itemD = new Dimension(525,25);
        ipAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(254,254,254)));
        serverSelect.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(254,254,254)));
        serverSelect.setPreferredSize(itemD);
        ipAddress.setPreferredSize(itemD);
        
        jpanel.add(ipAddress);
        jpanel.add(serverSelect);

        JButton submitButton = new JButton("Set Server DHCP");
        JButton startButton = new JButton("Start Server");
        JButton setupDHCPButton = new JButton("Update DHCP");
        JButton removeButton = new JButton("Remove DHCP");
        JPanel buttons = new JPanel();
        JLayeredPane closeContainer = new JLayeredPane();
        JLabel closeText = new JLabel();
        JLabel textInfo = new JLabel();
        buttons.add(submitButton);
        buttons.add(startButton);
        buttons.add(setupDHCPButton);
        buttons.add(removeButton);
        
        closeText.setText("close");
        //closeText.setIcon(new ImageIcon(getClass().getResource("/resources/images/close_button.png").getFile()));
                
        ipAddress.setText(ipAddresses.get(selectedTableRow)[1]);
        
        textInfo.setText("Updating "+ipAddresses.get(selectedTableRow)[0].replace("HostInterfaceNetworking-",""));
        textInfo.setForeground(new java.awt.Color(244,244,244));
        textInfo.setBounds(10,-10,300,50);
        
        closeContainer.add(closeText);
        closeContainer.add(textInfo);
        
        removeButton.addActionListener((ActionEvent e) -> {
            removeAdapter(ipAddresses.get(selectedTableRow)[0],ipAddresses.get(selectedTableRow)[1]);
        });
        
        updateDHCPFrameVisible.addMouseListener(new MouseAdapter(){
            public void mousepressed(){
                updateDHCPFrameVisible.dispose();
            }
        });
        
        updateDHCPFrameVisible.addMouseMotionListener(new MouseAdapter(){
            @Override
            public void mouseDragged(MouseEvent evt) {
                updateDHCPFrameVisible.setBounds(evt.getX()+updateDHCPFrameVisible.getX()-mouseOrigin[0],evt.getY()+updateDHCPFrameVisible.getY()-mouseOrigin[1],updateDHCPFrameVisible.getWidth(),updateDHCPFrameVisible.getHeight());
            }
        });
        
        VBox parent = this;
        updateDHCPFrameVisible.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                mouseOrigin = new int[]{e.getX(),e.getY()};
            }
        });
        
        closeText.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                updateDHCPFrameVisible.dispose();
            }
        });
        
        Color backgroundColor = new java.awt.Color(54,54,54);
        
        closeText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        closeText.setForeground(new java.awt.Color(240,240,240));
        closeText.setBounds(440,0,100,30);
        closeContainer.setBounds(0,0,550,30);
        jpanel.setBounds(0,30,550,70);
        buttons.setBounds(0,100,550,50);
        
        jpanel.setBackground(backgroundColor);
        buttons.setBackground(backgroundColor);
        closeContainer.setBackground(backgroundColor);
        closeContainer.setOpaque(true);
        closeContainer.setVisible(true);
        
        layeredPane.setOpaque(true);
        
        layeredPane.add(closeContainer);
        layeredPane.add(jpanel);
        layeredPane.add(buttons);
        layeredPane.setBounds(30,0,550,200);
        
        updateDHCPFrameVisible.setUndecorated(true);
        updateDHCPFrameVisible.add(layeredPane);
        
        startButton.addActionListener((ActionEvent e) -> {
            String server = serverSelect.getSelectedItem().toString();
            try{
                Process process = Runtime.getRuntime().exec(new String[]{vboxdir,"startvm","\""+server+"\""});
                startVM();
            }catch(IOException ex){
                System.out.println(ex);
            }
            setupIPTable();
            updateDHCPFrameVisible.dispose();
        });
        
        submitButton.addActionListener((ActionEvent e) -> {
            String ip = ipAddress.getText();
            String[] updateIPAddress = new String[]{vboxdir,"dhcpserver","modify","--netmask","255.255.255.0","--netname","\""+ipAddresses.get(selectedTableRow)[0].replace("HostInterfaceNetworking-","")+"\"","--ip",ip,"--upperip",ip.substring(ip.lastIndexOf('.')+1)+"101","--lowerip",ip.substring(ip.lastIndexOf('.')+1)+"101","--enable"};
            String[] pauseServer = new String[]{vboxdir,"controlvm","\""+serverSelect.getSelectedItem()+"\"","pause"};
            //String[] saveServer = new String[]{vboxdir,"controlvm","\""+serverSelect.getSelectedItem()+"\"","savestate"};
            String[] setupServerAdapter = new String[]{vboxdir,"modifyvm","\""+serverSelect.getSelectedItem()+"\"","--nic2","hostonly","--hostonlyadapter2","\""+ipAddresses.get(selectedTableRow)[0].replace("HostInterfaceNetworking-","")+"\""};
            String[] startServer = new String[]{vboxdir,"startvm","\""+serverSelect.getSelectedItem()+"\""};
            try{
                //Runtime.getRuntime().exec(saveServer);
                Process process = Runtime.getRuntime().exec(pauseServer);
                BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
                
                String sl = "";
                
                process = Runtime.getRuntime().exec(setupServerAdapter);
                process = Runtime.getRuntime().exec(startServer);
                updateIPAddress(ip,selectedTableRow);
                String string = "";
            }catch(IOException ex){
                System.out.println(ex);
            }
            setupIPTable();
            updateDHCPFrameVisible.dispose();
        });

        setupDHCPButton.addActionListener((ActionEvent e) -> {
            String ip = ipAddress.getText();
            ipAddresses.get(selectedTableRow)[1] = ip;
            updateIPAddress(ip,selectedTableRow);
            setupIPTable();
            updateDHCPFrameVisible.dispose();
        });
        
        updateDHCPFrameVisible.setBounds(0,0,550,150);
        updateDHCPFrameVisible.setLocationRelativeTo(null);
        updateDHCPFrameVisible.setVisible(true);
    }
    
    public void updateIPAddress(String ip,int ipIndex){
        String[] s = new String[]{vboxdir,"dhcpserver","modify","--netmask","255.255.255.0","--netname","\""+ipAddresses.get(ipIndex)[0]+"\"","--ip",ip,"--upperip",ip.substring(0,ip.lastIndexOf('.')+1)+"254","--lowerip",ip.substring(0,ip.lastIndexOf('.')+1)+"101","--enable"};
        String[] m = new String[]{vboxdir,"hostonlyif","ipconfig","\""+ipAddresses.get(ipIndex)[0].replace("HostInterfaceNetworking-","")+"\"","--ip",ip};
        for(String g : m){
            System.out.print(g+" ");
        }
        System.out.println();
        try{
            Process process = Runtime.getRuntime().exec(m);
            process = Runtime.getRuntime().exec(s);
            BufferedReader bh = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String v = " ";
            while((v = bh.readLine()) != null){
                System.out.println(v);
            }
            System.out.println(ip);
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    
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
        setupVMBox("/folder/install",true);
    }
    
    public void setupVMBox(String commandFile,Boolean isoSystem){
        if(commandFile == "/folder/install"){
            File t = new File(this.getClass().getResource("/folder/tmpinstall").getFile());
            try{
                BufferedWriter bm = new BufferedWriter(new FileWriter(t));
                BufferedReader br = new BufferedReader(new FileReader(this.getClass().getResource(commandFile).getFile()));
                
                String reader = "";
                while((reader = br.readLine()) != null){
                    reader = reader.replace("[vmname]",newVMInformation[0]+"_"+newVMInformation[2]);
                    reader = reader.replace("[osType]",newVMInformation[2]);
                    bm.write(reader+"\r\n");
                }
                br = new BufferedReader(new FileReader(this.getClass().getResource("/folder/comd").getFile()));
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
        String v = this.getClass().getResource(commandFile).getFile();
        File commands = new File(v);
        
        try{
            BufferedReader out = new BufferedReader(new FileReader(commands));
            ArrayList<String[]> arraylist = new ArrayList<>();
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
                File download = new File(this.getClass().getResource("/folder/operating_systems").getFile());
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
        } else {
            runCommand(commandList.get(count),true,"runCommandsSync",true,commandText.get(count));
            ++count;
        }
    }
    
    private void createLinuxUpdate(String type){
        serverCreation.setServerType(type);        
        String[] vboxHolder;
        
        vboxHolder = new String[]{vboxdir,"createvm","--name","testing","--ostype","Linux","--register"};
        runCommand(vboxHolder,true,"createVM",true,"Creating VMs");
    }
    
    public void setStartedVM(String startedVM){
        mostRecentVM  = startedVM;
        this.startedVM.put(startedVM,"started");
    }
    
    public Map<String,String> getStartedVM(){
        return startedVM;
    }
    
    public void startVM(){
        VMStarted vmstarted = new VMStarted(this,mostRecentVM);
        vmstarted.setVisible(true);
        vmstarted.setLocationRelativeTo(null);
    }
    
    public int setupConfigField(String text,int i){
        config.setupField(text,i);
        return i;
    }
    
    public void setupConfigError(int i){
        config.showErrors(i);
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
            VBox vbox = new VBox();
            vbox.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AdapterTab;
    private javax.swing.JLabel AdapterTabText;
    private javax.swing.JPanel ServerTab;
    private javax.swing.JLabel ServerTabText;
    private javax.swing.JTable ipTable;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLayeredPane jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
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