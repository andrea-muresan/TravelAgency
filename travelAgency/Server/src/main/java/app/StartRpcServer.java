package app;

import network.utils.AbstractServer;
import network.utils.RpcConcurrentServer;
import persistence.FlightRepository;
import persistence.TicketRepository;
import persistence.UserRepository;
import persistence.repository.FlightDBRepository;
import persistence.repository.TicketDBRepository;
import persistence.repository.UserDBRepository;
import app.server.Service;
import services.IServices;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static final int defaultPort=55555;
    public static void main(String[] args) {
        // UserRepository userRepo=new UserRepositoryMock();
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }
        UserRepository userRepo=new UserDBRepository(serverProps);
        FlightRepository flightRepo=new FlightDBRepository(serverProps);
        TicketRepository ticketRepo=new TicketDBRepository(serverProps);

        IServices service = new Service(userRepo, flightRepo, ticketRepo);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
