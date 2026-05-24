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

import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.servicos.ServicoNegocio;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

	@Autowired
	private ServicoNegocio servicoNegocio;

	private EntityModel<Servico> toModel(Servico servico) {
		return EntityModel.of(servico,
				linkTo(methodOn(ServicoControle.class).obterServico(servico.getId())).withSelfRel(),
				linkTo(methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Servico>>> listarServicos() {
		List<EntityModel<Servico>> servicos = servicoNegocio.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(servicos,
				linkTo(methodOn(ServicoControle.class).listarServicos()).withSelfRel()));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Servico>> obterServico(@PathVariable Long id) {
		return servicoNegocio.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PostMapping("/empresa/{empresaId}")
	public ResponseEntity<EntityModel<Servico>> cadastrarServico(@PathVariable Long empresaId,
			@RequestBody Servico servico) {
		return servicoNegocio.cadastrar(empresaId, servico)
				.map(this::toModel)
				.map(s -> ResponseEntity.status(HttpStatus.CREATED).body(s))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Servico>> atualizarServico(@PathVariable Long id,
			@RequestBody Servico dados) {
		return servicoNegocio.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirServico(@PathVariable Long id) {
		return servicoNegocio.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
