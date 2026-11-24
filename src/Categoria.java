public class Categoria {
    private final int id;
    private final String nome;

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int obterId() {
        return id;
    }

    public String obterNome() {
        return nome;
    }
}
