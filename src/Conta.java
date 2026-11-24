import java.math.BigDecimal;

public class Conta {
    private final int id;
    private final String nome;
    private BigDecimal saldo;

    public Conta(int id, String nome, BigDecimal saldo) {
        this.id = id;
        this.nome = nome;
        this.saldo = saldo;
    }

    public int obterId() {
        return id;
    }

    public String obterNome() {
        return nome;
    }

    public BigDecimal obterSaldo() {
        return saldo;
    }

    public void depositar(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    public void retirar(BigDecimal valor) {
        saldo = saldo.subtract(valor);
    }
}
