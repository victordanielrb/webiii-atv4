package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;

@Service
public class VeiculoServico {

	@Autowired
	private RepositorioVeiculo repositorioVeiculo;

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	public List<Veiculo> listar() {
		return repositorioVeiculo.findAll();
	}

	public Optional<Veiculo> obter(Long id) {
		return repositorioVeiculo.findById(id);
	}

	public List<Veiculo> listarPorProprietario(Long usuarioId) {
		return repositorioVeiculo.findByProprietarioId(usuarioId);
	}

	public boolean usuarioExiste(Long usuarioId) {
		return repositorioUsuario.existsById(usuarioId);
	}

	public Optional<Veiculo> cadastrar(Long usuarioId, Veiculo veiculo) {
		return repositorioUsuario.findById(usuarioId).map(usuario -> {
			veiculo.setId(null);
			veiculo.setProprietarioId(usuarioId);
			Veiculo salvo = repositorioVeiculo.save(veiculo);
			usuario.getVeiculos().add(salvo);
			repositorioUsuario.save(usuario);
			return salvo;
		});
	}

	public Optional<Veiculo> atualizar(Long id, Veiculo dados) {
		return repositorioVeiculo.findById(id).map(veiculo -> {
			veiculo.setTipo(dados.getTipo());
			veiculo.setModelo(dados.getModelo());
			veiculo.setPlaca(dados.getPlaca());
			return repositorioVeiculo.save(veiculo);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioVeiculo.existsById(id)) {
			return false;
		}
		repositorioVeiculo.findById(id).ifPresent(veiculo -> {
			if (veiculo.getProprietarioId() != null) {
				repositorioUsuario.findById(veiculo.getProprietarioId()).ifPresent(usuario -> {
					usuario.getVeiculos().removeIf(v -> v.getId().equals(id));
					repositorioUsuario.save(usuario);
				});
			}
			repositorioVeiculo.delete(veiculo);
		});
		return true;
	}
}
