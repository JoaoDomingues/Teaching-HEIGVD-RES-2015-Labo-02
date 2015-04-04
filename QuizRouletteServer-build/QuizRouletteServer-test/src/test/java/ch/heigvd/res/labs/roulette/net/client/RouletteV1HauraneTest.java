package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import sun.rmi.server.UnicastRef;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV1HauraneTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);
    
    @Test    
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void theServerShouldSendStudentDataWhenStudentsAreInDB() throws EmptyStoreException, IOException {
        IRouletteV1Client client = roulettePair.getClient();
        client.loadStudent("Jean");
        Student jean = client.pickRandomStudent();
        assertNotNull(jean.getFullname());
    }
    
    @Test    
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void theClientShouldBeAbleToAddMultipleStudents() throws IOException {
        List<Student> list = new LinkedList();
        list.add(new Student("Jean"));
        list.add(new Student("Jaques"));
        list.add(new Student("Michel"));
        roulettePair.getClient().loadStudents(list);
        assertEquals(3, roulettePair.getClient().getNumberOfStudents());
    }
    
    @Test    
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void serverShouldHandleEmptyLists() throws IOException {
        List<Student> list = new LinkedList();
        roulettePair.getClient().loadStudents(list);
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }
    
    @Test
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void theServerShouldNotLoadAnEmptyString() throws IOException {
        RouletteV1ClientImpl client = (RouletteV1ClientImpl) roulettePair.getClient();
        client.loadStudent("");
        assertEquals(0, client.getNumberOfStudents());
    }
    
    @Test    
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void serverHandlesMultipleConnectionsAndDeconnections() throws IOException {
        int port = roulettePair.getServer().getPort();
        int maxClients = 40;
        IRouletteV1Client clients[] = new IRouletteV1Client[maxClients];
        for (int i = 0; i < maxClients; i++) {
            clients[i] = new RouletteV1ClientImpl();
            clients[i].connect("localhost", port);
        }
        for (int i = 0; i < maxClients; i++) {
            clients[i].disconnect();
        }
        assertTrue(roulettePair.getServer().isRunning());
    }
    
    @Test
    @TestAuthor(githubId = {"haurane", "JoaoDomingues"})
    public void theClientShouldDisconnectProperly() throws IOException {
        IRouletteV1Client client = roulettePair.getClient();
        client.disconnect();
        assertFalse(client.isConnected());
    }
    
}
