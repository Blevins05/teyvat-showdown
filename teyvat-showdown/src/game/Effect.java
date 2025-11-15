package game;

	public abstract class Effect {
	    protected int duration;

	    public Effect(int duration) {
	        this.duration = duration;
	    }

	    public int getDuration() {
	        return duration;
	    }

	    public void decreaseDuration() {
	        this.duration--;
	    }

	    public boolean isExpired() {
	        return duration <= 0;
	    }

	    public abstract void apply(Character target);
}


