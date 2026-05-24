package com.autobots.automanager.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
	List<Usuario> findByEmpresaId(Long empresaId);

	@Query("SELECT u FROM Usuario u JOIN u.credenciais c WHERE TYPE(c) = CredencialUsuarioSenha AND TREAT(c AS CredencialUsuarioSenha).nomeUsuario = :nomeUsuario")
	Optional<Usuario> findByNomeUsuario(@Param("nomeUsuario") String nomeUsuario);
}
