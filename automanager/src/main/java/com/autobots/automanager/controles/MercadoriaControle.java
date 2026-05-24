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

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.servicos.MercadoriaServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {

	@Autowired
	private MercadoriaServico mercadoriaServico;

	private EntityModel<Mercadoria> toModel(Mercadoria mercadoria) {
		return EntityModel.of(mercadoria,
				linkTo(methodOn(MercadoriaControle.class).obterMercadoria(mercadoria.getId())).withSelfRel(),
				linkTo(methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Mercadoria>>> listarMercadorias() {
		List<EntityModel<Mercadoria>> mercadorias = mercadoriaServico.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(mercadorias,
				linkTo(methodOn(MercadoriaControle.class).listarMercadorias()).withSelfRel()));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Mercadoria>> obterMercadoria(@PathVariable Long id) {
		return mercadoriaServico.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PostMapping("/empresa/{empresaId}")
	public ResponseEntity<EntityModel<Mercadoria>> cadastrarMercadoria(@PathVariable Long empresaId,
			@RequestBody Mercadoria mercadoria) {
		return mercadoriaServico.cadastrar(empresaId, mercadoria)
				.map(this::toModel)
				.map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Mercadoria>> atualizarMercadoria(@PathVariable Long id,
			@RequestBody Mercadoria dados) {
		return mercadoriaServico.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirMercadoria(@PathVariable Long id) {
		return mercadoriaServico.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
