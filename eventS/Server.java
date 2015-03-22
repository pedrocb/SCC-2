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
	private double arrivalTick;
	private double serviceTick;
	private double endTick;
	public Token(double arrivalTick) {this.arrivalTick = this.serviceTick = arrivalTick;}
	public double waitTime() {return serviceTick - arrivalTick;}
	public double cycleTime(double time) {return time - arrivalTick;}
	public double cycleTime() {return endTick - arrivalTick;}
	public void arrivalTick(double arrivalTick) {this.arrivalTick = arrivalTick;}
	public double arrivalTick() {return arrivalTick;}
	public double serviceTick() {return serviceTick;}
	public void serviceTick(double serviceTick) {this.serviceTick = serviceTick;}
	public void endTick(double endTick) {this.endTick = endTick;}
	public void addcashiertime(int n,double time){
		cashiertime.set(n,time);
	}
	@Override
	public String toString() {return String.format("[%.2f]", arrivalTick);}
}

/*final class Arrival extends Event {
	private final Server model;
	public Arrival(Server model) {
		super();
		this.model = model;
	}
	@Override
	public void execute() {
		Token client = new Token(time);
		if (model.rest.value() > 0) {
			model.rest.inc(-1, time);
			model.schedule(new Departure(model, client), model.service.next());
		}
		else {
			model.queue.inc(1, time);
			model.line.add(client);
		}
		model.schedule(this, model.arrival.next());
	}
}
*/
/*final class Departure extends Event {
	private final Server model;
	public Departure(Server model, Token client) {
		super();
		this.model = model;
		this.client = client;
	}
	private Token client = null;
	@Override
	public void execute() {
		if (model.queue.value() > 0) {
			model.queue.inc(-1, time);
			client = model.line.remove(0);
			client.serviceTick(time);
			model.delayTime.add(client.waitTime());
			model.schedule(this, model.service.next());
		}
		else {
			model.rest.inc(1, time);
		}
	}
}*/

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

final class Server extends Model {
	final Fila hotFood;
	final Fila sandes;
	final Accumulate tamfilaCaixa;
	final Accumulate atendidoCaixa;
	final List<Token> filaCaixa;
	final Accumulate tamfilaBebida;
	final Accumulate atendidoBebida;
	final List<Token> filaBebida;
	final Average delayTime;
	public Server(int n) {
		super();
		hotFood = new Fila(new Accumulate(0),new Accumulate(n));
		this.delayTime = new Average();
		sandes = new Fila(new Accumulate(0),new Accumulate(n));
		this.tamfilaCaixa = new Accumulate(0);
		this.atendidoCaixa = new Accumulate(n);
		this.filaCaixa = new ArrayList<>();
		this.tamfilaBebida = new Accumulate(0);
		this.atendidoBebida = new Accumulate(n);
		this.filaBebida = new ArrayList<>();
	}
	@Override
	protected void init() {				
		schedule(new GeraTokens(this),0);
		schedule(new Stop(this),5400);
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
