import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        coneccion();
        //Estas son las sentencias de las dos tablas para que puedas comprobar el funcionamiento
        String sqlCreaciónTabla = "CREATE TABLE Ciclos (Cod_pincel char(3) PRIMARY KEY, Nombre char(50),  Grado char(1),Modalidad char(1))";
        String sql2CreaciónTabla= "CREATE TABLE Alumnos ( Nif char(9) PRIMARY KEY, Nombre char(40),  Cial char(10),Telefono char(9),Cod_pincel char(3)," +
                " CONSTRAINT fk_Ciclos FOREIGN KEY (Cod_pincel) REFERENCES Ciclos(Cod_pincel))";
        File sentences = new File("src/SentenciasSQL.sql");
        try {
//            insert(); //Esto solo se ejecuta una vez.

            FileReader fileRsen = new FileReader(sentences);
            BufferedReader bfSen = new BufferedReader(fileRsen);
            String lineSentence;
            ArrayList<String> sentencias= new ArrayList<>(1);
            while ((lineSentence = bfSen.readLine()) != null) {
                sentencias.add(lineSentence);
            }
            //Aqui desarrollaré el punto 4.a
            //Esta parte  es el isnert de los alumnos. Solo ejecutar una vez.
            PreparedStatement stmtInsertAlumnos = coneccion().prepareStatement(sentencias.get(0));
            File fileInsertAlumnos = new File("src/insertAlumnos.sql");
            //Leemos el fichero y lo separamos por ,
            try {
                FileReader insertAlumnos = new FileReader(fileInsertAlumnos);
                BufferedReader bfAlumnos = new BufferedReader(insertAlumnos);
                String line;
                while ((line = bfAlumnos.readLine()) != null) {
                    String[] elementos = line.split(",");
                    stmtInsertAlumnos.setString(1, elementos[0]);
                    stmtInsertAlumnos.setString(2, elementos[1]);
                    stmtInsertAlumnos.setString(3, elementos[2]);
                    stmtInsertAlumnos.setString(4, elementos[3]);
                    stmtInsertAlumnos.setString(5, elementos[4]);
                    stmtInsertAlumnos.executeUpdate();
                }
                stmtInsertAlumnos.close();
                bfAlumnos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //Pondre el select de la tabla Alumnos para comprobar la sentencia A
                selectAlumnos();
            //Compruebo la sentencia B
            String sqlSentenceB = sentencias.get(1);
            PreparedStatement stmB = coneccion().prepareStatement(sqlSentenceB);
            ResultSet rSentenceB = stmB.executeQuery();
            while(rSentenceB.next()){
                System.out.println("Nombre: " + rSentenceB.getString(1) + "TLF: " + rSentenceB.getString(2) + "Ciclo: " + rSentenceB.getString(3));
            }

            //Compruebo la sentencia C

            String sqlSentenceC = sentencias.get(2);
            PreparedStatement stmC = coneccion().prepareStatement(sqlSentenceC);
            ResultSet rSentenceC = stmC.executeQuery();
            while(rSentenceC.next()){
                System.out.println("Ciclo: " + rSentenceC.getString(1) + "  Numero_Alumnos_Matriculados: " + rSentenceC.getInt(2) );
            }

            //Modifico la modalidad distancia por presencial
            PreparedStatement stmtUpdateCiclos = coneccion().prepareStatement(sentencias.get(3));
            stmtUpdateCiclos.executeUpdate();
            stmtUpdateCiclos.close();
            //Hago un select en Ciclos para comprobar la sentencia D
            selectCiclos();


            //Llamaré al procedimiento y comprobaré de nuevo que se ha ejecutado correctamente. Debería borrarse los ciclos de modalidad S y grado M
            try{
                String sqlProcedure = "{call DELETEPROCEDURE(?, ?)}";
                PreparedStatement prepareCall = coneccion().prepareCall(sqlProcedure);
                prepareCall.setString(1, "M");
                prepareCall.setString(2, "S");
                prepareCall.execute();
                System.out.println("Procedimiento ejecutado correctamente.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //Vuelvo a hacer un select de la tabla ciclos para comprobar que el procedimiento se ha ejecutado correctamente.
            selectCiclos();
            //Cierro la conección a base de datos
            coneccion().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection coneccion() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "AZAEL", "1234");
            return con;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insert() {
        //Esto es la insercción de datos del apartado número dos
        try {
            coneccion();
            File fileInsertCiclos = new File("src/insertCiclos.sql");
            String InsertSqlCiclos = "Insert into Ciclos VALUES(?,?,?,?)";
            PreparedStatement stmtInsert = coneccion().prepareStatement(InsertSqlCiclos);
            try {
                FileReader insertCiclos = new FileReader(fileInsertCiclos);
                BufferedReader bf1 = new BufferedReader(insertCiclos);
                String line;
                while ((line = bf1.readLine()) != null) {
                    String[] elementos = line.split(",");
                    stmtInsert.setString(1, elementos[0]);
                    stmtInsert.setString(2, elementos[1]);
                    stmtInsert.setString(3, elementos[2]);
                    stmtInsert.setString(4, elementos[3]);
                    stmtInsert.executeUpdate();
                }
                stmtInsert.close();
                bf1.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void selectCiclos() {
        //Esta función hace un select a la tabla Ciclos
        coneccion();
        try {

            String sql = "SELECT * FROM Ciclos";
            PreparedStatement stmt =  coneccion().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Cod_pincel: " + rs.getString(1) +
                        " Nombre: " + rs.getString(2) +
                        " Grado: " + rs.getString(3) +
                        " Modalidad: " + rs.getString(4));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void selectAlumnos(){
            //Esta función hace un select a la tabla Alumnos
        coneccion();
            try {
                String sql = "SELECT * FROM Alumnos";
                PreparedStatement stmtAlum =  coneccion().prepareStatement(sql);
                ResultSet rs = stmtAlum.executeQuery();
                while (rs.next()) {
                    System.out.println("NIF: " + rs.getString(1) +
                            " Nombre: " + rs.getString(2) +
                            " Cial: " + rs.getString(3) +
                            " Telefono: " + rs.getString(4) +
                            " Cod_pincel: " + rs.getString(5));

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
}
