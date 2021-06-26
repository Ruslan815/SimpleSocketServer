import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Date;

public class SimpleSocketServer {
    public static void main(String[] args) throws Throwable {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client accepted.");
            new Thread(new SimpleSocketProcessor(clientSocket)).start();
        }
    }

    private static class SimpleSocketProcessor implements Runnable {
        private final Socket clientSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        private SimpleSocketProcessor(Socket clientSocket) throws Throwable {
            this.clientSocket = clientSocket;
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
        }

        public void run() {
            try {
                readInputHeaders();
                writeResponse("<html><body><h1>Simple Socket Server Response</h1><p>Hello!</p></body></html>");
            } catch (Throwable t) {
                System.err.println("Can't read request or send response to client!");
            } finally {
                try {
                    clientSocket.close();
                } catch (Throwable t) {
                    System.err.println("Can't close a client socket!");
                }
            }
            System.out.println("Client processing finished.\n");
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println("CLIENT REQUEST:");
            while (true) {
                String bufferString = bufferedReader.readLine();
                if (bufferString == null || bufferString.trim().length() == 0) {
                    break;
                }
                System.out.println(bufferString);
            }
        }

        private void writeResponse(String responseContent) throws Throwable {
            String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                    "Date: " + new Date().getTime() + "\r\n" +
                    "Server: SimpleSocketServer_26.06.2021\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n" +
                    "Content-Length: " + responseContent.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String response = responseHeaders + responseContent;
            outputStream.write(response.getBytes());
            outputStream.flush();
        }
    }
}
