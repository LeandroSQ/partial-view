package quevedo.soares.leandro;

public interface StepIndicator {

	/**
	 * Called when page change occurs
	 *
	 * @param oldPosition The old position, -1 if none
	 * @param newPosition The new updated position
	 **/
	void onPageChange (int oldPosition, int newPosition);

}
