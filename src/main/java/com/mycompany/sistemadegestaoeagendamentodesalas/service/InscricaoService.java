package com.mycompany.sistemadegestaoeagendamentodesalas.service;

import com.mycompany.sistemadegestaoeagendamentodesalas.dao.InscricaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.DisciplinaDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.model.Disciplina;
import java.util.List;

public class InscricaoService {
    private InscricaoDAO inscricaoDAO = new InscricaoDAO();
    private EstudanteDAO estudanteDAO = new EstudanteDAO();
    private DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    public boolean inscreverEstudante(int estudanteId, int disciplinaId) {
        Estudante estudante = estudanteDAO.buscarEstudantePorId(estudanteId);
        Disciplina disciplina = disciplinaDAO.buscarPorId(disciplinaId);
        if (estudante == null || disciplina == null) {
            return false;
        }

        return inscricaoDAO.salvar(new Inscricao(0, estudante, disciplina));
    }

    public List<Inscricao> listarInscricoesPorEstudante(int estudanteId) {
        return inscricaoDAO.buscarPorEstudante(estudanteId);
    }

    public List<Inscricao> listarInscricoesPorDisciplina(int disciplinaId) {
        return inscricaoDAO.buscarPorDisciplina(disciplinaId);
    }
}
