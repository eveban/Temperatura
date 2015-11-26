package br.com.tcc.modelo;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Enumeration;

import br.com.tcc.dao.MedicaoDAO;

public class Medicao implements SerialPortEventListener {

	String texto = "";
	SerialPort serialPort;

	// Configuração da Porta
	private static final String PORT_NAMES[] = {
			// "/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/tty.usbmodemfa131", "/dev/ttyUSB0", // Linux
			"COM6", // Windows
	};
	// Buffered de input da pota COM
	private InputStream input;
	// Output stream da porta COM
	private OutputStream output;
	// Milissegundos para bloquear enquanto aguarda abertura da COM
	private int TIME_OUT;
	// Defini��o do n�mero de bits por segundo da porta COM
	private int DATA_RATE;
	// Instacia do DAO
	private MedicaoDAO dao;
	// Instancia do objeto de leitura
	private Medida leitura;

	// Método Construtor da classe de medição
	public Medicao(String portaCOM, int timeOut, int dataRate) throws SQLException {
		dao = new MedicaoDAO();
		leitura = new Medida();
		TIME_OUT = timeOut;
		DATA_RATE = dataRate;
	}

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// Localizando portas de comunica��o dispon�veis
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Não foi possível encontrar a porta COM.");
			return;
		}

		try {
			// Abertura da porta serial
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// Setando parametros da porta
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// Abertura dos streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// Adicionando eventos de monitoramento da porta
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	/**
	 * Fim do Pilar Configuração
	 */

	/**
	 * Isto deve ser chamado quando você parar de usar a porta. Isso ir� evitar
	 * o bloqueio de porta em plataformas como o Linux.
	 **/
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			System.out.println(texto);
		}
	}

	/**
	 * Lidar com um evento na porta serial. Leitura dos dados, tratamento e
	 * persistencia.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte bloco[] = new byte[available];
				input.read(bloco, 0, available);

				// Resultados apresentados são a leitura de input
				for (int i = 0; i < available; i++) {
					if (bloco[i] == '$')
						texto = "";
					else if (bloco[i] == '#') {
						System.out.println(texto.lastIndexOf('-'));
						System.out.println(texto.indexOf('-'));

						if (texto.indexOf("-") >= 0) {
							texto = texto.replace("-", "");
							texto = "-" + texto;
						}
						System.out.println(texto);

						float valor = Float.parseFloat(texto);
						if (valor != -127) {
							// Escrevendo as vari�veis monitoradas
							leitura.setValor(valor);

							// Realizando Persistencia no Banco de Dados
							dao.adiciona(leitura);
						}

						System.out.println("Gravação realizada no Banco: " + leitura.getValor());
					} else
						texto = texto + new String(bloco).charAt(i);
				}
			} catch (

			Exception e)

			{
				System.err.println(e.toString());
			}

		}
	}

	public static void main(String[] args) throws Exception {
		Medicao main = new Medicao("/dev/tty.usbmodemfa131", 2000, 9600);
		main.initialize();
		System.out.println("Inicializada Leitura");
	}
}