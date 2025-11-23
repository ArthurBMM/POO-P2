package org.example;

public class SistemaCoworking {
    private MenuPrincipal menu;
    private SistemaReservas reservas;
    private SistemaPagamentos pagamentos;
    private Relatorios relatorios;
    private VerificadorCidades verificadorCidades;

    public SistemaCoworking() {
        this.verificadorCidades = VerificadorCidades.getInstance();
        this.reservas = new SistemaReservas(verificadorCidades);
        this.pagamentos = new SistemaPagamentos();
        this.relatorios = new Relatorios();
        this.menu = new MenuPrincipal(this);
    }

    public void iniciar() {
        menu.acessarSistema();
    }

    public SistemaReservas getReservas() { return reservas; }
    public SistemaPagamentos getPagamentos() { return pagamentos; }
    public Relatorios getRelatorios() { return relatorios; }
    public VerificadorCidades getVerificadorCidades() { return verificadorCidades; }
}
