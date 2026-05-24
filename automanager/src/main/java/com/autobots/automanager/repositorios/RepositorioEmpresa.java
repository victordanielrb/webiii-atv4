package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Empresa;

public interface RepositorioEmpresa extends JpaRepository<Empresa, Long> {
	Optional<Empresa> findByMercadoriasId(Long mercadoriaId);
	Optional<Empresa> findByServicosId(Long servicoId);
}