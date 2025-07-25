package com.oriontek.clientes.controllers;
import com.oriontek.clientes.models.Cliente;
import com.oriontek.clientes.models.Direccion;
import com.oriontek.clientes.persistence.IClienterepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Para manejar 404 de forma limpia

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private IClienterepository clienteRepository;

    // Endpoint que devuelve todos los clientes
    @GetMapping
    public List<Cliente> getAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        clienteRepository.findAll().forEach(clientes::add);
        return clientes;
    }

    // Endpoint para dado un id obtener cliente y sus direcciones
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con ID: " + id));

        return ResponseEntity.ok(cliente);
    }

    //Endpoint para crear un nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
            cliente.getDirecciones().forEach(direccion -> direccion.setCliente(cliente));
        }
        Cliente savedCliente = clienteRepository.save(cliente);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }

    //Endpoint para actualizar un cliente y direccion
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con ID: " + id));

        cliente.setNombre(clienteDetails.getNombre());
        cliente.setApellidos(clienteDetails.getApellidos());
        cliente.setEmail(clienteDetails.getEmail());
        List<Long> incomingAddressIds = clienteDetails.getDirecciones() != null ?
                clienteDetails.getDirecciones().stream()
                        .map(Direccion::getId)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        cliente.getDirecciones().clear();
        if (clienteDetails.getDirecciones() != null && !clienteDetails.getDirecciones().isEmpty()) {
            clienteDetails.getDirecciones().forEach(newDireccion -> {
                cliente.addDireccion(newDireccion);
            });
        }
        List<Direccion> direccionesToRemove = new ArrayList<>();
        for (Direccion existingDireccion : cliente.getDirecciones()) {
            if (!incomingAddressIds.contains(existingDireccion.getId())) {
                direccionesToRemove.add(existingDireccion);
            }
        }
        for (Direccion direccion : direccionesToRemove) {
            cliente.removeDireccion(direccion); // <--- Aquí se usa!
        }

        Cliente updatedCliente = clienteRepository.save(cliente);
        return ResponseEntity.ok(updatedCliente);
    }

    //Endpoint para eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCliente(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con ID: " + id));

        clienteRepository.delete(cliente);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
