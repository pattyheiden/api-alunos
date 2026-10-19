package com.lima.api_alunos.service;

import com.lima.api_alunos.dto.AlunoRequest;
import com.lima.api_alunos.dto.AlunoResponse;
import com.lima.api_alunos.exception.AlunoNaoEncontradoException;
import com.lima.api_alunos.exception.EmailExistenteException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class AlunoServiceTest {

    private AlunoService service;

    @BeforeEach
    void setUp() {
        service = new AlunoService();
    }

    //Cadastrar Aluno

    @Test
    void deveCadastrarAlunoQuandoDadosForemValidos() {

        //Arrange
        // service = new AlunoService(); -> não é mais necessário por causa do setUp

        AlunoRequest request = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );

        //Act
        AlunoResponse response = service.cadastrarAluno(request);

        //Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isGreaterThan(0);
    }

    @Test
    void deveLancarExcecaoQuandoEmailForDuplicado() {

        //Arrange
        //service = new AlunoService(); -> não é mais necessário por causa do setUp

        AlunoRequest primeiroAluno = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );

        AlunoRequest alunoComMesmoEmail = new AlunoRequest(
                "Outro Nome",
                "patricia@gmail.com", // Mesmo e-mail
                "OutraSenha@123",
                LocalDate.of(1995, 5, 10),
                7
        );

        //Act
        service.cadastrarAluno(primeiroAluno);

        //Assert
        Assertions.assertThatThrownBy(() -> service.cadastrarAluno(alunoComMesmoEmail))
                .isInstanceOf(EmailExistenteException.class)
                .hasMessage("E-mail já existe");
    }

    //Listar alunos

    @Test
    void deveListarTodosOsAlunosQuandoExistiremCadastros() {
        // Arrange
        AlunoRequest aluno1 = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        AlunoRequest aluno2 = new AlunoRequest(
                "Henrique Heiden",
                "henrique@gmail.com",
                "Senha@456",
                LocalDate.of(1998, 3, 15),
                9
        );

        service.cadastrarAluno(aluno1);
        service.cadastrarAluno(aluno2);

        // Act
        List<AlunoResponse> lista = service.listarAlunos();

        // Assert
        Assertions.assertThat(lista)
                .isNotNull()
                .hasSize(2);

        AlunoResponse primeiro = lista.get(0);
        Assertions.assertThat(primeiro.getId()).isGreaterThan(0);
        Assertions.assertThat(primeiro.getNome()).isEqualTo("Patricia");
        Assertions.assertThat(primeiro.getEmail()).isEqualTo("patricia@gmail.com");
        Assertions.assertThat(primeiro.getDataNascimento()).isEqualTo(LocalDate.of(1992, 10, 26));
        Assertions.assertThat(primeiro.getMedia()).isEqualTo(8);

        Assertions.assertThat(lista)
                .extracting(AlunoResponse::getNome)
                .containsExactly("Patricia", "Lucas");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverAlunosCadastrados() {
        // Arrange

        // Act
        List<AlunoResponse> lista = service.listarAlunos();

        // Assert
        Assertions.assertThat(lista)
                .isNotNull()
                .isEmpty();
    }

    //Aluno por ID

    @Test
    void deveRetornarAlunoQuandoIdExistir() {
        // Arrange
        AlunoRequest request = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );

        AlunoResponse alunoCadastrado = service.cadastrarAluno(request);
        int idEsperado = alunoCadastrado.getId();

        // Act
        AlunoResponse response = service.obterAlunoPorId(idEsperado);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(idEsperado);
        Assertions.assertThat(response.getNome()).isEqualTo("Patricia");
        Assertions.assertThat(response.getEmail()).isEqualTo("patricia@gmail.com");
        Assertions.assertThat(response.getDataNascimento()).isEqualTo(LocalDate.of(1992, 10, 26));
        Assertions.assertThat(response.getMedia()).isEqualTo(8);
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExistir() {
        // Arrange
        int idInexistente = 999;

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.obterAlunoPorId(idInexistente))
                .isInstanceOf(AlunoNaoEncontradoException.class)
                .hasMessage("Aluno não encontrado");
    }

    //atualizar

    @Test
    void deveAtualizarAlunoComSucessoQuandoDadosForemValidos() {
        // Arrange
        AlunoRequest inicial = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        AlunoResponse cadastrado = service.cadastrarAluno(inicial);

        AlunoRequest dadosAtualizados = new AlunoRequest(
                "Patricia Silva",
                "patricia.silva@gmail.com",
                "NovaSenha@123",
                LocalDate.of(1992, 10, 26),
                10
        );

        // Act
        AlunoResponse response = service.atualizarAluno(cadastrado.getId(), dadosAtualizados);

        // Assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(cadastrado.getId());
        Assertions.assertThat(response.getNome()).isEqualTo("Patricia Silva");
        Assertions.assertThat(response.getEmail()).isEqualTo("patricia.silva@gmail.com");
        Assertions.assertThat(response.getMedia()).isEqualTo(10);
    }

    @Test
    void devePermitirAtualizarMantendoOMesmoEmail() {
        // Arrange
        AlunoRequest inicial = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        AlunoResponse cadastrado = service.cadastrarAluno(inicial);

        AlunoRequest mesmosDadosComMesmoEmail = new AlunoRequest(
                "Patricia Santos",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                9
        );

        // Act
        AlunoResponse response = service.atualizarAluno(cadastrado.getId(), mesmosDadosComMesmoEmail);

        // Assert
        Assertions.assertThat(response.getNome()).isEqualTo("Patricia Santos");
        Assertions.assertThat(response.getEmail()).isEqualTo("patricia@gmail.com");
        Assertions.assertThat(response.getMedia()).isEqualTo(9);
    }

    @Test
    void deveLancarExcecaoQuandoEmailAtualizadoJaPertencerAOutroAluno() {
        // Arrange
        AlunoRequest aluno1 = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        AlunoRequest aluno2 = new AlunoRequest(
                "Henrique Heiden",
                "henrique@gmail.com",
                "Senha@456",
                LocalDate.of(1998, 3, 15),
                7
        );

        service.cadastrarAluno(aluno1);
        AlunoResponse cadastrado2 = service.cadastrarAluno(aluno2);

        AlunoRequest requestConflitante = new AlunoRequest(
                "Henrique Heiden",
                "henrique@gmail.com",
                "Senha@456",
                LocalDate.of(1998, 3, 15),
                8
        );

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.atualizarAluno(cadastrado2.getId(), requestConflitante))
                .isInstanceOf(EmailExistenteException.class)
                .hasMessage("E-mail já existe");
    }

    @Test
    void deveLancarExcecaoAoAtualizarAlunoComIdInexistente() {
        // Arrange
        int idInexistente = 999;
        AlunoRequest dadosAtualizacao = new AlunoRequest(
                "Inexistente",
                "inexistente@gmail.com",
                "Senha@123",
                LocalDate.of(2000, 1, 1),
                10
        );

        // Act & Assert
        Assertions.assertThatThrownBy(() -> service.atualizarAluno(idInexistente, dadosAtualizacao))
                .isInstanceOf(AlunoNaoEncontradoException.class)
                .hasMessage("Aluno não encontrado");
    }

    //excluir

    @Test
    void deveExcluirAlunoQuandoIdExistir() {
        // Arrange
        AlunoRequest request = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        AlunoResponse cadastrado = service.cadastrarAluno(request);

        // Act
        service.excluirAluno(cadastrado.getId());

        // Assert
        Assertions.assertThat(service.listarAlunos()).isEmpty();

        Assertions.assertThatThrownBy(() -> service.obterAlunoPorId(cadastrado.getId()))
                .isInstanceOf(AlunoNaoEncontradoException.class)
                .hasMessage("Aluno não encontrado");
    }

    @Test
    void naoDeveAlterarListaQuandoIdInexistenteForInformadoParaExclusao() {
        // Arrange
        AlunoRequest request = new AlunoRequest(
                "Patricia",
                "patricia@gmail.com",
                "Senha@123",
                LocalDate.of(1992, 10, 26),
                8
        );
        service.cadastrarAluno(request);
        int idInexistente = 999;

        // Act
        service.excluirAluno(idInexistente);

        // Assert
        List<AlunoResponse> lista = service.listarAlunos();
        Assertions.assertThat(lista)
                .hasSize(1)
                .extracting(AlunoResponse::getNome)
                .containsExactly("Patricia");
    }

}
