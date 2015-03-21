package eventS;

public class Run {
	public static void main(String args[]){
		Model modelo = new Server(1);
		Simulator simulador = new Simulator(modelo);
		modelo.simulator(simulador);
		simulador.run();
	}
}
