package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Disciplina;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Docente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class DisciplinaDAO {
    private static final String file= "files/disciplina.txt";
    private DocenteDAO docenteDAO = new DocenteDAO();

    public void salvar(Disciplina ds){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file,true))){
            bw.write(ds.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar a disciplina "+e.getMessage());}
    }

    public List<Disciplina> listaDisciplina(){
        List<Disciplina> lista= new ArrayList<>();
        String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String[]dados=linha.split("; ");
                if(dados.length>=3){
                    int id = Integer.parseInt(dados[0]);
                    String nome = dados[1];
                    int docenteId = Integer.parseInt(dados[2]);
                    Docente docente = docenteDAO.buscarPorDocente(docenteId);
                    if(docente == null){
                        docente = new Docente(docenteId, "", "", "", 0, "", "");
                    }
                    String curso = (dados.length >= 4) ? dados[3] : "";
                    lista.add(new Disciplina(id, nome, docente, curso));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao listar disciplinas "+e.getMessage());
        }
        return lista;
    }

    public List<Disciplina> listarPorDocente(Docente docente) {
        List<Disciplina> lista = listaDisciplina();
        List<Disciplina> resultado = new ArrayList<>();
        if(docente != null) {
            for (Disciplina ds : lista) {
                if (ds.getDocente() != null && ds.getDocente().getId() == docente.getId()) {
                    resultado.add(ds);
                }
            }
        }
        return resultado;
    }

    public List<Disciplina> listarPorCurso(String curso) {
        List<Disciplina> lista = listaDisciplina();
        List<Disciplina> resultado = new ArrayList<>();
        if(curso != null && !curso.isEmpty()) {
            for (Disciplina ds : lista) {
                if (ds.getCurso() != null && ds.getCurso().equalsIgnoreCase(curso)) {
                    resultado.add(ds);
                }
            }
        }
        return resultado;
    }

    public Disciplina buscarPorId(int id){
        List<Disciplina> lista=listaDisciplina();
        for(Disciplina dsc: lista){
            if(id==dsc.getId()){
                return dsc;
            }
        }
        return null;
    }

    public int gerarProximoId(){
        int max = 0;
        for(Disciplina ds : listaDisciplina()){
            max = Math.max(max, ds.getId());
        }
        return max + 1;
    }

    public Disciplina buscarPorNome(String nome) {
        if (nome == null) return null;
        List<Disciplina> lista = listaDisciplina();
        for (Disciplina d : lista) {
            if (d.getNome() != null && d.getNome().equalsIgnoreCase(nome.trim())) {
                return d;
            }
        }
        return null;
    }

}
