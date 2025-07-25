package com.oriontek.clientes.models;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cedula")
    private String cedula;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "apellidos")
    private String apellidos;
    @Column(name = "edad")
    private int edad;
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Direccion> direcciones = new ArrayList<>();

    public void addDireccion(Direccion direccion) {
        if (this.direcciones == null) {
            this.direcciones = new ArrayList<>();
        }
        this.direcciones.add(direccion);
        direccion.setCliente(this);
    }

    public void removeDireccion(Direccion direccion) {
        if (this.direcciones != null) {
            this.direcciones.remove(direccion);
            direccion.setCliente(null);
        }

    }
}
