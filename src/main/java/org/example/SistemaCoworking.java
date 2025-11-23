package org.example;

public class SistemaCoworking {
    private MenuPrincipal menu;
    private SistemaReservas reservas;
    private SistemaPagamentos pagamentos;
    private Relatorios relatorios;

    public SistemaCoworking() {
        this.reservas = new SistemaReservas();
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
}
