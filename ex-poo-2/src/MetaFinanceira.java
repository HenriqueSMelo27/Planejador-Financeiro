import java.math.BigDecimal;
import java.math.RoundingMode;

public class MetaFinanceira {
    private final int id;
    private final String nome;
    private final BigDecimal valorAlvo;
    private BigDecimal valorAcumulado;

    public MetaFinanceira(int id, String nome, BigDecimal valorAlvo, BigDecimal valorAcumulado) {
        this.id = id;
        this.nome = nome;
        this.valorAlvo = valorAlvo;
        this.valorAcumulado = valorAcumulado;
    }

    public int obterId() {
        return id;
    }

    public String obterNome() {
        return nome;
    }

    public BigDecimal obterValorAlvo() {
        return valorAlvo;
    }

    public BigDecimal obterValorAcumulado() {
        return valorAcumulado;
    }

    public void adicionarValor(BigDecimal valor) {
        valorAcumulado = valorAcumulado.add(valor);
    }

    public BigDecimal calcularPercentual() {
        return valorAcumulado.movePointRight(2).divide(valorAlvo, 2, RoundingMode.HALF_UP);
    }
}
