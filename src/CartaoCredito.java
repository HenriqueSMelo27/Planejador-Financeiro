import java.math.BigDecimal;

public class CartaoCredito {
    private final int id;
    private final String nome;
    private final BigDecimal limite;

    public CartaoCredito(int id, String nome, BigDecimal limite) {
        this.id = id;
        this.nome = nome;
        this.limite = limite;
    }

    public int obterId() {
        return id;
    }

    public String obterNome() {
        return nome;
    }

    public BigDecimal obterLimite() {
        return limite;
    }
}
