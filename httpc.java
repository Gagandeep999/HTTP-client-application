
//java socket client example
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class httpc {

    static List<String> inLineData = new ArrayList<>();
    static List<String> headerData = new ArrayList<>();
    static BufferedWriter s_out = null;
    static BufferedReader s_in = null;
    static boolean vMode = false;
    static boolean f_Switch = false;
    static boolean d_Switch = false;
    static Socket socket;
    static String host;
    static String specifics;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error --- unable to run httpc");
        }
    }

    public static void run(String[] args) {
        // check first if args is null
        if (args.length == 0) {
            System.out.println("try httpc help for more information");
        } else if (args.length == 1 && args[0].equals("help")) {
            getGenericHelpMessage();
        } else if (args.length == 2 && args[0].equals("help")) {
            getSpecificHelpMessage(args[1]);
        } else if (args[0].equals("get")) {
            get(args);
        } else if (args[0].equals("post")) {
            post(args);
        } else {
            getGenericHelpMessage();
        }
    }

    // methode where we connect with the website and try and send it "GET /
    private static void get(String[] args) {

        // check to see the modes that are activated
        args = checkMode(args);
        String url = args[1].toString();
        host = "";
        specifics = "";
        getHost_Specifics(url);
       //url needs to be split into different tokens. first big chunk is used when we connect the socket to the Web.
       //second part is then addedto the message we send. that forms the request
        try {
            InetAddress addr = InetAddress.getByName(host);
            socket = new Socket(addr, 80);
            //writer for socket
            s_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            //reader for socket
            s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Send message to server
            String message ="GET /"+specifics+" HTTP/1.0\r\n"
                            +getHeaderData()
                            +"\r\n";
            //System.out.println(message);
            s_out.write(message);
            s_out.flush();
            getResponse();
            s_in.close();
            s_out.close();
            socket.close();
        }
            //Host not found
        catch (IOException e) 
        {
            System.err.println("Don't know about host : " + url);
            System.exit(1);
        }
            
    }
    
    private static void getHost_Specifics(String url) {
        if(url.contains("//")){
            String[] before  = url.split("//",2);
            String[] before2 = before[1].split("/", 2);
            if(before2.length == 1) {
                host=before2[0];
                specifics="";
            }
            else{
                host=before2[0];
                specifics=before2[1]; 
            }
        }
        else{
            String[] before  = url.split("/",2);
            if(before.length == 1) {
                host=before[0];
                specifics="";
            }
            else{
                host=before[0];
                specifics=before[1];
            }
        }
    }

    private static void post(String[] args){
        try{
            String data="";
            args = checkMode(args);
            String[] inLine = inLineData.toArray(new String[0]);
//
//need to figure out what to do when we gave more than one inline data
//          
            if(d_Switch){
                for (int i =0;i<inLine.length;i=i+2){
                    if(i>0){
                        data=data+"&";
                    }
                    data=data+URLEncoder.encode(inLine[i], "UTF-8") + "=" + URLEncoder.encode(inLine[i+1], "UTF-8");
                }
            }
            if(f_Switch){
                for (int i =0;i<inLine.length;i++){
                    if(i>0){
                        data=data+"&";
                    }
                    data=data+(inLine[i]);
                }
            }
            String url = args[1]; 
            getHost_Specifics(url);
            InetAddress addr = InetAddress.getByName(host);
            socket = new Socket(addr, 80);
            s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            s_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            String message ="POST /"+specifics+" HTTP/1.0\r\n"
                            +"Content-Length: " + data.length() + "\r\n"
                            +getHeaderData()
                            +"\r\n"
                            +data;
            //System.out.println(message);
            s_out.write(message);          
            s_out.flush();
            getResponse();
            s_in.close();
            s_out.close();

        }catch(Exception e){
            System.out.println("something went wrong");
            System.exit(1);
        }
    }

    private static String[] checkMode(String[] args) {
        List<String> list = new ArrayList<String>(Arrays.asList(args));
        if(list.contains("-v")){
            list.remove("-v");
            vMode=true;
        }
        if(list.contains("-d") && list.contains("-f")){
            System.out.println("error ---- cannot have \"d\" and \"f\" options at the same time.\n");
            System.exit(1);
        }
        //can use this to check other modes also
        if(list.contains("-d") && f_Switch==false){
            d_Switch=true;
            int index = list.indexOf("-d");
            String temp ="";
            try{  
                temp = list.get(index + 1);
                if(!temp.contains("{")){
                    throw new Exception();
                }
                
            }
            //in case the user doesnt input anything as inline data
            catch( Exception e){
                System.out.println
                    ("error ---- did not assign any inline data or failed to follow format\n"
                    +"-d {key:value}");
                System.exit(1);
            }
            temp= temp.replace("{","");
            temp= temp.replace("}","");
            temp= temp.replace("\"","");
            temp= temp.replace(" ","");
            String[] before=temp.split("-");
            for(int i = 0; i<before.length;i++){
                String[] before2 = before[i].split(":");
                inLineData.add(before2[0]);
                inLineData.add(before2[1]);
            }
            list.remove("-d");
            list.remove(index);
        }
        int i=0;
        while(list.contains("-h")){
            int index = list.indexOf("-h");
            String temp;
            try{
                temp = list.get(index + 1);
                String[] before=temp.split(":");
                headerData.add(before[i]);
                headerData.add(before[i+1]);
                list.remove("-h");
                list.remove(index);
            }
            //in case the user doesnt input anything as inline data
            catch( Exception e){
                System.out.println
                    ("error ---- did not assign any header data or failed to follow format\n"
                    +"-h key:value");
                System.exit(1);
            }
            i=i+2;
        }
        if(list.contains("-f") && d_Switch==false){
            f_Switch=true;
            int index = list.indexOf("-f");
            String path;
            //in case file not found
            try{
                path = list.get(index + 1);
                File file = new File(path);
                BufferedReader f_in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while((line = f_in.readLine()) != null){
                    inLineData.add(line);
               }
                f_in.close();
                list.remove("-f");
                list.remove(index);
            }
            //in case the user doesnt input anything as inline data
            catch( Exception e){
                System.out.println
                    ("error ---- problem opening the file\n");
                System.exit(1);
            }
        }
        args = list.toArray(new String[0]);
        return args;
    }

    private static String getHeaderData() {
        String text="";
        boolean headerInfo =false;
        while(!headerData.isEmpty()){
            headerInfo=true;
            text=headerData.get(0)+": "+headerData.get(1)+"\r\n";
            headerData.remove(0);
            headerData.remove(0);
        }
        if(headerInfo==false){
            text="Content-Type: application/x-www-form-urlencoded\r\n";
        }
        return text;
    }

    private static void getResponse() throws IOException {
        
        String response = s_in.readLine();
        if(!vMode){
            while (!response.isEmpty()) {
                response = s_in.readLine();
            }
            while ((response = s_in.readLine()) != null) {
                System.out.println( response );
            }
        }
        else{
            while ((response = s_in.readLine()) != null) {
                System.out.println( response );
            }
        }
    }

    // prints the generic help information
    private static void getGenericHelpMessage() {
        System.out.println("\nhttpc help\n" 
        +"\nhttpc is a curl-like application but supports HTTP protocol only.\n"
        +"Usage:\n"
        +"\t httpc command [arguments]\n"
        +"The commands are:\n"
        +"\t get \t executes a HTTP GET request and prints the response.\n"
        +"\t post \t executes a HTTP POST request and prints the response.\n"
        +"\t help \t prints this screen.\n"
        +"\nUse \"httpc help [command]\" for more information about a command.\n");
    }
    //prints the information for the specified action
    private static void getSpecificHelpMessage(String action) {
        if(action.equals("get")){
            System.out.println("\nhttpc help get\n"
            +"\nusage: httpc get [-v] [-h key:value] URL\n"
            +"\nGet executes a HTTP GET request for a given URL.\n"
            +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
            +"-h key:value Associates headers to HTTP Request with the format 'key:value'.");
        }
        else if(action.equals("post")){
            System.out.println("\nhttpc help post\n"
            +"\nusage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
            +"\nPost executes a HTTP POST request for a given URL with inline data or from file.\n"
            +"\n-v Prints the detail of the response such as protocol, status, and headers.\n"
            +"-h key:value Associates headers to HTTP Request with the format 'key:value'.\n"
            +"-d string Associates an inline data to the body HTTP POST request.\n"
            +"-f file Associates the content of a file to the body HTTP POST request.\n"
            +"\nEither [-d] or [-f] can be used but not both.\n");
        }
        //if does not match with any of our cases
        else{
            getGenericHelpMessage();
        }
    }
}
