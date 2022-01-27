
public class Player{
	int id;
	int skillLv;
	double trainDur;
	double trainingWaiting;
	double physioWaiting;
	double massageWaiting;
	double startWaiting;
	double trainingTime;
	double physioTime;
	double massageTime;
	boolean isInProcess;
	int trainingCount;
	int physioCount;
	int massageCount;
	
	public Player(int id, int skillLv) {
		this.id = id;
		this.skillLv = skillLv;
		this.isInProcess = false;
		this.trainingCount = 0;
		this.physioCount = 0;
		this.massageCount = 0;
		this.trainingTime = 0;
		this.physioTime = 0;
		this.massageTime = 0;
	}

	/*@Override
	public int compareTo(Player o) {
		if(this.id < o.id) {
			return -1;
		}
		else {
			return 1;
		}
	}
	*/
	public void setStartWait(double startTime) {
		this.startWaiting =startTime;
	}
	
	public void wait(double endTime, String queueType) {
		if(Math.abs(this.startWaiting - endTime) > 0.0000000001) {
			double waitTime = endTime - this.startWaiting;
			if(queueType.equals("trainingQueue")) {
				this.trainingWaiting += waitTime;
			}
			if(queueType.equals("physioQueue")) {
				this.physioWaiting += waitTime;
			}
			if(queueType.equals("massageQueue")) {
				this.massageWaiting += waitTime;
			}
		}
	}
	
	
}
