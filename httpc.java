
//java socket client example
import java.io.*;
import java.net.*;

public class httpc {

    static PrintWriter s_out = null;
    static BufferedReader s_in = null;
    static boolean vMode = false;
    static Socket socket;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error --- unable to run httpc");
        }
    }

    public static void run(String[] args){
        // check first if args is null
        if (args.length == 0) {
            System.out.println("try httpc --help for more information");
        }
        // make condition if not equal to help
        else if (args.length == 1 && args[0].equals("help")) {
            getGenericHelpMessage();
        } else if (args.length == 2 && args[0].equals("help")) {
            getSpecificHelpMessage(args[1]);
        } else if (args[0].equals("get")) {
            get(args);
        }
    }

    // methode where we connect with the website and try and send it "GET /
    // HTTP/1.1\r\n\r\n"
    private static void get(String[] args) {

        String url = args[1].toString();
       // System.out.println(url);
        
       //needs work
        try {
            InetAddress IP = InetAddress.getByName(url);
            System.out.println(IP);
            socket = new Socket(IP,80);
            System.out.println("connected");
            //writer for socket
            s_out = new PrintWriter(socket.getOutputStream(), true);
            //reader for socket
            s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Send message to server
            String message = "GET / HTTP/1.1\r\n\r\n";
            s_out.println( message );
                
            System.out.println("Message send");
            
            //Get response from server
            String response;
            while ((response = s_in.readLine()) != null) {
                System.out.println( response );
            }
            //close everything
            s_in.close();
            s_out.close();
            socket.close();
        }
            //Host not found
        catch (IOException e) 
        {
            System.err.println("Don't know about host : " + url);
            //System.exit(1);
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
            System.out.println("do not recognize this action :"+action);
            System.exit(1);
        }
    }
}