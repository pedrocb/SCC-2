package eventS;

import java.util.Random;

class ChegadaHotFood extends Event{
	private final Server model;
	private Token pessoa;
	public ChegadaHotFood(Server model,Token pessoa){
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		Token client = new Token(time);
		if (model.hotFood.atendido.value() > 0) {
			model.hotFood.atendido.inc(-1, time);
			model.schedule(new SaidaHotFood(model, client), new Uniform(new Random().nextInt(),50.0,120.0).next());
		}
		else {
			model.hotFood.tamFila.inc(1, time);
			model.hotFood.fila.add(client);
		}
	}
}

class SaidaHotFood extends Event{
	private final Server model;
	private Token pessoa;
	public SaidaHotFood(Server model, Token pessoa) {
		super();
		this.model = model;
		this.pessoa = pessoa;
	}
	@Override
	public void execute() {
		if (model.hotFood.atendido.value() > 0) {
			model.hotFood.tamFila.inc(-1, time);
			pessoa = model.hotFood.fila.remove(0);
			pessoa.serviceTick(time);
			model.delayTime.add(pessoa.waitTime());
			model.schedule(this, new Uniform(new Random().nextInt(),50.0,120.0).next());
		}
		else {
			model.hotFood.atendido.inc(1, time);
		}
	}
}

class ChegadaSandes extends Event{
	private final Server model;
	private Token pessoa;
	public ChegadaSandes(Server model,Token pessoa){
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		Token client = new Token(time);
		if (model.sandes.atendido.value() > 0) {
			model.sandes.atendido.inc(-1, time);
			model.schedule(new SaidaSandes(model, client), new Uniform(new Random().nextInt(),60.0,180.0).next());
		}
		else {
			model.sandes.tamFila.inc(1, time);
			model.sandes.fila.add(client);
		}
	}
}

class SaidaSandes extends Event{
	private final Server model;
	private Token pessoa;
	public SaidaSandes(Server model, Token client) {
		super();
		this.model = model;
		this.client = client;
	}
	private Token client = null;
	@Override
	public void execute() {
		if (model.sandes.tamFila.value() > 0) {
			model.sandes.tamFila.inc(-1, time);
			client = model.sandes.fila.remove(0);
			client.serviceTick(time);
			model.delayTime.add(client.waitTime());
			model.schedule(this, new Uniform(new Random().nextInt(),60.0,180.0).next());
		}
		else {
			model.sandes.atendido.inc(1, time);
		}
	}
}

class ChegadaBebidas extends Event{
	private final Server model;
	private Token pessoa;
	public ChegadaBebidas(Server model,Token pessoa){
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		model.atendidoBebida.inc(-1, time);
		//model.schedule(new ChegadaCaixa(model, pessoa), new Uniform(new Random().nextInt(),5.0,20.0).next());
	}
}



/*class SaidaBebida extends Event{
	private final Server model;
	private Token pessoa;
	public SaidaBebida(Server model, Token pessoa) {
		super();
		this.model = model;
		this.pessoa = pessoa;
	}
	@Override
	public void execute() {
		System.out.format("Saiu %.2f\t%.2f\t%.2f\n", pessoa.arrivalTick(), pessoa.serviceTick(), time);
		if (model.tamfilaBebida.value() > 0) {
			model.tamfilaBebida.inc(-1, time);
			pessoa = model.filaBebida.remove(0);
			pessoa.serviceTick(time);
			model.delayTime.add(pessoa.waitTime());
			model.schedule(this, new Uniform(new Random().nextInt(),5.0,20.0).next());
		}
		else {
			model.atendidoBebida.inc(1, time);
		}
		System.out.println(model.tamfilaBebida.value());
	}
}*/

class GeraTokens extends Event{
	private final Server model;
	public GeraTokens(Server model){
		this.model = model;
	}
	public void execute(){
		double prob[] = {0.5,0.3,0.1,0.1};
		double values[] = {1.0,2.0,3.0,4.0};
		double prob1[] = {0.80,0.15,0.05};
		double values1[] = {0,1,2}; 
		int n = (int)new Discrete(new Random().nextInt(),values,prob).next();
		for(int i=0;i<n;i++){
			Token pessoa = new Token(time);
			int nn = (int)new Discrete(new Random().nextInt(),values1,prob1).next();
			if(nn==0){
				model.schedule(new ChegadaHotFood(model,pessoa),0);
			}
			else if(nn==1){
				model.schedule(new ChegadaSandes(model,pessoa),0);
			}
			else{
				model.schedule(new ChegadaBebidas(model,pessoa), 0);
			}
		}
		model.schedule(new GeraTokens(model),new Exponential(new Random().nextInt(),30).next());
	}
}

