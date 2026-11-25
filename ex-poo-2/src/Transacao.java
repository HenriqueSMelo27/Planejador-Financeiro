import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class Transacao {
    public static final String RECEITA = "RECEITA";
    public static final String DESPESA = "DESPESA";
    public static final String COMPRA_CARTAO = "COMPRA_CARTAO";
    public static final String PAGAMENTO_FATURA = "PAGAMENTO_FATURA";

    private final int id;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDate data;
    private final String tipo;
    private final Categoria categoria;
    private final Conta conta;
    private final CartaoCredito cartao;
    private final YearMonth mesReferencia;

    public Transacao(int id, String descricao, BigDecimal valor, LocalDate data, String tipo,
                     Categoria categoria, Conta conta, CartaoCredito cartao, YearMonth mesReferencia) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.categoria = categoria;
        this.conta = conta;
        this.cartao = cartao;
        this.mesReferencia = mesReferencia;
    }

    public int obterId() {
        return id;
    }

    public String obterDescricao() {
        return descricao;
    }

    public BigDecimal obterValor() {
        return valor;
    }

    public LocalDate obterData() {
        return data;
    }

    public String obterTipo() {
        return tipo;
    }

    public Categoria obterCategoria() {
        return categoria;
    }

    public Conta obterConta() {
        return conta;
    }

    public CartaoCredito obterCartao() {
        return cartao;
    }

    public YearMonth obterMesReferencia() {
        return mesReferencia;
    }
}
