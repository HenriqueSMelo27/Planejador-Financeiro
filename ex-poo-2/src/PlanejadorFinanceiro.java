import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public final class PlanejadorFinanceiro {
    private final ArrayList<Conta> contas = new ArrayList<>();
    private final ArrayList<Categoria> categorias = new ArrayList<>();
    private final ArrayList<Transacao> transacoes = new ArrayList<>();
    private final ArrayList<CartaoCredito> cartoes = new ArrayList<>();
    private final ArrayList<MetaFinanceira> metas = new ArrayList<>();
    private long proximoId = 1;

    public void cadastrarConta(String nome, BigDecimal saldoInicial) {
        Conta conta = new Conta(gerarId(), validarTexto(nome), validarValor(saldoInicial, true));
        contas.add(conta);
        proximoId++;
    }

    public void cadastrarCategoria(String nome) {
        Categoria categoria = new Categoria(gerarId(), validarTexto(nome));
        categorias.add(categoria);
        proximoId++;
    }

    public void cadastrarCartao(String nome, BigDecimal limite) {
        CartaoCredito cartao = new CartaoCredito(gerarId(), validarTexto(nome), validarValor(limite, false));
        cartoes.add(cartao);
        proximoId++;
    }

    public void cadastrarMeta(String nome, BigDecimal valorAlvo) {
        MetaFinanceira meta = new MetaFinanceira(gerarId(), validarTexto(nome),
                validarValor(valorAlvo, false), BigDecimal.ZERO.setScale(2));
        metas.add(meta);
        proximoId++;
    }

    public void contribuirParaMeta(int idMeta, BigDecimal valor) {
        MetaFinanceira meta = buscarMeta(idMeta);
        meta.adicionarValor(validarValor(valor, false));
    }

    public void registrarMovimentacao(String tipo, String descricao, BigDecimal valor, LocalDate data,
                                     int idCategoria, int idConta) {
        Transacao transacao = new Transacao(gerarId(), validarTexto(descricao), validarValor(valor, false), data, tipo,
                buscarCategoria(idCategoria), buscarConta(idConta), null, null);
        registrarTransacao(transacao);
    }

    public void registrarCompra(String descricao, BigDecimal valor, LocalDate data,
                                int idCategoria, int idCartao, YearMonth mes) {
        Transacao transacao = new Transacao(gerarId(), validarTexto(descricao), validarValor(valor, false), data,
                Transacao.COMPRA_CARTAO, buscarCategoria(idCategoria), null, buscarCartao(idCartao), mes);
        registrarTransacao(transacao);
    }

    public void pagarFatura(int idCartao, YearMonth mes, int idConta) {
        CartaoCredito cartao = buscarCartao(idCartao);
        BigDecimal pendencia = pendenciaDaFatura(idCartao, mes);
        Transacao transacao = new Transacao(gerarId(), "Pagamento de fatura", pendencia, LocalDate.now(),
                Transacao.PAGAMENTO_FATURA, null, buscarConta(idConta), cartao, mes);
        registrarTransacao(transacao);
    }

    public ArrayList<Conta> listarContas() {
        return new ArrayList<>(contas);
    }

    public ArrayList<Categoria> listarCategorias() {
        return new ArrayList<>(categorias);
    }

    public ArrayList<Transacao> listarTransacoes() {
        return new ArrayList<>(transacoes);
    }

    public ArrayList<CartaoCredito> listarCartoes() {
        return new ArrayList<>(cartoes);
    }

    public ArrayList<MetaFinanceira> listarMetas() {
        return new ArrayList<>(metas);
    }

    public ArrayList<Transacao> comprasDaFatura(int idCartao, YearMonth mes) {
        CartaoCredito cartao = buscarCartao(idCartao);
        ArrayList<Transacao> compras = new ArrayList<>();
        for (Transacao transacao : transacoes) {
            if (Transacao.COMPRA_CARTAO.equals(transacao.obterTipo()) && transacao.obterCartao() == cartao
                    && mes.equals(transacao.obterMesReferencia())) {
                compras.add(transacao);
            }
        }
        return compras;
    }

    public BigDecimal totalComprasDaFatura(int idCartao, YearMonth mes) {
        return somarMovimentacoes(Transacao.COMPRA_CARTAO, buscarCartao(idCartao), mes);
    }

    public BigDecimal totalPagoDaFatura(int idCartao, YearMonth mes) {
        return somarMovimentacoes(Transacao.PAGAMENTO_FATURA, buscarCartao(idCartao), mes);
    }

    public BigDecimal pendenciaDaFatura(int idCartao, YearMonth mes) {
        return totalComprasDaFatura(idCartao, mes).subtract(totalPagoDaFatura(idCartao, mes));
    }

    public BigDecimal limiteDisponivel(int idCartao) {
        CartaoCredito cartao = buscarCartao(idCartao);
        BigDecimal compras = somarMovimentacoes(Transacao.COMPRA_CARTAO, cartao, null);
        BigDecimal pagamentos = somarMovimentacoes(Transacao.PAGAMENTO_FATURA, cartao, null);
        return cartao.obterLimite().subtract(compras.subtract(pagamentos));
    }

    public BigDecimal totalReceitas(YearMonth mes) {
        return somarMovimentacoes(Transacao.RECEITA, null, mes);
    }

    public BigDecimal totalDespesas(YearMonth mes) {
        return somarMovimentacoes(Transacao.DESPESA, null, mes).add(totalComprasNoMes(mes));
    }

    public BigDecimal totalComprasNoMes(YearMonth mes) {
        return somarMovimentacoes(Transacao.COMPRA_CARTAO, null, mes);
    }

    public BigDecimal resultadoFinanceiro(YearMonth mes) {
        return totalReceitas(mes).subtract(totalDespesas(mes));
    }

    public BigDecimal totalSaldos() {
        BigDecimal total = BigDecimal.ZERO;
        for (Conta conta : contas) {
            total = total.add(conta.obterSaldo());
        }
        return total;
    }

    public BigDecimal totalPendenteNoMes(YearMonth mes) {
        return totalComprasNoMes(mes).subtract(somarMovimentacoes(Transacao.PAGAMENTO_FATURA, null, mes));
    }

    private BigDecimal somarMovimentacoes(String tipo, CartaoCredito cartao, YearMonth mes) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transacao transacao : transacoes) {
            if (!tipo.equals(transacao.obterTipo()) || (cartao != null && transacao.obterCartao() != cartao)) {
                continue;
            }
            YearMonth mesTransacao = transacao.obterMesReferencia();
            if (mesTransacao == null) {
                mesTransacao = YearMonth.from(transacao.obterData());
            }
            if (mes == null || mes.equals(mesTransacao)) {
                total = total.add(transacao.obterValor());
            }
        }
        return total;
    }

    private void registrarTransacao(Transacao transacao) {
        validarTransacao(transacao);
        switch (transacao.obterTipo()) {
            case Transacao.RECEITA -> transacao.obterConta().depositar(transacao.obterValor());
            case Transacao.DESPESA, Transacao.PAGAMENTO_FATURA -> transacao.obterConta().retirar(transacao.obterValor());
        }
        transacoes.add(transacao);
        proximoId++;
    }

    private void validarTransacao(Transacao transacao) {
        if (transacao.obterData() == null) {
            throw new IllegalArgumentException("Informe a data da movimentação.");
        }
        switch (transacao.obterTipo()) {
            case Transacao.RECEITA -> validarVinculos(transacao, true, true, false);
            case Transacao.DESPESA -> {
                validarVinculos(transacao, true, true, false);
                validarSaldo(transacao);
            }
            case Transacao.COMPRA_CARTAO -> {
                validarVinculos(transacao, false, true, true);
                if (transacao.obterValor().compareTo(limiteDisponivel(transacao.obterCartao().obterId())) > 0) {
                    throw new IllegalArgumentException("Limite disponível insuficiente.");
                }
            }
            case Transacao.PAGAMENTO_FATURA -> {
                validarVinculos(transacao, true, false, true);
                if (transacao.obterValor().signum() == 0) {
                    throw new IllegalArgumentException("A fatura não possui valor pendente.");
                }
                validarSaldo(transacao);
            }
            default -> throw new IllegalArgumentException("Tipo de movimentação inválido.");
        }
    }

    private void validarVinculos(Transacao transacao, boolean temConta, boolean temCategoria, boolean temCartao) {
        if ((transacao.obterConta() != null) != temConta || (transacao.obterCategoria() != null) != temCategoria
                || (transacao.obterCartao() != null) != temCartao
                || (transacao.obterMesReferencia() != null) != temCartao) {
            throw new IllegalArgumentException("Conta, categoria, cartão ou mês incompatível com a movimentação.");
        }
    }

    private void validarSaldo(Transacao transacao) {
        if (transacao.obterValor().compareTo(transacao.obterConta().obterSaldo()) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente na conta.");
        }
    }

    private Conta buscarConta(int id) {
        for (Conta conta : contas) {
            if (conta.obterId() == id) {
                return conta;
            }
        }
        throw new IllegalArgumentException("Conta não encontrada.");
    }

    private Categoria buscarCategoria(int id) {
        for (Categoria categoria : categorias) {
            if (categoria.obterId() == id) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria não encontrada.");
    }

    private CartaoCredito buscarCartao(int id) {
        for (CartaoCredito cartao : cartoes) {
            if (cartao.obterId() == id) {
                return cartao;
            }
        }
        throw new IllegalArgumentException("Cartão não encontrado.");
    }

    private MetaFinanceira buscarMeta(int id) {
        for (MetaFinanceira meta : metas) {
            if (meta.obterId() == id) {
                return meta;
            }
        }
        throw new IllegalArgumentException("Meta não encontrada.");
    }

    private String validarTexto(String texto) {
        String limpo = texto.strip();
        if (limpo.isEmpty()) {
            throw new IllegalArgumentException("O nome ou a descrição não pode ficar em branco.");
        }
        return limpo;
    }

    private BigDecimal validarValor(BigDecimal valor, boolean aceitaZero) {
        if (valor.signum() < 0 || (!aceitaZero && valor.signum() == 0)) {
            throw new IllegalArgumentException(aceitaZero ? "O valor deve ser igual ou maior que zero."
                    : "O valor deve ser maior que zero.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Informe um valor com até duas casas decimais.");
        }
        return valor.setScale(2);
    }

    private int gerarId() {
        if (proximoId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Limite de identificadores atingido.");
        }
        return (int) proximoId;
    }
}
