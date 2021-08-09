/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newpackage;

/**
 *
 * @author student
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.JFrame;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//this is for the filechooser, making sure it choose files with .labpack
class MyCustomFilter extends javax.swing.filechooser.FileFilter {
    @Override
        public boolean accept(java.io.File file) {
            // Allow only directories, or files with ".labpack" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".labpack");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Text documents (*.labpack)";
        }
}


public class NewJFrame extends javax.swing.JFrame {
private ArrayList<java.io.File> filelist = new ArrayList<java.io.File>(); //this is for storing the list of existing labs to look for

String labdir = System.getenv("LABTAINER_DIR");
private  String labpack_path = labdir + java.io.File.separator + "labpacks";//making a String name that defines the path to labpacks directory
    
private static java.util.HashMap<String, String> labnotes = new java.util.HashMap<String, String>();
    /**
     * Creates new form NewJFrame
     */
     private static java.util.HashMap<String, String> labpack = new java.util.HashMap<String, String>();//making a dictionary to storing labpack contents the the UI currently shows
     
     public void sorting(javax.swing.JList list){ //this is for sorting the elements in list models alphabetically
        javax.swing.ListModel model= list.getModel();
        int n = model.getSize();
        
        String [] data = new String[n];
        
        for(int i=0; i<n; i++){
            data[i] = (String) model.getElementAt(i);
            
            
           
        }
        Arrays.sort(data);
        
        list.setListData(data);
        
    }
     //refresh is for the lablist model so the list of labs can be refreshed after clicking the clear button
     public void refresh(javax.swing.DefaultListModel<String> mode){
        mode.clear();
        String labdir = System.getenv("LABTAINER_DIR");
        
        
        String labpath = labdir + java.io.File.separator + "labs";
        
        java.io.File path = new java.io.File(labpath);
        String contents[] = path.list();
        for(int i=0; i<contents.length; i++) {
            
            mode.addElement(contents[i]);
            
            }
        sorting(lablist);
    }
     private void savepackname(String packname){
        try{
            java.io.FileWriter writer = new java.io.FileWriter("/tmp/packname.txt");
            writer.write(packname);
            writer.close();
        } catch(IOException ex) {
            System.out.println("problem accessing file packname.txt");
        }
        
        //saves packname into /tmp/packname.txt
    }
     private boolean SomethingChanged(){
        saving(java.io.File.separator+"tmp");
        String name1 = labpack.get("name");
        String pathtmp = "/tmp"+java.io.File.separator+ name1 + ".labpack";
        java.io.File filetmp = new java.io.File(pathtmp);
        
        boolean value = false;
        
        
        String labpath = labpack_path;
        String pathpack = labpath+java.io.File.separator+java.io.File.separator+ name1 + ".labpack";
        java.io.File filepack = new java.io.File(pathpack);
        try{
                java.util.Scanner Freader = new java.util.Scanner(filepack);
                java.util.Scanner Treader = new java.util.Scanner(filetmp);
                while (Freader.hasNextLine()&& Treader.hasNextLine()) {
                    String line1 = Freader.nextLine().trim();
                    
                    String line2 = Treader.nextLine().trim();
                    System.out.println(line1);
                    System.out.println(line2);
                    
                    if(!line1.equals(line2)){
                        value = true;
                        System.out.println("I found a difference");
                        break;
                    }
                }
            }catch (java.io.FileNotFoundException e) {
                
                //e.printStackTrace();
            }
        
        return value;
        
    }
    
  

    //DoesOPEN is a method called when we want to open a jsonFile labpack and display it in the UI
    private void DoesOPEN(java.io.File file){
        
        labsadded.clear();
        labnotes.clear();
        try {
          // What to do with the file, e.g. display it in a TextArea
          
          java.util.Scanner input = new java.util.Scanner(file);

          while (input.hasNextLine())
          {
            System.out.println(input.nextLine());
          }       
          //textarea.read( new java.io.FileReader( file.getAbsolutePath() ), null );
          JSONParser jsonparser = new JSONParser();
          FileReader Reader = new FileReader(file.getAbsolutePath());
          Object obj = jsonparser.parse(Reader);
          JSONObject packobj = (JSONObject)obj;
          
          String packname = (String) packobj.get("name");
          this.setTitle("makepack: "+packname);
          labpack.put("name", packname);
          
          if(packobj.containsKey("order")){
            long order = (long) packobj.get("order");
            String Order =Long.toString(order);
            labpack.put("order", Order);
          }
          String description = (String) packobj.get("description");
          labpack.put("description", description);
          JSONArray array = (JSONArray) packobj.get("labs");
          
          
          for(int i =0; i<array.size();i++){
              JSONObject lab = (JSONObject) array.get(i);
              String name = (String) lab.get("name");
              String notes = (String) lab.get("notes");
              labnotes.put(name, notes);
              labsadded.addElement(name);
          }
          
          
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
          //System.out.println("problem accessing file"+file.getAbsolutePath());
        }
        catch(ParseException e) {
            //e.printStackTrace();
        }
    
        //System.out.println("File access cancelled by user.");
        
    
    }
    
     private void saving(String path){
        //save the labpack given by the path
        java.util.List<Object> labs = new ArrayList<Object>();
        java.util.Set<String> keys = labnotes.keySet();
        java.util.List<String> listKeys = new ArrayList<String>(keys);
        for(int i =0; i<labnotes.size(); i++){
            java.util.HashMap<String, String> labdes = new java.util.HashMap<String, String>();
            labdes.put("name",labsadded.getElementAt(i));
            labdes.put("notes",labnotes.get(labsadded.getElementAt(i)));
            labs.add(labdes);
        }
        JSONObject Objects = new JSONObject();

        Objects.put("name",labpack.get("name"));
        Objects.put("labs", labs);
        Objects.put("description",labpack.get("description"));
        if (labpack.containsKey("order")){
        Objects.put("order",Long.parseLong(labpack.get("order")));
        }
        try {
         FileWriter file = new FileWriter(path+java.io.File.separator+labpack.get("name")+".labpack");
         file.write(Objects.toJSONString());
         file.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
        System.out.println("JSON file created: "+Objects);
    }   
    
    //Right as we close out of the frame, a method will be called to check if something changed or a new labpack was created.
     //the dialog will ask if you want to save.
    private void CloseWindow(){
         this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saving("/tmp");
                if(labpack.containsKey("name")){
                    java.io.File labpac = new java.io.File(labpack_path+java.io.File.separator+labpack.get("name")+".labpack");
                    if(!labpac.exists() || SomethingChanged()){
                    

                    int choose = javax.swing.JOptionPane.showConfirmDialog(null,
                            "You have made changes to the labpack",
                            "keep chages?", javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    if (choose == javax.swing.JOptionPane.YES_OPTION) {
                        saving(labpack_path);
                        e.getWindow().dispose();
                        System.exit(0);
                        System.out.println("close");

                    } else if(choose == javax.swing.JOptionPane.NO_OPTION) {

                        e.getWindow().dispose();
                        System.exit(0);
                        System.out.println("close");
                    } else {
                        System.out.println("do nothing");
                    }
                    
                    }
                    else{
                        e.getWindow().dispose();
                        System.exit(0);
                    }
                    
                }
                else{
                        e.getWindow().dispose();
                        System.exit(0);
                    }
                
            }
        
        
         });
    }
    public NewJFrame() {
        
        initComponents();
        //defining list models, setting them to panels.
        lab = new javax.swing.DefaultListModel<String>();
        labslabel = new javax.swing.DefaultListModel<javax.swing.JLabel>();
        labsadded = new javax.swing.DefaultListModel<String>();
        keys  = new javax.swing.DefaultListModel<String>();
        labs_in_labpack.setModel(labsadded);
        lablist.setModel(lab);
        keywords.setModel(keys);
        
        String labdir = System.getenv("LABTAINER_DIR");
        
        
        String labpath = labdir + java.io.File.separator + "labs";
        
        java.io.File path = new java.io.File(labpath);
        String contents[] = path.list();
        
        //For each lab look at keywords
        for(int i=0; i<contents.length; i++) {
            
            lab.addElement(contents[i]);
        //keywords
            String path2 = labpath + java.io.File.separator + contents[i] + java.io.File.separator + "config" + java.io.File.separator + "keywords.txt";
            java.io.File keypath = new java.io.File(path2);
            if (keypath.exists()){
                
                filelist.add(keypath); //creating a global keywords list file.
                
            }
            //adding keywords to keys list model.
            try{
                java.util.Scanner Freader = new java.util.Scanner(keypath);
                while (Freader.hasNextLine()) {
                    String data = Freader.nextLine().trim();
                    
                    if(keys.contains(data)==false){
                        keys.addElement(data);
                    }
                }
            }catch (java.io.FileNotFoundException e) {
                System.out.println("keywords.txt missing: " + path2);
                //e.printStackTrace();
            }
            
        }
        sorting(lablist);
        sorting(keywords);    
            
            
            
        //looking into tmp/labname.txt to see what previous labpack you opened/created.
        try{
            String pathp = java.io.File.separator+"tmp" + java.io.File.separator+ "packname.txt";
            java.io.File file = new java.io.File(pathp);
            java.util.Scanner Fsreader = new java.util.Scanner(file);
            
                while (Fsreader.hasNextLine()) {
                String data = Fsreader.nextLine().trim();
                java.io.File packfile = new java.io.File(labpack_path+java.io.File.separator+data);
                DoesOPEN(packfile);    
            }
            
        } catch(java.io.FileNotFoundException e) {
            System.out.println("problem accessing file labname.txt");
        }
            
    
        
        
        
        lablist.revalidate();
        lablist.repaint();
        keywords.revalidate();
        keywords.repaint();
        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        CloseWindow();
       
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        labpackinfo = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        Create = new javax.swing.JButton();
        TextName = new javax.swing.JTextField();
        TextDescription = new javax.swing.JTextField();
        TextOrder = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        order_and_description = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        save_OandD = new javax.swing.JButton();
        TextOrder1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextDescription1 = new javax.swing.JTextArea();
        listlabpacks = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        labpacktextbox = new javax.swing.JTextArea();
        KeyPane = new javax.swing.JScrollPane();
        keywords = new javax.swing.JList<>();
        LablistlPane = new javax.swing.JScrollPane();
        lablist = new javax.swing.JList<>();
        labsPane = new javax.swing.JScrollPane();
        labs_in_labpack = new javax.swing.JList<>();
        labdescriptionPane = new javax.swing.JScrollPane();
        description_box = new javax.swing.JTextPane();
        labnotePane = new javax.swing.JScrollPane();
        notes_box = new javax.swing.JTextPane();
        FindButton = new javax.swing.JButton();
        ClearButton = new javax.swing.JButton();
        RemoveButton = new javax.swing.JButton();
        AddNoteButton = new javax.swing.JButton();
        Move_Up_Button = new javax.swing.JButton();
        Move_Down_Button = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        OpenButton = new javax.swing.JMenuItem();
        NewButton = new javax.swing.JMenuItem();
        SaveButton = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        Order_Description = new javax.swing.JMenuItem();
        ViewButton = new javax.swing.JMenu();
        list_labpacks = new javax.swing.JMenuItem();
        ChangeFont = new javax.swing.JMenu();
        InreaseFont = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        fileChooser.setCurrentDirectory(new java.io.File("/home/student/labtainer/trunk/labpacks"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Labpack"));

        Create.setText("Create");
        Create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateActionPerformed(evt);
            }
        });

        TextName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextNameActionPerformed(evt);
            }
        });
        TextName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextNameKeyTyped(evt);
            }
        });

        TextDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextDescriptionActionPerformed(evt);
            }
        });

        TextOrder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextOrderKeyTyped(evt);
            }
        });

        jLabel1.setText("Name:");

        jLabel2.setText("Description:");

        jLabel3.setText("Order:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TextDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .addComponent(TextOrder)
                            .addComponent(TextName)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Create)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TextName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TextDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TextOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Create)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout labpackinfoLayout = new javax.swing.GroupLayout(labpackinfo.getContentPane());
        labpackinfo.getContentPane().setLayout(labpackinfoLayout);
        labpackinfoLayout.setHorizontalGroup(
            labpackinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        labpackinfoLayout.setVerticalGroup(
            labpackinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Labpack"));

        save_OandD.setText("Save");
        save_OandD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_OandDActionPerformed(evt);
            }
        });

        TextOrder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextOrder1KeyTyped(evt);
            }
        });

        jLabel5.setText("Description:");

        jLabel6.setText("Order:");

        TextDescription1.setColumns(20);
        TextDescription1.setRows(5);
        jScrollPane1.setViewportView(TextDescription1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(save_OandD))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(TextOrder1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(9, 9, 9)
                .addComponent(TextOrder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(save_OandD)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout order_and_descriptionLayout = new javax.swing.GroupLayout(order_and_description.getContentPane());
        order_and_description.getContentPane().setLayout(order_and_descriptionLayout);
        order_and_descriptionLayout.setHorizontalGroup(
            order_and_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        order_and_descriptionLayout.setVerticalGroup(
            order_and_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        labpacktextbox.setColumns(20);
        labpacktextbox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        labpacktextbox.setRows(5);
        jScrollPane2.setViewportView(labpacktextbox);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout listlabpacksLayout = new javax.swing.GroupLayout(listlabpacks.getContentPane());
        listlabpacks.getContentPane().setLayout(listlabpacksLayout);
        listlabpacksLayout.setHorizontalGroup(
            listlabpacksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        listlabpacksLayout.setVerticalGroup(
            listlabpacksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("makepack");

        KeyPane.setBorder(javax.swing.BorderFactory.createTitledBorder("keywords"));

        keywords.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        KeyPane.setViewportView(keywords);

        LablistlPane.setBorder(javax.swing.BorderFactory.createTitledBorder("lab list"));

        lablist.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lablist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lablistMouseClicked(evt);
            }
        });
        lablist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lablistValueChanged(evt);
            }
        });
        LablistlPane.setViewportView(lablist);

        labsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("labs in labpack"));

        labs_in_labpack.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        labs_in_labpack.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                labs_in_labpackValueChanged(evt);
            }
        });
        labsPane.setViewportView(labs_in_labpack);

        labdescriptionPane.setBorder(javax.swing.BorderFactory.createTitledBorder("lab description"));
        labdescriptionPane.setViewportView(description_box);

        labnotePane.setBorder(javax.swing.BorderFactory.createTitledBorder("notes"));
        labnotePane.setViewportView(notes_box);

        FindButton.setText("Find");
        FindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindButtonActionPerformed(evt);
            }
        });

        ClearButton.setText("Clear");
        ClearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearButtonActionPerformed(evt);
            }
        });

        RemoveButton.setText("Remove");
        RemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveButtonActionPerformed(evt);
            }
        });

        AddNoteButton.setText("Save");
        AddNoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNoteButtonActionPerformed(evt);
            }
        });

        Move_Up_Button.setText("/\\");
            Move_Up_Button.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Move_Up_ButtonActionPerformed(evt);
                }
            });

            Move_Down_Button.setText("\\/");
            Move_Down_Button.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Move_Down_ButtonActionPerformed(evt);
                }
            });

            jMenu1.setText("File");

            OpenButton.setText("Open");
            OpenButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpenButtonActionPerformed(evt);
                }
            });
            jMenu1.add(OpenButton);

            NewButton.setText("New");
            NewButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NewButtonActionPerformed(evt);
                }
            });
            jMenu1.add(NewButton);

            SaveButton.setText("Save");
            SaveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SaveButtonActionPerformed(evt);
                }
            });
            jMenu1.add(SaveButton);

            jMenuBar1.add(jMenu1);

            jMenu2.setText("Edit");

            Order_Description.setText("Order & Description");
            Order_Description.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Order_DescriptionActionPerformed(evt);
                }
            });
            jMenu2.add(Order_Description);

            jMenuBar1.add(jMenu2);

            ViewButton.setText("View");

            list_labpacks.setText("labpacks");
            list_labpacks.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    list_labpacksActionPerformed(evt);
                }
            });
            ViewButton.add(list_labpacks);

            ChangeFont.setText("Font Size");

            InreaseFont.setText("Increase");
            InreaseFont.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    InreaseFontActionPerformed(evt);
                }
            });
            ChangeFont.add(InreaseFont);

            jMenuItem3.setText("Decrease");
            jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem3ActionPerformed(evt);
                }
            });
            ChangeFont.add(jMenuItem3);

            ViewButton.add(ChangeFont);

            jMenuBar1.add(ViewButton);

            setJMenuBar(jMenuBar1);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labdescriptionPane)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(KeyPane)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(FindButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)))
                            .addGap(7, 7, 7)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(ClearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE))
                                .addComponent(LablistlPane))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(RemoveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(labnotePane, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                                            .addComponent(AddNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(14, 14, 14))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(labsPane)
                                    .addGap(18, 18, 18)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Move_Down_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Move_Up_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(LablistlPane)
                        .addComponent(labsPane)
                        .addComponent(KeyPane)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(Move_Up_Button)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(Move_Down_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FindButton)
                        .addComponent(ClearButton)
                        .addComponent(RemoveButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(labdescriptionPane, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                        .addComponent(labnotePane, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(AddNoteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents
    //when clicked it will call the refresh function to refresh labs
    private void ClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButtonActionPerformed
        refresh(lab);
        sorting(lablist);
        description_box.setText("");//for the lab description textbox
    }//GEN-LAST:event_ClearButtonActionPerformed

    private void RemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveButtonActionPerformed
        //this is for the remove button that will remove any selcted lab elements from the labs_in_labpack list model;
        java.util.List<String> value = labs_in_labpack.getSelectedValuesList();
        
        
        for(int i=0; i<value.size(); i++){
           labsadded.removeElement(value.get(i));
           labnotes.remove(value.get(i));;
        }

    }//GEN-LAST:event_RemoveButtonActionPerformed

    private void AddNoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNoteButtonActionPerformed
        String lab = labs_in_labpack.getSelectedValue();
        String description = notes_box.getText();
        
        labnotes.put(lab, description);//labnotes is a hashmap that maps labs to labnotes to be added or retreuve later 
        //this is for the save button next to labnotes to save notes to labs
    }//GEN-LAST:event_AddNoteButtonActionPerformed
//move a lab in the labs in labpack model up in the order
    private void Move_Up_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Move_Up_ButtonActionPerformed
        String selectedItem = labs_in_labpack.getSelectedValue();//get item value
        int itemIndex = labs_in_labpack.getSelectedIndex();// get item index
        javax.swing.DefaultListModel model = (javax.swing.DefaultListModel)labs_in_labpack.getModel();// get list model
        
        if(itemIndex > 0){
            model.remove(itemIndex);// remove selected item from the list
            model.add(itemIndex - 1, selectedItem);// add the item to a new position in the list
            labs_in_labpack.setSelectedIndex(itemIndex - 1);// set selection to the new item
        } 
    }//GEN-LAST:event_Move_Up_ButtonActionPerformed
//move a lab in the labs in labpack model down in the order
    private void Move_Down_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Move_Down_ButtonActionPerformed
        String selectedItem = labs_in_labpack.getSelectedValue();//get item value
        int itemIndex = labs_in_labpack.getSelectedIndex();// get item index
        javax.swing.DefaultListModel model = (javax.swing.DefaultListModel)labs_in_labpack.getModel();// get list model
        
        if(itemIndex < model.getSize() -1){
            model.remove(itemIndex);// remove selected item from the list
            model.add(itemIndex + 1, selectedItem);// add the item to a new position in the list
            labs_in_labpack.setSelectedIndex(itemIndex + 1);// set selection to the new item
        }
    }//GEN-LAST:event_Move_Down_ButtonActionPerformed

    private void OpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenButtonActionPerformed
        
        //refresh(labs);
        
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == fileChooser.APPROVE_OPTION) {
        java.io.File file = fileChooser.getSelectedFile();
        savepackname(file.getName());
        DoesOPEN(file);
    } else {
        //System.out.println("File access cancelled by user.");
        ;
    }
    }//GEN-LAST:event_OpenButtonActionPerformed
    //set dialog for new labpack to visible.
    private void NewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewButtonActionPerformed
        labpackinfo.setVisible(rootPaneCheckingEnabled);
        labpackinfo.pack();
    }//GEN-LAST:event_NewButtonActionPerformed
