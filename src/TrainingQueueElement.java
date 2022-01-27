
public class TrainingQueueElement implements Comparable<TrainingQueueElement>{
	Player player;
	double time;
	double duration;

	public TrainingQueueElement(Player player, double time, double duration) {
		this.player = player;
		this.time = time;
		this.duration = duration;
	}


	@Override
	public int compareTo(TrainingQueueElement o) {
		if(Math.abs(this.time - o.time) < 0.00000000001) {
			if(this.player.id < o.player.id) {
				return -1;
			}
			else {
				return 1;
			}
		}
		else {
			return Double.compare(this.time, o.time);
		}
	}

}
