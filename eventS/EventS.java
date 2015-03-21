/*
Author: Fernando J. Barros
University of Coimbra
Department of Informatics Enginnering
3030 Coimbra, Portugal
Date: 20/2/2015
 */
package eventS;
import java.util.*;


//Interface para os vários geradores de números aleatórios, só garante que têm um método chamado next().
interface RandomStream {
	abstract public double next();
}

//Gerador de números aleatórios, segundo uma distribuição binomial (?)
class Discrete extends Uniform01 {
	private final double[] prob;
	private final double[] values;
	public Discrete(int stream, double[] values, double[] prob) {
		super(stream);
		double sum = 0.0;
		for (int i = 0; i < prob.length; ++i) sum += prob[i];
		assert(sum <= 1.0); 
		this.prob = prob;
		this.values = values;
	}
	@Override
	//Devolve o próximo valor aleatório do gerador.
	public double next() {
		double rnd = super.next();
		int i = 0;
		double sum = prob[i];
		while (sum < rnd) sum += prob[++i];
		return values[i];
	}
}


//Yup, é uma classe que simplesmente guarda uma constante e devolve-a quando for preciso.
class Constant implements RandomStream {
	final private double value;
	public Constant(int value) {this.value = value;}
	@Override
	//Devolve o valor armazenado.
	public double next() {return value;}
}

//Cria um gerador de números aleatórios e devolve o próximo valor do gerador. Esta classe não vos interessa para a implementação do trabalho, é necessária para as classes Discrete, Exponential e Uniform.
class Uniform01 implements RandomStream {
	final private Random rnd;
	public Uniform01(int stream) {rnd = new Random(stream);}
	@Override
	//Devolve o próximo valor do gerador de números aleatórios.
	public double next() {return rnd.nextDouble();}
}

//Classe usada para gerar um número aleatório entre a e b, segundo uma distribuição uniforme.
class Uniform extends Uniform01 {
	final private double a, b;
	public Uniform(int stream, double a, double b) {
		super(stream);
		assert (a < b);
		this.a = a;
		this.b = b;
	}
	@Override
	//Devolve o próximo valor da distribuição uniforme.
	public double next() {return a + super.next() * (b - a);}
}

//So, classe estranha que, dado um array de valores, devolve o próximo valor da sequência quando pedido. Não verifica o limite do array. Esta classe faz sentido em C, em Java vocês podiam usar um ArrayList ou something.
class Sequence implements RandomStream {
	final private double[] values;
	private int curr;
	public Sequence(double[] values) {
		this.values = values;
		curr = 0;
	}
	@Override
	//Devolve o valor seguinte da sequência armazenada.
	public double next() {
		return values[curr++];
	}
}

//Classe usada para gerar um número aleatório, segundo uma distribuição exponencial, dado um valor médio.
class Exponential extends Uniform01 {
	final private double mean;
	public Exponential(int stream, double mean) {
		super(stream);
		this.mean = mean;
	}
	@Override
	//Devolve o próximo valor da distribuição exponencial.
	public double next() {return -mean * Math.log(super.next());}
}

//Classe para armazenamento de valores e diferenças de tempo, dunno.
class Accumulate {
	private int value = 0;
	private double accum = 0.0;
	private double last = 0.0;
	public Accumulate(int value) {this.value = value;}
	//Função que incrementa o valor armazenado em d unidades, no instante time.
	public void inc(int d, double time) {set(value + d, time);}
	//Função que atualiza o valor armazenado, no momento dado, atualizando também as outras várias variáveis internas correspondentemente.
	public void set(int v, double time) {
		double delta = time - last;
		accum += value * delta;
		this.value = v;
		last = time;
	}
	//Devolve a média de acumulações até ao instante dado.
	public double mean(double time) {return integral(time) / time;}
	//Atualiza os valores de accum e last sem mexer no value.
	public double integral(double time) {
		double delta = time - last;
		accum += value * delta;
		last = time;
		return accum;
	}
	//getter para a variável value.
	public int value() {return value;}
	@Override
	public String toString() {return "" + value + " " + accum + " " + last;}
}

//Classe usada para efetuar operações de incremento, e calcular o valor médio dos ditos incrementos.
class Average {
	private double sum;
	private int count;
	public Average() {clear();}
	//Calcula a média do valor incrementado em cada operação efetuada.
	public double mean() {return sum / count;}
	//Adiciona value à soma interna e incrementa o número de adições efetuadas.
	public void add(double value) {
		sum += value;
		++count;
	}
	//getter da variável count.
	public int count() {return count;}
	//Função usada para zerar as variáveis internas.
	public final void clear() {
		sum = 0.0;
		count = 0;
	}
	@Override
	//Devolve uma string com o valor médio dos incrementos e o número de operações efetuadas.
	public String toString() {return String.format("%.3f\t%d", mean(), count);}
}

