package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private Socket connection;
  private PrintWriter writer;
  private BufferedReader reader;

  @Override
  public void connect(String server, int port) throws IOException {
     connection = new Socket(server, port);
     writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
     reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     reader.readLine();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void disconnect() throws IOException {
     writer.println(RouletteV1Protocol.CMD_BYE);
     writer.flush();
     writer.close();
     //lit le message de bienvenue
     reader.close();
     connection.close();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isConnected() {
     return (connection != null) && connection.isConnected();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     //lit le message de début
     reader.readLine();
     writer.println(fullname);
     writer.flush();
     writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     writer.flush();
     //lit le message de fin
     reader.readLine();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
     writer.println(RouletteV1Protocol.CMD_LOAD);
     writer.flush();
     for(Student student : students){
        writer.println(student.getFullname());
        writer.flush();
     }
     writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
     writer.flush();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      writer.println(RouletteV1Protocol.CMD_RANDOM);
      writer.flush();
      RandomCommandResponse student = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
      //vérifie s'il y a une erreur (base vide)
      if(student.getError() != null || !student.getError().isEmpty())
         throw new EmptyStoreException();
      return new Student(student.getFullname());
      
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getNumberOfStudents() throws IOException {
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();
      InfoCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
      return info.getNumberOfStudents();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getProtocolVersion() throws IOException {
      writer.println(RouletteV1Protocol.CMD_INFO);
      writer.flush();
      InfoCommandResponse info = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
      return info.getProtocolVersion();
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
