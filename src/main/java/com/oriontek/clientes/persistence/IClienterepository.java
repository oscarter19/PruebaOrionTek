package com.oriontek.clientes.persistence;

import com.oriontek.clientes.models.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface IClienterepository extends CrudRepository<Cliente,Long> {
}
