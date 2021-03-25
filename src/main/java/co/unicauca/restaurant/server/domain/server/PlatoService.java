/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.unicauca.restaurant.server.domain.server;

import co.unicauca.restaurant.commons.domain.PlatoEjecutivo;
import co.unicauca.restaurant.commons.domain.Restaurant;
import co.unicauca.restaurant.server.access.IPlatoRepository;

/**
 *
 * @author braia
 */
public class PlatoService {

    /**
     * repositorio de platos, via de comunicacion a bajo nivel
     */
    IPlatoRepository repositorio;

    /**
     * constructor parametrizado que hace inyeccion de dependencias
     *
     * @param repositorio repositorio a la base de datos, tipo IPlatoRepositorio
     */
    public PlatoService(IPlatoRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * envia la solicitud a la capa de bajo nivel para guardar un plato del dia
     * en la base de datos
     *
     * @param plato instancia a guardar
     * @return
     */
    public String savePlatoEjecutivo(PlatoEjecutivo plato) {
        //hacer validaciones aqui OJO aqui no se han hecho pero deben hacerse
        //comprobar que los datos enviados sean correctos y en caso de ids que no esten repetidos
        return repositorio.savePlatoEjecutivo(plato);
    }

    public String saveRestaurant(Restaurant res) {
        return repositorio.saveRestaurant(res);
    }

    /**
     * modifica un plato del dia en la base de datos
     *
     * @param clave identificador del plato
     * @param atributo columna de la base de datos a modificar
     * @param valor nuevo valor para la celda
     * @return retorno "FALLO" en caso de error
     */
    public String updatePlatoEjecutivo(String clave, String atributo, String valor) {
        //hacer validaciones, conversion del valor
        return repositorio.updatePlatoEjecutivo(clave, atributo, valor);
    }

    /**
     * envia la solicitud a la capa de bajo nivel para eliminar un plato dia
     *
     * @param idPlaE instancia de plato especial a guardar
     * @return
     */
    public String deletePlatoEjecutivo(int idPlaE) {
        return repositorio.deletePlatoEjecutivo(idPlaE);
    }

    public String listarMenuDia(int resNit) {
        return repositorio.listarMenuDia(resNit);
    }

}
