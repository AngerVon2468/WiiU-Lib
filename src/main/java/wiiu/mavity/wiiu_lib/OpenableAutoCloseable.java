package wiiu.mavity.wiiu_lib;

public interface OpenableAutoCloseable<SELF extends OpenableAutoCloseable<SELF>> extends AutoCloseable {

	<SUBCLASS_OR_SELF extends SELF> SUBCLASS_OR_SELF open() throws Exception;

	@Override
	void close() throws Exception;
}