import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

    // Se crea un ArrayList estático que guardará la información sobre los archivos recibidos.
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // Used to track the file (jpanel that has the file name in it on a label).
        int fileId = 0;

        //Se crea un Frame prncipal
        JFrame jFrame = new JFrame("Servidor");
        jFrame.setSize(500, 500);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);

        //Se crea un panel en el que se mostrarán los archivos que se enviaron al servidor
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        // Se pone un scroll vertical en la pantalla para poder navegar hacia arriba o abajo en el panel de los archivos que se subieron al servidor
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Título de la ventana
        JLabel jlTitle = new JLabel("Archivos recibidos:");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Se añade el contenido al Frame principal y se hace visible
        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        // Create a server socket that the server will be listening with.
        ServerSocket serverSocket = new ServerSocket(1234);

        // Se crea un bucle infinito para que el servidor nunca termine a menos que la aplicación se cierre.
        while (true) {

            try {
                // Se crea un socket para comunicarse con el cliente una vez que este se conecte.
                Socket socket = serverSocket.accept();

                // Se crea un InputStream que recibe la información del cliente a través del socket
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                // Se lee el tamaño del nombre del archivo.
                int fileNameLength = dataInputStream.readInt();
                // Si el archivo existe, se ejecuta el argumento del condicional
                if (fileNameLength > 0) {
                    // Se crea un array de bytes que guarda el nombre del archivo
                    byte[] fileNameBytes = new byte[fileNameLength];
                    // Se lee el InputStream (que recibe la info del cliente) y se guarda en el array de bytes.
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);
                    //Se lee cuanta información se espera del contenido del archivo
                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                        JLabel jlFileName = new JLabel(fileName);
                       //jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10,0, 10,0));
                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            // Se establece que el nombre sea el fileId, de tal forma que se pueda obtener el archivo correcto desde el panel.
                            jpFileRow.setName((String.valueOf(fileId)));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        } else {

                            jpFileRow.setName((String.valueOf(fileId)));
                            jpFileRow.addMouseListener(getMyMouseListener());
                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        }

                        // Se añade el archivo al arrayList que guarda nuestra información
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        // Se incrementa el fileID para que puedan seguir agregándose archivos
                        fileId++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param fileName
     * @return The extension type of the file.
     */
    public static String getFileExtension(String fileName) {
        // Get the file type by using the last occurence of . (for example aboutMe.txt returns txt).
        // Will have issues with files like myFile.tar.gz.
        int i = fileName.lastIndexOf('.');
        // If there is an extension.
        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }

    /**
     * Cuando se hace click en el nombre del archivo, se abre una ventana nueva (método createFrame) para verificar si el usuario quiere o no descargar el archivo.
     * @return A mouselistener that is used by the jpanel.
     */
    public static MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the source of the click which is the JPanel.
                JPanel jPanel = (JPanel) e.getSource();
                // Get the ID of the file.
                int fileId = Integer.parseInt(jPanel.getName());
                // Loop through the file storage and see which file is the selected one.
                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }
//Se crea la ventana que sirve para previsualizar el archivo a descargar y para confirmar si se desea descargar o no.
    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        JFrame jFrame = new JFrame("Descarga de archivo");
        jFrame.setSize(500, 500);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));


        JLabel jlPrompt = new JLabel("¿Estás seguro de descargar el archivo " + fileName + "?");
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20,0,10,0));
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Se crea el boton SI
        JButton jbYes = new JButton("Si");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));
        jbYes.setFocusable(false);
        jbYes.setBackground (new Color (-394241));

        // Se crea el botón NO
        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));
        jbNo.setFocusable(false);
        jbNo.setBackground (new Color (-394241));


        // Se crea un Label para mostrar el contenido del archivo que se quiere descargar
        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Panel que mantiene el label para mostrar el contenido del archivo.
        JPanel jpFileContent = new JPanel();
        jpFileContent.add(jlFileContent);


        // Panel que mantiene los botones sí y no
        JPanel jpButtons = new JPanel();
        // Espaciado alrededor del panel
        jpButtons.setBorder(new EmptyBorder(100, 0, 10, 0));
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        // Si el archivo es de texto, se muestra el texto
        if (fileExtension.equalsIgnoreCase("txt")) {
            // Wrap it with <html> so that new lines are made.
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            // Si el archivo es una imagen, se ve la imagen.
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        // Si se presiona Sí, se descarga.
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se crea el archivo con su nombre
                File fileToDownload = new File(fileName);
                try {
                    //  Se crea un OutputStream para plasmar el texto del archivo en el descargado
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    // Se copia el texto del archivo en el descargado.
                    fileOutputStream.write(fileData);
                    // Se cierra el copiado de información
                    fileOutputStream.close();
                    // Se cierra la ventana después de clickear Sí
                    jFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // Si se presiona No, se cierra la ventana.
        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jlPrompt);
        jPanel.add(jpFileContent);
        jPanel.add(jpButtons);

        jFrame.add(jPanel);
        jFrame.setLocationRelativeTo(null);

        return jFrame;

    }


}