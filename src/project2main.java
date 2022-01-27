import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class project2main {

	public static void main(String args[]) throws FileNotFoundException{
		Scanner in = new Scanner(new File(args[0])); Locale.setDefault(new Locale("en", "US"));
		PrintStream out = new PrintStream(new File(args[1]));
		
		
		int numOfEvents = 0;
		int numOfPlayers = Integer.parseInt(in.nextLine());

		PriorityQueue<Event> events = new PriorityQueue<>();
		ArrayList<Player> players = new ArrayList<Player>();

		//Creates players
		for(int i=0; i<numOfPlayers; i++) {
			String data = in.nextLine();
			String[] datas = data.split(" ");

			int id = Integer.parseInt(datas[0]);
			int skillLv = Integer.parseInt(datas[1]);
			players.add(new Player(id, skillLv));

		}

		int numOfArrivs = Integer.parseInt(in.nextLine());

		//Creates arrivals
		for(int i=0; i<numOfArrivs; i++) {

			String type;
			if(in.next().equals("t")){
				type = "enterTrainingQueue"; 
			}
			else {
				type = "enterMassageQueue";
			}
			int playerID = Integer.parseInt(in.next());
			double arrivTime = Double.parseDouble(in.next());
			double duration = Double.parseDouble(in.next());
			events.add(new Event(type, playerID, arrivTime, duration));
			numOfEvents++;
		}

		//Creates physiotherapists
		int numOfPT = Integer.parseInt(in.next());
		ArrayList<Double> serviceTimePT = new ArrayList<Double>();
		ArrayList<Boolean> isEmptyPT = new ArrayList<Boolean>();
		for(int i=0; i<numOfPT; i++) {
			isEmptyPT.add(true);
		}
		for(int i=0; i<numOfPT;i++) {
			double serviceTime = Double.parseDouble(in.next());
			serviceTimePT.add(serviceTime);
		}

		//Creates training coaches
		int numOfTC = Integer.parseInt(in.next());
		ArrayList<Boolean> isEmptyTC = new ArrayList<Boolean>();
		for(int i=0; i<numOfTC; i++) {
			isEmptyTC.add(true);
		}

		//Creates masseurs
		int numOfMasseurs = Integer.parseInt(in.next());
		ArrayList<Boolean> isEmptyMasseur = new ArrayList<Boolean>();
		for(int i=0; i<numOfMasseurs; i++) {
			isEmptyMasseur.add(true);
		}

		Queue<TrainingQueueElement> trainingQueue = new PriorityQueue<TrainingQueueElement>();
		Queue<PhysioQueueElement> physioQueue = new PriorityQueue<PhysioQueueElement>();
		Queue<MassageQueueElement> massageQueue = new PriorityQueue<MassageQueueElement>();
		double totalTime = 0;					
		int maxLengthTrainQueue = 0;
		int maxLengthPTQueue = 0;
		int maxLengthMassageQueue = 0;
		int invalidAttempts = 0;			
		int cancelledAttempts = 0;
		
		while(numOfEvents != 0) {
			Event event = events.peek();

			numOfEvents--;

			if(numOfEvents == 0) {
				totalTime = events.peek().time; 
			}
			
			events.poll();
			
			//System.out.println(event.playerID +" "+event.type + " " + event.time);
			
			if(event.type.equals("enterTrainingQueue")) {
				
				if(players.get(event.playerID).isInProcess) {
					cancelledAttempts++;
				}
				else { 
					TrainingQueueElement trainQueueEnter = new TrainingQueueElement(players.get(event.playerID), event.time, event.duration);
					trainingQueue.add(trainQueueEnter);
					players.get(event.playerID).trainingCount++;
					players.get(event.playerID).isInProcess = true;
					trainQueueEnter.player.setStartWait(event.time);
					trainQueueEnter.player.trainDur = event.duration;
					if(isEmptyTC.contains(true) && trainingQueue.size()==1) { //if queue is empty go immediately to training
						int coachNum = isEmptyTC.indexOf(true);
						Event queueEnter  = new Event("enterTraining", event.playerID, event.time, event.duration);
						queueEnter.coachNum = coachNum;
						events.add(queueEnter);
						numOfEvents++;
					}
					else { //training queue is not empty then wait
						if(trainingQueue.size() > maxLengthTrainQueue) {
							maxLengthTrainQueue = trainingQueue.size();
						}
					}
				}
			}
			if(event.type.equals("enterTraining")) {
				if(!trainingQueue.isEmpty()) {
					Player player = trainingQueue.peek().player;
					trainingQueue.poll();
					player.wait(event.time, "trainingQueue");
					player.trainingTime += event.duration;
					isEmptyTC.set(event.coachNum, false);
					Event trainExit = new Event("exitTraining", player.id, event.time + event.duration, 0.0);
					trainExit.coachNum = event.coachNum;
					events.add(trainExit);
					numOfEvents++;
				}
			}
			if(event.type.equals("exitTraining")) {
				isEmptyTC.set(event.coachNum, true);
				Event queueEnter = new Event("enterPhysioQueue", event.playerID, event.time, 0.0);
				players.get(event.playerID).isInProcess = false;
				events.add(queueEnter);
				numOfEvents++;
				if(!trainingQueue.isEmpty()) {
					int id = trainingQueue.peek().player.id;
					Event enterTraining = new Event("enterTraining", id, event.time, trainingQueue.peek().player.trainDur); 
					enterTraining.coachNum = event.coachNum;
					events.add(enterTraining);
					numOfEvents++;
				}	
			}
			if(event.type.equals("enterPhysioQueue")) {
				PhysioQueueElement physioQueueEnter = new PhysioQueueElement(players.get(event.playerID), event.time, players.get(event.playerID).trainDur);
				physioQueue.add(physioQueueEnter);
				players.get(event.playerID).physioCount++;
				players.get(event.playerID).trainDur = 0.0;
				players.get(event.playerID).setStartWait(event.time);
				if(isEmptyPT.contains(true) && physioQueue.size()==1) {
					int ptNum = isEmptyPT.indexOf(true);
					Event physioEnter = new Event("enterPhysio", event.playerID, event.time, serviceTimePT.get(ptNum));
					physioEnter.ptNum = ptNum;
					events.add(physioEnter);
					numOfEvents++;
				}
				else {
					if (physioQueue.size() > maxLengthPTQueue) {
						maxLengthPTQueue = physioQueue.size();
					}
				}
			}
			if(event.type.equals("enterPhysio")) {
				if(!physioQueue.isEmpty()) {
					Player player = physioQueue.peek().player;
					physioQueue.poll();
					player.wait(event.time, "physioQueue");
					player.physioTime += event.duration;
					player.isInProcess = true;
					isEmptyPT.set(event.ptNum, false);
					Event physioExit = new Event("exitPhysio", player.id, event.time + event.duration, 0.0);
					physioExit.ptNum = event.ptNum;
					events.add(physioExit);
					numOfEvents++;
				}
			}
			if(event.type.equals("exitPhysio")){
				isEmptyPT.set(event.ptNum, true);
				players.get(event.playerID).isInProcess = false;
				if(!physioQueue.isEmpty()) {
					int id = physioQueue.peek().player.id;
					Event enterPhysio = new Event("enterPhysio", id, event.time, serviceTimePT.get(event.ptNum));
					enterPhysio.ptNum = event.ptNum;
					events.add(enterPhysio);
					numOfEvents++;
				}
			}
			if(event.type.equals("enterMassageQueue")) {
				Player player = players.get(event.playerID);
				
				if(player.massageCount == 3) {
					invalidAttempts++;
				}
				
				else if(player.isInProcess) {
					cancelledAttempts++;
					//System.out.println("Ýptal Edildi");
				}
				else {
					MassageQueueElement massageQueueEnter = new MassageQueueElement(players.get(event.playerID), event.time, event.duration);
					massageQueue.add(massageQueueEnter);
					player.setStartWait(event.time);
					player.isInProcess = true;
					if(isEmptyMasseur.contains(true) && massageQueue.size()==1) {
						int masseurNum = isEmptyMasseur.indexOf(true);
						Event massageEnter = new Event("enterMassage", player.id, event.time, event.duration);
						massageEnter.masseurNum = masseurNum;
						events.add(massageEnter);
						numOfEvents++;
					}
					else {
						if(massageQueue.size() > maxLengthMassageQueue) {
							maxLengthMassageQueue = massageQueue.size();
						}
					}
				}
			}
			if(event.type.equals("enterMassage")) {
				if(!massageQueue.isEmpty()) {
					Player player = massageQueue.peek().player;
					massageQueue.poll();
					player.wait(event.time, "massageQueue");
					player.massageTime += event.duration;
					player.massageCount++;
					isEmptyMasseur.set(event.masseurNum, false);
					Event massageExit = new Event("exitMassage", player.id, event.time + event.duration, 0.0);
					massageExit.masseurNum = event.masseurNum;
					events.add(massageExit);
					numOfEvents++;
				}
			}
			if(event.type.equals("exitMassage")) {
				isEmptyMasseur.set(event.masseurNum, true);
				players.get(event.playerID).isInProcess = false;
				if(!massageQueue.isEmpty()) {
					int id = massageQueue.peek().player.id;
					double duration = massageQueue.peek().duration;
					Event enterMassage = new Event("enterMassage", id, event.time, duration);
					enterMassage.masseurNum = event.masseurNum;
					events.add(enterMassage);
					numOfEvents++;
				}
			}
			
		}
		
		int numOfTrainings = 0;
		int numOfPhysios = 0;
		int numOfMassages = 0;
		double waitForTraining = 0;
		double waitForPhysio = 0;
		double waitForMassage = 0;
		double totalTrainingTime = 0;
		double totalPhysioTime = 0;
		double totalMassageTime = 0;
		double totalTurnaround = 0;
		double maxPhysioWait = 0.0;
		int maxPhysioWaitID = 0;
		double minWait = -1.0;
		int minWaitID = -1;
		ArrayList<Double> massageWaitList = new ArrayList<Double>();
		
		for(int i=0; i<numOfPlayers; i++) {
			Player player1 = players.get(i);
			numOfTrainings += player1.trainingCount;
			numOfPhysios += player1.physioCount;
			numOfMassages += player1.massageCount;
			waitForTraining += player1.trainingWaiting;
			waitForPhysio += player1.physioWaiting;
			waitForMassage += player1.massageWaiting;//System.out.println("Player "+i+" " +player1.massageWaiting);
			totalTrainingTime += player1.trainingTime;
			totalPhysioTime += player1.physioTime;
			totalMassageTime += player1.massageTime;
			double turnaround = player1.trainingWaiting + player1.trainingTime + player1.physioWaiting + player1.physioTime;
			totalTurnaround += turnaround;
			if(player1.physioWaiting - maxPhysioWait > 0.0000000001) {
				maxPhysioWait = player1.physioWaiting;
				maxPhysioWaitID = player1.id;
			}
			if(player1.massageCount==3) {
				massageWaitList.add(player1.massageWaiting);
			}
			
		}
		if(!massageWaitList.isEmpty()) {
			minWaitID = massageWaitList.indexOf(Collections.min(massageWaitList));
			minWait = Collections.min(massageWaitList);
		}
		
		out.println(maxLengthTrainQueue);													//1
		out.println(maxLengthPTQueue);														//2
		out.println(maxLengthMassageQueue);													//3						
		//---------------------------------
		if(numOfTrainings==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", waitForTraining/numOfTrainings);out.println();
		}
		//---------------------------------
		if(numOfPhysios==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", waitForPhysio/numOfPhysios);out.println();
		}
		//---------------------------------
		if(numOfMassages==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", waitForMassage/numOfMassages);out.println();
		}
		//---------------------------------						
		if(numOfTrainings==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", totalTrainingTime/numOfTrainings);out.println();
		}
		//---------------------------------
		if(numOfPhysios==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", totalPhysioTime/numOfPhysios);out.println();
		}
		//---------------------------------
		if(numOfMassages==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", totalMassageTime/numOfMassages);out.println();
		}
		//---------------------------------
		if(numOfTrainings==0) {
			out.printf("%.3f", 0.0);out.println();
		}
		else {
			out.printf("%.3f", totalTurnaround/numOfTrainings);out.println();
		}
		out.print(maxPhysioWaitID+ " "); out.printf("%.3f", maxPhysioWait);out.println();	
		out.print(minWaitID+ " ");out.printf("%.3f", minWait); out.println(); 				
		out.println(invalidAttempts);
		out.println(cancelledAttempts);
		out.printf("%.3f", totalTime);	
	}
}