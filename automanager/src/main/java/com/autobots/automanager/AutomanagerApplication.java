package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.autobots.automanager.enumeracoes.TipoDocumento;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;

@SpringBootApplication
public class AutomanagerApplication implements CommandLineRunner {

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	@Autowired
	private RepositorioUsuario repositorioUsuario;

	@Autowired
	private RepositorioVeiculo repositorioVeiculo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Observações e Sobre o projeto:
		//  EntityModel é um DTO+links do HATEOAS
		//  ToModel é uma classe em cada controler que converte a entidade para EntityModel, adicionando os links relacionados
		//  CollectionModel é uma coleção de EntityModel, EntityModel[] + os links de coleçao tipo collection+self ( rota de get all )
		// Tirando isso não vejo nada além de uma api crud simples, próximos passos que consigo pensar seria o spring Security, dtos e validação de respostas com validator.

		// 1. Criar e salvar empresa (sem usuários — cascade não inclui mais Usuario)
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("Car service toyota ltda");
		empresa.setNomeFantasia("Car service manutenção veicular");
		empresa.setCadastro(new Date());

		Endereco enderecoEmpresa = new Endereco();
		enderecoEmpresa.setEstado("São Paulo");
		enderecoEmpresa.setCidade("São Paulo");
		enderecoEmpresa.setBairro("Centro");
		enderecoEmpresa.setRua("Av. São João");
		enderecoEmpresa.setNumero("00");
		enderecoEmpresa.setCodigoPostal("01035-000");
		empresa.setEndereco(enderecoEmpresa);

		Telefone telefoneEmpresa = new Telefone();
		telefoneEmpresa.setDdd("011");
		telefoneEmpresa.setNumero("986454527");
		empresa.getTelefones().add(telefoneEmpresa);

		Mercadoria rodaLigaLeve = new Mercadoria();
		rodaLigaLeve.setCadastro(new Date());
		rodaLigaLeve.setFabricao(new Date());
		rodaLigaLeve.setNome("Roda de liga leve modelo toyota etios");
		rodaLigaLeve.setValidade(new Date());
		rodaLigaLeve.setQuantidade(30);
		rodaLigaLeve.setValor(300.0);
		rodaLigaLeve.setDescricao("Roda de liga leve original de fábrica da toyota para modelos do tipo hatch");
		empresa.getMercadorias().add(rodaLigaLeve);

		Servico trocaRodas = new Servico();
		trocaRodas.setDescricao("Troca das rodas do carro por novas");
		trocaRodas.setNome("Troca de rodas");
		trocaRodas.setValor(50);
		empresa.getServicos().add(trocaRodas);

		Servico alinhamento = new Servico();
		alinhamento.setDescricao("Alinhamento das rodas do carro");
		alinhamento.setNome("Alinhamento de rodas");
		alinhamento.setValor(50);
		empresa.getServicos().add(alinhamento);

		empresa = repositorioEmpresa.save(empresa);

		// 2. Criar e salvar usuários com empresaId
		Usuario funcionario = new Usuario();
		funcionario.setNome("Pedro Alcântara de Bragança e Bourbon");
		funcionario.setNomeSocial("Dom Pedro");
		funcionario.setCpf("00000000001");
		funcionario.setEmpresaId(empresa.getId());
		funcionario.getPerfis().add(PerfilUsuario.ROLE_GERENTE);

		Email emailFuncionario = new Email();
		emailFuncionario.setEndereco("a@a.com");
		funcionario.getEmails().add(emailFuncionario);

		Endereco enderecoFuncionario = new Endereco();
		enderecoFuncionario.setEstado("São Paulo");
		enderecoFuncionario.setCidade("São Paulo");
		enderecoFuncionario.setBairro("Jardins");
		enderecoFuncionario.setRua("Av. São Gabriel");
		enderecoFuncionario.setNumero("00");
		enderecoFuncionario.setCodigoPostal("01435-001");
		funcionario.setEndereco(enderecoFuncionario);

		Telefone telefoneFuncionario = new Telefone();
		telefoneFuncionario.setDdd("011");
		telefoneFuncionario.setNumero("9854633728");
		funcionario.getTelefones().add(telefoneFuncionario);

		Documento cpfDoc = new Documento();
		cpfDoc.setDataEmissao(new Date());
		cpfDoc.setNumero("856473819229");
		cpfDoc.setTipo(TipoDocumento.CPF);
		funcionario.getDocumentos().add(cpfDoc);

