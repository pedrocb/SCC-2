package eventS;

import java.util.Random;

class ChegadaHotFood extends Event{
	private final Server model;
	public ChegadaHotFood(Server model){
		this.model = model;
	}
	public void execute(){
		Token client = new Token(time);
		if (model.atendidoHotFood.value() > 0) {
			model.atendidoHotFood.inc(-1, time);
			model.schedule(new SaidaHotFood(model, client), model.service.next());
		}
		else {
			model.queue.inc(1, time);
			model.line.add(client);
		}
	}
}

class ChegadaSandes extends Event{
	private final Server model;
	public ChegadaSandes(Server model){
		this.model = model;
	}
	public void execute(){
		Token client = new Token(time);
		if (model.atendidoHotFood.value() > 0) {
			model.atendidoHotFood.inc(-1, time);
			model.schedule(new SaidaHotFood(model, client), model.service.next());
		}
		else {
			model.queue.inc(1, time);
			model.line.add(client);
		}
	}
}

class ChegadaBebidas extends Event{
	private final Server model;
	public ChegadaBebidas(Server model){
		this.model = model;
	}
	public void execute(){
		Token client = new Token(time);
		if (model.atendidoHotFood.value() > 0) {
			model.atendidoHotFood.inc(-1, time);
			model.schedule(new SaidaHotFood(model, client), model.service.next());
		}
		else {
			model.queue.inc(1, time);
			model.line.add(client);
		}
	}
}

class SaidaHotFood extends Event{
	private final Server model;
	public SaidaHotFood(Server model, Token client) {
		super();
		this.model = model;
		this.client = client;
	}
	private Token client = null;
	@Override
	public void execute() {
		System.out.format("Saiu %.2f\t%.2f\t%.2f\n", client.arrivalTick(), client.serviceTick(), time);
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
			int nn = (int)new Discrete(new Random().nextInt(),values1,prob1).next();
			if(nn==0){
				model.schedule(new ChegadaHotFood(model),0);
			}
			else if(nn==1){
				model.schedule(new ChegadaSandes(model),0);
			}
			else{
				model.schedule(new ChegadaBebidas(model), 0);
			}
		}
		model.schedule(new GeraTokens(model),new Exponential(new Random().nextInt(),30).next());
	}
}
