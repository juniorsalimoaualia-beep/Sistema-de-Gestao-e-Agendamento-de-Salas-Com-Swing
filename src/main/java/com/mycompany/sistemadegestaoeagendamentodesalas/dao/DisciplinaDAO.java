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

    public void salvar(Disciplina ds){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file,true))){
            bw.write(ds.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar o departamento "+e.getMessage());}
    }

    public List<Disciplina> listaDisciplina(){
        List<Disciplina> lista= new ArrayList<>();
        String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String[]dados=linha.split("; ");
                if(dados.length==3){
                    lista.add(new Disciplina(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2]
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao listar disciplinas "+e.getMessage());
        }
        return lista;
    }

    public List<Disciplina> listarPorDocenteId(int docenteId) {
        DocenteDAO docenteDAO = new DocenteDAO();
        Docente docente = docenteDAO.buscarPorDocente(docenteId);
        if (docente == null) {
            return new ArrayList<>();
        }
        String docenteNome = docente.getNomeCompleto();
        List<Disciplina> lista = listaDisciplina();
        List<Disciplina> resultado = new ArrayList<>();
        for (Disciplina ds : lista) {
            if (docenteNome.equals(ds.getDocente())) {
                resultado.add(ds);
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

}
