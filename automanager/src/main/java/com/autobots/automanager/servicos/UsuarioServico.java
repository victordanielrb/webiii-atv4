package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UsuarioServico {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	public List<Usuario> listar() {
		return repositorioUsuario.findAll();
	}

	public Optional<Usuario> obter(Long id) {
		return repositorioUsuario.findById(id);
	}

	public List<Usuario> listarPorEmpresa(Long empresaId) {
		return repositorioUsuario.findByEmpresaId(empresaId);
	}

	public boolean empresaExiste(Long empresaId) {
		return repositorioEmpresa.existsById(empresaId);
	}

	public Usuario cadastrar(Long empresaId, Usuario usuario) {
		usuario.setId(null);
		usuario.setEmpresaId(empresaId);
		return repositorioUsuario.save(usuario);
	}

	public Optional<Usuario> atualizar(Long id, Usuario dados) {
		return repositorioUsuario.findById(id).map(usuario -> {
			usuario.setNome(dados.getNome());
			usuario.setNomeSocial(dados.getNomeSocial());
			usuario.setCpf(dados.getCpf());
			if (dados.getEndereco() != null) {
				usuario.setEndereco(dados.getEndereco());
			}
			return repositorioUsuario.save(usuario);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioUsuario.existsById(id)) {
			return false;
		}
		repositorioUsuario.deleteById(id);
		return true;
	}

	public Optional<Usuario> adicionarPerfil(Long id, String perfil) {
		return repositorioUsuario.findById(id).map(usuario -> {
			usuario.getPerfis().add(PerfilUsuario.valueOf(perfil));
			return repositorioUsuario.save(usuario);
		});
	}

	public Optional<Usuario> removerPerfil(Long id, String perfil) {
		return repositorioUsuario.findById(id).map(usuario -> {
			usuario.getPerfis().remove(PerfilUsuario.valueOf(perfil));
			return repositorioUsuario.save(usuario);
		});
	}

	public Optional<Usuario> associarEmpresa(Long id, Long empresaId) {
		return repositorioUsuario.findById(id).map(usuario -> {
			usuario.setEmpresaId(empresaId);
			return repositorioUsuario.save(usuario);
		});
	}
}
