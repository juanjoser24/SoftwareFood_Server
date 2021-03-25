/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.unicauca.restaurant.server.access;

import co.unicauca.restaurant.commons.domain.DiaEnum;
import co.unicauca.restaurant.commons.domain.Plato;
import co.unicauca.restaurant.commons.domain.PlatoEjecutivo;
import co.unicauca.restaurant.commons.domain.Restaurant;
import co.unicauca.restaurant.commons.infra.Utilities;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author braia
 */
public class RestaurantRepositoryMysql implements IPlatoRepository {

    /**
     * Conección con Mysql
     */
    private Connection conn;

    public RestaurantRepositoryMysql() {

    }

    /**
     * busca un plato del dia en la base de datos
     *
     * @param id identificador del plato
     * @return true si lo encuentra, false de lo contrario.
     */
    private boolean findPlatoEjecutivo(int id) {
        boolean resultado;
        try {
            this.connect();
            String sql = "select pdia_nombre from platoejecutivo where PEJE_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            resultado = rs.next();
            ps.close();
            this.disconnect();
            return resultado;
        } catch (SQLException ex) {
            System.out.println("revento excepcion encontrar plato_:" + ex.getMessage());
            return false;
        }
    }

    /**
     * actualiza un item de plato del dia en la base de datos.
     *
     * @param clave identificador del plato
     * @param atributo columna a modificar en la base de datos.
     * @param valor nuevo valor para la columna.
     * @return retorna "FALLO" si erra el metodo, identificador de lo contrario.
     */
    @Override
    public String updatePlatoEjecutivo(String clave, String atributo, String valor) {
        if (!this.findPlatoEjecutivo(Integer.parseInt(clave))) {
            return "FALLO";
        }
        try {
            this.connect();
            //String sql = "UPDATE platoejecutivo set "+atributo+" = "+valor+" WHERE PEJE_NOMBRE = "+clave;
            String sql = "UPDATE platoejecutivo SET " + atributo + " = ? WHERE PEJE_ID = ?";
            System.out.println("SENTENCIA SQL UPDATE PLATO EJECUTIVO: " + sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (atributo.equals("PEJE_PRECIO")) {
                int valorNum = Integer.parseInt(valor);
                pstmt.setInt(1, valorNum);
            } else {
                pstmt.setString(1, valor);
            }
            pstmt.setInt(2, Integer.parseInt(clave));

            pstmt.executeUpdate();

            pstmt.close();
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al insertar el registro", ex);
        }
        return clave;
    }

    /**
     * cumunicacion con la base de datos para eliminar un plato del dia
     *
     * @param idPlaE id del plato a eliminar
     * @return
     */
    @Override
    public String deletePlatoEjecutivo(int idPlaE) {
        if (findPlatoEjecutivo(idPlaE)) {
            System.out.println("EXISTE EL ELEMENTO");
        } else {
            System.out.println("NO EXISTE EL ELEMENTO");
            return "FALLO";
        }
        try {
            //primero se establece la conexion
            this.connect(); //validar cuando la conexion no sea exitosa
            //se estructura la sentencia sql en un string
            String sql = "DELETE FROM platoejecutivo WHERE peje_id = (?)";
            //pstmt mantendra la solicitud sobre la base de datos, se asignam sus columnas
            PreparedStatement pstmt = conn.prepareStatement(sql);
            //se compara el id, OJO Ddebe cumplir estrictamente el orden y el tipo de dato(de las tablas)
            pstmt.setInt(1, idPlaE);
            //se ejecuta la sentencia sql
            pstmt.executeUpdate();
            //se cierra
            pstmt.close();
            //se termina la coneccion
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al eliminar el plato", ex);
        }
        return "" + idPlaE;
    }

    /**
     * Permite hacer la conexion con la base de datos
     *
     * @return
     */
    public int connect() {
        try {
            Class.forName(Utilities.loadProperty("server.db.driver"));
            //crea una instancia de la controlador de la base de datos
            //estos datos estan quemados en el archivo propertis, si la base de datos cambia propertis debe modificarse
            String url = Utilities.loadProperty("server.db.url");
            String username = Utilities.loadProperty("server.db.username"); //usuario de la base de datos
            String pwd = Utilities.loadProperty("server.db.password");//contraseña de usuario
            //se establece la coneccion con los datos previos
            conn = DriverManager.getConnection(url, username, pwd);
            if (conn == null) {
                System.out.println("coneccion fallida a la base de datos");
            } else {
                System.out.println("conecion exitosa a la base de datos");
            }
            return 1;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al consultar Customer de la base de datos", ex);
        }
        return -1;
    }

    /**
     * Cierra la conexion con la base de datos
     *
     */
    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.FINER, "Error al cerrar Connection", ex);
        }
    }

    /**
     * Lista el menu desde la consulta hecha a la base de datos añade las tuplas
     * encontradas en una lista de Plato y convierte la lista en json para
     * enviarla por el sockect devuelta al cliente
     *
     * @param resNit
     * @return
     */
    @Override
    public String listarMenuDia(int resNit) {
        List<Plato> list = new ArrayList<>();
        String response = null;
        System.out.println("ingreso al listar Menu Dia");
        try {
            this.connect();
            String sql = "select peje_id,peje_nombre,peje_descripcion,peje_dia, peje_entrada,peje_principio,peje_proteina,peje_bebida,peje_precio, m.mdia_id from (restaurante r inner join menudia m on r.res_nit=m.res_nit) inner join platoejecutivo p on m.mdia_id=p.mdia_id where r.res_nit =" + resNit;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Plato pla = new PlatoEjecutivo(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(9)), rs.getString(3), DiaEnum.valueOf(rs.getString(4)), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), Integer.parseInt(rs.getString(10)));
                list.add(pla);
            }
            response = listToJson(list);
            //se cierra
            pstmt.close();
            //se termina la coneccion
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al listar el menu del dia", ex);
        }
        return response;
    }

    /**
     * Convierte una lista de tipo plato en un json
     *
     * @param list
     * @return
     */
    public String listToJson(List<Plato> list) {
        Gson gson = new Gson();
        String response = gson.toJson(list);
        return response;
    }

    @Override
    public String savePlatoEjecutivo(PlatoEjecutivo instance) {
        try {
            if (findPlatoEjecutivo(instance.getId())) {
                return "FALLO";
            }

            System.out.println("entro");

            //primero se establece la conexion
            this.connect();
            //se estructura la sentencia sql en un string
            String sql = "INSERT INTO platoejecutivo(PEJE_ID,MDIA_ID,PEJE_NOMBRE,PEJE_DESCRIPCION,PEJE_DIA,PEJE_ENTRADA,PEJE_PRINCIPIO,PEJE_BEBIDA,PEJE_PROTEINA,PEJE_PRECIO) VALUES (?,?,?,?,?,?,?,?,?,?)";
            //pstmt mantendra la solicitud sobre la base de datos, se asignam sus columnas
            PreparedStatement pstmt = conn.prepareStatement(sql);
            //se registra cada elemento, OJO Ddebe cumplir estrictamente el orden y el tipo de dato
            pstmt.setInt(1, instance.getId());
            pstmt.setInt(2, instance.getMenuId());
            pstmt.setString(3, instance.getNombre());
            pstmt.setString(4, instance.getDescripcion());
            pstmt.setString(5, String.valueOf(instance.getDiaSemana()));
            pstmt.setString(6, instance.getEntrada());
            pstmt.setString(7, instance.getPrincipio());
            pstmt.setString(8, instance.getBebida());
            pstmt.setString(9, instance.getProteina());
            pstmt.setInt(10, (int) instance.getPrecio());
            //se ejecuta la sentencia sql
            pstmt.executeUpdate();
            //se cierra
            pstmt.close();
            //se termina la coneccion
            this.disconnect();
            return instance.getNombre();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al insertar el registro", ex);
            return "FALLO";
        }
        //lo ideal es retornor un id

    }

    /**
     * guarda un restaurante en la base de datos
     *
     * @param res instancia a guardar
     * @return
     */
    @Override
    public String saveRestaurant(Restaurant res) {
        System.out.println("ingreso a guardar");
        try {
            this.connect();
            String sql = "INSERT INTO restaurante(RES_NIT,RES_NOMBRE) VALUES (?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, res.getNit());
            pstmt.setString(2, res.getNombre());
            pstmt.executeUpdate();
            //se cierra
            pstmt.close();
            //se termina la coneccion
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(RestaurantRepositoryMysql.class.getName()).log(Level.SEVERE, "Error al insertar el registro", ex);
        }
        return res.getNombre();
    }

}
