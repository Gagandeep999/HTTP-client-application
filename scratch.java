// public class scratch{
//     public static void main(String[] args) {
//         String url = "http://httpbin.org/get?course=networking&assignment=1";
//         String[] before  = url.split("//");
//         for (String s: before){
//             System.out.println(s);
//         }
//         // before[1] contains httpbin.org/get?course=networking&assignment=1
//         String[] before2 = before[1].split("/", 2);
//         for (String s: before2){
//             System.out.println(s);
//         }
//     }
// }

// //writer for socket
// s_out = new PrintWriter(socket.getOutputStream(), true);
// //reader for socket
// s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// //Send message to server
// String message = "GET / HTTP/1.1\r\n\r\n";
// s_out.println( message );
    
// System.out.println("Message send");

// //Get response from server
// String response;
// while ((response = s_in.readLine()) != null) {
//     System.out.println( response );
// }
// //close everything
// s_in.close();
// s_out.close();
// socket.close();

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
 
public class scratch {
     
    public static void main(String[] args) {
         
        try {
             
            String params = URLEncoder.encode("Assignment", "UTF-8")
+ "=" + URLEncoder.encode("1", "UTF-8");
//             params += "&" + URLEncoder.encode("param2", "UTF-8")
// + "=" + URLEncoder.encode("value2", "UTF-8");
 
            String hostname = "httpbin.org";
            int port = 80;
             
            // InetAddress addr = InetAddress.getByName(hostname);
            Socket socket = new Socket();
            // String path = "/post";


            socket.connect(new InetSocketAddress(hostname, 80));
            System.out.println("connected");
            // PrintWriter sockePrintWriterOutputStream = new PrintWriter(socket.getOutputStream(), true);
            // BufferedReader socketBufferedReaderInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
            // Send headers
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            System.out.println("params: "+params);
            // String message = "POST /post HTTP/1.0\r\n" + "Content-Length: "+params.length()+"\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "\r\n" + params;
            // System.out.println("message is : "+message);
            String message = "POST /post HTTP/1.0\r\n";
            // message = message.concat("Content-Length: "+params.length()+"\r\n");
            // message = message.concat("Content-Type: application/x-www-form-urlencoded\r\n");
            message = message.concat("\r\n");
            // message = message.concat(params);
            // wr.write("POST /post HTTP/1.0\r\n");
            // wr.write("Content-Length: "+params.length()+"\r\n");
            // // wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
            // wr.write("\r\n");
            System.out.println(message);
            // Send parameters
            wr.write(message);
            wr.flush();
 
            // Get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
             
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
             
            wr.close();
            rd.close();
             
        }
        catch (Exception e) {
            e.printStackTrace();
        }
         
    }
 
}