//Classe que vos permite armazenar vários doubles, calcular a sua média e desvio padrão, e acrescentar valores à lista.
class Tally {
	final private List<Double> values;
	public Tally() {values = new ArrayList<>();}
	//Calcula a média dos valores armazenados.
	public double mean() {
		double sum = 0.0;
		for (Double value: values) sum += value;
		return sum / values.size();
	}
	//Calcula o desvio padrão dos valores armazenados.
	public double stdDev() {
		double sum = 0.0;
		double mean = mean();
		for (Double value: values) sum +=  Math.pow(mean - value, 2);
		return Math.sqrt(sum / (values.size() - 1));
	}
	//Acrescenta um valor à ArrayList.
	public void add(double value) {values.add(value);}
	//Limpa a ArrayList.
	public void clear() {values.clear();}
	@Override
	//Devolve uma string com a lista de valores.
	public String toString() {return "" + values;}
}

//Classe-mãe/pai dos eventos do simulador.
abstract class Event implements Comparable<Event> {
	protected double time = 0;
	public Event() {}
	//setter do momento do evento, depois de garantir que o momento dado é válido (maior que 0), atualiza o momento do evento.
	public void time(double time) {
		assert (time >= 0): "Event.time: Time error.";
		this. time = time;
	}
	//getter do momento do evento.
	public double time() {return time;}
	//Têm que implementar nas subclasses, é o que o evento faz, basicamente.
	public abstract void execute();
	@Override
	//Determina se o evento ocorreu antes ou depois de um evento dado.
	public int compareTo(Event e) {
		if (time > e.time) return 1;
		return -1;
	}
	@Override
	//Devolve uma string com o tipo do evento (nome da sub-classe) e o momento em que o evento ocorre.
	public String toString() {return this.getClass().getSimpleName() + " " + String.format("%.2f", time);}
}

//Modelo de execução do simulador.
abstract class Model {
	private Simulator simulator;
	protected void simulator(Simulator simulator) {this.simulator = simulator;}
	public Model() {}
	//Têm que implementar nas sub-classes, é a função de inicialização do modelo.
	abstract protected void init();
	//Cancela a execução de um evento.
	protected final void cancel(Event e) {simulator.cancel(e);}
	//Agenda um evento para acontecer num dado momento.
	protected final Event schedule(Event ev, double delta) {
		assert (delta >= 0): "Model.schedule, time error! " + delta;
		simulator.schedule(ev, delta);
		return ev;
	}
	//Reagenda a execução de um evento.
	protected final boolean reschedule(Event e, double delta) {
		assert (delta >= 0): "Model.reschedule, time error! " + delta;
		return simulator.reschedule(e, delta);
	}
	//Remove todos os eventos do simulador.
	protected final void clear() {simulator.clear();}
}

//Classe que armazena os vários eventos do simulador.
final class EventList {
	protected final PriorityQueue<Event> eventList;
	public EventList() {
		this.eventList = new PriorityQueue<>(10);
	}
	//Devolve verdadeiro se a lista estiver vazia, falso em caso contrário.
	public boolean empty() {return eventList.isEmpty();}
	//Limpa a lista de eventos.
	public void clear() {eventList.clear();}
	//Agenda um evento para um dado momento, acrescentando-o à lista de eventos.
	public void schedule(Event e, double time) {
		e.time(time);
		eventList.add(e);
	}
	//Reagenda um evento (ou agenda-o, se ainda não tiver sido agendado).
	public boolean reschedule(Event e, double time) {
		boolean bool = eventList.remove(e);
		e.time(time);
		eventList.add(e);
		return bool;
	}
	//Cancela a execução de um evento, removendo-o da lista.
	public void cancel(Event e) {eventList.remove(e);}
	@Override
	//Devolve string com a lista de eventos.
	public String toString() {return eventList.toString();}
	//Assumindo que existe um evento agendado (sim, não verifica se existe), devolve o seu momento de execução.
	public double timeNext() {return eventList.peek().time();}
	//Remove próximo evento da lista e devolve-o.
	public Event pop() {return eventList.poll();}
}

//Simulador de eventos.
final class Simulator {
	private final Model model;
	double clock;
	private final EventList events;
	public Simulator(Model model) {
		this.model = model;
		clock = 0.0;
		events = new EventList();
	}
	//Cancela a execução de um evento.
	protected void cancel(Event e) {events.cancel(e);}
	//Agenda a execução de um evento.
	public Event schedule(Event e, double delta) {
		assert (delta >= 0): "Simulator.schedule, time error! " + delta;
		events.schedule(e, clock + delta);
		return e;
	}
	//Reagenda a execução de um evento.
	public boolean reschedule(Event e, double delta) {
		assert (delta >= 0): "Simulation.reschedule, time error! " + delta;
		return events.reschedule(e, clock + delta);
	}
	//Limpa a lista de eventos do simulador.
    public void clear() {events.clear();}
    //Corpo principal do simulador: enquanto houver eventos a executar, executa-os, atualizando o relógio interno.
    public final void run() {
        model.init();
        while (! events.empty()) {
                Event event = events.pop();
                double curr = event.time();
                assert (curr >= clock): "Simulation.run, time error. " + curr + " > " + clock;
                clock = curr;
                event.execute();
        }
    }   
}       