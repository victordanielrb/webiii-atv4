package com.autobots.automanager.controles;

import java.util.List;
import java.util.Map;
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

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.servicos.UsuarioServico;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class UsuarioControle {

	@Autowired
	private UsuarioServico usuarioServico;

	private EntityModel<Usuario> toModel(Usuario usuario) {
		EntityModel<Usuario> model = EntityModel.of(usuario,
				linkTo(methodOn(UsuarioControle.class).obterUsuario(usuario.getId())).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"),
				linkTo(methodOn(VeiculoControle.class).listarPorProprietario(usuario.getId())).withRel("veiculos"),
				linkTo(methodOn(CredencialControle.class).listarCredenciais(usuario.getId())).withRel("credenciais"));
		if (usuario.getEmpresaId() != null) {
			model.add(linkTo(methodOn(EmpresaControle.class).obterEmpresa(usuario.getEmpresaId())).withRel("empresa"));
		}
		return model;
	}

	@PreAuthorize("hasRole('GERENTE')")
	@GetMapping("/usuario")
	public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {
		List<EntityModel<Usuario>> usuarios = usuarioServico.listar().stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(usuarios,
				linkTo(methodOn(UsuarioControle.class).listarUsuarios()).withSelfRel()));
	}

	@PreAuthorize("hasRole('GERENTE')")
	@GetMapping("/usuario/{id}")
	public ResponseEntity<EntityModel<Usuario>> obterUsuario(@PathVariable Long id) {
		return usuarioServico.obter(id)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@GetMapping("/empresa/{empresaId}/usuario")
	public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarPorEmpresa(@PathVariable Long empresaId) {
		if (!usuarioServico.empresaExiste(empresaId)) {
			return ResponseEntity.notFound().build();
		}
		List<EntityModel<Usuario>> usuarios = usuarioServico.listarPorEmpresa(empresaId).stream()
				.map(this::toModel)
				.collect(Collectors.toList());
		return ResponseEntity.ok(CollectionModel.of(usuarios,
				linkTo(methodOn(UsuarioControle.class).listarPorEmpresa(empresaId)).withSelfRel()));
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PostMapping("/empresa/{empresaId}/usuario")
	public ResponseEntity<EntityModel<Usuario>> cadastrarUsuario(@PathVariable Long empresaId,
			@RequestBody Usuario usuario) {
		if (!usuarioServico.empresaExiste(empresaId)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(toModel(usuarioServico.cadastrar(empresaId, usuario)));
	}

	@PreAuthorize("hasRole('GERENTE')")
	@PutMapping("/usuario/{id}")
	public ResponseEntity<EntityModel<Usuario>> atualizarUsuario(@PathVariable Long id,
			@RequestBody Usuario dados) {
		return usuarioServico.atualizar(id, dados)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('GERENTE')")
	@DeleteMapping("/usuario/{id}")
	public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
		return usuarioServico.excluir(id)
				? ResponseEntity.ok().build()
				: ResponseEntity.notFound().build();
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@PostMapping("/usuario/{id}/perfil")
	public ResponseEntity<EntityModel<Usuario>> adicionarPerfil(@PathVariable Long id,
			@RequestBody Map<String, String> body) {
		return usuarioServico.adicionarPerfil(id, body.get("perfil"))
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@DeleteMapping("/usuario/{id}/perfil/{perfil}")
	public ResponseEntity<EntityModel<Usuario>> removerPerfil(@PathVariable Long id,
			@PathVariable String perfil) {
		return usuarioServico.removerPerfil(id, perfil)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PreAuthorize("hasRole('ADMINISTRADOR')")
	@RequestMapping("/usuario/{id}/empresa/{empresaId}")
	public ResponseEntity<EntityModel<Usuario>> associarEmpresa(@PathVariable Long id,
			@PathVariable Long empresaId) {
		if (!usuarioServico.empresaExiste(empresaId)) {
			return ResponseEntity.notFound().build();
		}
		return usuarioServico.associarEmpresa(id, empresaId)
				.map(this::toModel)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
