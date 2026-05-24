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

import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.servicos.VendaServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/venda")
public class VendaControle {

	@Autowired
	private VendaServico vendaServico;

	private EntityModel<Venda> toModel(Venda venda) {
		EntityModel<Venda> model = EntityModel.of(venda,
				linkTo(methodOn(VendaControle.class).obterVenda(venda.getId())).withSelfRel(),
				linkTo(methodOn(VendaControle.class).listarVendas()).withRel("vendas"));
		if (venda.getClienteId() != null) {
			model.add(linkTo(methodOn(UsuarioControle.class).obterUsuario(venda.getClienteId())).withRel("cliente"));
		}
		if (venda.getFuncionarioId() != null) {
			model.add(linkTo(methodOn(UsuarioControle.class).obterUsuario(venda.getFuncionarioId())).withRel("funcionario"));
		}
		if (venda.getVeiculoId() != null) {
			model.add(linkTo(methodOn(VeiculoControle.class).obterVeiculo(venda.getVeiculoId())).withRel("veiculo"));
		}
		return model;
	}

	@PreAuthorize("hasRole('GERENTE')")
	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Venda>>> listarVendas() {
		List<EntityModel<Venda>> vendas = vendaServico.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(vendas,
				linkTo(methodOn(VendaControle.class).listarVendas()).withSelfRel()));
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Venda>> obterVenda(@PathVariable Long id) {
		return vendaServico.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<CollectionModel<EntityModel<Venda>>> listarPorCliente(@PathVariable Long usuarioId) {
		if (!vendaServico.usuarioExiste(usuarioId)) {
			return ResponseEntity.notFound().build();
		}
		List<EntityModel<Venda>> vendas = vendaServico.listarPorCliente(usuarioId).stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(vendas,
				linkTo(methodOn(VendaControle.class).listarPorCliente(usuarioId)).withSelfRel()));
	}

	@PreAuthorize("hasRole('VENDEDOR')")
	@PostMapping
	public ResponseEntity<EntityModel<Venda>> cadastrarVenda(@RequestBody Venda venda) {
		return ResponseEntity.status(HttpStatus.CREATED).body(toModel(vendaServico.cadastrar(venda)));
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Venda>> atualizarVenda(@PathVariable Long id,
			@RequestBody Venda dados) {
		return vendaServico.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
		return vendaServico.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
