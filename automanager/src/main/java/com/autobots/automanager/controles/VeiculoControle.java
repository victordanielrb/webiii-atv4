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
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.servicos.VeiculoServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class VeiculoControle {

	@Autowired
	private VeiculoServico veiculoServico;

	private EntityModel<Veiculo> toModel(Veiculo veiculo) {
		EntityModel<Veiculo> model = EntityModel.of(veiculo,
				linkTo(methodOn(VeiculoControle.class).obterVeiculo(veiculo.getId())).withSelfRel(),
				linkTo(methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"));
		if (veiculo.getProprietarioId() != null) {
			model.add(linkTo(methodOn(UsuarioControle.class).obterUsuario(veiculo.getProprietarioId())).withRel("proprietario"));
		}
		return model;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/veiculo")
	public ResponseEntity<CollectionModel<EntityModel<Veiculo>>> listarVeiculos() {
		List<EntityModel<Veiculo>> veiculos = veiculoServico.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(veiculos,
				linkTo(methodOn(VeiculoControle.class).listarVeiculos()).withSelfRel()));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/veiculo/{id}")
	public ResponseEntity<EntityModel<Veiculo>> obterVeiculo(@PathVariable Long id) {
		return veiculoServico.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/usuario/{usuarioId}/veiculo")
	public ResponseEntity<CollectionModel<EntityModel<Veiculo>>> listarPorProprietario(
			@PathVariable Long usuarioId) {
		if (!veiculoServico.usuarioExiste(usuarioId)) {
			return ResponseEntity.notFound().build();
		}
		List<EntityModel<Veiculo>> veiculos = veiculoServico.listarPorProprietario(usuarioId).stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(veiculos,
				linkTo(methodOn(VeiculoControle.class).listarPorProprietario(usuarioId)).withSelfRel()));
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@PostMapping("/usuario/{usuarioId}/veiculo")
	public ResponseEntity<EntityModel<Veiculo>> cadastrarVeiculo(@PathVariable Long usuarioId,
			@RequestBody Veiculo veiculo) {
		return veiculoServico.cadastrar(usuarioId, veiculo)
				.map(this::toModel)
				.map(v -> ResponseEntity.status(HttpStatus.CREATED).body(v))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@PutMapping("/veiculo/{id}")
	public ResponseEntity<EntityModel<Veiculo>> atualizarVeiculo(@PathVariable Long id,
			@RequestBody Veiculo dados) {
		return veiculoServico.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@DeleteMapping("/veiculo/{id}")
	public ResponseEntity<Void> excluirVeiculo(@PathVariable Long id) {
		return veiculoServico.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