//saves
    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
        this.saving(labpack_path);//this save is for saving labpacks changes to the actual file
    }//GEN-LAST:event_SaveButtonActionPerformed

    private void FindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindButtonActionPerformed
        //find a display labs with keywords that are selected.
        java.util.List<String> selectedlist = keywords.getSelectedValuesList();
        
        System.out.println(selectedlist);
        lab.clear();
        
        for(int i=0; i<filelist.size(); i++) {
            
            try{
                java.util.Scanner Freader = new java.util.Scanner(filelist.get(i));//filelist is a list of keywords.txt files
                java.util.List<String> keywordslist = new ArrayList <String> ();
                keywordslist.clear();
                while (Freader.hasNextLine()) {
                    String line = Freader.nextLine().trim();
                    keywordslist.add(line);
                    
                }
            System.out.println(keywordslist);
            if (keywordslist.containsAll(selectedlist)){
                String labname = filelist.get(i).getParentFile().getParentFile().getName();
                        //the keywords.txt parent is config, and config's parent is the name of the lab
                       lab.addElement(labname);
            }
            }catch (java.io.FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            } 

        }
        sorting(lablist);    
        System.out.println(lab);    
           
        
        lablist.setModel(lab);

        
        
        
        
        lablist.revalidate();
        lablist.repaint();
        
        
    }//GEN-LAST:event_FindButtonActionPerformed
