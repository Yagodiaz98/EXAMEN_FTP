import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class SubirFicheros {
    //INICIAMOS SESION CON EL CLIENTE
    public static FTPClient cliente = new FTPClient();
    public static boolean login;
    public static int decision1;
    public static int decision2;
    public static Scanner sc = new Scanner(System.in);


    public static void main(String[] arg) throws Exception{
        iniciarSesion();
        if(login){
            System.out.println("¿Qué quieres hacer?");
            System.out.println("1.- Subir fichero");
            System.out.println("2.- Descargar fichero");
            decision1= Integer.parseInt(sc.nextLine());

            switch(decision1){
                case 1:
                    System.out.println("Pulsa 1 para subir fichero pasandole el nombre");
                    System.out.println("Pulsa 2 para subir fichero con file chooser");
                    System.out.println("Pulsa 3 para subir toda la carpeta pasando ruta");
                    System.out.println("Pulsa 4 para subir fichero con un nombre diferente");
                    decision2=Integer.parseInt(sc.nextLine());
                    switch (decision2){
                        case 1:
                            System.out.println("¿Cuántos archivos quieres subir?");
                            int numveces = Integer.parseInt(sc.nextLine());
                            int contador = 0;
                            while (contador < numveces) {
                                System.out.println("Dime el nombre del archivo que se suba");
                                String nombre = sc.nextLine();
                                subirFicheroNombre(nombre);
                                contador++;

                            }
                            break;
                        case 2:
                            subirFicheroFileChooser();
                            break;
                        case 3:
                            System.out.println("Escribe la ruta de la carpeta que quieres subir");
                            String ruta= sc.nextLine();
                            String rutaBarras=ruta.replace("\\", "\\\\");
                            File carpeta=new File(rutaBarras);
                            String[] listado=carpeta.list();
                            if (listado == null || listado.length==0){
                                System.out.println("La carpeta está vacía");
                                return;
                            }else{
                                for (int i=0; i< listado.length;i++){
                                    System.out.println(listado[i]);
                                    subirFicheroRuta(rutaBarras+"\\"+listado[i],listado[i]);
                                }
                            }
                            break;
                        case 4:
                            System.out.println("Introduce la ruta del archivo que quieres subir");
                            ruta= sc.nextLine();
                            rutaBarras=ruta.replace("\\", "\\\\");
                            System.out.println("Dime el nombre del archivo que se suba");
                            String nombre = sc.nextLine();
                            subirFicheroCambiandoNombre(rutaBarras,nombre);
                            break;
                    }
                case 2:
                    System.out.println("Pulsa 1 para descargar fichero pasandole el nombre");
                    System.out.println("Pulsa 2 para descargar fichero entero y decidir con FileChooser donde descargarlo");
                    //System.out.println("Pulsa 3 para descargar toda la carpeta");
                    //System.out.println("Pulsa 4 para descargar fichero y guardarlo con un nombre diferente");
                    decision2=Integer.parseInt(sc.nextLine());
                    switch (decision2){
                        case 1:
                            System.out.println("¿Cuántos archivos quieres descargar?");
                            int numveces = Integer.parseInt(sc.nextLine());

                            int contador = 0;

                            while (contador < numveces) {
                                System.out.println("Dime el nombre del archivo que quieres descargar");
                                String nombre = sc.nextLine();
                                descargarFicheroNombre(nombre);
                                contador++;
                            }
                            break;
                        case 2:
                            descargarDirectorioEnteroFileChooser();
                            break;
                    }
            }
        }

    }

    public static void iniciarSesion() throws IOException {
        //La clase FTPClient esta integrada en java

        String servidor = "localhost";//Tambien puede ser localhost
        String user = "admin";
        String pasw = "1";


        System.out.println("Conectandose a " + servidor);

        //CONECTAMOS CON EL SERVIDOR
        cliente.connect(servidor);
        login = cliente.login(user, pasw);

        //Fijo conexión binaria y pasiva. Declaro direc que va a ser el directorio del FTP al que subo el archivo
        cliente.setFileType(FTP.BINARY_FILE_TYPE);
        //Comprobamos si se ha hecho login o no
        if(login){
            System.out.println("Login correcto");
            cliente.enterLocalPassiveMode(); //La conexion es en modo pasivo, que es más segura
        }else{
            System.out.println("Login incorrecto");
        }
    }

    public static void cerrarSesion() throws IOException{
        cliente.logout();
        cliente.disconnect();
    }

    public static void crearDirectorio(String direc) throws IOException{//Para crear el directorio en la carpeta del FileZilla
        //Si no puede cambiarse al directorio direc
        if (!cliente.changeWorkingDirectory(direc)) {
            String directorio = "NUEVODIREC";
            //Aquí lo crea
            if (cliente.makeDirectory(directorio)) {
                System.out.println("Directorio :  " + directorio + " creado ...");
                cliente.changeWorkingDirectory(directorio);//Aquí como ya está creado se cambia
            } else {
                System.out.println("No se ha podido crear el Directorio");
                System.exit(0);
            }

        }
        //Muestro el directorio actual del FTP
        System.out.println("Directorio actual: " +  cliente.printWorkingDirectory());
    }
    public static void subirFicheroNombre(String nombreArchivo) throws IOException{
        iniciarSesion();//Iniciamos sesion ya que cuando el bucle se hace mas de 1 vez, la sesion se cierra antes

        String direc = "/NUEVODIREC"; //Se indica directorio destino

        crearDirectorio(direc);

        String archivo ="C:\\Users\\llago\\Desktop\\Prueba_PSP\\"+nombreArchivo;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));

        if (cliente.storeFile(nombreArchivo, in))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");

        in.close(); // Cerrar flujo
        cerrarSesion();
    }

    public static void subirFicheroFileChooser() throws IOException{
        String direc = "/NUEVODIREC";

        crearDirectorio(direc);

        //Creo y abro un file chooser para leer el archivo de mi ordenador que quiero subir al FTP
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        String rutaynombre="";

        //Guardo la ruta completa + el nombre del archivo que ha seleccionado el usuario
        rutaynombre = fileChooser.getSelectedFile().getAbsolutePath();

        //Guardo el nombre sin ruta del archivo que ha seleccionado el usuario
        String nombresinruta=fileChooser.getSelectedFile().getName();//Esta linea es para coger solo el nombre

        //En la rutaynombre cambio las contrabarras por dobles contrabarras
        rutaynombre=rutaynombre.replace("\\", "\\\\");
        System.out.println(rutaynombre);

        //Guardo el contenido del archivo rutaynombre en un buffer
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(rutaynombre));

        //Estas dos lineas comentadas son lo mismo que la liena de arriba para coger solo el nombre

        /*String[] arrayarchivo=archivo.split("\\\\");
        String archivoDestino=arrayarchivo[arrayarchivo.length-1];*/

        //Almaceno el contenido del buffer en el servidor FTP
        if (cliente.storeFile(nombresinruta, in))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");

        in.close(); // Cerrar flujo
        cerrarSesion();
    }

    public static void subirFicheroRuta(String ruta,String nombre) throws IOException{
        iniciarSesion();//Porque es necesario iniciar sesion aqui¿?¿?¿? Si lo quito solo pasa un archivo porque cierra sesion antes de pasar al 2º
        String direc = "/NUEVODIREC"; //Se indica directorio destino

        crearDirectorio(direc);

        String archivo =ruta;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(ruta));

        if (cliente.storeFile(nombre, in))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");

        in.close(); // Cerrar flujo
        cerrarSesion();
    }
    public static void subirFicheroCambiandoNombre(String ruta,String nombre) throws IOException{

        String direc = "/NUEVODIREC"; //Se indica directorio destino

        crearDirectorio(direc);

        String archivo =ruta+"\\"+nombre;
        BufferedInputStream in = new BufferedInputStream (new FileInputStream(archivo));
        //String nuevoNombre=JOptionPane.showInputDialog("Dime el nuevo nombre para guardar el archivo");
        System.out.println("Dime el nombre que quieres que se le aplique al archivo");
        String nuevoNombre=sc.nextLine();
        if (cliente.storeFile(nuevoNombre, in))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");

        in.close(); // Cerrar flujo
        cerrarSesion();
    }

    public static void descargarFicheroNombre(String nombre)throws IOException{//No entiendo porque s va yvuelve al bucle antes de imprimir que el fichero no existe en el server
        iniciarSesion();

        //descargar fichero
        String direc = "/Nuevo";
        if(cliente.changeWorkingDirectory(direc)){
            //stream de salida para recibir el fichero descargado
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("C:\\Users\\llago\\Desktop\\Prueba_PSP\\"+nombre));

            if(cliente.retrieveFile(nombre, out))
                System.out.println("Recuperado correctamente... ");
            else
                System.out.println("No se ha podido descargar... ");

            out.close();

            cerrarSesion();
        }else{
            System.out.println("No existe el fichero especificado en código en el servidor");
        }

    }

    public static void descargarDirectorioEnteroFileChooser()throws IOException{
        iniciarSesion();

        FTPFile[] ficheros = cliente.listFiles();
        System.out.println(Arrays.toString(ficheros));

        System.out.println("Ficheros en la raiz: ");
        for(FTPFile fichero :ficheros){//QUE HACEN LOS ":" ¿?¿?¿?¿?¿?¿¿?¿?
            System.out.println(fichero.getName());
        }
        if (ficheros == null || ficheros.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
            return;
        } else {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.showOpenDialog(fileChooser);
            String ruta="";
            //Guardo la ruta completa + el nombre del archivo que ha seleccionado el usuario
            ruta = fileChooser.getCurrentDirectory().getAbsolutePath();

            //En la rutaynombre cambio las contrabarras por dobles contrabarras
            ruta=ruta.replace("\\", "\\\\");

            //crearDirectorio(ruta);//Comentando esta linea deniega el acceso igualmente

            for (int i=0; i< ficheros.length; i++) {
                System.out.println(ficheros[i].getName());

                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ruta+"\\"+ficheros[i].getName()));//RUTA DE DESTINO DEL ARCHIVO CON SU NOMBRE

                if(cliente.retrieveFile(ficheros[i].getName(), out))
                    System.out.println("Recuperado correctamente... ");
                else
                    System.out.println("No se ha podido descargar... ");

                out.close();
            }
        }
        cerrarSesion();
    }
}
