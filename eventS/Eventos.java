package eventS;

import java.util.Random;

class ChegadaHotFood extends Event{
	private final Server model;
	private int server;
	private Token pessoa;
	public ChegadaHotFood(Server model,Token pessoa,int server){
		this.model = model;
		this.pessoa = pessoa;
		this.server = server;
	}
	public void execute(){
		Uniform uniform;
		if(server == 3){
			uniform = new Uniform(new Random().nextInt(),25.0,60.0);	
		}
		else{
			uniform = new Uniform(new Random().nextInt(),50.0,120.0);	
		}
		if (model.hotFood.atendido.value() > 0) {
			model.hotFood.atendido.inc(-1, time);
			model.schedule(new SaidaHotFood(model, pessoa,server), uniform.next());
		}
		else {
			pessoa.arrivalhotfood(time);
			model.hotFood.tamFila.inc(1, time);
			model.hotFood.fila.add(pessoa);
			if(model.hotFood.tamFila.value() > model.maxhotfoodqueue) model.maxhotfoodqueue = model.hotFood.tamFila.value();
		}
	}
	public String toString(){
		return pessoa + " chegou a hotfood " + time;
	}
}

class SaidaHotFood extends Event{
	private final Server model;
	private Token pessoa;
	private int server;
	public SaidaHotFood(Server model, Token pessoa,int server) {
		super();
		this.model = model;
		this.pessoa = pessoa;
		this.server = server;
	}
	@Override
	public void execute() {
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),20.0,40.0).next(), time);
		model.schedule(new ChegadaBebidas(model,pessoa,server),0);
		if (model.hotFood.tamFila.value() > 0) {
			model.hotFood.tamFila.inc(-1, time);
			pessoa = model.hotFood.fila.remove(0);
			model.schedule(new SaidaHotFood(model,pessoa,server), new Uniform(new Random().nextInt(),50.0,120.0).next());
			pessoa.hotfooddelay = time-pessoa.arrivalhotfood();
			model.hotfooddelay.add(pessoa.hotfooddelay);
		}
		else {
			int n;
			if(server==5 || server==6||server==8) n=2;
			else n=1;
			if(model.hotFood.atendido.value()!=n)model.hotFood.atendido.inc(1, time);
		}
	}
	
	public String toString(){
		return pessoa + " saiu da hotfood "+time;
	}
}

class ChegadaSandes extends Event{
	private final Server model;
	private Token pessoa;
	private int server;
	public ChegadaSandes(Server model,Token pessoa,int server){
		this.model = model;
		this.pessoa = pessoa;
		this.server = server;
	}
	public void execute(){
		Uniform uniform;
		if(server == 4){
			uniform = new Uniform(new Random().nextInt(),30.0,90.0);
		}
		else{
			uniform = new Uniform(new Random().nextInt(),60.0,180.0);
		}
		if (model.sandes.atendido.value() > 0) {
			model.sandes.atendido.inc(-1, time);
			model.schedule(new SaidaSandes(model, pessoa,server), uniform.next());
		}
		else {
			pessoa.arrivalsandes(time);
			model.sandes.tamFila.inc(1, time);
			model.sandes.fila.add(pessoa);
			if(model.sandes.tamFila.value() > model.maxsandesqueue) model.maxsandesqueue = model.sandes.tamFila.value();
		}
	}
	
	public String toString(){
		return pessoa + " chegou a sandes " + time;
	}
}

class SaidaSandes extends Event{
	private final Server model;
	private Token pessoa;
	private int server;
	public SaidaSandes(Server model, Token pessoa,int server) {
		super();
		this.model = model;
		this.pessoa = pessoa;
		this.server = server;
	}
	@Override
	public void execute() {
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),5.0,15.0).next(), time);
		model.schedule(new ChegadaBebidas(model,pessoa,server), 0);
		if (model.sandes.tamFila.value() > 0) {
			model.sandes.tamFila.inc(-1, time);
			pessoa = model.sandes.fila.remove(0);
			model.schedule(new SaidaSandes(model,pessoa,server), new Uniform(new Random().nextInt(),60.0,180.0).next());
			pessoa.sandesdelay = time-pessoa.arrivalsandes();
			model.sandesdelay.add(pessoa.sandesdelay);
		}
		else {
			int n;
			if(server==5||server==7||server==8) n=2;
			else n=1;
			if(model.sandes.atendido.value()!=n)model.sandes.atendido.inc(1, time);		}
	}
	public String toString(){
		return pessoa + " saiu da sandes " + time;
	}
}

class ChegadaBebidas extends Event{
	private final Server model;
	private Token pessoa;
	private int server;
	public ChegadaBebidas(Server model,Token pessoa,int server){
		this.model = model;
		this.pessoa = pessoa;
		this.server = server;
	}
	public void execute(){
		pessoa.addcashiertime((int)new Uniform(new Random().nextInt(),5.0,10.0).next(), time);
		model.schedule(new ChegadaCaixa(model, pessoa,server), new Uniform(new Random().nextInt(),5.0,20.0).next());
	}
	
	public String toString(){
		return pessoa + " chegou a bebidas " + time;
	}
}

