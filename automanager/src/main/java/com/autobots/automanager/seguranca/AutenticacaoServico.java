package com.autobots.automanager.seguranca;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class AutenticacaoServico implements UserDetailsService {

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@Override
	public UserDetails loadUserByUsername(String nomeUsuario) throws UsernameNotFoundException {
		Usuario usuario = repositorioUsuario.findByNomeUsuario(nomeUsuario)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + nomeUsuario));

		CredencialUsuarioSenha credencial = usuario.getCredenciais().stream()
				.filter(c -> c instanceof CredencialUsuarioSenha)
				.map(c -> (CredencialUsuarioSenha) c)
				.filter(c -> c.getNomeUsuario().equals(nomeUsuario))
				.findFirst()
				.orElseThrow(() -> new UsernameNotFoundException("Credencial não encontrada: " + nomeUsuario));

		return User.builder()
				.username(credencial.getNomeUsuario())
				.password(credencial.getSenha())
				.authorities(usuario.getPerfis().stream()
						.map(p -> new SimpleGrantedAuthority(p.name()))
						.collect(Collectors.toList()))
				.build();
	}
}
