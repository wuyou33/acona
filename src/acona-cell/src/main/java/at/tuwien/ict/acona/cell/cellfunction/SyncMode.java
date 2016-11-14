package at.tuwien.ict.acona.cell.cellfunction;

public enum SyncMode {
	/**
	 * Get the datappoint by reading it from an address. No write back.
	 */
	pull,
	/**
	 * Get the datappoint by subscribing it from an address. No write back.
	 */
	push,
	/**
	 * Get the datappoint by reading it from an address. Write back value after
	 * function finished
	 */
	pullreturn,
	/**
	 * Get the datappoint by subscribing it from an address. Write back value
	 * after function finished
	 */
	pushreturn;

}