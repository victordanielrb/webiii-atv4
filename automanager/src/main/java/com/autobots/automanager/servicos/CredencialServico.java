package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Credencial;
import com.autobots.automanager.entitades.CredencialCodigoBarra;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioCredencial;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class CredencialServico {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@Autowired
	private RepositorioCredencial repositorioCredencial;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public Optional<List<Credencial>> listar(Long usuarioId) {
		return repositorioUsuario.findById(usuarioId)
				.map(usuario -> List.copyOf(usuario.getCredenciais()));
	}

	public Optional<Credencial> adicionarSenha(Long usuarioId, CredencialUsuarioSenha credencial) {
		return repositorioUsuario.findById(usuarioId).map(usuario -> {
			credencial.setId(null);
			credencial.setCriacao(new Date());
			credencial.setSenha(passwordEncoder.encode(credencial.getSenha()));
			usuario.getCredenciais().add(credencial);
			repositorioUsuario.save(usuario);
			return (Credencial) credencial;
		});
	}

	public Optional<Credencial> adicionarCodigoBarra(Long usuarioId, CredencialCodigoBarra credencial) {
		return repositorioUsuario.findById(usuarioId).map(usuario -> {
			credencial.setId(null);
			credencial.setCriacao(new Date());
			usuario.getCredenciais().add(credencial);
			repositorioUsuario.save(usuario);
			return (Credencial) credencial;
		});
	}

	public Optional<Credencial> atualizar(Long usuarioId, Long credencialId, Credencial dados) {
		return repositorioUsuario.findById(usuarioId).flatMap(usuario ->
				usuario.getCredenciais().stream()
						.filter(c -> c.getId().equals(credencialId))
						.findFirst()
		).map(credencial -> {
			credencial.setInativo(dados.isInativo());
			credencial.setUltimoAcesso(dados.getUltimoAcesso());
			return repositorioCredencial.save(credencial);
		});
	}

	public boolean excluir(Long usuarioId, Long credencialId) {
		if (!repositorioUsuario.existsById(usuarioId)) {
			return false;
		}
		return repositorioUsuario.findById(usuarioId).map(usuario -> {
			boolean removido = usuario.getCredenciais().removeIf(c -> c.getId().equals(credencialId));
			if (removido) {
				repositorioUsuario.save(usuario);
			}
			return removido;
		}).orElse(false);
	}
}
