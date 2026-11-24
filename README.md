# Planejador Financeiro Pessoal

Aplicação Java de terminal para registrar receitas e despesas, acompanhar contas, cartões, faturas e metas e consultar o resumo mensal.

## Como executar

Com o JDK 21 no `PATH`, execute na raiz do projeto pelo terminal do Windows ou Linux.

```text
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Digite datas no formato `DD/MM/AAAA`, meses no formato `MM/AAAA` e valores como `10,50` ou `10.50`. Use a opção `0` do menu principal para sair.

O código está em `src/`. Os registros ficam em memória enquanto o programa está aberto. Cada execução começa sem cadastros ou movimentações.