//This is for whenever you change the selection for the lablist model, the lab's description appears
    private void lablistValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lablistValueChanged
        String word = lablist.getSelectedValue();
        System.out.println("value change to: "+ word);
        String labdir = System.getenv("LABTAINER_DIR");
        String path = labdir + java.io.File.separator + "labs" + java.io.File.separator + word +java.io.File.separator+ "config" + java.io.File.separator + "about.txt";
        java.io.File aboutpath = new java.io.File(path);
        try{
                java.util.Scanner Freader = new java.util.Scanner(aboutpath);
                while (Freader.hasNextLine()) {
                    String data = Freader.nextLine().trim();
                    description_box.setText(data);
                    
                    
                    
                }
            }catch (java.io.FileNotFoundException e) {
               // System.out.println("about.txt missing: " + path);
                //e.printStackTrace();
            }
    }//GEN-LAST:event_lablistValueChanged
//this is for whenever a lab in the the labs_in_labpack model is selected, it will display its notes in the textbox
    private void labs_in_labpackValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_labs_in_labpackValueChanged
        String lab = labs_in_labpack.getSelectedValue();
        String description = labnotes.get(lab);
        notes_box.setText(description);
    }//GEN-LAST:event_labs_in_labpackValueChanged

    private void lablistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lablistMouseClicked
        javax.swing.JList<String> list = (javax.swing.JList<String>)evt.getSource();
        if (evt.getClickCount() == 2) {
            int index = list.locationToIndex(evt.getPoint());
            String name = lablist.getModel().getElementAt(index);
            System.out.println("index: "+name);
            if(labsadded.contains(name)==false) {
                labsadded.addElement(name);
                labnotes.put(name, "");
            }
        }
    }//GEN-LAST:event_lablistMouseClicked
