public class scratch{
    public static void main(String[] args) {
        String url = "http://httpbin.org/get?course=networking&assignment=1";
        String[] before  = url.split("//");
        for (String s: before){
            System.out.println(s);
        }
        // before[1] contains httpbin.org/get?course=networking&assignment=1
        String[] before2 = before[1].split("/", 2);
        for (String s: before2){
            System.out.println(s);
        }
    }
}

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