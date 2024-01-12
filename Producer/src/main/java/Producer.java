import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Producer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(1212).addService(new UserService()).build();
        server.start();

        System.out.println("Server started at port : " + server.getPort());
        server.awaitTermination();
    }
}
