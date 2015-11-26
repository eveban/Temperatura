package br.com.tcc.modelo;

import java.util.GregorianCalendar;

public class Medida {
	private float valor;
	private GregorianCalendar data;

	public Medida() {
	}

	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	public GregorianCalendar getData() {
		return data;
	}
	public void setData(GregorianCalendar data) {
		this.data = data;
	}
}
