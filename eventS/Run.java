package eventS;

public class Run {
	public static void main(String args[]){
		Model modelo = new Server();
		//Model modelo = new Server2();
		//Model modelo = new Server3();
		//Model modelo = new Server4();
		//Model modelo = new Server5();
		//Model modelo = new Server6();
		//Model modelo = new Server7();
		//Model modelo = new Server8();
		Simulator simulador = new Simulator(modelo);
		modelo.simulator(simulador);
		simulador.run();
	}
}
