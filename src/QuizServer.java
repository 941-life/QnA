import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizServer {
    private static final int PORT = 1234;  // 서버 포트 번호
    private static final List<Question> questions = new ArrayList<>();
    private static ServerSocket serverSocket;
    private static final int MAX_CLIENTS = 5;  // 최대 클라이언트 수
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);

    public static void main(String[] args) {
        loadQuestions();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("퀴즈 서버가 실행 중입니다...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다!");

                // 여러 클라이언트 접속을 위해 threadpool 추가
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }

    private static void loadQuestions() {
        questions.add(new Question("IPv4의 고갈 문제의 단기 처방 목적으로 마련 된 기술은?", "NAT"));
        questions.add(new Question("server가 모든 host들에게 DHCP offer 을 전송해 ip 주소를 얻는 방법은?", "broadcast"));
        questions.add(new Question("Java의 기본 자료형 개수는?", "8"));
    }

    // 서버 종료
    private static void shutdownServer() {
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdown();
            System.out.println("서버가 종료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트 처리!
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int score = 0;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                for (Question question : questions) {

                    out.println("Q:" + question.getQuestion());

                    String answer = in.readLine();
                    if (answer != null && answer.equalsIgnoreCase("A:" + question.getAnswer())) {
                        score++;
                        out.println("R:정답입니다!");
                    } else {
                        out.println("R:오답입니다");
                    }
                }
                // 최종 점수 전송
                out.println("S:최종 점수는 " + score + "/" + questions.size());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 질문 클래스 정의
    private static class Question {
        private final String question;
        private final String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
