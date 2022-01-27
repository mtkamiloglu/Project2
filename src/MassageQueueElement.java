
public class MassageQueueElement implements Comparable<MassageQueueElement>{
	Player player;
	double time;
	double duration;

	public MassageQueueElement(Player player, double time, double duration) {
		this.player = player;
		this.time = time;
		this.duration = duration;
	}

	@Override
	public int compareTo(MassageQueueElement o) {
		if(this.player.skillLv > o.player.skillLv) {
			return -1;
		}
		else if(this.player.skillLv == o.player.skillLv) {
			if(Math.abs(this.time - o.time) < 0.0000000001) {
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
		else {
			return 1;
		}
	}
}