class ChegadaCaixa extends Event{
	private final Server model;
	private Token pessoa;
	private int server;
	public ChegadaCaixa(Server model,Token pessoa,int server){
		this.server = server;
		this.model = model;
		this.pessoa = pessoa;
	}
	public void execute(){
		if(server == 2 || server == 6 || server == 8){
			if(model.caixa[0].atendido.value()==0 && model.caixa[1].atendido.value()==0 && model.caixa[1].atendido.value()==0){
				pessoa.arrivalcashier(time);
				if(model.caixa[0].tamFila.value()>=model.caixa[1].tamFila.value() && model.caixa[2].tamFila.value()>=model.caixa[1].tamFila.value()){
					model.caixa[1].tamFila.inc(1, time);
					model.caixa[1].fila.add(pessoa);
					if(model.caixa[1].tamFila.value() > model.maxcashier1queue) model.maxcashier1queue = model.caixa[1].tamFila.value();
				}
				else if(model.caixa[1].tamFila.value()>=model.caixa[0].tamFila.value() && model.caixa[2].tamFila.value()>=model.caixa[0].tamFila.value()){
					model.caixa[0].tamFila.inc(1,time);
					model.caixa[0].fila.add(pessoa);
					if(model.caixa[0].tamFila.value() > model.maxcashier0queue) model.maxcashier0queue = model.caixa[0].tamFila.value();
				}
				else{
					model.caixa[2].tamFila.inc(1,time);
					model.caixa[2].fila.add(pessoa);
					if(model.caixa[2].tamFila.value() > model.maxcashier2queue) model.maxcashier2queue = model.caixa[2].tamFila.value();
				}
				
			}
			else if (model.caixa[0].atendido.value()==1){
				model.caixa[0].atendido.inc(-1,time);
				model.schedule(new SaidaCaixa(model,pessoa,0,server),pessoa.cashiertime());
			}
			else if(model.caixa[1].atendido.value()==1){
				model.caixa[1].atendido.inc(-1,time);
				model.schedule(new SaidaCaixa(model,pessoa,1,server),pessoa.cashiertime());
			}
			else if(model.caixa[2].atendido.value()==1){
				model.caixa[2].atendido.inc(-1, time);
				model.schedule(new SaidaCaixa(model,pessoa,1,server),pessoa.cashiertime());
			}
		}
		else{
			if(model.caixa[0].atendido.value()==0 && model.caixa[1].atendido.value()==0){
				pessoa.arrivalcashier(time);
				if(model.caixa[0].tamFila.value()>model.caixa[1].tamFila.value()){
					model.caixa[1].tamFila.inc(1, time);
					model.caixa[1].fila.add(pessoa);
					if(model.caixa[1].tamFila.value() > model.maxcashier1queue) model.maxcashier1queue = model.caixa[1].tamFila.value();
				}
				else{
					model.caixa[0].tamFila.inc(1,time);
					if(model.caixa[0].tamFila.value() > model.maxcashier0queue) model.maxcashier0queue = model.caixa[0].tamFila.value();
					model.caixa[0].fila.add(pessoa);
				}
			}
			else if (model.caixa[0].atendido.value()==1){
				model.caixa[0].atendido.inc(-1,time);
				model.schedule(new SaidaCaixa(model,pessoa,0,server),pessoa.cashiertime());
			}
			else if(model.caixa[1].atendido.value()==1){
				model.caixa[1].atendido.inc(-1,time);
				model.schedule(new SaidaCaixa(model,pessoa,1,server),pessoa.cashiertime());
			}
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
	private int server;
	public SaidaCaixa(Server model,Token pessoa,int caixa,int server){
		this.model = model;
		this.pessoa = pessoa;
		this.ncaixa = caixa;
		this.server = server;
	}
	public void execute(){
		model.npessoas.inc(-1, time);
		model.delaytipo[(pessoa.tipo)-1].add(pessoa.hotfooddelay+pessoa.cashierdelay+pessoa.sandesdelay);
		if (model.caixa[ncaixa].tamFila.value() > 0) {
			model.caixa[ncaixa].tamFila.inc(-1, time);
			pessoa = model.caixa[ncaixa].fila.remove(0);
			model.schedule(new SaidaCaixa(model,pessoa,ncaixa,server), new Uniform(new Random().nextInt(),60.0,180.0).next());
			pessoa.cashierdelay = time-pessoa.arrivalcashier();
			model.cashierdelay.add(pessoa.cashierdelay);
		}
		else {
			model.caixa[ncaixa].atendido.inc(1, time);
		}
	}
	public String toString(){
		return pessoa + "saiu da caixa " +ncaixa + " " + time + " " + pessoa.hotfooddelay + " " + pessoa.sandesdelay + " " + pessoa.cashierdelay;
	}
}

class GeraTokens extends Event{
	private final Server model;
	private final int server;
	public GeraTokens(Server model,int server){
		this.model = model;
		this.server = server;
	}
	public void execute(){
		double prob[] = {0.5,0.3,0.1,0.1};
		double values[] = {1.0,2.0,3.0,4.0};
		double prob1[] = {0.80,0.15,0.05};
		double values1[] = {0,1,2}; 
		int n = (int)new Discrete(new Random().nextInt(),values,prob).next();
		model.npessoas.inc(n, time);
		if(model.npessoas.value()>model.pessoasmax){
			model.pessoasmax = model.npessoas.value();
		}
		for(int i=0;i<n;i++){
			Token pessoa;
			int nn = (int)new Discrete(new Random().nextInt(),values1,prob1).next();
			if(nn==0){
				pessoa = new Token(time,1);
				model.schedule(new ChegadaHotFood(model,pessoa,server),0);
			}
			else if(nn==1){
				pessoa = new Token(time,2);
				model.schedule(new ChegadaSandes(model,pessoa,server),0);
			}
			else{
				pessoa = new Token(time,3);
				model.schedule(new ChegadaBebidas(model,pessoa,server), 0);
			}
		}
		model.schedule(new GeraTokens(model,server),new Exponential(new Random().nextInt(),30).next());
	}
}

