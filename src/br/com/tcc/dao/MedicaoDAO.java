package br.com.tcc.dao;
/**
 * Projeto de Ger�ncia
 * Classe DAO - Data Access Object
 * @autor Paulo C. Barreto / Luiz R. Barreto
 * @date 26/05/2012
 */

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.GregorianCalendar;

import br.com.tcc.modelo.Medida;

public class MedicaoDAO {
	// Estabele conex�o com o banco de dados
	private Connection connection;

	public MedicaoDAO() throws SQLException {
		this.connection = ConnectionFactory.getConnection();
	}

	public void adiciona(Medida leitura) throws SQLException {
		// Prepara statement para inser��o
		PreparedStatement stmt = this.connection
				.prepareStatement("insert into Medida (id,data, hora, temperatura) values (?,?,?,?)");

		// Seta os valores dos argumentos da stmt
		GregorianCalendar atual = new GregorianCalendar();
		Date data = new Date(atual.getTime().getYear(), atual.getTime().getMonth(), atual.getTime().getDate());
		Time hora = new Time(atual.getTime().getHours(), atual.getTime().getMinutes(), atual.getTime().getSeconds());
		stmt.setDate(1, null);
		stmt.setDate(2, data);
		stmt.setTime(3, hora);
		stmt.setFloat(4, (float) leitura.getValor());

		// Executa query
		stmt.execute();
		stmt.close();
	}
}