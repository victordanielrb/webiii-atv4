package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioServico;

@Service
public class ServicoNegocio {

	@Autowired
	private RepositorioServico repositorioServico;

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	public List<Servico> listar() {
		return repositorioServico.findAll();
	}

	public Optional<Servico> obter(Long id) {
		return repositorioServico.findById(id);
	}

	public Optional<Servico> cadastrar(Long empresaId, Servico servico) {
		return repositorioEmpresa.findById(empresaId).map(empresa -> {
			servico.setId(null);
			empresa.getServicos().add(servico);
			repositorioEmpresa.save(empresa);
			return servico;
		});
	}

	public Optional<Servico> atualizar(Long id, Servico dados) {
		return repositorioServico.findById(id).map(servico -> {
			servico.setNome(dados.getNome());
			servico.setDescricao(dados.getDescricao());
			servico.setValor(dados.getValor());
			return repositorioServico.save(servico);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioServico.existsById(id)) {
			return false;
		}
		repositorioEmpresa.findByServicosId(id).ifPresent(empresa -> {
			empresa.getServicos().removeIf(s -> s.getId().equals(id));
			repositorioEmpresa.save(empresa);
		});
		repositorioServico.deleteById(id);
		return true;
	}
}
