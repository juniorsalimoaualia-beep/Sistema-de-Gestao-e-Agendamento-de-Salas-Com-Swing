package com.mycompany.sistemadegestaoeagendamentodesalas.service;

import com.mycompany.sistemadegestaoeagendamentodesalas.dao.InscricaoDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.EstudanteDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dao.DisciplinaDAO;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Inscricao;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Estudante;
import com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
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
        // Verificar se já está inscrito
        List<Inscricao> inscricoes = inscricaoDAO.buscarPorEstudante(estudanteId);
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getDisciplina().getId() == disciplinaId) {
                System.out.println("Erro: Estudante já inscrito nesta disciplina.");
                return false; // Já inscrito
            }
        }
        int id = inscricaoDAO.gerarProximoId();
        Inscricao inscricao = new Inscricao(id, estudante, disciplina);
        inscricaoDAO.salvar(inscricao);
        return true;
    }

    public boolean inscreverEstudanteAutomatico(int estudanteId, int disciplinaId) {
        Estudante estudante = estudanteDAO.buscarEstudantePorId(estudanteId);
        Disciplina disciplina = disciplinaDAO.buscarPorId(disciplinaId);
        if (estudante == null || disciplina == null) {
            return false;
        }
        // Verificar se já está inscrito
        List<Inscricao> inscricoes = inscricaoDAO.buscarPorEstudante(estudanteId);
        for (Inscricao inscricao : inscricoes) {
            if (inscricao.getDisciplina().getId() == disciplinaId) {
                return false; // Já inscrito
            }
        }
        int id = inscricaoDAO.gerarProximoId();
        Inscricao inscricao = new Inscricao(id, estudante, disciplina);
        inscricaoDAO.salvar(inscricao);
        System.out.println("Estudante " + estudante.getNomeCompleto() + " inscrito automaticamente na disciplina " + disciplina.getNome());
        return true;
    }

    public List<Inscricao> listarInscricoesPorEstudante(int estudanteId) {
        return inscricaoDAO.buscarPorEstudante(estudanteId);
    }

    public List<Inscricao> listarInscricoesPorDisciplina(int disciplinaId) {
        return inscricaoDAO.buscarPorDisciplina(disciplinaId);
    }
}
