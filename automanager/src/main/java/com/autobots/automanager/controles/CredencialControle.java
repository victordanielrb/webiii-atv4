package com.autobots.automanager.controles;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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

import com.autobots.automanager.entitades.Credencial;
import com.autobots.automanager.entitades.CredencialCodigoBarra;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.servicos.CredencialServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuario/{usuarioId}/credencial")
public class CredencialControle {

	@Autowired
	private CredencialServico credencialServico;

	private EntityModel<Credencial> toModel(Credencial credencial, Long usuarioId) {
		Link selfLink = linkTo(methodOn(CredencialControle.class)
				.listarCredenciais(usuarioId)).withRel("credenciais");
		Link usuarioLink = linkTo(methodOn(UsuarioControle.class)
				.obterUsuario(usuarioId)).withRel("usuario");
		return EntityModel.of(credencial, selfLink, usuarioLink);
	}

	@PreAuthorize("hasRole('GERENTE')")
	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Credencial>>> listarCredenciais(
			@PathVariable Long usuarioId) {
		return credencialServico.listar(usuarioId).map(credenciais -> {
			List<EntityModel<Credencial>> lista = credenciais.stream()
					.map(c -> toModel(c, usuarioId))
					.collect(Collectors.toList());
			return ResponseEntity.ok(CollectionModel.of(lista,
					linkTo(methodOn(CredencialControle.class).listarCredenciais(usuarioId)).withSelfRel()));
		}).orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PostMapping("/senha")
	public ResponseEntity<EntityModel<Credencial>> adicionarSenha(@PathVariable Long usuarioId,
			@RequestBody CredencialUsuarioSenha credencial) {
		return credencialServico.adicionarSenha(usuarioId, credencial)
				.map(c -> ResponseEntity.status(HttpStatus.CREATED).body(toModel(c, usuarioId)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PostMapping("/codigoBarra")
	public ResponseEntity<EntityModel<Credencial>> adicionarCodigoBarra(@PathVariable Long usuarioId,
			@RequestBody CredencialCodigoBarra credencial) {
		return credencialServico.adicionarCodigoBarra(usuarioId, credencial)
				.map(c -> ResponseEntity.status(HttpStatus.CREATED).body(toModel(c, usuarioId)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PutMapping("/{credencialId}")
	public ResponseEntity<EntityModel<Credencial>> atualizarCredencial(@PathVariable Long usuarioId,
			@PathVariable Long credencialId, @RequestBody Credencial dados) {
		return credencialServico.atualizar(usuarioId, credencialId, dados)
				.map(c -> ResponseEntity.ok(toModel(c, usuarioId)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@DeleteMapping("/{credencialId}")
	public ResponseEntity<Void> excluirCredencial(@PathVariable Long usuarioId,
			@PathVariable Long credencialId) {
		return credencialServico.excluir(usuarioId, credencialId)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}
}
