package com.lima.api_alunos.service;

import java.util.ArrayList;
import java.util.List;

import com.lima.api_alunos.exception.AlunoNaoEncontradoException;
import com.lima.api_alunos.exception.EmailExistenteException;
import org.springframework.stereotype.Service;

import com.lima.api_alunos.dto.AlunoRequest;
import com.lima.api_alunos.dto.AlunoResponse;
import com.lima.api_alunos.model.Aluno;

@Service
public class AlunoService {

    private final List<Aluno> alunos;

    private int id = 1;

    public AlunoService() {
        alunos = new ArrayList<Aluno>();
    }

    public List<AlunoResponse> listarAlunos() {
        List<Aluno> alunosModel = alunos;

        List<AlunoResponse> alunosResponse = new ArrayList<>();

        for (Aluno a : alunosModel) {
            alunosResponse
                    .add(new AlunoResponse(a.getId(), a.getNome(), a.getEmail(), a.getDataNascimento(), a.getMedia()));
        }
        return alunosResponse;

    }

    public AlunoResponse obterAlunoPorId(int id) {
        for (Aluno a : alunos) {
            if (a.getId() == id) {
                return new AlunoResponse(id, a.getNome(), a.getEmail(), a.getDataNascimento(), a.getMedia());
            }
        }
        throw new AlunoNaoEncontradoException("Aluno não encontrado");
    }

    public AlunoResponse cadastrarAluno(AlunoRequest request) {

        for (Aluno a : alunos) {
            if (a.getEmail().equalsIgnoreCase(request.getEmail())) {
                throw new EmailExistenteException("E-mail já existe");
            }
        }

        alunos.add(new Aluno(id, request.getNome(), request.getEmail(), request.getSenha(), request.getDataNascimento(),
                request.getMedia()));

        id++;

        Aluno alunoCadastrado = alunos.get(alunos.size() - 1 /* alunos.getLast() */);

        return new AlunoResponse(alunoCadastrado.getId(), alunoCadastrado.getNome(), alunoCadastrado.getEmail(),
                alunoCadastrado.getDataNascimento(), alunoCadastrado.getMedia());

    }

    public AlunoResponse atualizarAluno(int id, AlunoRequest request) {

        for (Aluno a : alunos) {
            if (a.getEmail().equalsIgnoreCase(request.getEmail()) && id != a.getId()) {
                throw new EmailExistenteException("E-mail já existe");
            }
        }

        for (Aluno a : alunos) {
            if (a.getId() == id) {
                a.setNome(request.getNome());
                a.setEmail(request.getEmail());
                a.setSenha(request.getSenha());
                a.setDataNascimento(request.getDataNascimento());
                a.setMedia(request.getMedia());
                return new AlunoResponse(id, a.getNome(), a.getEmail(), a.getDataNascimento(), a.getMedia());
            }
        }
        throw new AlunoNaoEncontradoException("Aluno não encontrado");

    }

    public void excluirAluno(int id) {
        for (Aluno a : alunos) {
            if (a.getId() == id) {
                alunos.remove(a);
                return;
            }
        }
    }

}
