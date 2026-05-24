package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.modelos.AutenticacaoRequisicao;
import com.autobots.automanager.modelos.AutenticacaoResposta;
import com.autobots.automanager.seguranca.JwtUtils;

@RestController
@RequestMapping("/autenticacao")
public class AutenticacaoControle {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping
	public ResponseEntity<AutenticacaoResposta> autenticar(@RequestBody AutenticacaoRequisicao requisicao) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(requisicao.getNomeUsuario(), requisicao.getSenha())
		);
		String token = jwtUtils.gerarToken((UserDetails) auth.getPrincipal());
		return ResponseEntity.ok(new AutenticacaoResposta("Bearer " + token));
	}
}