		CredencialUsuarioSenha credencialFuncionario = new CredencialUsuarioSenha();
		credencialFuncionario.setInativo(false);
		credencialFuncionario.setNomeUsuario("meuidolotemnomeeéflaviocaçarato");
		credencialFuncionario.setSenha(passwordEncoder.encode("FlavioMouseHunter2011"));
		credencialFuncionario.setCriacao(new Date());
		credencialFuncionario.setUltimoAcesso(new Date());
		funcionario.getCredenciais().add(credencialFuncionario);

		funcionario = repositorioUsuario.save(funcionario);

		Usuario fornecedor = new Usuario();
		fornecedor.setNome("Componentes varejo de partes automotivas ltda");
		fornecedor.setNomeSocial("Loja do carro, vendas de componentes automotivos");
		fornecedor.setCpf("00000000002");
		fornecedor.setEmpresaId(empresa.getId());
		fornecedor.getPerfis().add(PerfilUsuario.ROLE_VENDEDOR);

		Email emailFornecedor = new Email();
		emailFornecedor.setEndereco("f@f.com");
		fornecedor.getEmails().add(emailFornecedor);

		CredencialUsuarioSenha credencialFornecedor = new CredencialUsuarioSenha();
		credencialFornecedor.setInativo(false);
		credencialFornecedor.setNomeUsuario("dompedrofornecedor");
		credencialFornecedor.setSenha(passwordEncoder.encode("123456"));
		credencialFornecedor.setCriacao(new Date());
		credencialFornecedor.setUltimoAcesso(new Date());
		fornecedor.getCredenciais().add(credencialFornecedor);

		Documento cnpj = new Documento();
		cnpj.setDataEmissao(new Date());
		cnpj.setNumero("00014556000100");
		cnpj.setTipo(TipoDocumento.CNPJ);
		fornecedor.getDocumentos().add(cnpj);

		Endereco enderecoFornecedor = new Endereco();
		enderecoFornecedor.setEstado("Rio de Janeiro");
		enderecoFornecedor.setCidade("Rio de Janeiro");
		enderecoFornecedor.setBairro("Centro");
		enderecoFornecedor.setRua("Av. República do chile");
		enderecoFornecedor.setNumero("00");
		enderecoFornecedor.setCodigoPostal("20031-170");
		fornecedor.setEndereco(enderecoFornecedor);

		fornecedor = repositorioUsuario.save(fornecedor);

		Usuario cliente = new Usuario();
		cliente.setNome("Pedro Alcântara de Bragança e Bourbon");
		cliente.setNomeSocial("Dom pedro cliente");
		cliente.setCpf("00000000003");
		cliente.setEmpresaId(empresa.getId());
		cliente.getPerfis().add(PerfilUsuario.ROLE_CLIENTE);

		Email emailCliente = new Email();
		emailCliente.setEndereco("c@c.com");
		cliente.getEmails().add(emailCliente);

		Documento cpfCliente = new Documento();
		cpfCliente.setDataEmissao(new Date());
		cpfCliente.setNumero("12584698533");
		cpfCliente.setTipo(TipoDocumento.CPF);
		cliente.getDocumentos().add(cpfCliente);

		CredencialUsuarioSenha credencialCliente = new CredencialUsuarioSenha();
		credencialCliente.setInativo(false);
		credencialCliente.setNomeUsuario("dompedrocliente");
		credencialCliente.setSenha(passwordEncoder.encode("123456"));
		credencialCliente.setCriacao(new Date());
		credencialCliente.setUltimoAcesso(new Date());
		cliente.getCredenciais().add(credencialCliente);

		Endereco enderecoCliente = new Endereco();
		enderecoCliente.setEstado("São Paulo");
		enderecoCliente.setCidade("São José dos Campos");
		enderecoCliente.setBairro("Centro");
		enderecoCliente.setRua("Av. Dr. Nelson D'Ávila");
		enderecoCliente.setNumero("00");
		enderecoCliente.setCodigoPostal("12245-070");
		cliente.setEndereco(enderecoCliente);

		cliente = repositorioUsuario.save(cliente);

