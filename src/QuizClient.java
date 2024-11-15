import java.io.*;
import java.net.*;
public class QuizClient {
    private static String serverAddress = "localhost";
    private static int serverPort = 1234;
    public static void main(String[] args) {
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Connect Server. Start Quiz.");
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("Q:")) {
                    System.out.println("------------------ question ------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("----------------------------------------------");
                    System.out.print("answer: ");
                    String answer = userInput.readLine();
                    out.println("A:" + answer);
                } else if (serverMessage.startsWith("R:")) {
                    System.out.println("---------------------------------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("---------------------------------------------");
                } else if (serverMessage.startsWith("S:")) {
                    System.out.println("------------------ result -------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("--------------------------------------------");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
