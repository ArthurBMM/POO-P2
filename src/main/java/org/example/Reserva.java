package org.example;

import java.time.LocalDateTime;

public class Reserva {
    private String id;
    private String idEspaco;
    private String emailUsuario;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private int horas;
    private double valorTotal;
    private double valorComDesconto;
    private String status; // PENDENTE, CONFIRMADA, CANCELADA, FINALIZADA

    public Reserva(String id, String idEspaco, String emailUsuario,
                   LocalDateTime dataInicio, int horas, double valorTotal, double valorComDesconto) {
        this.id = id;
        this.idEspaco = idEspaco;
        this.emailUsuario = emailUsuario;
        this.dataInicio = dataInicio;
        this.horas = horas;
        this.dataFim = dataInicio.plusHours(horas);
        this.valorTotal = valorTotal;
        this.valorComDesconto = valorComDesconto;
        this.status = "PENDENTE";
    }

    // Getters
    public String getId() { return id; }
    public String getIdEspaco() { return idEspaco; }
    public String getEmailUsuario() { return emailUsuario; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public int getHoras() { return horas; }
    public double getValorTotal() { return valorTotal; }
    public double getValorComDesconto() { return valorComDesconto; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
