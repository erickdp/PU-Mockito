package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;
import org.ediaz.appmockito.repositories.ExamenRepository;
import org.ediaz.appmockito.repositories.PreguntaRepository;

import java.util.Optional;

public class ExamenServiceImp implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImp(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.preguntaRepository = preguntaRepository;
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

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        var examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()) { // Si existe la instancia
            examen = examenOptional.orElseThrow(); // Dame la instancia o envia una excepcion
            var preguntas = this.preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }
}
