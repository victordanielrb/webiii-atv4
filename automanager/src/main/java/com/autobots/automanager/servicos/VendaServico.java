package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;

@Service
public class VendaServico {

	@Autowired
	private RepositorioVenda repositorioVenda;

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	public List<Venda> listar() {
		return repositorioVenda.findAll();
	}

	public Optional<Venda> obter(Long id) {
		return repositorioVenda.findById(id);
	}

	public List<Venda> listarPorCliente(Long usuarioId) {
		return repositorioVenda.findByClienteId(usuarioId);
	}

	public boolean usuarioExiste(Long usuarioId) {
		return repositorioUsuario.existsById(usuarioId);
	}

	public Venda cadastrar(Venda venda) {
		venda.setId(null);
		venda.setCadastro(new Date());
		return repositorioVenda.save(venda);
	}

	public Optional<Venda> atualizar(Long id, Venda dados) {
		return repositorioVenda.findById(id).map(venda -> {
			venda.setIdentificacao(dados.getIdentificacao());
			venda.setClienteId(dados.getClienteId());
			venda.setFuncionarioId(dados.getFuncionarioId());
			venda.setVeiculoId(dados.getVeiculoId());
			if (dados.getMercadorias() != null) {
				venda.getMercadorias().clear();
				venda.getMercadorias().addAll(dados.getMercadorias());
			}
			if (dados.getServicos() != null) {
				venda.getServicos().clear();
				venda.getServicos().addAll(dados.getServicos());
			}
			return repositorioVenda.save(venda);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioVenda.existsById(id)) {
			return false;
		}
		repositorioVenda.deleteById(id);
		return true;
	}
}
