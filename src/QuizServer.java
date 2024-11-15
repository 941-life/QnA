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
            System.out.println("Quiz server is running..");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connect Client!");

                // 여러 클라이언트 접속을 위해 threadpool 사용
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdownServer();
        }
    }
    private static void loadQuestions() {
        // 각 질문에 대해 여러 정답을 List 형태로 저장
        questions.add(new Question("IPv4의 고갈 문제의 단기 처방 목적으로 마련 된 기술은?", Arrays.asList("NAT", "Network Address Translation","네트워크 주소 변환")));
        questions.add(new Question("server가 모든 host들에게 DHCP offer 을 전송해 ip 주소를 얻는 방법은?", Arrays.asList("broadcast", "브로드캐스트","Broadcast")));
        questions.add(new Question("Java의 기본 자료형 개수는?", Arrays.asList("8", "8개")));
    }
    // 서버 종료
    private static void shutdownServer() {
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdown();
            System.out.println("Close Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트 처리
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
                    if (answer != null && question.isCorrectAnswer(answer.substring(2))) {
                        score++;
                        out.println("R:Correct");
                    } else {
                        out.println("R:Incorrect");
                    }
                }
                // 최종 점수 전송
                out.println("S:Your score is " + score + "/" + questions.size());
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

    // 질문 클래스
    private static class Question {
        private final String question;
        private final List<String> correctAnswers;
        public Question(String question, List<String> correctAnswers) {
            this.question = question;
            this.correctAnswers = correctAnswers;
        }
        public String getQuestion() {
            return question;
        }
        // 사용자가 입력한 답변이 정답 리스트에 있는지 검사
        public boolean isCorrectAnswer(String answer) {
            return correctAnswers.stream().anyMatch(correctAnswer -> correctAnswer.equalsIgnoreCase(answer.trim()));
        }
    }
}
