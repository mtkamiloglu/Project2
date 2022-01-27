
public class PhysioQueueElement implements Comparable<PhysioQueueElement>{
	Player player;
	double time;
	double lastTrainDur;
	
	public PhysioQueueElement(Player player, double time, double lastTrainDur) {
		this.player = player;
		this.time = time;
		this.lastTrainDur = lastTrainDur;
	}

	@Override
	public int compareTo(PhysioQueueElement o) {
		if(Math.abs(this.lastTrainDur - o.lastTrainDur) < 0.0000000001) {
			if(Math.abs(this.time - o.time) < 0.0000000001) {
				if(this.player.id < o.player.id) {
					return -1;
				}
				else{
					return 1;
				}
			}
			else {
				return Double.compare(this.time, o.time);
			}
		}
		else {
			return Double.compare(this.lastTrainDur, o.lastTrainDur);
		}
	}
	
}


