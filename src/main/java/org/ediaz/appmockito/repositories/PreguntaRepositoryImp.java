package org.ediaz.appmockito.repositories;

import org.ediaz.appmockito.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImp implements PreguntaRepository {

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntaRepositoryImp.findPreguntasPorExamenId");
        try {
            TimeUnit.SECONDS.sleep(2L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImp.guardarVarias");
    }
}
