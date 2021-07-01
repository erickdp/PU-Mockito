package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;

import java.util.Optional;

public class ExamenServiceImp implements ExamenService {

    private ExamenRepository examenRepository;

    public ExamenServiceImp(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return this.examenRepository.findAll() // Con mock toma esta ejecucion
                .stream()
                .filter(e -> e.getNombre().contains(nombre))
                .findFirst();
//        Devuelve optional findFirst que es una representacion del objeto que evita los nulos
    }
}
