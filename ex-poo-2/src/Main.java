import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("MM/uuuu");

    private final Scanner leitor;
    private final PlanejadorFinanceiro planejador;

    private Main(Scanner leitor, PlanejadorFinanceiro planejador) {
        this.leitor = leitor;
        this.planejador = planejador;
    }

    public static void main(String[] argumentos) {
        try (Scanner leitor = new Scanner(System.in, System.console() == null
                ? StandardCharsets.UTF_8 : System.console().charset())) {
            System.out.println("Os registros ficam disponíveis enquanto o programa estiver aberto.");
            new Main(leitor, new PlanejadorFinanceiro()).executar();
        } catch (NoSuchElementException erro) {
            System.out.println("Entrada encerrada.");
        }
    }

    private void executar() {
        while (true) {
            System.out.print("""

                    ================================
                          PLANEJADOR FINANCEIRO
                    ================================

                    1 - Contas financeiras
                    2 - Registrar receita
                    3 - Registrar despesa
                    4 - Categorias
                    5 - Cartões de crédito
                    6 - Faturas
                    7 - Metas financeiras
                    8 - Listar movimentações
                    9 - Resumo financeiro
                    0 - Sair
                    """);
            try {
                switch (lerTexto("Escolha uma opção.")) {
                    case "1" -> menuContas();
                    case "2" -> registrarMovimentacao(Transacao.RECEITA);
                    case "3" -> registrarMovimentacao(Transacao.DESPESA);
                    case "4" -> menuCategorias();
                    case "5" -> menuCartoes();
                    case "6" -> menuFaturas();
                    case "7" -> menuMetas();
                    case "8" -> listarTransacoes();
                    case "9" -> exibirResumo();
                    case "0" -> {
                        System.out.println("Programa encerrado.");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException erro) {
                System.out.println(erro.getMessage());
            }
        }
    }

    private void menuContas() {
        System.out.print("""

                Contas financeiras
                1 - Cadastrar conta
                2 - Listar contas e saldos
                0 - Voltar
                """);
        switch (lerTexto("Escolha uma opção.")) {
            case "1" -> {
                planejador.cadastrarConta(lerTexto("Nome da conta."), lerValor("Saldo inicial."));
                System.out.println("Conta cadastrada.");
            }
            case "2" -> listarContas();
            case "0" -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void menuCategorias() {
        System.out.print("""

                Categorias
                1 - Cadastrar categoria
                2 - Listar categorias
                0 - Voltar
                """);
        switch (lerTexto("Escolha uma opção.")) {
            case "1" -> {
                planejador.cadastrarCategoria(lerTexto("Nome da categoria."));
                System.out.println("Categoria cadastrada.");
            }
            case "2" -> listarCategorias();
            case "0" -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void menuCartoes() {
        System.out.print("""

                Cartões de crédito
                1 - Cadastrar cartão
                2 - Listar cartões e limites
                3 - Registrar compra
                0 - Voltar
                """);
        switch (lerTexto("Escolha uma opção.")) {
            case "1" -> {
                planejador.cadastrarCartao(lerTexto("Nome do cartão."), lerValor("Limite total."));
                System.out.println("Cartão cadastrado.");
            }
            case "2" -> listarCartoes();
            case "3" -> registrarCompra();
            case "0" -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void menuFaturas() {
        System.out.print("""

                Faturas
                1 - Consultar fatura
                2 - Pagar valor pendente
                0 - Voltar
                """);
        String opcao = lerTexto("Escolha uma opção.");
        switch (opcao) {
            case "1", "2" -> {
                int idCartao = escolherId(listarCartoes(), "do cartão");
                YearMonth mes = lerMes();
                if ("1".equals(opcao)) {
                    exibirFatura(idCartao, mes);
                } else {
                    int idConta = escolherId(listarContas(), "da conta");
                    planejador.pagarFatura(idCartao, mes, idConta);
                    System.out.println("Fatura paga.");
                }
            }
            case "0" -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void menuMetas() {
        System.out.print("""

                Metas financeiras
                1 - Cadastrar meta
                2 - Listar metas e progresso
                3 - Adicionar valor à meta
                0 - Voltar
                """);
        switch (lerTexto("Escolha uma opção.")) {
            case "1" -> {
                planejador.cadastrarMeta(lerTexto("Nome da meta."), lerValor("Valor-alvo."));
                System.out.println("Meta cadastrada.");
            }
            case "2" -> listarMetas();
            case "3" -> {
                int idMeta = escolherId(listarMetas(), "da meta");
                planejador.contribuirParaMeta(idMeta, lerValor("Valor da contribuição."));
                System.out.println("Progresso atualizado.");
            }
            case "0" -> { }
            default -> System.out.println("Opção inválida.");
        }
    }

    private void registrarMovimentacao(String tipo) {
        String descricao = lerTexto("Descrição.");
        BigDecimal valor = lerValor("Valor.");
        LocalDate data = lerData();
        int idCategoria = escolherId(listarCategorias(), "da categoria");
        int idConta = escolherId(listarContas(), "da conta");
        planejador.registrarMovimentacao(tipo, descricao, valor, data, idCategoria, idConta);
        System.out.println("Movimentação registrada.");
    }

    private void registrarCompra() {
        String descricao = lerTexto("Descrição.");
        BigDecimal valor = lerValor("Valor.");
        LocalDate data = lerData();
        int idCategoria = escolherId(listarCategorias(), "da categoria");
        int idCartao = escolherId(listarCartoes(), "do cartão");
        YearMonth mes = lerMes();
        planejador.registrarCompra(descricao, valor, data, idCategoria, idCartao, mes);
        System.out.println("Compra registrada.");
    }

    private boolean listarContas() {
        ArrayList<Conta> contas = planejador.listarContas();
        for (Conta conta : contas) {
            System.out.println(conta.obterId() + " - " + conta.obterNome() + " | Saldo " + moeda(conta.obterSaldo()));
        }
        return informarRegistros(!contas.isEmpty(), "Nenhuma conta cadastrada.");
    }

    private boolean listarCategorias() {
        ArrayList<Categoria> categorias = planejador.listarCategorias();
        for (Categoria categoria : categorias) {
            System.out.println(categoria.obterId() + " - " + categoria.obterNome());
        }
        return informarRegistros(!categorias.isEmpty(), "Nenhuma categoria cadastrada.");
    }

    private boolean listarCartoes() {
        ArrayList<CartaoCredito> cartoes = planejador.listarCartoes();
        for (CartaoCredito cartao : cartoes) {
            System.out.println(cartao.obterId() + " - " + cartao.obterNome()
                    + " | Limite total " + moeda(cartao.obterLimite())
                    + " | Disponível " + moeda(planejador.limiteDisponivel(cartao.obterId())));
        }
        return informarRegistros(!cartoes.isEmpty(), "Nenhum cartão cadastrado.");
    }

    private boolean listarMetas() {
        ArrayList<MetaFinanceira> metas = planejador.listarMetas();
        for (MetaFinanceira meta : metas) {
            System.out.println(meta.obterId() + " - " + meta.obterNome()
                    + " | Acumulado " + moeda(meta.obterValorAcumulado()) + " de " + moeda(meta.obterValorAlvo())
                    + " | " + meta.calcularPercentual().toPlainString().replace('.', ',') + "%");
        }
        return informarRegistros(!metas.isEmpty(), "Nenhuma meta cadastrada.");
    }

    private void listarTransacoes() {
        ArrayList<Transacao> transacoes = planejador.listarTransacoes();
        for (Transacao transacao : transacoes) {
            exibirTransacao(transacao);
        }
        informarRegistros(!transacoes.isEmpty(), "Nenhuma movimentação registrada.");
    }

    private void exibirTransacao(Transacao transacao) {
        String tipo = switch (transacao.obterTipo()) {
            case Transacao.RECEITA -> "Receita";
            case Transacao.DESPESA -> "Despesa";
            case Transacao.COMPRA_CARTAO -> "Compra no cartão";
            default -> "Pagamento de fatura";
        };
        String linha = transacao.obterId() + " - " + transacao.obterData().format(FORMATO_DATA)
                + " | " + tipo + " | " + transacao.obterDescricao() + " | " + moeda(transacao.obterValor());
        if (transacao.obterCategoria() != null) {
            linha += " | Categoria " + transacao.obterCategoria().obterNome();
        }
        if (transacao.obterConta() != null) {
            linha += " | Conta " + transacao.obterConta().obterNome();
        }
        if (transacao.obterCartao() != null) {
            linha += " | Cartão " + transacao.obterCartao().obterNome()
                    + " | Fatura " + transacao.obterMesReferencia().format(FORMATO_MES);
        }
        System.out.println(linha);
    }

    private void exibirFatura(int idCartao, YearMonth mes) {
        System.out.println("Fatura de " + mes.format(FORMATO_MES));
        ArrayList<Transacao> compras = planejador.comprasDaFatura(idCartao, mes);
        for (Transacao compra : compras) {
            exibirTransacao(compra);
        }
        informarRegistros(!compras.isEmpty(), "Nenhuma compra registrada nesta fatura.");
        System.out.println("Total da fatura " + moeda(planejador.totalComprasDaFatura(idCartao, mes)));
        System.out.println("Valor pago " + moeda(planejador.totalPagoDaFatura(idCartao, mes)));
        System.out.println("Valor pendente " + moeda(planejador.pendenciaDaFatura(idCartao, mes)));
    }

    private void exibirResumo() {
        YearMonth mes = lerMes();
        System.out.println("Resumo de " + mes.format(FORMATO_MES));
        System.out.println("Total de receitas " + moeda(planejador.totalReceitas(mes)));
        System.out.println("Total de despesas (inclui compras no cartão) " + moeda(planejador.totalDespesas(mes)));
        System.out.println("Total de compras no cartão " + moeda(planejador.totalComprasNoMes(mes)));
        System.out.println("Resultado financeiro do mês " + moeda(planejador.resultadoFinanceiro(mes)));
        System.out.println("Total dos saldos atuais das contas " + moeda(planejador.totalSaldos()));
        System.out.println("Total pendente das faturas do mês " + moeda(planejador.totalPendenteNoMes(mes)));
    }

    private boolean informarRegistros(boolean temRegistros, String mensagem) {
        if (!temRegistros) {
            System.out.println(mensagem);
        }
        return temRegistros;
    }

    private int escolherId(boolean temRegistros, String entidade) {
        if (!temRegistros) {
            throw new IllegalArgumentException("Faça o cadastro antes de continuar.");
        }
        String texto = lerTexto("ID " + entidade + ".");
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Informe um identificador numérico.");
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem + " ");
        return leitor.nextLine().strip();
    }

    private BigDecimal lerValor(String mensagem) {
        String texto = lerTexto(mensagem);
        if (!texto.matches("[+-]?\\d+([.,]\\d+)?")) {
            throw new IllegalArgumentException("Valor inválido. Digite um número, como 10,50.");
        }
        return new BigDecimal(texto.replace(',', '.'));
    }

    private LocalDate lerData() {
        try {
            return LocalDate.parse(lerTexto("Data (DD/MM/AAAA)."), FORMATO_DATA);
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Data inválida. Use DD/MM/AAAA.");
        }
    }

    private YearMonth lerMes() {
        try {
            return YearMonth.parse(lerTexto("Mês de referência (MM/AAAA)."), FORMATO_MES);
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Mês inválido. Use MM/AAAA.");
        }
    }

    private String moeda(BigDecimal valor) {
        return "R$ " + valor.setScale(2).toPlainString().replace('.', ',');
    }
}
