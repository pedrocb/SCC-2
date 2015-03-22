/*
Author: Fernando J. Barros
University of Coimbra
Department of Informatics Enginnering
3030 Coimbra, Portugal
Date: 20/2/2015
 */
package eventS;

import java.util.ArrayList;
import java.util.List;

class Token {
	private Accumulate cashiertime;
	public double arrivalhotfood;
	public double arrivalcashier;
	public double arrivalsandes;
	
	public double arrivalTick;
	public double hotfooddelay;
	public double cashierdelay;
	public double sandesdelay;
	
	public int tipo;
	
	public Token(double arrivalTick,int tipo) {
		cashiertime = new Accumulate(0);
		hotfooddelay = 0.0;
		cashierdelay = 0.0;
		sandesdelay = 0.0;
		arrivalhotfood = 0.0;
		arrivalcashier = 0.0;
		arrivalsandes = 0.0;
		this.arrivalTick = arrivalTick;
		this.tipo = tipo;
	}
	public void arrivalTick(double arrivalTick) {this.arrivalTick = arrivalTick;}
	public double arrivalTick() {return arrivalTick;}
	public void arrivalhotfood(double delay){
		this.arrivalhotfood = delay;
	}
	public void arrivalcashier(double delay){
		this.arrivalcashier = delay;
	}
	public void arrivalsandes(double delay){
		this.arrivalsandes = delay;
	}

	public double arrivalcashier() {return arrivalcashier;}
	public double arrivalsandes() {return arrivalsandes;}
	public double arrivalhotfood() {return arrivalhotfood;}
	public double cashiertime() {return cashiertime.value();}
	public void addcashiertime(int n,double time){
		cashiertime.inc(n,time);
	}
	@Override
	public String toString() {return String.format("[%.2f]", arrivalTick);}
}

final class Stop extends Event {
	private final Server model;
	public Stop(Server model) {
		super();
		this.model = model;
	}
	@Override
	public void execute() {
		model.clear();
	}
}
//Model base
public class Server extends Model {
	Fila hotFood;
	Fila sandes;
	Fila[] caixa;
	public Server() {
		super();
		npessoas = new Accumulate(0);
		hotFood = new Fila(new Accumulate(0),new Accumulate(1));
		sandes = new Fila(new Accumulate(0),new Accumulate(1));
		caixa = new Fila[2];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
		hotfooddelay = new Average();
		sandesdelay = new Average();
		cashierdelay = new Average();
		pessoasmax = 0;
		delaytipo = new Average[3];
		delaytipo[0] = new Average();
		delaytipo[1] = new Average();
		delaytipo[2] = new Average();
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,1),0);
		schedule(new Stop(this),60*90);
	}
}
//Model a)i
final class Server2 extends Server {
	public Server2() {
		super();
		caixa = new Fila[3];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[2] = new Fila(new Accumulate(0),new Accumulate(1));
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,2),0);
		schedule(new Stop(this),60*90);
	}
}
//Model a)ii
final class Server3 extends Server {
	public Server3() {
		super();
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,3),0);
		schedule(new Stop(this),90*60);
	}
}
//Model a)iii
final class Server4 extends Server {
	public Server4() {
		super();
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,4),0);
		schedule(new Stop(this),60*90);
	}
}
//Model b)i
final class Server5 extends Server {
	public Server5() {
		super();
		hotFood = new Fila(new Accumulate(0),new Accumulate(2));
		sandes = new Fila(new Accumulate(0),new Accumulate(2));
		caixa = new Fila[2];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,5),0);
		schedule(new Stop(this),60*90);
	}
}
//Model b)ii
final class Server6 extends Server {
	public Server6() {
		super();
		hotFood = new Fila(new Accumulate(0),new Accumulate(2));
		sandes = new Fila(new Accumulate(0),new Accumulate(1));
		caixa = new Fila[3];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[2] = new Fila(new Accumulate(0),new Accumulate(1));
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,6),0);
		schedule(new Stop(this),60*90);
	}
}
//Model b)iii
final class Server7 extends Server {
	public Server7() {
		super();
		hotFood = new Fila(new Accumulate(0),new Accumulate(1));
		sandes = new Fila(new Accumulate(0),new Accumulate(2));
		caixa = new Fila[3];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[2] = new Fila(new Accumulate(0),new Accumulate(1));
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,7),0);
		schedule(new Stop(this),60*90);
	}
}

final class Server8 extends Server {
	public Server8() {
		super();
		hotFood = new Fila(new Accumulate(0),new Accumulate(2));
		sandes = new Fila(new Accumulate(0),new Accumulate(2));
		caixa = new Fila[3];
		caixa[0] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[1] = new Fila(new Accumulate(0),new Accumulate(1));
		caixa[2] = new Fila(new Accumulate(0),new Accumulate(1));
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this,8),0);
		schedule(new Stop(this),60*90);
	}
}

final class Fila{
	Accumulate tamFila;
	Accumulate atendido;
	List<Token> fila;
	public Fila(Accumulate tamFila,Accumulate atendido){
		this.tamFila = tamFila;
		this.atendido = atendido;
		fila = new ArrayList<>();
	}
}
