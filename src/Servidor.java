import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
        final File[] fileToSend = new File[1];
        JFrame jFrame = new JFrame ("Servidor");
        jFrame.setSize (450,450);
        jFrame.setLayout (new BoxLayout (jFrame.getContentPane (), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle  = new JLabel ("Enviar Archivo");
        jlTitle.setFont (new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder (new EmptyBorder (20,0,10,0));
        jlTitle.setAlignmentX (Component.CENTER_ALIGNMENT);

        JLabel jlFileName = new JLabel("Elije un archivo para enviar");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));
        JButton jbSendFile = new JButton("Enviar Archivo");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));
        JButton jbChooseFile = new JButton("Elegir Archivo");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        //Agregar botones al panel
        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Creamos un File Chooser para abrir una ventana que permita al usuario elegir un archivo
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Elija un archivo para enviar");
                //Si un archivo es elegido desde el File Chooser, entonces se ejecutan los siguientes comandos
                if (jFileChooser.showOpenDialog(null)  == JFileChooser.APPROVE_OPTION) {
                    // Obtiene el archivo seleccionado
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("El archivo que quiere enviar es: " + fileToSend[0].getName());
                }
            }
        });

        // Agregar al Frame y hacerl o visible
        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);

        // Create a server socket that the server will be listening with.
        ServerSocket serverSocket = new ServerSocket(1234);

        // This while loop will run forever so the server will never stop unless the application is closed.
        while (true) {

            try {
                // Wait for a client to connect and when they do create a socket to communicate with them.
                Socket socket = serverSocket.accept();



        // Envía el archivo cuando el botón se clickea
        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // si aún no se selecciona un archivo, mostramos este mensaje
                if (fileToSend[0] == null) {
                    jlFileName.setText("Por favor, primero elija un archivo para enviar!");
                } else {
                    try {
                        //Crea un input stream en el archivo que se quiere enviar
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        // Create an output stream to write to write to the server over the socket connection.
                        //Crea un OutPutStream para escribir al servidor
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        // Get the name of the file you want to send and store it in filename.
                        String fileName = fileToSend[0].getName();
                        // Convert the name of the file into an array of bytes to be sent to the server.
                        byte[] fileNameBytes = fileName.getBytes();
                        // Create a byte array the size of the file so don't send too little or too much data to the server.
                        byte[] fileBytes = new byte[(int)fileToSend[0].length()];
                        // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                        fileInputStream.read(fileBytes);
                        // Send the length of the name of the file so server knows when to stop reading.
                        dataOutputStream.writeInt(fileNameBytes.length);
                        // Send the file name.
                        dataOutputStream.write(fileNameBytes);
                        // Send the length of the byte array so the server knows when to stop reading.
                        dataOutputStream.writeInt(fileBytes.length);
                        // Send the actual file.
                        dataOutputStream.write(fileBytes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
