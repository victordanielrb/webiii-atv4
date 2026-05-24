package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@Service
public class EmpresaServico {

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	public List<Empresa> listar() {
		return repositorioEmpresa.findAll();
	}

	public Optional<Empresa> obter(Long id) {
		return repositorioEmpresa.findById(id);
	}

	public Empresa cadastrar(Empresa empresa) {
		empresa.setId(null);
		empresa.setCadastro(new Date());
		return repositorioEmpresa.save(empresa);
	}

	public Optional<Empresa> atualizar(Long id, Empresa dados) {
		return repositorioEmpresa.findById(id).map(empresa -> {
			empresa.setRazaoSocial(dados.getRazaoSocial());
			empresa.setNomeFantasia(dados.getNomeFantasia());
			if (dados.getEndereco() != null) {
				empresa.setEndereco(dados.getEndereco());
			}
			if (dados.getTelefones() != null) {
				empresa.getTelefones().clear();
				empresa.getTelefones().addAll(dados.getTelefones());
			}
			return repositorioEmpresa.save(empresa);
		});
	}

	public boolean excluir(Long id) {
		if (!repositorioEmpresa.existsById(id)) {
			return false;
		}
		repositorioEmpresa.deleteById(id);
		return true;
	}
}
