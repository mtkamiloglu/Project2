
public class Event implements Comparable<Event>{
	String type;
	int playerID;
	Double time;
	Double duration;
	int coachNum;
	int ptNum;
	int masseurNum;
	
	public Event(String type, int playerID, Double time, Double duration) {
		this.type = type;
		this.playerID = playerID;
		this.time = time;
		this.duration = duration;
	}

	public int compareTo(Event o) {
		if(Math.abs(this.time - o.time) < 0.0000000001) {
			if(this.playerID < o.playerID) {
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
