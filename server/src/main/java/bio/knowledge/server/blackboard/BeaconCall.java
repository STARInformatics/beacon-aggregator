package bio.knowledge.server.blackboard;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Wraps the CompletableFuture that is running the Supplier that
 * harvests data from the beacons. It also has fields for keeping
 * track of the progress of that Supplier.
 *
 * @param <T>
 */
public class BeaconCall<T> {
	/**
	 * A supplier interface that has some extra methods for reporting
	 * the number of discovered and processed items
	 * @param <S>
	 */
	public interface ReportableSupplier<S> extends Supplier<S> {
		public Integer reportProcessed();
		public Integer reportDiscovered();
	}
	
	private final CompletableFuture<T> future;
	private final ReportableSupplier<T> supplier;
	
	public BeaconCall(ReportableSupplier<T> supplier) {
		this.supplier = supplier;
		this.future = CompletableFuture.supplyAsync(supplier);
	}
	
	public BeaconCall(ReportableSupplier<T> supplier, Executor executor) {
		this.supplier = supplier;
		this.future = CompletableFuture.supplyAsync(supplier, executor);
	}
	
	public CompletableFuture<T> future() {
		return this.future;
	}
	
	public Integer processed() {
		return this.supplier.reportProcessed();
	}
	
	public Integer discovered() {
		return this.supplier.reportDiscovered();
	}
	
	public boolean isDone() {
		return this.future.isDone();
	}
	
	public T get() throws InterruptedException, ExecutionException {
		return this.future.get();
	}

}
