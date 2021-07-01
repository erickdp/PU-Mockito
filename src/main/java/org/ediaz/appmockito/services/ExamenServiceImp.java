package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;

public class ExamenServiceImp implements ExamenService {

    private ExamenRepository examenRepository;

    public ExamenServiceImp(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Examen findExamenPorNombre(String nombre) {
        var exmaneOpcional = this.examenRepository.findAll()
                .stream()
                .filter(e -> e.getNombre().contains(nombre))
                .findFirst();
//        Devuelve optional findFirst que es una representacion del objeto que evita los nulos
        Examen examen = null;
        if(exmaneOpcional.isPresent()){
            examen = exmaneOpcional.orElseThrow();
        }
        return examen;
    }
}