		Usuario admin = new Usuario();
		admin.setNome("Administrador do Sistema");
		admin.setNomeSocial("Admin");
		admin.setCpf("00000000000");
		admin.setEmpresaId(empresa.getId());
		admin.getPerfis().add(PerfilUsuario.ROLE_ADMINISTRADOR);

		CredencialUsuarioSenha credencialAdmin = new CredencialUsuarioSenha();
		credencialAdmin.setInativo(false);
		credencialAdmin.setNomeUsuario("admin");
		credencialAdmin.setSenha(passwordEncoder.encode("admin123"));
		credencialAdmin.setCriacao(new Date());
		credencialAdmin.setUltimoAcesso(new Date());
		admin.getCredenciais().add(credencialAdmin);
		repositorioUsuario.save(admin);

		// 3. Criar e salvar veículo com proprietarioId
		Veiculo veiculo = new Veiculo();
		veiculo.setPlaca("ABC-0000");
		veiculo.setModelo("corolla-cross");
		veiculo.setTipo(TipoVeiculo.SUV);
		veiculo.setProprietarioId(cliente.getId());
		veiculo = repositorioVeiculo.save(veiculo);

		cliente.getVeiculos().add(veiculo);
		repositorioUsuario.save(cliente);

		// 4. Criar vendas com IDs referenciando as entidades já persistidas
		Servico alinhamento2 = new Servico();
		alinhamento2.setDescricao("Alinhamento das rodas do carro");
		alinhamento2.setNome("Alinhamento de rodas");
		alinhamento2.setValor(50);

		Servico balanceamento = new Servico();
		balanceamento.setDescricao("Balanceamento das rodas do carro");
		balanceamento.setNome("Balanceamento de rodas");
		balanceamento.setValor(30);

		Mercadoria rodaLigaLeve2 = new Mercadoria();
		rodaLigaLeve2.setCadastro(new Date());
		rodaLigaLeve2.setFabricao(new Date());
		rodaLigaLeve2.setNome("Roda de liga leve modelo toyota etios");
		rodaLigaLeve2.setValidade(new Date());
		rodaLigaLeve2.setQuantidade(30);
		rodaLigaLeve2.setValor(300.0);
		rodaLigaLeve2.setDescricao("Roda de liga leve original de fábrica da toyota para modelos do tipo hatch");

		Mercadoria rodaLigaLeve1Venda = new Mercadoria();
		rodaLigaLeve1Venda.setCadastro(new Date());
		rodaLigaLeve1Venda.setFabricao(new Date());
		rodaLigaLeve1Venda.setNome("Roda de liga leve modelo toyota etios");
		rodaLigaLeve1Venda.setValidade(new Date());
		rodaLigaLeve1Venda.setQuantidade(30);
		rodaLigaLeve1Venda.setValor(300.0);
		rodaLigaLeve1Venda.setDescricao("Roda de liga leve original de fábrica da toyota para modelos do tipo hatch");

		Servico trocaRodas1Venda = new Servico();
		trocaRodas1Venda.setDescricao("Troca das rodas do carro por novas");
		trocaRodas1Venda.setNome("Troca de rodas");
		trocaRodas1Venda.setValor(50);

		Servico alinhamento1Venda = new Servico();
		alinhamento1Venda.setDescricao("Alinhamento das rodas do carro");
		alinhamento1Venda.setNome("Alinhamento de rodas");
		alinhamento1Venda.setValor(50);

		Venda venda1 = new Venda();
		venda1.setCadastro(new Date());
		venda1.setIdentificacao("1234698745");
		venda1.setClienteId(cliente.getId());
		venda1.setFuncionarioId(funcionario.getId());
		venda1.setVeiculoId(veiculo.getId());
		venda1.getMercadorias().add(rodaLigaLeve1Venda);
		venda1.getServicos().add(trocaRodas1Venda);
		venda1.getServicos().add(alinhamento1Venda);

		Venda venda2 = new Venda();
		venda2.setCadastro(new Date());
		venda2.setIdentificacao("1234698749");
		venda2.setClienteId(cliente.getId());
		venda2.setFuncionarioId(funcionario.getId());
		venda2.setVeiculoId(veiculo.getId());
		venda2.getMercadorias().add(rodaLigaLeve2);
		venda2.getServicos().add(balanceamento);
		venda2.getServicos().add(alinhamento2);

		empresa.getVendas().add(venda1);
		empresa.getVendas().add(venda2);
		repositorioEmpresa.save(empresa);
	}
}
