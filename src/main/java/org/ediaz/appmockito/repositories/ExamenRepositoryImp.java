package org.ediaz.appmockito.repositories;

import org.ediaz.appmockito.Datos;
import org.ediaz.appmockito.models.Examen;

import java.util.List;

public class ExamenRepositoryImp implements ExamenRepository{

    @Override
    public List<Examen> findAll() {
        return Datos.EXAMENES;
    }

    @Override
    public Examen save(Examen examen) {
        return examen;
    }
}