//creating a new labpack from a dialog, it will take the name, description and order but will not be saved if you don't click save.
    private void CreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateActionPerformed
        
        labsadded.clear();
        labnotes.clear();
        String pack_name = TextName.getText();
        labpack.put("name", pack_name);

        String des_name = TextDescription.getText();
        labpack.put("description", des_name);
        
        
        String order_name = TextOrder.getText().toString();
        if (order_name.length() !=0){
        labpack.put("order", order_name);
        }
        if (order_name.length() ==0){
            labpack.remove("order");
        }
        this.setTitle("makepack: "+pack_name);
        labpackinfo.setVisible(false);
        TextName.setText("");
        TextDescription.setText("");
        TextOrder.setText("");
        //OrderField.setText(labpack.get("order"));
        //jTextArea1.setText(labpack.get("description"));
        saving("/tmp");
        savepackname(labpack.get("name")+".labpack");
        System.out.println(labpack.get("name"));

    }//GEN-LAST:event_CreateActionPerformed

    private void TextNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextNameActionPerformed

    private void TextNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextNameKeyTyped
        char c = evt.getKeyChar();
        if(c == ' '){
            evt.consume();
        }
    }//GEN-LAST:event_TextNameKeyTyped

    private void TextDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextDescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextDescriptionActionPerformed

    private void TextOrderKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextOrderKeyTyped
        char c = evt.getKeyChar();
        if(!Character.isDigit(c)){
            evt.consume();
        }

    }//GEN-LAST:event_TextOrderKeyTyped
