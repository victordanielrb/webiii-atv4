package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;

@Service
public class MercadoriaServico {

	@Autowired
	private RepositorioMercadoria repositorioMercadoria;

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	public List<Mercadoria> listar() {
		return repositorioMercadoria.findAll();
	}

	public Optional<Mercadoria> obter(Long id) {
		return repositorioMercadoria.findById(id);
	}

	public Optional<Mercadoria> cadastrar(Long empresaId, Mercadoria mercadoria) {
		return repositorioEmpresa.findById(empresaId).map(empresa -> {
			mercadoria.setId(null);
			mercadoria.setCadastro(new Date());
			empresa.getMercadorias().add(mercadoria);
			repositorioEmpresa.save(empresa);
			return mercadoria;
		});
	}

	public Optional<Mercadoria> atualizar(Long id, Mercadoria dados) {
		return repositorioMercadoria.findById(id).map(mercadoria -> {
			mercadoria.setNome(dados.getNome());
			mercadoria.setDescricao(dados.getDescricao());
			mercadoria.setValor(dados.getValor());
			mercadoria.setQuantidade(dados.getQuantidade());
			mercadoria.setValidade(dados.getValidade());
			mercadoria.setFabricao(dados.getFabricao());
			return repositorioMercadoria.save(mercadoria);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioMercadoria.existsById(id)) {
			return false;
		}
		repositorioEmpresa.findByMercadoriasId(id).ifPresent(empresa -> {
			empresa.getMercadorias().removeIf(m -> m.getId().equals(id));
			repositorioEmpresa.save(empresa);
		});
		repositorioMercadoria.deleteById(id);
		return true;
	}
}
