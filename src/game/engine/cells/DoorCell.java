package game.engine.cells;

public class DoorCell extends Cell{
	private Role role;
	private int energy;
	private boolean activated;
	
	public DoorCell(String name, Role role, int energy) {
		super(name);
		this.role=role;
		this.energy=energy;
		this.activated=false;
	}
	public Role getRole() {
		return this.role;
	}
	public int getEnergy() {
		return this.energy;
	}
	public void setActivated(boolean x) {
		this.activated=x;
	}
	public boolean isActivated() {
		return this.activated;
	}
}
