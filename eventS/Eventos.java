package eventS;

import java.util.Random;

class ChegadaHotFood extends Event{
	private final Server model;
	private int server;
	private Token pessoa;
	public ChegadaHotFood(Server model,Token pessoa){
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		if (model.hotFood.atendido.value() > 0) {
			System.out.println("Foi atendido.");
			model.hotFood.atendido.inc(-1, time);
			model.schedule(new SaidaHotFood(model, pessoa), new Uniform(new Random().nextInt(),50.0,120.0).next());
		}
		else {
			System.out.println("Foi para a fila");
			model.hotFood.tamFila.inc(1, time);
			model.hotFood.fila.add(pessoa);
		}
	}
	public String toString(){
		return pessoa + " chegou a hotfood " + time;
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
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),20.0,40.0).next(), time);
		model.schedule(new ChegadaBebidas(model,pessoa),0);
		if (model.hotFood.tamFila.value() > 0) {
			model.hotFood.tamFila.inc(-1, time);
			pessoa = model.hotFood.fila.remove(0);
			model.schedule(new SaidaHotFood(model,pessoa), new Uniform(new Random().nextInt(),50.0,120.0).next());
			System.out.println(pessoa + " foi atendido");
		}
		else {
			model.hotFood.atendido.inc(1, time);
		}
	}
	
	public String toString(){
		return pessoa + " saiu da hotfood "+time;
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
		if (model.sandes.atendido.value() > 0) {
			System.out.println("Atendido");
			model.sandes.atendido.inc(-1, time);
			model.schedule(new SaidaSandes(model, pessoa), new Uniform(new Random().nextInt(),60.0,180.0).next());
		}
		else {
			System.out.println("Fila");
			model.sandes.tamFila.inc(1, time);
			model.sandes.fila.add(pessoa);
		}
	}
	
	public String toString(){
		return pessoa + " chegou a sandes " + time;
	}
}

class SaidaSandes extends Event{
	private final Server model;
	private Token pessoa;
	public SaidaSandes(Server model, Token pessoa) {
		super();
		this.model = model;
		this.pessoa = pessoa;
	}
	@Override
	public void execute() {
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),5.0,15.0).next(), time);
		model.schedule(new ChegadaBebidas(model,pessoa), 0);
		if (model.sandes.tamFila.value() > 0) {
			model.sandes.tamFila.inc(-1, time);
			pessoa = model.sandes.fila.remove(0);
			model.schedule(new SaidaSandes(model,pessoa), new Uniform(new Random().nextInt(),60.0,180.0).next());
			System.out.println(pessoa + " foi atendido");
		}
		else {
			model.sandes.atendido.inc(1, time);
		}
	}
	public String toString(){
		return pessoa + " saiu da sandes " + time;
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
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),5.0,10.0).next(), time);
		model.schedule(new ChegadaCaixa(model, pessoa), new Uniform(new Random().nextInt(),5.0,20.0).next());
	}
	
	public String toString(){
		return pessoa + " chegou a bebidas " + time;
	}
}

class ChegadaCaixa extends Event{
	private final Server model;
	private Token pessoa;
	public ChegadaCaixa(Server model,Token pessoa){
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		if(model.caixa[0].atendido.value()==0 && model.caixa[1].atendido.value()==0){
			if(model.caixa[0].tamFila.value()>model.caixa[1].tamFila.value()){
				System.out.println("Foi pa fila 1");
				model.caixa[1].tamFila.inc(1, time);
				model.caixa[1].fila.add(pessoa);
			}
			else{
				System.out.println("Foi pa fila 0");
				model.caixa[0].tamFila.inc(1,time);
				model.caixa[0].fila.add(pessoa);
			}
		}
		else if (model.caixa[0].atendido.value()==1){
			System.out.println("Foi atendido na caixa 0");
			model.caixa[0].atendido.inc(-1,time);
			model.schedule(new SaidaCaixa(model,pessoa,0),pessoa.cashiertime());
		}
		else if(model.caixa[1].atendido.value()==1){
			System.out.println("Foi atendido na caixa 1");
			model.caixa[1].atendido.inc(-1,time);
			model.schedule(new SaidaCaixa(model,pessoa,1),pessoa.cashiertime());
		}
	}
	
	public String toString(){
		return pessoa + " chegou a caixa " + time;
	}
}

class SaidaCaixa extends Event{
	private final Server model;
	private Token pessoa;
	private int ncaixa;
	public SaidaCaixa(Server model,Token pessoa,int caixa){
		this.model = model;
		this.pessoa = pessoa;
		this.ncaixa = caixa;
	}
	public void execute(){
		if (model.caixa[ncaixa].tamFila.value() > 0) {
			model.caixa[ncaixa].tamFila.inc(-1, time);
			pessoa = model.caixa[ncaixa].fila.remove(0);
			model.schedule(new SaidaCaixa(model,pessoa,ncaixa), new Uniform(new Random().nextInt(),60.0,180.0).next());
			System.out.println(pessoa + "foi para atendido em " +ncaixa);
		}
		else {
			model.caixa[ncaixa].atendido.inc(1, time);
		}
	}
	public String toString(){
		return pessoa + " saiu da caixa " +ncaixa + " " + time;
	}
}

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

