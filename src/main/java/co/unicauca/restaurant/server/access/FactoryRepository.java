/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.unicauca.restaurant.server.access;

/**
 *
 * @author braia
 */
public class FactoryRepository {

    private static FactoryRepository instance;

    private FactoryRepository() {
    }

    /**
     * retorno solo una instancia de fabrica singleton
     *
     * @return instancia de FabricaRepositorio
     */
    public static FactoryRepository getInstance() {
        if (instance == null) {
            instance = new FactoryRepository();
        }
        return instance;
    }

    /**
     * retorna un repositorio
     *
     * @return instancia del repositorio
     */
    public IPlatoRepository getRepository() {
        IPlatoRepository repository = new RestaurantRepositoryMysql();
        //IPlatoRepositorio repositorio = new RestauranteRepositorioDeveloper();
        return repository;
    }
}
