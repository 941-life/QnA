import java.io.*;
import java.net.*;

public class QuizClient {
    private static final String CONFIG_FILE = "server_info.dat";
    private static String serverAddress = "localhost";
    private static int serverPort = 1234;

    public static void main(String[] args) {
        readServerConfig();

        try (
                Socket socket = new Socket(serverAddress, serverPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("서버에 연결되었습니다. 퀴즈를 시작합니다...");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("Q:")) {
                    System.out.println("------------------ 질문 ------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("-----------------------------------------");
                    System.out.print("답변을 입력하세요: ");
                    String answer = userInput.readLine();
                    out.println("A:" + answer);
                } else if (serverMessage.startsWith("R:")) {
                    System.out.println("-----------------------------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("-----------------------------------------");
                } else if (serverMessage.startsWith("S:")) {
                    System.out.println("------------------ 결과 ------------------");
                    System.out.println(serverMessage.substring(2));
                    System.out.println("-----------------------------------------");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 서버 정보 읽기
    private static void readServerConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                serverAddress = reader.readLine();
                serverPort = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("서버 정보 찾을 수 없음 " + serverAddress + ":" + serverPort);
        }
    }
}
