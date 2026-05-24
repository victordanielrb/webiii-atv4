package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Venda;

public interface RepositorioVenda extends JpaRepository<Venda, Long> {
	List<Venda> findByClienteId(Long clienteId);
	List<Venda> findByFuncionarioId(Long funcionarioId);
}