//when you click on edit order & description make dialog visible and set textboxes for order and description
    private void Order_DescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Order_DescriptionActionPerformed
        TextOrder1.setText(labpack.get("order"));
        TextDescription1.setText(labpack.get("description"));
        order_and_description.setVisible(rootPaneCheckingEnabled);
        order_and_description.pack();
        
        
    }//GEN-LAST:event_Order_DescriptionActionPerformed
//the save button action method is for saving changes made to description and order in the order and description dialog
    private void save_OandDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_OandDActionPerformed
        String order = TextOrder1.getText();
        
        if (order.length() != 0){
        labpack.put("order", order);
        }
        if (order.length() ==0){
            labpack.remove("order");
        }
        String description = TextDescription1.getText();
        labpack.put("description", description);
        order_and_description.setVisible(false);
        
    }//GEN-LAST:event_save_OandDActionPerformed
//this prevents user from typing letter in the order textbox that requires digits
    private void TextOrder1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextOrder1KeyTyped
        char c = evt.getKeyChar();
        if(!Character.isDigit(c)){
            evt.consume();
        }
    }//GEN-LAST:event_TextOrder1KeyTyped
//to view the list of labpacks click on view then labpacks
    public void doCommand(String cmd){
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", cmd);
        try{
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException e){
                System.out.println(e);
        } catch (InterruptedException ie){
                System.out.println(ie);
        }
    }
    private void list_labpacksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list_labpacksActionPerformed
    String labdir = System.getenv("LABTAINER_DIR");
    String instructor_path = labdir + java.io.File.separator + "scripts"+java.io.File.separator +"labtainer-instructor";
    String labpack_path = "bin" +java.io.File.separator +"makepack"; 
    System.out.println(labpack_path);
    try{
        ProcessBuilder pb = new ProcessBuilder(labpack_path);
        pb.directory(new java.io.File(instructor_path));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int waitfor = process.waitFor();
        BufferedReader reader = 
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            if(line.equals("usage: makepack [-h] [name]")){
                break;
            
            } else{
                builder.append(line);
            
            builder.append(System.getProperty("line.separator"));
            }
        }
        String result = builder.toString();
        System.out.println(result);
        labpacktextbox.setText(result);
        } catch (IOException | InterruptedException ex) {
          System.out.println(ex);
        
        }
        
        listlabpacks.setVisible(rootPaneCheckingEnabled);
        listlabpacks.pack();


    }//GEN-LAST:event_list_labpacksActionPerformed
