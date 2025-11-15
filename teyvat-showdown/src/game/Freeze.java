package game;

public class Freeze extends Effect {

    public Freeze(int duration) {
        super(duration);
    }

    @Override
    public void apply(Character target) {
        // “Congelado” simplemente hace que pierda el turno
        System.out.println(target.getName() + " está congelado y no puede actuar!");
    }
}
