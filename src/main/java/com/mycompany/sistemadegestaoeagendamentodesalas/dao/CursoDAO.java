package main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dao;
import main.java.com.mycompany.sistemadegestaoeagendamentodesalas.dto1.Curso;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class CursoDAO {
    private static final String file ="files/Curso.txt";

    public void salvar(Curso dp){
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(file, true))){
            bw.write(dp.toString());
            bw.newLine();
        }catch(IOException e){System.out.println("Erro ao salvar Curso "+e.getMessage());}
    }

    public List<Curso> listaCurso(){
        List<Curso> lista=new ArrayList<>();
        String linha;
        try(BufferedReader br= new BufferedReader(new FileReader(file))){
            while((linha=br.readLine())!=null){
                String []dados=linha.split("; ");
                if(dados.length==2){
                    lista.add(new Curso(Integer.parseInt(dados[0]), dados[1]));
                }
            }
        }catch(IOException e){System.out.println("Erro ao ler o ficheiro "+e.getMessage());}
        return lista;
    }

    public String buscarPorCurso(int id){
        List<Curso> lista=listaCurso();
        String nome;

        for(Curso dp:lista){
            if(id==dp.getId()){
                nome=dp.getNome();
                return nome;
            }
        }
        return "Curso nao encontrado";
    }

    public int gerarProximoId(){
        int max = 0;
        for(Curso dp : listaCurso()){
            max = Math.max(max, dp.getId());
        }
        return max + 1;
    }

    public Curso buscarCursoPorId(int id) {
        List<Curso> lista = listaCurso();
        for (Curso curso : lista) {
            if (id == curso.getId()) {
                return curso;
            }
        }
        return null;
    }

    public void editarCurso(int id, String novoNome) {
        List<Curso> lista = listaCurso();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                Curso curso = lista.get(i);
                curso.setNome(novoNome);
                lista.set(i, curso);
                reescreverArquivo(lista);
                return;
            }
        }
    }

    public void reescreverArquivo(List<Curso> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Curso curso : lista) {
                bw.write(curso.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao reescrever arquivo de Cursos: " + e.getMessage());
        }
    }
}