//Increase font size from font size menue item
    private void InreaseFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InreaseFontActionPerformed
        keywords.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,18));
        lablist.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,18));
        labs_in_labpack.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,18));
        description_box.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,18));
        notes_box.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,18));
    }//GEN-LAST:event_InreaseFontActionPerformed
//Decrease font size from font size menue item
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        keywords.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,12));
        lablist.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,12));
        labs_in_labpack.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,12));
        description_box.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,12));
        notes_box.setFont(new java.awt.Font("Dialog",java.awt.Font.PLAIN,12));
    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;
 
        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }
 
        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
              .forEach(consumer);
        }
    }
    private javax.swing.DefaultListModel<String> lab;
    private javax.swing.DefaultListModel<javax.swing.JLabel> labslabel;
    private javax.swing.DefaultListModel<String> keys;
    private javax.swing.DefaultListModel<String> labsadded;
    private javax.swing.JList<String> JlabelList;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNoteButton;
    private javax.swing.JMenu ChangeFont;
    private javax.swing.JButton ClearButton;
    private javax.swing.JButton Create;
    private javax.swing.JButton FindButton;
    private javax.swing.JMenuItem InreaseFont;
    private javax.swing.JScrollPane KeyPane;
    private javax.swing.JScrollPane LablistlPane;
    private javax.swing.JButton Move_Down_Button;
    private javax.swing.JButton Move_Up_Button;
    private javax.swing.JMenuItem NewButton;
    private javax.swing.JMenuItem OpenButton;
    private javax.swing.JMenuItem Order_Description;
    private javax.swing.JButton RemoveButton;
    private javax.swing.JMenuItem SaveButton;
    private javax.swing.JTextField TextDescription;
    private javax.swing.JTextArea TextDescription1;
    private javax.swing.JTextField TextName;
    private javax.swing.JTextField TextOrder;
    private javax.swing.JTextField TextOrder1;
    private javax.swing.JMenu ViewButton;
    private javax.swing.JTextPane description_box;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> keywords;
    private javax.swing.JScrollPane labdescriptionPane;
    private javax.swing.JList<String> lablist;
    private javax.swing.JScrollPane labnotePane;
    private javax.swing.JDialog labpackinfo;
    private javax.swing.JTextArea labpacktextbox;
    private javax.swing.JScrollPane labsPane;
    private javax.swing.JList<String> labs_in_labpack;
    private javax.swing.JMenuItem list_labpacks;
    private javax.swing.JDialog listlabpacks;
    private javax.swing.JTextPane notes_box;
    private javax.swing.JDialog order_and_description;
    private javax.swing.JButton save_OandD;
    // End of variables declaration//GEN-END:variables
}
