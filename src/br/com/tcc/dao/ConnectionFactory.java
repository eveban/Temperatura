package br.com.tcc.dao;
/**
 * Projeto de Ger�ncia
 * Disciplina: 21845- SISTEMAS INFORMA��O GER�NCIA TELECOMUNICA��ES
 * ConnectionFactory - F�brica de Conex�es com o Banco
 * @autor Paulo C. Barreto / Luiz R. Barreto
 * @date 26/05/2012
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost/temperatura","root","123456");
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage());
		}
	}
}