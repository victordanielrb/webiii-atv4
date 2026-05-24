package com.autobots.automanager.controles;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.servicos.EmpresaServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {

	@Autowired
	private EmpresaServico empresaServico;

	private EntityModel<Empresa> toModel(Empresa empresa) {
		return EntityModel.of(empresa,
				linkTo(methodOn(EmpresaControle.class).obterEmpresa(empresa.getId())).withSelfRel(),
				linkTo(methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"),
				linkTo(methodOn(UsuarioControle.class).listarPorEmpresa(empresa.getId())).withRel("usuarios"),
				linkTo(methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"),
				linkTo(methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Empresa>>> listarEmpresas() {
		List<EntityModel<Empresa>> empresas = empresaServico.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(empresas,
				linkTo(methodOn(EmpresaControle.class).listarEmpresas()).withSelfRel()));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Empresa>> obterEmpresa(@PathVariable Long id) {
		return empresaServico.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PostMapping
	public ResponseEntity<EntityModel<Empresa>> cadastrarEmpresa(@RequestBody Empresa empresa) {
		return ResponseEntity.status(HttpStatus.CREATED).body(toModel(empresaServico.cadastrar(empresa)));
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Empresa>> atualizarEmpresa(@PathVariable Long id, @RequestBody Empresa dados) {
		return empresaServico.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirEmpresa(@PathVariable Long id) {
		return empresaServico.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
