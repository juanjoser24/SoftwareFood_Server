/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.unicauca.restaurant.server.access;

import co.unicauca.restaurant.commons.domain.PlatoEjecutivo;
import co.unicauca.restaurant.commons.domain.Restaurant;

/**
 *
 * @author braia
 */
public interface IPlatoRepository {

    /**
     * registrar una tupla en la base de datos
     *
     * @param instance objeto plato dia que se desea almacenar
     * @return
     */
    public String savePlatoEjecutivo(PlatoEjecutivo instance);

    public String saveRestaurant(Restaurant res);

    /**
     * hace un update sobre la tabla platoEjecutivo
     *
     * @param clave valor con el que se encuentra la tupla
     * @param atributo columna a modificar
     * @param valor nuevo valor
     * @return
     */
    public String updatePlatoEjecutivo(String clave, String atributo, String valor);
    /*
     * elimina una tupla en la base de datos
     * @param idPlaE id plato  que se desea borrar
     * @return 
     */
    public String deletePlatoEjecutivo(int idPlaE);
    
    /**
     * lista todas las tuplas de los menus
     *
     * @param resNit id del restaurante del que se va a mostrar el menu
     * @return
     */
    public String listarMenuDia(int resNit);
}
